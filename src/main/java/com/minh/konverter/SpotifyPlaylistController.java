package com.minh.konverter;

import java.util.Map;
import java.util.Optional;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.minh.konverter.Repository.PlaylistRepository;

import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import com.minh.konverter.Model.Playlist;
import com.minh.konverter.Repository.PlaylistRepository;


@RestController
@RequestMapping("/")
public class SpotifyPlaylistController {
    private static final Logger logger = LoggerFactory.getLogger(SpotifyPlaylistController.class);
    private final SpotifyService spotifyService;
    private final PlaylistCacheService cacheService;
    private final PlaylistRepository playlistRepository;
    
    public SpotifyPlaylistController(SpotifyService spotifyService, PlaylistCacheService cacheService, PlaylistRepository playlistRepository) {
        this.spotifyService = spotifyService;
        this.cacheService = cacheService;
        this.playlistRepository = playlistRepository;
    }

    @GetMapping("/playlists")
    public ResponseEntity<List<Map<String, Object>>> getUserPlaylists(
            HttpServletResponse response,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        String accessToken = extractAccessToken(authHeader);
        if (accessToken == null) {
            logger.warn("Access token not provided");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        try {
            List<Map<String, Object>> playlists = spotifyService.getUserPlaylists(accessToken);

            // Save playlists to the database
            playlists.forEach(playlistData -> {
                try {
                    Playlist playlist = new Playlist();
                    playlist.setTitle((String) playlistData.get("name"));
                    playlist.setPlatform("Spotify");
                    playlist.setUserId(1L); // Hardcoded user ID for demo purposes
                    playlist.setCreatedAt(LocalDateTime.now());
                    playlistRepository.save(playlist);
                } catch (Exception e) {
                    logger.error("Error saving playlist to the database", e);
                }
            });

            logger.info("Successfully retrieved and saved user playlists");
            return ResponseEntity.ok(playlists);
        } catch (Exception e) {
            logger.error("Error retrieving playlists", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


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
            Optional<List<Map<String, Object>>> cachedTracks = cacheService.getCachedPlaylistTracks(playlistId);
            if (cachedTracks.isPresent()) {
                logger.info("Cache hit for playlist: {}", playlistId);
                return ResponseEntity.ok(cachedTracks.get());
            }

            logger.info("Cache miss for playlist: {}, fetching from Spotify API", playlistId);
            List<Map<String, Object>> tracks = spotifyService.getSpotifyTracks(playlistId, accessToken);
            
            cacheService.cachePlaylistTracks(playlistId, tracks);
            logger.info("Successfully cached tracks for playlist: {}", playlistId);
            
            return ResponseEntity.ok(tracks);
        } catch (Exception e) {
            logger.error("Error retrieving playlist tracks for playlist: {}", playlistId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/playlists/{playlistId}/cache")
    public ResponseEntity<Void> invalidatePlaylistCache(@PathVariable("playlistId") String playlistId) {
        try {
            cacheService.invalidateCache(playlistId);
            logger.info("Successfully invalidated cache for playlist: {}", playlistId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error invalidating cache for playlist: {}", playlistId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private String extractAccessToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    @PostMapping("/save-playlists")
    public ResponseEntity<String> savePlaylists(@RequestBody List<Map<String, Object>> playlists, @RequestParam Long userId) {
        try {
            for (Map<String, Object> playlistData : playlists) {
                String title = (String) playlistData.get("title");
                String platform = "Spotify";
                if (!playlistRepository.existsByTitleAndPlatformAndUserId(title, platform, userId)) {
                    Playlist playlist = new Playlist();
                    playlist.setTitle(title);
                    playlist.setPlatform(platform);
                    playlist.setUserId(userId);
                    playlist.setCreatedAt(LocalDateTime.now());
                    playlistRepository.save(playlist);
                }
            }
            return ResponseEntity.ok("Playlists saved successfully");
        } catch (Exception e) {
            logger.error("Error saving playlists", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save playlists");
        }
    }
}