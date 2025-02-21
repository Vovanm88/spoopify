package com.musicservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequest {
    private String login;
    private String passwordHash;
}