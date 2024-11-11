package com.minh.konverter;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
public class YTServices {
    @Autowired
    private SpotifyPlaylistController getSPTracks;
    
    @Autowired 
    private RestTemplate restTemplate;

    @Value("${spring.youtube.api.key}")
    private String API_KEY;
    
    private final String baseURL = "https://www.googleapis.com/youtube/v3";

    private static final Logger logger = LoggerFactory.getLogger(YTServices.class);
    
    @Autowired
    public YTServices(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    public void createPlaylist(String accessToken, String name, String description, 
                             String privacy, List<Map<String,Object>> tracks) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("snippet", Map.of(
            "title", name,
            "description", description
        ));
        requestBody.put("status", Map.of("privacyStatus", privacy));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        String url = UriComponentsBuilder.fromHttpUrl(baseURL + "/playlists")
            .queryParam("key", API_KEY)
            .queryParam("part", "snippet,status")
            .toUriString();

        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, request, Map.class);
            Map<String, Object> responseBody = response.getBody();
            
            if (responseBody != null) {
                String playlistId = (String) responseBody.get("id");
                if (tracks != null && !tracks.isEmpty()) {
                    addPlaylistItems(accessToken, playlistId, tracks);
                }
            }
        } catch (Exception e) {
            logger.error("Failed to create YouTube playlist", e);
            throw new RuntimeException("Failed to create YouTube playlist", e);
        }
    }

    private void addPlaylistItems(String accessToken, String playlistId, List<Map<String,Object>> tracks) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        for (Map<String,Object> track : tracks) {
            String videoId = searchVideo(accessToken, track);
            if (videoId != null) {
                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("snippet", Map.of(
                    "playlistId", playlistId,
                    "resourceId", Map.of(
                        "kind", "youtube#video",
                        "videoId", videoId
                    )
                ));

                HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

                String url = UriComponentsBuilder.fromHttpUrl(baseURL + "/playlistItems")
                    .queryParam("key", API_KEY)  
                    .queryParam("part", "snippet")
                    .toUriString();

                try {
                    restTemplate.exchange(url, HttpMethod.POST, request, Map.class);
                    logger.info("Added video {} to playlist {}", videoId, playlistId);
                } catch (Exception e) {
                    logger.error("Failed to add video to playlist", e);
                }
            }
        }
    }

    private String searchVideo(String accessToken, Map<String,Object> trackInfo) {
        try {
            String encodedTrackInfo = URLEncoder.encode(formatTrackInfo(trackInfo), StandardCharsets.UTF_8.toString());
            String url = UriComponentsBuilder.fromHttpUrl(baseURL + "/search")
                .queryParam("key", API_KEY)
                .queryParam("part", "snippet")
                .queryParam("maxResults", 1)
                .queryParam("q", encodedTrackInfo)
                .queryParam("type", "video")
                .queryParam("videoCategoryId", "10") 
                .toUriString();

            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            Map<String, Object> responseBody = response.getBody();
            
            if (responseBody != null && responseBody.containsKey("items")) {
                List<Map<String, Object>> items = (List<Map<String, Object>>) responseBody.get("items");
                if (!items.isEmpty()) {
                    Map<String, Object> videoId = (Map<String, Object>) items.get(0).get("id");
                    return (String) videoId.get("videoId");
                }
            }
        } catch (Exception e) {
            logger.error("Failed to search for video", e);
        }
        return null;
    }

    private String formatTrackInfo(Map<String,Object> trackInfo) {
        String trackName = (String) trackInfo.get("trackName");
        Object[] artists = (Object[]) trackInfo.get("artistNames");
        String artistName = artists != null && artists.length > 0 ? artists[0].toString() : "";
        
        return String.format("%s %s official", trackName, artistName);
    }
} 
