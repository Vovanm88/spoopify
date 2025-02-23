package com.musicservice.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.musicservice.dto.SongDto;
import com.musicservice.exception.BadRequestException;
import com.musicservice.exception.InternalServerErrorException;
import com.musicservice.exception.ServiceUnavailableException;
import com.musicservice.service.RecommendationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {
    
    private final RecommendationService recommendationService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<SongDto>> getRecommendations(@AuthenticationPrincipal UserDetails userDetails) {
        String userId = userDetails.getUsername(); // используем userId вместо username
        return ResponseEntity.ok(recommendationService.getRecommendations(userId));
    }

    @GetMapping("/random")  
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SongDto> getRandomTracks() {
        return ResponseEntity.ok(recommendationService.getRandomTracks());
    }

    @GetMapping("/personal")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<SongDto>> getPersonalRecommendations(@AuthenticationPrincipal UserDetails userDetails) {
        String userId = userDetails.getUsername(); // используем userId вместо username
        return ResponseEntity.ok(recommendationService.getPersonalRecommendations(userId));
    }

    @GetMapping("/health")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> checkHealth() {
        try {
            String healthStatus = recommendationService.healthCheck();
            return ResponseEntity.ok(healthStatus);
        } catch (ServiceUnavailableException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Service is unavailable: " + e.getMessage());
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request: " + e.getMessage());
        } catch (InternalServerErrorException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unknown error occurred: " + e.getMessage());
        }
    }
}