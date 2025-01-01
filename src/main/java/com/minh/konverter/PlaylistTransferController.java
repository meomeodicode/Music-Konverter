package com.minh.konverter;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class PlaylistTransferController {
    private final PlaylistTransferService playlistTransferService;
    private final Logger logger = LoggerFactory.getLogger(PlaylistTransferController.class);

    public PlaylistTransferController(PlaylistTransferService playlistTransferService) {
        this.playlistTransferService = playlistTransferService;
    }

    @PostMapping("/transfer-to-spotify")
    public ResponseEntity<Map<String, Object>> transferToSpotify(
            @RequestParam("youtubeAccessToken") String youtubeAccessToken,
            @RequestParam("spotifyAccessToken") String spotifyAccessToken,
            @RequestParam("youtubePlaylistId") String youtubePlaylistId,
            @RequestParam("playlistName") String playlistName) {
        try {
            List<Map<String, Object>> tracks = playlistTransferService.transferYouTubeToSpotify(
                youtubePlaylistId,
                youtubeAccessToken,
                spotifyAccessToken,
                playlistName,
                "Imported from YouTube"
            );

            Map<String, Object> response = Map.of(
                "status", "success",
                "tracksTransferred", tracks.size(),
                "playlistName", playlistName
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error transferring playlist", e);
            Map<String, Object> errorResponse = Map.of(
                "status", "error",
                "message", e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/transfer-to-youtube")
    public ResponseEntity<Map<String, Object>> transferToYouTube(
            @RequestParam("spotifyAccessToken") String spotifyAccessToken,
            @RequestParam("youtubeAccessToken") String youtubeAccessToken,
            @RequestParam("spotifyPlaylistId") String spotifyPlaylistId,
            @RequestParam("playlistName") String playlistName) {
        try {
            playlistTransferService.transferSpotifyToYouTube(
                spotifyPlaylistId,
                spotifyAccessToken,
                youtubeAccessToken,
                playlistName,
                "Imported from Spotify",
                "public"
            );

            Map<String, Object> response = Map.of(
                "status", "success",
                "playlistName", playlistName
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error transferring playlist", e);
            Map<String, Object> errorResponse = Map.of(
                "status", "error",
                "message", e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}

