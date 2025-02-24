package com.musicservice.service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class StreamingTokenService {
    private final Map<String, StreamToken> tokenStorage = new ConcurrentHashMap<>();
    private final Random random = new Random();

    public String generateToken(Long userId, Long songId) {
        String token = generateRayId(userId, songId);
        tokenStorage.put(token, new StreamToken(songId, System.currentTimeMillis()));
        cleanupExpiredTokens();
        return token;
    }

    public Long getSongIdByToken(String token) {
        StreamToken streamToken = tokenStorage.get(token);
        if (streamToken != null && !isTokenExpired(streamToken)) {
            return streamToken.songId;
        }
        return null;
    }

    private String generateRayId(Long userId, Long songId) {
        return String.format("%d_%d_%d", userId, songId, random.nextInt(10000));
    }

    private boolean isTokenExpired(StreamToken token) {
        return System.currentTimeMillis() - token.timestamp > 900000; // 15 minutes
    }

    private void cleanupExpiredTokens() {
        tokenStorage.entrySet().removeIf(entry -> isTokenExpired(entry.getValue()));
    }

    private static class StreamToken {
        final Long songId;
        final long timestamp;

        StreamToken(Long songId, long timestamp) {
            this.songId = songId;
            this.timestamp = timestamp;
        }
    }
}