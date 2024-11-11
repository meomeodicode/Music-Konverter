package com.minh.konverter.Authen;

import org.springframework.web.util.UriComponentsBuilder;

import com.minh.konverter.StateTracker;
import com.minh.konverter.YTServices;
import com.minh.konverter.YouTubeController;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class YTAuthen {
    private Logger logger = LoggerFactory.getLogger(YTAuthen.class);
    
    @Value("${custom.google.redirect-authen}")
    private String redirectUri;
    
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;
    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;
    private YouTubeController youTubeController;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private StateTracker stateTracker;

    @Autowired
    public YTAuthen(YouTubeController youTubeController) {
        this.youTubeController = youTubeController;
    }
    
    @GetMapping("/redirectingToYoutube")
    public void login(HttpServletResponse response, HttpSession session) { 
        try {
            String state = generateRandomString(16);
            logger.info("Generated state parameter: {}", state);
            logger.info("Stored state mapping for session: {}", session.getId());
            session.setAttribute("youtube_oauth_state", state);
            logger.info("Stored state in session with key 'youtube_oauth_state'");
            
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
    public void handleCallback(
        @RequestParam String code,
        @RequestParam String state,
        HttpSession session,
        HttpServletResponse response) throws IOException {
        
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("client_id", clientId);
        map.add("client_secret", clientSecret);
        map.add("code", code);
        map.add("redirect_uri", redirectUri);
        map.add("grant_type", "authorization_code");
        
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        
        logger.info("Exchanging code for access token...");
        ResponseEntity<Map> tokenResponse = restTemplate.postForEntity(
            "https://oauth2.googleapis.com/token",
            request,
            Map.class
        );
        
        Map<String, Object> tokenData = tokenResponse.getBody();
        String accessToken = (String) tokenData.get("access_token");
        logger.info("Access token received successfully: {}", accessToken.substring(0, 10) + "...");
        try {
            List<Map<String, Object>> tracks = stateTracker.getTracks();
            
            if (tracks == null) {
                logger.error("No tracks found in Redis");
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "No tracks found");
                return;
            }
            
            logger.info("Successfully retrieved {} tracks from Redis", tracks.size());
        
            String name = "Radiohead";
            String description = "My first project";
            String privacy = "Private";
            youTubeController.createPlaylist(accessToken, name, description, privacy, tracks);
        } catch (Exception e) {
            logger.error("Error processing tracks", e);
            throw e;
        }
    }

    private String generateRandomString(int length) {
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[length];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }
    
}

