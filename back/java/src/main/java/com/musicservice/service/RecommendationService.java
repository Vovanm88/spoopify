package com.musicservice.service;

import com.musicservice.repository.SongRepository;
import com.musicservice.dto.SongDto;
import com.musicservice.model.Song;

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
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RecommendationService {
    private final RestTemplate restTemplate;
    private final String recommendationServiceUrl;
    private final SongRepository songRepository; // добавляем репозиторий

    public RecommendationService(
            RestTemplate restTemplate,
            @Value("${recommendation-service.url}") String recommendationServiceUrl,
            SongRepository songRepository // добавляем в конструктор
    ) {
        this.restTemplate = restTemplate;
        this.recommendationServiceUrl = recommendationServiceUrl;
        this.songRepository = songRepository;
    }
    @Data
    @AllArgsConstructor
    private static class RecommendationResponse {
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
            
            if (response.getBody() == null || response.getBody().getSongIds() == null) {
                log.warn("Получен пустой ответ от сервиса рекомендаций для пользователя {}", userId);
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
            log.error("Ошибка при получении персональных рекомендаций для пользователя {}: {}", userId, e.getMessage());
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
            log.error("Ошибка при получении рекомендаций для пользователя {}: {}", userId, e.getMessage());
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
            log.error("Ошибка при получении случайного трека: {}", e.getMessage());
            throw new RuntimeException("Не удалось получить случайный трек");
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

}