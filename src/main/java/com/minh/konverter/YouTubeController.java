package com.minh.konverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/transferYT")
public class YouTubeController {
    private static final Logger logger = LoggerFactory.getLogger(YouTubeController.class);
    private final YTServices youtubeService;
    
    public YouTubeController(YTServices youtubeService) {
        this.youtubeService = youtubeService;
    }

    public ResponseEntity<String> playlistController(
            @RequestParam("accessToken") String accessToken,
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam(value = "privacy", defaultValue = "private") String privacy,
            @RequestBody List<Map<String, Object>> tracks) {
        try {
            logger.info("=== playlistController started with {} tracks ===", tracks.size());
            if (accessToken == null || accessToken.isEmpty()) {
                logger.error("No YouTube access token provided");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("No YouTube access token found. Please authenticate first.");
            }
            
            logger.info("Creating YouTube playlist: {} with {} tracks", name, tracks.size());
            youtubeService.createPlaylist(accessToken, name, description, privacy, tracks);
            return ResponseEntity.ok("Playlist created successfully");
        } catch (Exception e) {
            logger.error("Failed to create YouTube playlist", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to create playlist: " + e.getMessage());
        }
    }
}


