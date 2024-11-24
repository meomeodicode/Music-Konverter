package com.minh.konverter;

import java.time.Duration;
import java.util.*;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class PlaylistCacheService {
    private static final String PLAYLIST_KEY_PREFIX = "spotify_tracks:";
    private static final String TRACK_KEY_PREFIX = "track:";
    private static final String TRACK_SET_PREFIX = "playlist_tracks:";
    private static final String TRANSFER_STATUS_PREFIX = "transfer_status:";
    private final RedisTemplate<String, Object> redisTemplate;
    private final Logger logger = LoggerFactory.getLogger(PlaylistCacheService.class);

    @Value("${redis.playlist.ttl:3600}") // 1 hour default for playlists
    private long playlistTtl;

    @Value("${redis.track.ttl:86400}") // 24 hours default for individual tracks
    private long trackTtl;

    @Value("${redis.max.retry:3}")
    private int maxRetryAttempts;

    public PlaylistCacheService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Optional<List<Map<String, Object>>> getCachedPlaylistTracks(String playlistId) {
        String trackSetKey = generateTrackSetKey(playlistId);
        try {
            // First, try to get the set of track IDs for this playlist
            @SuppressWarnings("unchecked")
            Set<String> trackIds = (Set<String>) redisTemplate.opsForValue().get(trackSetKey);
            
            if (trackIds == null || trackIds.isEmpty()) {
                return Optional.empty();
            }

            // Fetch all tracks from their individual cache entries
            List<Map<String, Object>> tracks = new ArrayList<>();
            Set<String> missingTrackIds = new HashSet<>();

            for (String trackId : trackIds) {
                Optional<Map<String, Object>> track = getCachedTrack(trackId);
                if (track.isPresent()) {
                    tracks.add(track.get());
                } else {
                    missingTrackIds.add(trackId);
                }
            }

            // If all tracks were found, return them
            if (missingTrackIds.isEmpty()) {
                return Optional.of(tracks);
            }
            
            // If some tracks are missing, return empty to trigger a full refresh
            return Optional.empty();
            
        } catch (Exception e) {
            logger.error("Error retrieving cached playlist: {}", playlistId, e);
            return Optional.empty();
        }
    }

    public void cachePlaylistTracks(String playlistId, List<Map<String, Object>> tracks) {
        try {
            redisTemplate.execute(new SessionCallback<Object>() {
                @Override
                public Object execute(RedisOperations operations) throws DataAccessException {
                    operations.multi();
                    
                    // Cache individual tracks and collect their IDs
                    Set<String> trackIds = new HashSet<>();
                    for (Map<String, Object> track : tracks) {
                        String trackId = (String) track.get("id");
                        if (trackId != null) {
                            String trackKey = generateTrackKey(trackId);
                            operations.opsForValue().set(trackKey, track);
                            operations.expire(trackKey, trackTtl, TimeUnit.SECONDS);
                            trackIds.add(trackId);
                        }
                    }

                    // Cache the set of track IDs for this playlist
                    String trackSetKey = generateTrackSetKey(playlistId);
                    operations.opsForValue().set(trackSetKey, trackIds);
                    operations.expire(trackSetKey, playlistTtl, TimeUnit.SECONDS);

                    return operations.exec();
                }
            });
            logger.info("Successfully cached playlist: {} with {} tracks", playlistId, tracks.size());
        } catch (Exception e) {
            logger.error("Error caching playlist: {}", playlistId, e);
            throw new RuntimeException("Failed to cache playlist tracks", e);
        }
    }

    public Optional<Map<String, Object>> getCachedTrack(String trackId) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> track = (Map<String, Object>) redisTemplate.opsForValue()
                    .get(generateTrackKey(trackId));
            return Optional.ofNullable(track);
        } catch (Exception e) {
            logger.error("Error retrieving cached track: {}", trackId, e);
            return Optional.empty();
        }
    }

    public void cacheTrack(String trackId, Map<String, Object> trackData) {
        try {
            String trackKey = generateTrackKey(trackId);
            redisTemplate.opsForValue().set(trackKey, trackData, trackTtl, TimeUnit.SECONDS);
            logger.debug("Successfully cached track: {}", trackId);
        } catch (Exception e) {
            logger.error("Error caching track: {}", trackId, e);
        }
    }

    public void invalidateCache(String playlistId) {
        try {
            redisTemplate.execute(new SessionCallback<Object>() {
                @Override
                public Object execute(RedisOperations operations) throws DataAccessException {
                    operations.multi();
                    
                    // Get track IDs before deleting
                    String trackSetKey = generateTrackSetKey(playlistId);
                    @SuppressWarnings("unchecked")
                    Set<String> trackIds = (Set<String>) operations.opsForValue().get(trackSetKey);
                    
                    // Delete track set
                    operations.delete(trackSetKey);
                    
                    // Delete individual tracks if they exist
                    if (trackIds != null) {
                        for (String trackId : trackIds) {
                            operations.delete(generateTrackKey(trackId));
                        }
                    }
                    
                    return operations.exec();
                }
            });
            logger.info("Successfully invalidated cache for playlist: {}", playlistId);
        } catch (Exception e) {
            logger.error("Error invalidating cache for playlist: {}", playlistId, e);
        }
    }

    private String generateTrackKey(String trackId) {
        return TRACK_KEY_PREFIX + trackId;
    }

    private String generateTrackSetKey(String playlistId) {
        return TRACK_SET_PREFIX + playlistId;
    }

    // Existing methods remain unchanged
    private String generatePlaylistKey(String playlistId) {
        return PLAYLIST_KEY_PREFIX + playlistId;
    }

    private String generateStatusKey(String playlistId) {
        return TRANSFER_STATUS_PREFIX + playlistId;
    }
}