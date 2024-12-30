package com.minh.konverter;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class SpotifyService {

    private static final Logger logger = LoggerFactory.getLogger(SpotifyService.class);
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private StateTracker stateTracker;

 public String getCurrentSpotifyUser(String accessToken) {
        logger.info("Getting current Spotify user");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);
        
        String url = "https://api.spotify.com/v1/me";
        HttpEntity<Void> request = new HttpEntity<>(headers);
        
        try {
            ResponseEntity<Map> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                Map.class
            );
            String userId = (String) responseEntity.getBody().get("id");
            if (userId == null) {
                logger.error("Could not retrieve Spotify user ID");
                throw new RuntimeException("User ID not found in response");
            }
            return userId;
        } catch (Exception e) {
            logger.error("Error retrieving current Spotify user", e);
            throw new RuntimeException("Error retrieving current Spotify user: " + e.getMessage(), e);
        }
    }

    private String formatTrackForSearch(Map<String, Object> youtubeTrack) {
        String title = (String) youtubeTrack.get("title");
        String artist = (String) youtubeTrack.get("artist");
        
        if (title != null) {
            title = title.replaceAll("(?i)(\\(Official.*?\\))|(\\[Official.*?\\])|" +
                                   "(\\(Lyric.*?\\))|(\\[Lyric.*?\\])|" +
                                   "(\\(Audio.*?\\))|(\\[Audio.*?\\])|" +
                                   "(\\(Music.*?\\))|(\\[Music.*?\\])", "")
                        .trim();
        }
        
        if (artist != null && !artist.isEmpty()) {
            return String.format("%s %s", title, artist);
        } else {
            return title;
        }
    }

    public List<Map<String, Object>> searchSpotifyTracks(String accessToken, List<Map<String, Object>> youtubeTracks) {
        List<Map<String, Object>> spotifyTracks = new ArrayList<>();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        
        for (Map<String, Object> youtubeTrack : youtubeTracks) {
            String searchQuery = formatTrackForSearch(youtubeTrack);
            try {
                String url = UriComponentsBuilder.fromHttpUrl("https://api.spotify.com/v1/search")
                    .queryParam("q", searchQuery)
                    .queryParam("type", "track")
                    .queryParam("limit", 1)  
                    .toUriString();

                HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
                logger.info("Searching Spotify for track: {}", searchQuery);
                
                ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, Map.class);
                Map<String, Object> responseBody = response.getBody();
                
                if (responseBody != null && responseBody.containsKey("tracks")) {
                    Map<String, Object> tracksMap = (Map<String, Object>) responseBody.get("tracks");
                    List<Map<String, Object>> items = (List<Map<String, Object>>) tracksMap.get("items");
                    
                    if (!items.isEmpty()) {
                        spotifyTracks.add(items.get(0));
                    } else {
                        logger.warn("No Spotify match found for: {}", searchQuery);
                    }
                }
                
                Thread.sleep(100);
                
            } catch (Exception e) {
                logger.error("Error searching for track: {}", searchQuery, e);
            }
        }
        
        return spotifyTracks;
    }

    private void addTracksToPlaylist(String accessToken, String playlistId, List<Map<String, Object>> tracks) {
        if (tracks.isEmpty()) {
            logger.warn("No tracks to add to playlist");
            return;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        List<String> trackUris = tracks.stream()
            .map(track -> (String) track.get("uri"))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

        for (int i = 0; i < trackUris.size(); i += 100) {
            int endIndex = Math.min(i + 100, trackUris.size());
            List<String> batch = trackUris.subList(i, endIndex);
            
            Map<String, Object> requestBody = Map.of("uris", batch);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            
            String url = String.format("https://api.spotify.com/v1/playlists/%s/tracks", playlistId);
            
            try {
                restTemplate.exchange(url, HttpMethod.POST, request, Map.class);
                logger.info("Added batch of {} tracks to playlist", batch.size());
            } catch (Exception e) {
                logger.error("Failed to add tracks to playlist", e);
                throw new RuntimeException("Failed to add tracks to playlist: " + e.getMessage(), e);
            }
        }
    }

    public void createPlaylistAndAddSongs(String accessToken, String name, String description, 
                                        String privacy, List<Map<String, Object>> spotifyTracks) {
        String userId = getCurrentSpotifyUser(accessToken);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);
        
        Map<String, Object> requestBody = Map.of(
            "name", name,
            "description", description,
            "public", "public".equalsIgnoreCase(privacy)
        );

        String createUrl = String.format("https://api.spotify.com/v1/users/%s/playlists", userId);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        
        try {
            ResponseEntity<Map> response = restTemplate.exchange(createUrl, HttpMethod.POST, request, Map.class);
            Map<String, Object> responseBody = response.getBody();
            
            if (responseBody != null && responseBody.containsKey("id")) {
                String playlistId = (String) responseBody.get("id");
                addTracksToPlaylist(accessToken, playlistId, spotifyTracks);
            }
        } catch (Exception e) {
            logger.error("Failed to create Spotify playlist", e);
            throw new RuntimeException("Failed to create Spotify playlist: " + e.getMessage(), e);
        }
    }


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
            List<Map<String, Object>> allTracks = new ArrayList<>();
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
