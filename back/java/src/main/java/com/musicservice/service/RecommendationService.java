package com.musicservice.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class RecommendationService {
    private final RestTemplate restTemplate;
    private final String recommendationServiceUrl;

    public RecommendationService(
            RestTemplate restTemplate,
            @Value("${recommendation-service.url}") String recommendationServiceUrl
    ) {
        this.restTemplate = restTemplate;
        this.recommendationServiceUrl = recommendationServiceUrl;
    }

    public List<String> getRecommendations(String userId) {
        try {
            ResponseEntity<RecommendationResponse> response = restTemplate.getForEntity(
                    recommendationServiceUrl + "/api/recommendations/{userId}",
                    RecommendationResponse.class,
                    userId
            );
            return response.getBody().getSongIds();
        } catch (Exception e) {
            log.error("Ошибка при получении рекомендаций для пользователя {}: {}", userId, e.getMessage());
            return Collections.emptyList();
        }
    }

    public void sendFeedback(String userId, String songId, String action) {
        try {
            FeedbackRequest feedback = new FeedbackRequest(
                    userId,
                    songId,
                    action,
                    LocalDateTime.now()
            );
            
            restTemplate.postForEntity(
                    recommendationServiceUrl + "/api/feedback",
                    feedback,
                    Void.class
            );
        } catch (Exception e) {
            log.error("Ошибка при отправке фидбека: {}", e.getMessage());
        }
    }

    @Data
    @AllArgsConstructor
    private static class FeedbackRequest {
        private String userId;
        private String songId;
        private String action;
        private LocalDateTime timestamp;
    }

    @Data
    private static class RecommendationResponse {
        private List<String> songIds;
        private List<Float> scores;
    }
}