package com.minh.konverter;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.minh.konverter.Authen.YoutubeAuth;

import java.util.List;
import java.util.Map;


@Component
public class StateTracker {
    private final RedisTemplate<String, Object> redisTemplate;
    private Logger logger = LoggerFactory.getLogger(YoutubeAuth.class);
    private static final String TRACKS_KEY = "spotify:tracks"; 
    private static final long TRACKS_EXPIRATION = 1; 
    
    public StateTracker(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    
    public void saveTracks(List<Map<String, Object>> tracks) {
        redisTemplate.opsForValue().set(TRACKS_KEY, tracks);
        redisTemplate.expire(TRACKS_KEY, TRACKS_EXPIRATION, TimeUnit.HOURS);
        logger.info("Saved {} tracks to Redis", tracks.size());
    }
    
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getTracks() {
        List<Map<String, Object>> tracks = (List<Map<String, Object>>) 
            redisTemplate.opsForValue().get(TRACKS_KEY);
        logger.info("Retrieved {} tracks from Redis", 
            tracks != null ? tracks.size() : 0);
        return tracks;
    }
}