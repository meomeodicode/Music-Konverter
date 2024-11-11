package com.minh.konverter;

import java.util.Map;
import java.util.List;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;


@RestController
@RequestMapping("/")
public class SpotifyPlaylistController
{
    private static final Logger logger = LoggerFactory.getLogger(SpotifyPlaylistController.class);
    private final SpotifyService spotifyService;
    private static final String SPOTIFY_TRACKS_SESSION_KEY = "spotify_tracks";
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public SpotifyPlaylistController(SpotifyService spotifyService) {
        this.spotifyService = spotifyService;
    }

    @GetMapping("/playlists")
    public ResponseEntity<List<Map<String, Object>>> getUserPlaylists(
            HttpServletResponse response,
            @RequestHeader(value = "Authorization", required = false) String authHeader) 
       {
        String accessToken = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            accessToken = authHeader.substring(7);
        }
    
        if (accessToken == null) {
            logger.warn("Access token not provided");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    
        try {
            List<Map<String, Object>> playlists = spotifyService.getUserPlaylists(accessToken);
            logger.info("Successfully retrieved user playlists");
            return ResponseEntity.ok(playlists);
        } catch (Exception e) {
            logger.error("Error retrieving playlists", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    @GetMapping("/playlists/{playlistId}/tracks")
    public ResponseEntity<List<Map<String, Object>>> getPlaylistTracks(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable("playlistId") String playlistId,
            HttpSession session
            ) {
        
        String accessToken = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            accessToken = authHeader.substring(7);
        }
        
        if (accessToken == null) {
            logger.warn("Access token not provided");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        
        try {
            List<Map<String, Object>> tracks = spotifyService.getSpotifyTracks(playlistId, accessToken);
            logger.info("Successfully stored tracks in Redis for playlist: {}", playlistId);
            return ResponseEntity.ok(tracks);
        } catch (Exception e) {
            logger.error("Error retrieving playlist tracks", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}



