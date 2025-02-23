package com.musicservice.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.musicservice.dto.FeedbackRequest;
import com.musicservice.dto.SongDto;
import com.musicservice.exception.BadRequestException;
import com.musicservice.exception.InternalServerErrorException;
import com.musicservice.exception.ServiceUnavailableException;
import com.musicservice.model.Song;
import com.musicservice.repository.SongRepository;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RecommendationService {
    private final RestTemplate restTemplate;
    private final String recommendationServiceUrl;
    private final SongRepository songRepository; // добавляем репозиторий

    public RecommendationService(
            RestTemplate restTemplate,
            @Value("${recommendation-service.url:http://localhost:8085}") String recommendationServiceUrl,
            SongRepository songRepository // добавляем в конструктор
    ) {
        this.restTemplate = restTemplate;
        this.recommendationServiceUrl = recommendationServiceUrl;
        this.songRepository = songRepository;
    }
    @Data
    @AllArgsConstructor
    private class RecommendationResponse {
        private List<String> songIds;
        private List<Float> scores;
    }

    public List<SongDto> getPersonalRecommendations(String userId) {
        try {
            ResponseEntity<RecommendationResponse> response = restTemplate.getForEntity(
                    recommendationServiceUrl + "/api/recommendations/{userId}?type=personal",
                    RecommendationResponse.class,
                    userId
            );
            
            if (response == null || response.getBody() == null || response.getBody().getSongIds() == null) {
                log.warn("Received an empty response from the recommendation service for user {}", userId);
                return Collections.emptyList();
            }

            List<String> songIds = response.getBody().getSongIds();
            return songIds.stream()
                    .map(id -> songRepository.findById(UUID.fromString(id)))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error getting personal recommendations for user {}: {}", userId, e.getMessage());
            return Collections.emptyList();
        }
    }
    public List<SongDto> getRecommendations(String userId) {
        try {
            ResponseEntity<RecommendationResponse> response = restTemplate.getForEntity(
                    recommendationServiceUrl + "/api/recommendations/{userId}",
                    RecommendationResponse.class,
                    userId
            );
            List<String> songIds = response.getBody().getSongIds();
            return songIds.stream()
                    .<Optional<Song>>map(id -> songRepository.findById(UUID.fromString(id)))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error getting recommendations for user {}: {}", userId, e.getMessage());
            return Collections.emptyList();
        }
    }
    public SongDto getRandomTracks() {
        try {
            ResponseEntity<RandomTrackResponse> response = restTemplate.getForEntity(
                    recommendationServiceUrl + "/api/random",
                    RandomTrackResponse.class
            );
            String songId = response.getBody().getSongId();
            return songRepository.findById(UUID.fromString(songId))
                    .map(this::convertToDto)
                    .orElseThrow(() -> new RuntimeException("Песня не найдена"));
        } catch (Exception e) {
            log.error("Error getting random track: {}", e.getMessage());
            throw new RuntimeException("Failed to get random track");
        }
    }

    @Data
    private static class RandomTrackResponse {
        private String songId;
    }

    private SongDto convertToDto(Song song) {
        return SongDto.builder()
                .id(song.getId().toString())
                .title(song.getTitle())
                .artist(song.getArtist())
                .album(song.getAlbum())
                .description(song.getDescription())
                .s3Bucket(song.getS3Bucket())
                .s3FilePath(song.getS3FilePath())
                .build();
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
            log.error("Error sending feedback: {}", e.getMessage());
        }
    }
    @SuppressWarnings("UseSpecificCatch")
    public String healthCheck() throws ServiceUnavailableException, BadRequestException, InternalServerErrorException, RuntimeException {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(
                    recommendationServiceUrl + "/api/health", 
                    String.class
            );
            
            if (response.getStatusCode().equals(org.springframework.http.HttpStatus.OK)) {
                return response.getBody();
            } else {
                return "Recommendation service returned status: " + response.getStatusCode();
            }
        } catch (org.springframework.web.client.ResourceAccessException e) {
            if (e.getMessage().contains("Read timed out")) {
                throw new ServiceUnavailableException("Recommendation service not responding (timeout)");
            }
        } catch (HttpClientErrorException e) {
            throw new BadRequestException("Некорректный запрос к сервису рекомендаций: " + e.getMessage());
        } catch (HttpServerErrorException e) {
            throw new InternalServerErrorException("Внутренняя ошибка сервиса рекомендаций: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Неизвестная ошибка сервиса рекомендаций: " + e.getMessage());
        }
        return "Unknown error occurred";    
    }

}