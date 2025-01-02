package com.minh.konverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.minh.konverter.Model.Playlist;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/youtube")
public class YouTubeController {
    private static final Logger logger = LoggerFactory.getLogger(YouTubeController.class);
    private final YTServices youtubeService;

    public YouTubeController(YTServices youtubeService) {
        this.youtubeService = youtubeService;
    }

    @GetMapping("/playlists")
    public ResponseEntity<List<Map<String, Object>>> getPlaylists(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        String accessToken = extractAccessToken(authHeader);
        if (accessToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        try {
            return youtubeService.getPlaylist(accessToken);
        } catch (Exception e) {
            logger.error("Error retrieving playlists", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Fetch all tracks from a specific playlist
    @GetMapping("/playlists/{playlistId}/tracks")
    public ResponseEntity<List<Map<String, Object>>> getPlaylistTracks(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable("playlistId") String playlistId) {

        String accessToken = extractAccessToken(authHeader);
        if (accessToken == null) {
            logger.warn("Access token not provided");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        try {
            List<Map<String, Object>> tracks = youtubeService.getYoutubeTracks(playlistId, accessToken);
            return ResponseEntity.ok(tracks);
        } catch (Exception e) {
            logger.error("Error fetching tracks for playlist {}", playlistId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    private String extractAccessToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        return authHeader.substring(7);
    }
}