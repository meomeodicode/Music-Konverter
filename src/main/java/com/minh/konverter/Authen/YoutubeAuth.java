package com.minh.konverter.Authen;

import org.springframework.web.util.UriComponentsBuilder;
import com.minh.konverter.StateTracker;
import com.minh.konverter.YouTubeController;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class YoutubeAuth {
    private final Logger logger = LoggerFactory.getLogger(YoutubeAuth.class);
    
    @Value("${custom.google.redirect-authen}")
    private String redirectUri;
    
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    private final YouTubeController youTubeController;
    private final StateTracker stateTracker;
    
    public YoutubeAuth(YouTubeController youTubeController, StateTracker stateTracker) {
        this.youTubeController = youTubeController;
        this.stateTracker = stateTracker;
    }

    @GetMapping("/redirectingToYoutube")
    public void login(HttpServletResponse response, HttpSession session) {
        try {
            String state = generateRandomString(16);
            logger.info("Generated state parameter: {}", state);
            session.setAttribute("youtube_oauth_state", state);
            String scope = "https://www.googleapis.com/auth/youtube.force-ssl";
            String authorizationUrl = UriComponentsBuilder.fromHttpUrl("https://accounts.google.com/o/oauth2/v2/auth")
                .queryParam("response_type", "code")
                .queryParam("client_id", clientId)
                .queryParam("scope", scope)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("state", state)
                .queryParam("access_type", "offline")
                .build()
                .toUriString();

            logger.info("Redirecting to Google with state: {}", state);
            response.sendRedirect(authorizationUrl);
        } catch (IOException e) {
            logger.error("Error during YouTube login redirect", e);
        }
    }

    @GetMapping("/YTCallback")
    public ResponseEntity<String> handleCallback(
            @RequestParam("code") String code,
            @RequestParam("state") String state,
            HttpSession session,
            HttpServletRequest request) {
        try {
            if (session.getAttribute("state_used_" + state) != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("State already used");
            }
            session.setAttribute("state_used_" + state, true);
            logger.info("=== YTCallback started with state: {} ===", state);
            logger.info("Request URL: {}", request.getRequestURL());
            logger.info("User Agent: {}", request.getHeader("User-Agent"));
            logger.info("Request Method: {}", request.getMethod());
            String storedState = (String) session.getAttribute("youtube_oauth_state");
            if (!state.equals(storedState)) {
                logger.error("State parameter mismatch");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid state parameter");
            }

            String accessToken = exchangeCodeForToken(code);
            if (accessToken == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to obtain access token");
            }
            List<Map<String, Object>> tracks = stateTracker.getTracks();
            if (tracks == null || tracks.isEmpty()) {
                logger.error("No tracks found in state tracker");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No tracks found");
            }            
            return youTubeController.playlistController(
                accessToken,
                "Transferred from Spotify",
                "Playlist transferred from Spotify",
                "private",
                tracks
            );
            
        } catch (Exception e) {
            logger.error("Error in callback processing", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error processing callback: " + e.getMessage());
        }
    }

    private String exchangeCodeForToken(String code) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            
            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("client_id", clientId);
            map.add("client_secret", clientSecret);
            map.add("code", code);
            map.add("redirect_uri", redirectUri);
            map.add("grant_type", "authorization_code");
            
            HttpEntity<MultiValueMap<String, String>> request = 
                new HttpEntity<>(map, headers);
            
            logger.info("Exchanging code for access token...");
            ResponseEntity<Map> tokenResponse = restTemplate.postForEntity(
                "https://oauth2.googleapis.com/token",
                request,
                Map.class
            );
            
            Map<String, Object> tokenData = tokenResponse.getBody();
            if (tokenData != null) {
                String accessToken = (String) tokenData.get("access_token");
                if (accessToken != null) {
                    logger.info("Access token received successfully: {}...", 
                        accessToken.substring(0, 10));
                    return accessToken;
                }
            }
            logger.error("Failed to extract access token from response");
            return null;
        } catch (Exception e) {
            logger.error("Error exchanging code for token", e);
            return null;
        }
    }

    private String generateRandomString(int length) {
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[length];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }
}
