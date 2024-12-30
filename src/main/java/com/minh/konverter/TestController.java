package com.minh.konverter;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

    private final YTServices ytServices;
    private final SpotifyService spotifyService;
    private final Logger logger = LoggerFactory.getLogger(TestController.class);

    public TestController(YTServices ytServices, SpotifyService spotifyService) {
        this.ytServices = ytServices;
        this.spotifyService = spotifyService;
    }

    @GetMapping("/youtube-playlist")
    public ResponseEntity<List<Map<String, Object>>> testGetYouTubePlaylist(
            @RequestParam("accessToken") String accessToken,
            @RequestParam("playlistId") String playlistId) {
        try {
            List<Map<String, Object>> tracks = ytServices.getYoutubeTracks(playlistId, accessToken);
            logger.info("Retrieved {} tracks from YouTube playlist", tracks.size());
            return ResponseEntity.ok(tracks);
        } catch (Exception e) {
            logger.error("Error retrieving YouTube playlist", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/transfer-to-spotify")
    public ResponseEntity<Map<String, Object>> transferToSpotify(
            @RequestParam("youtubeAccessToken") String youtubeAccessToken,
            @RequestParam("spotifyAccessToken") String spotifyAccessToken,
            @RequestParam("youtubePlaylistId") String youtubePlaylistId,
            @RequestParam("playlistName") String playlistName) {
        try {
            // Get tracks from YouTube
            List<Map<String, Object>> youtubeTracks = ytServices.getYoutubeTracks(youtubePlaylistId, youtubeAccessToken);
            logger.info("Retrieved {} tracks from YouTube", youtubeTracks.size());

            // Search and get matching Spotify tracks
            List<Map<String, Object>> spotifyTracks = spotifyService.searchSpotifyTracks(spotifyAccessToken, youtubeTracks);
            logger.info("Found {} matching tracks on Spotify", spotifyTracks.size());

            // Create Spotify playlist and add tracks
            spotifyService.createPlaylistAndAddSongs(
                spotifyAccessToken,
                playlistName,
                "Imported from YouTube playlist: " + youtubePlaylistId,
                "public",
                spotifyTracks
            );

            Map<String, Object> response = Map.of(
                "totalYoutubeTracks", youtubeTracks.size(),
                "matchedSpotifyTracks", spotifyTracks.size(),
                "status", "success"
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error transferring playlist to Spotify", e);
            Map<String, Object> errorResponse = Map.of(
                "status", "error",
                "message", e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/test-spotify-search")
    public ResponseEntity<List<Map<String, Object>>> testSpotifySearch(
            @RequestParam("spotifyAccessToken") String spotifyAccessToken,
            @RequestParam("youtubeAccessToken") String youtubeAccessToken,
            @RequestParam("youtubePlaylistId") String youtubePlaylistId) {
        try {
            List<Map<String, Object>> youtubeTracks = ytServices.getYoutubeTracks(youtubePlaylistId, youtubeAccessToken);
            logger.info("Retrieved {} tracks from YouTube for testing search", youtubeTracks.size());

            List<Map<String, Object>> spotifyTracks = spotifyService.searchSpotifyTracks(spotifyAccessToken, youtubeTracks);
            logger.info("Found {} matching tracks on Spotify", spotifyTracks.size());

            return ResponseEntity.ok(spotifyTracks);
        } catch (Exception e) {
            logger.error("Error testing Spotify search", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/spotify-user")
    public ResponseEntity<String> testGetSpotifyUser(
            @RequestParam("accessToken") String accessToken) {
        try {
            String userId = spotifyService.getCurrentSpotifyUser(accessToken);
            return ResponseEntity.ok(userId);
        } catch (Exception e) {
            logger.error("Error getting Spotify user", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}