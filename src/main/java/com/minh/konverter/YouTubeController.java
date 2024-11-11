package com.minh.konverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
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
    
    @Autowired
    public YouTubeController(YTServices youtubeService) {
        this.youtubeService = youtubeService;
    }
    @PostMapping("/prepareTransfer")
    public ResponseEntity<String> createPlaylist(
            @RequestParam String accessToken,  
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam(defaultValue = "private") String privacy,
            @RequestBody List<Map<String, Object>> tracks) {
        try {
            if (accessToken == null) {
                logger.error("No YouTube access token found in session");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("No YouTube access token found. Please authenticate first.");
            }
            
            logger.info("Retrieved access token from session: {}...", accessToken.substring(0, 10));
            logger.info("Creating YouTube playlist: {}", name);
            
            youtubeService.createPlaylist(accessToken, name, description, privacy, tracks);
            return ResponseEntity.ok("Playlist created successfully");
        } catch (Exception e) {
            logger.error("Failed to create YouTube playlist", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to create playlist: " + e.getMessage());
        }
    }
}

