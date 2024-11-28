package com.minh.konverter;

import java.util.*;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

@Service
public class PlaylistCacheService {
    private static final String PLAYLIST_KEY_PREFIX = "spotify_tracks:";
    private static final String TRACK_KEY_PREFIX = "track:";
    private static final String TRACK_SET_PREFIX = "playlist_tracks:";
    private static final String TRANSFER_STATUS_PREFIX = "transfer_status:";
    private final RedisTemplate<String, Object> redisTemplate;
    private final Logger logger = LoggerFactory.getLogger(PlaylistCacheService.class);

    @Value("${redis.playlist.ttl:3600}")
    private long playlistTtl;

    @Value("${redis.track.ttl:86400}")
    private long trackTtl;

    @Value("${redis.max.retry:3}")
    private int maxRetryAttempts;

    public PlaylistCacheService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Optional<List<Map<String, Object>>> getCachedPlaylistTracks(String playlistId) {
        String trackSetKey = generateTrackSetKey(playlistId);
        try {
            @SuppressWarnings("unchecked")
            Set<String> trackIds = (Set<String>) redisTemplate.opsForValue().get(trackSetKey);
            if (trackIds == null || trackIds.isEmpty()) {
                return Optional.empty();
            }

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

            if (missingTrackIds.isEmpty()) {
                return Optional.of(tracks);
            }
            
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
                    Set<String> trackIds = new HashSet<>();
                    for (Map<String, Object> track : tracks) {
                        String trackId = (String) track.get("id");
                        //miss cache
                        if (trackId != null) {
                            String trackKey = generateTrackKey(trackId);
                            operations.opsForValue().set(trackKey, track);
                            operations.expire(trackKey, trackTtl, TimeUnit.SECONDS);
                            trackIds.add(trackId);
                        }
                    }

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
                    String trackSetKey = generateTrackSetKey(playlistId);

                    @SuppressWarnings("unchecked")
                    Set<String> trackIds = (Set<String>) operations.opsForValue().get(trackSetKey);
                    operations.delete(trackSetKey);
                    
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

    private String generatePlaylistKey(String playlistId) {
        return PLAYLIST_KEY_PREFIX + playlistId;
    }

    private String generateStatusKey(String playlistId) {
        return TRANSFER_STATUS_PREFIX + playlistId;
    }
}