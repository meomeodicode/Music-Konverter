package com.minh.konverter;

import java.io.FileNotFoundException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

import jakarta.annotation.PostConstruct;

@Configuration
@PropertySource("classpath:.env") // Ensure .env is in src/main/resources
public class ConfigEnv {
    
    @Value("${SPOTIFY_CLIENT_ID:not_found}")
    private String spotifyClientId;

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ConfigEnv.class);

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
        configurer.setIgnoreResourceNotFound(false); // Throws error if .env is missing
        configurer.setLocation(new ClassPathResource(".env")); // Reads .env from resources
        return configurer;
    }

    
    @PostConstruct
    public void logEnvVars() {
        logger.info("🔍 Checking environment variables...");
        logger.info("SPOTIFY_CLIENT_ID: {}", 
            spotifyClientId.equals("not_found") ? "❌ Not loaded" : "✅ Loaded: " + spotifyClientId);

        URL resourceUrl = getClass().getClassLoader().getResource(".env");
        if (resourceUrl != null) {
            logger.info("📂 .env file found in resources at: {}", resourceUrl.getPath());
        } else {
            logger.error("❌ .env file not found in resources");
        }
    }
}