package com.minh.konverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.HashMap;

@Service
    public class YTServices {
        private static final Logger logger = LoggerFactory.getLogger(YTServices.class);
        private final RestTemplate restTemplate;
        private final String baseURL = "https://www.googleapis.com/youtube/v3";
    
        @Value("${youtube.api.key}")
        private String API_KEY;
        
        public YTServices(RestTemplate restTemplate) {
            this.restTemplate = restTemplate;
        }
    
        public Map<String, Object> getPlaylist(String accessToken) {
            logger.info("Fetching YouTube playlist details");
            String url = UriComponentsBuilder.fromHttpUrl(baseURL + "/playlists")
                    .queryParam("key", API_KEY)
                    .queryParam("part", "snippet")
                    .queryParam("mine", true)
                    .toUriString();
    
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            HttpEntity<String> entity = new HttpEntity<>(headers);
    
            try {
                ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
                Map<String, Object> responseBody = response.getBody();
                
                if (responseBody != null && responseBody.containsKey("items")) {
                    List<Map<String, Object>> items = (List<Map<String, Object>>) responseBody.get("items");
                    if (!items.isEmpty()) {
                        Map<String, Object> snippet = (Map<String, Object>) items.get(0).get("snippet");
                        return Map.of(
                            "name", snippet.get("title"),
                            "description", snippet.getOrDefault("description", ""),
                            "privacy", "private" 
                        );
                    }
                }
                throw new RuntimeException("No playlists found for the authenticated user");
            } catch (Exception e) {
                logger.error("Failed to fetch YouTube playlist: {}", e.getMessage());
                throw new RuntimeException("Failed to fetch YouTube playlist", e);
            }
        }
    
        public List<Map<String, Object>> getYoutubeTracks(String playlistId, String accessToken) {
            logger.info("Fetching tracks for playlist: {}", playlistId);
            try {
                List<Map<String, Object>> allTracks = new ArrayList<>();
                String nextPageToken = null;
    
                do {
                    Map<String, Object> pageResult = fetchPlaylistPage(playlistId, accessToken, nextPageToken);
                    List<Map<String, Object>> tracks = extractTracksFromPage(pageResult);
                    allTracks.addAll(tracks);
                    
                    nextPageToken = (String) pageResult.get("nextPageToken");
                } while (nextPageToken != null);
    
                logger.info("Successfully fetched {} tracks from YouTube playlist", allTracks.size());
                return allTracks;
            } catch (Exception e) {
                logger.error("Error fetching YouTube tracks: {}", e.getMessage());
                throw new RuntimeException("Error fetching YouTube tracks", e);
            }
        }
    
        private Map<String, Object> fetchPlaylistPage(String playlistId, String accessToken, String pageToken) {
            UriComponentsBuilder preURL = UriComponentsBuilder.fromHttpUrl(baseURL + "/playlistItems")
                    .queryParam("key", API_KEY)
                    .queryParam("part", "snippet")
                    .queryParam("playlistId", playlistId)
                    .queryParam("maxResults", 50);
            
            String url = "";
    
            if (pageToken != null) {
                url = preURL.queryParam("pageToken", pageToken).toUriString();
            } else {
                url = preURL.toUriString();
            }
    
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
    
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    requestEntity,
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            );
    
            if (response.getBody() == null) {
                throw new RuntimeException("Received null response from YouTube API");
            }
    
            return response.getBody();
        }
    
        private List<Map<String, Object>> extractTracksFromPage(Map<String, Object> pageData) {
            List<Map<String, Object>> tracks = new ArrayList<>();
            List<Map<String, Object>> items = (List<Map<String, Object>>) pageData.get("items");
            
            if (items != null) {
                for (Map<String, Object> item : items) {
                    Map<String, Object> snippet = (Map<String, Object>) item.get("snippet");
                    
                    if (snippet != null) {
                        try {
                            tracks.add(extractTrackInfo(snippet));
                        } catch (Exception e) {
                            logger.warn("Failed to extract track info: {}", e.getMessage());
                        }
                    }
                }
            }
            
            return tracks;
        }
    
        private Map<String, Object> extractTrackInfo(Map<String, Object> snippet) {
            return Map.of(
                "title", snippet.get("title"),
                "publish", snippet.get("publishedAt")
            );
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
