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
    private final RestTemplate restTemplate;

    @Value("${youtube.api.key}")
    private String API_KEY;
    
    private final String baseURL = "https://www.googleapis.com/youtube/v3";

    private static final Logger logger = LoggerFactory.getLogger(YTServices.class);
    
    public YTServices(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    public void createPlaylist(String accessToken, String name, String description, 
                             String privacy, List<Map<String,Object>> tracks) {
        logger.info("=== createPlaylist service started with {} tracks ===", tracks.size());
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
            String rawQuery = formatTrackInfo(trackInfo);     
            String url = baseURL + "/search?key=" + API_KEY
                    + "&part=snippet"
                    + "&maxResults=1"
                    + "&q=" + rawQuery
                    + "&type=video"
                    + "&videoCategoryId=10";
            logger.info("Url raw: {}", url);
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            Map<String, Object> responseBody = response.getBody();
            logger.info("API Response: {}", responseBody);
    
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
        logger.info(trackName + artistName);
        String result = String.format("%s %s", trackName, artistName);
        logger.info("Here are songs: {}", result);

        return result;
    }
} 
