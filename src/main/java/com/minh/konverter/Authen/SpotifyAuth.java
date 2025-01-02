package com.minh.konverter.Authen;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.slf4j.Logger;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/")
public class SpotifyAuth {
    private static final Logger logger = LoggerFactory.getLogger(SpotifyAuth.class);

    @Value("${spring.security.oauth2.client.registration.spotify.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.spotify.client-secret}")
    private String clientSecret;

    @Value("${custom.callback.url}")
    private String redirectUri;

    @Value("${spring.security.oauth2.client.provider.spotify.token-uri}")
    private String tokenUrl;

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/login")
    public void login(HttpServletResponse response) {
        try {
            String state = generateRandomString(16);
            String scope = "user-read-private user-read-email " +
                    "playlist-read-private playlist-modify-private " +
                    "playlist-modify-public";
            String authorizationUrl = UriComponentsBuilder.fromHttpUrl("https://accounts.spotify.com/authorize")
                    .queryParam("response_type", "code")
                    .queryParam("client_id", clientId)
                    .queryParam("scope", scope)
                    .queryParam("redirect_uri", redirectUri)
                    .queryParam("state", state)
                    .build()
                    .toUriString();
            logger.info("Redirecting to Spotify: {}", authorizationUrl);
            response.sendRedirect(authorizationUrl);
        } catch (IOException e) {
            logger.error("Error during Spotify login redirect", e);
        }
    }

    @GetMapping("/callback")
    public void callback(@RequestParam("code") String code, @RequestParam("state") String state,
            HttpServletResponse response) {
        try {
            MultiValueMap<String, String> bodyParams = new LinkedMultiValueMap<>();
            bodyParams.add("code", code);
            bodyParams.add("redirect_uri", redirectUri);
            bodyParams.add("grant_type", "authorization_code");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.set("Authorization",
                    "Basic " + Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes()));

            ResponseEntity<String> responseEntity = restTemplate.exchange(
                    tokenUrl,
                    HttpMethod.POST,
                    new HttpEntity<>(bodyParams, headers),
                    String.class);

            String accessToken = new ObjectMapper()
                    .readTree(responseEntity.getBody())
                    .get("access_token")
                    .asText();

            if (accessToken != null) {
                String frontendUri = UriComponentsBuilder.fromHttpUrl("http://localhost:3000/playlists")
                        .queryParam("service", "spotify")
                        .queryParam("access_token", accessToken)
                        .build()
                        .toUriString();
                response.sendRedirect(frontendUri);
            } else {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Access token is null.");
            }
        } catch (Exception e) {
            logger.error("Error during Spotify OAuth2 callback", e);
            try {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            } catch (IOException ie) {
                logger.error("Failed to send error response", ie);
            }
        }
    }

    private String generateRandomString(int length) {
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[length];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }
}
