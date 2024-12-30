package com.minh.konverter;

import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class PlaylistTransferController {

    private final PlaylistTransferService playlistTransferService;

    public PlaylistTransferController(PlaylistTransferService playlistTransferService) {
        this.playlistTransferService = playlistTransferService;
    }

    @GetMapping("/youtube-to-spotify")
    public ResponseEntity<List<Map<String, Object>>> testTransferYouTubeToSpotify(
            @RequestParam String sourcePlaylistId,
            @RequestParam String youtubeToken,
            @RequestParam String spotifyToken,
            @RequestParam String playlistName,
            @RequestParam String description) {

        List<Map<String, Object>> tracks = playlistTransferService.transferYouTubeToSpotify(
            sourcePlaylistId,
            youtubeToken,
            spotifyToken,
            playlistName,
            description
        );

        return ResponseEntity.ok(tracks);
    }
}
