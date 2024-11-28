package com.minh.konverter;

import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class SpotifyService {

    private static final Logger logger = LoggerFactory.getLogger(SpotifyService.class);
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private StateTracker stateTracker;

    private final List<Map<String, Object>> allTracks = new ArrayList<>();

    public List<Map<String, Object>> getUserPlaylists(String accessToken) {
        try {
            String url = "https://api.spotify.com/v1/me/playlists";
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);

            logger.info("Sending request to Spotify API: {}", url);

            ResponseEntity<Map> response = restTemplate.exchange(
                url, 
                HttpMethod.GET, 
                new HttpEntity<>(headers), 
                Map.class
            );

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> items = (List<Map<String, Object>>) response.getBody().get("items");
            return items;
        } catch (HttpClientErrorException e) {
            logger.error("HTTP error when calling Spotify API", e);
            throw new RuntimeException("Error calling Spotify API", e);
        }
    }

    public List<Map<String, Object>> getSpotifyTracks(String playlistID, String accessToken) {
        try {
            String url = "https://api.spotify.com/v1/playlists/" + playlistID + "/tracks";
            while (url != null) {
                HttpHeaders headers = new HttpHeaders();
                headers.setBearerAuth(accessToken);
                HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

                ResponseEntity<Map<String, Object>> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    requestEntity,
                    new ParameterizedTypeReference<Map<String, Object>>() {}
                );

                Map<String, Object> response = responseEntity.getBody();
                if (response == null) {
                    throw new RuntimeException("Response Entity is null");
                }

                List<Map<String, Object>> trackPerPage = (List<Map<String, Object>>) response.get("items");
                trackPerPage.forEach(track -> {
                    Map<String, Object> trackInfo = extractTrackInfo((Map<String, Object>) track.get("track"));
                    allTracks.add(trackInfo);
                });

                url = (String) response.get("next");
            }

            stateTracker.saveTracks(allTracks);
            return allTracks;
        } catch (Exception e) {
            logger.error("Error fetching Spotify tracks", e);
            throw new RuntimeException("Error fetching Spotify tracks", e);
        }
    }

    private Map<String, Object> extractTrackInfo(Map<String, Object> track) {
        return Map.of(
            "trackId", track.get("id"),
            "trackName", extractSongName((String) track.get("name")),
            "artistNames", ((List<Map<String, Object>>) track.get("artists"))
                .stream()
                .map(artist -> artist.get("name"))
                .toArray(),
            "albumName", ((Map<String, Object>) track.get("album")).get("name"),
            "duration", track.get("duration_ms")
        );
    }

    private String extractSongName(String songName) {
        if (songName.contains("(feat. ")) {
            return songName.substring(0, songName.indexOf("(feat. "));
        } else if (songName.contains("(with ")) {
            return songName.substring(0, songName.indexOf("(with "));
        }
        return songName;
    }
}
