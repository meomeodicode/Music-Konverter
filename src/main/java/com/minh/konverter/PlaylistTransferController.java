package com.minh.konverter;

import java.time.LocalDateTime;
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

import com.minh.konverter.Model.Conversion;
import com.minh.konverter.Model.User;
import com.minh.konverter.Repository.UserRepository;

@RestController
@RequestMapping("/")
public class PlaylistTransferController {

    private final PlaylistTransferService playlistTransferService;
    private final ConversionService conversionService;
    private final UserRepository userRepository;
    private final Logger logger = LoggerFactory.getLogger(PlaylistTransferController.class);

    public PlaylistTransferController(
            PlaylistTransferService playlistTransferService,
            ConversionService conversionService,
            UserRepository userRepository) {
        this.playlistTransferService = playlistTransferService;
        this.conversionService = conversionService;
        this.userRepository = userRepository;
    }

    /**
     * Transfer playlist from YouTube to Spotify.
     */
    @PostMapping("/transfer-to-spotify")
    public ResponseEntity<Map<String, Object>> transferToSpotify(
            @RequestParam("youtubeAccessToken") String youtubeAccessToken,
            @RequestParam("spotifyAccessToken") String spotifyAccessToken,
            @RequestParam("youtubePlaylistId") String youtubePlaylistId,
            @RequestParam("playlistName") String playlistName) {
        try {
            // Perform the playlist transfer
            List<Map<String, Object>> tracks = playlistTransferService.transferYouTubeToSpotify(
                    youtubePlaylistId, youtubeAccessToken, spotifyAccessToken, playlistName, "Imported from YouTube");

            // Simulate fetching an authenticated user
            User authenticatedUser = getAuthenticatedUser();
            saveConversion("YouTube", "Spotify", youtubePlaylistId, authenticatedUser);

            Map<String, Object> response = Map.of(
                    "status", "success",
                    "tracksTransferred", tracks.size(),
                    "playlistName", playlistName);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error transferring playlist to Spotify", e);
            Map<String, Object> errorResponse = Map.of(
                    "status", "error",
                    "message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Transfer playlist from Spotify to YouTube.
     */
    @PostMapping("/transfer-to-youtube")
    public ResponseEntity<Map<String, Object>> transferToYouTube(
            @RequestParam("spotifyAccessToken") String spotifyAccessToken,
            @RequestParam("youtubeAccessToken") String youtubeAccessToken,
            @RequestParam("spotifyPlaylistId") String spotifyPlaylistId,
            @RequestParam("playlistName") String playlistName) {
        try {
            // Perform the playlist transfer
            playlistTransferService.transferSpotifyToYouTube(
                    spotifyPlaylistId, spotifyAccessToken, youtubeAccessToken, playlistName, "Imported from Spotify",
                    "public");

            // Simulate fetching an authenticated user
            User authenticatedUser = getAuthenticatedUser();
            saveConversion("Spotify", "YouTube", spotifyPlaylistId, authenticatedUser);

            Map<String, Object> response = Map.of(
                    "status", "success",
                    "playlistName", playlistName);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error transferring playlist to YouTube", e);
            Map<String, Object> errorResponse = Map.of(
                    "status", "error",
                    "message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Fetch conversion history for a specific user.
     */
    @GetMapping("/api/conversion-history")
    public ResponseEntity<List<Conversion>> getConversionHistory(@RequestParam("userId") Long userId) {
        // // Find the user from the UserRepository
        // User user = userRepository.findById(userId)
        //         .orElseThrow(() -> new IllegalArgumentException("User not found for ID: " + userId));

        // Fetch conversions for the User
        List<Conversion> conversions = conversionService.getConversionsForUser(userId);

        // Return the results
        return ResponseEntity.ok(conversions);
    }

    /**
     * Save a conversion record to the database.
     */
    private void saveConversion(String sourcePlatform, String targetPlatform, String playlistId, User user) {
        Conversion conversion = new Conversion();
        conversion.setSourcePlatform(sourcePlatform);
        conversion.setTargetPlatform(targetPlatform);
        conversion.setPlaylistId(playlistId);
        conversion.setConversionDate(LocalDateTime.now());
        conversion.setUid(user.getUid());

        conversionService.saveConversion(conversion);
    }

    /**
     * Simulate fetching an authenticated user.
     */
    private User getAuthenticatedUser() {
        // For testing, returning a hardcoded user
        return userRepository.findById(1L).orElseThrow(() ->
                new IllegalArgumentException("User not found for ID: 1"));
    }
}
