package com.minh.konverter;

import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import java.util.concurrent.TimeUnit;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;

@Service
public class SpotifyService {
    private static final Logger logger = LoggerFactory.getLogger(SpotifyService.class);
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Resource(name="redisTemplate")
    List<Map<String, Object>> allTracks = new ArrayList<>();
    
    @Autowired
    private HttpSession httpSession;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private StateTracker stateTracker;
    
    public List<Map<String, Object>> getUserPlaylists(String accessToken) {
        try {
            String url = "https://api.spotify.com/v1/me/playlists";
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            
            logger.info("Sending request to Spotify API: {}", url);
            logger.info("Using access token: {}", accessToken);

            ResponseEntity<Map> response = restTemplate.exchange(
                url, 
                HttpMethod.GET, 
                new HttpEntity<>(headers), 
                Map.class);
            logger.info("Received response from Spotify API. Status code: {}", response.getStatusCode());
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> items = (List<Map<String, Object>>) response.getBody().get("items");
            return items;
        } catch (HttpClientErrorException e) {
            logger.error("HTTP error when calling Spotify API. Status code: {}, Response body: {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Error calling Spotify API", e);
        } catch (Exception e) {
            logger.error("Unexpected error when retrieving playlists from Spotify", e);
            throw new RuntimeException("Failed to retrieve playlists", e);
        }
    }

    public List<Map<String, Object>> getSpotifyTracks(String playlistID, String accessToken) {
        try {
            String url = "https://api.spotify.com/v1/playlists/" + playlistID + "/tracks";
            List<Map<String, Object>> allTracks = new ArrayList<>();
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
                for (Map<String, Object> track : trackPerPage) {
                    Map<String, Object> trackInfo = extractTrackInfo((Map<String, Object>) track.get("track"));
                    allTracks.add(trackInfo);
                }
                url = (String) response.get("next");
            }
            
            // Store tracks in Redis
            stateTracker.saveTracks(allTracks);
            return allTracks;
        } catch (Exception e) {
            logger.error("Error fetching Spotify tracks", e);
            throw new RuntimeException("Error fetching Spotify tracks", e);
        }
    }
      
    private Map<String,Object> extractTrackInfo(Map<String,Object> track) {
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
    

    private String extractSongName(String songName) 
    {
        if (songName.contains("(feat. ")) {
            return songName.substring(0, songName.indexOf("(feat. "));
        } else if (songName.contains("(Feat. ")) {
            return songName.substring(0, songName.indexOf("(Feat. "));
        } else if (songName.contains("(with ")) {
            return songName.substring(0, songName.indexOf("(with "));
        }
        return songName;
    }
}
