package com.minh.konverter;
import org.springframework.stereotype.Service;
import com.minh.konverter.YTServices;
import java.util.List;
import java.util.Map;

@Service
public class PlaylistTransferService {

    private final SpotifyService spotifyService;
    private final YTServices youTubeService;

    public PlaylistTransferService(SpotifyService spotifyService, YTServices youTubeService) {
        this.spotifyService = spotifyService;
        this.youTubeService = youTubeService;
    }

    public void transferSpotifyToYouTube(String sourcePlaylistId,
                                           String spotifyToken,
                                           String youtubeToken,
                                           String playlistName,
                                           String description,
                                           String privacy) {
        List<Map<String, Object>> tracks = spotifyService.getSpotifyTracks(sourcePlaylistId, spotifyToken);
        youTubeService.createPlaylist(youtubeToken, playlistName, description, privacy, tracks);
    }

    public List<Map<String, Object>> transferYouTubeToSpotify(String sourcePlaylistId,
                                           String youtubeToken,
                                           String spotifyToken,
                                           String playlistName,
                                           String description) {
        List<Map<String, Object>> tracks = youTubeService.getYoutubeTracks(sourcePlaylistId, youtubeToken);
        return tracks;
    }
}