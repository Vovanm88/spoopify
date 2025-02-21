package com.musicservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class AuthResponse {
    private String token;
    private String refreshToken;
}