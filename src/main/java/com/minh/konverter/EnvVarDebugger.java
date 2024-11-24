package com.minh.konverter;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class EnvVarDebugger implements CommandLineRunner {

    @org.springframework.beans.factory.annotation.Value("${SPOTIFY_CLIENT_ID:not_found}")
    private String spotifyClientId;

    @Override
    public void run(String... args) {
        System.out.println("🔍 Checking environment variables...");
        System.out.println("SPOTIFY_CLIENT_ID: " + 
            (spotifyClientId.equals("not_found") ? "❌ Not loaded" : "✅ Loaded: " + spotifyClientId));
    }
}
