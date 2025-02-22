package com.musicservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;


import com.musicservice.dto.AuthResponse;
import com.musicservice.dto.LoginRequest;
import com.musicservice.dto.LoginResponse;

import com.musicservice.dto.UserRegistrationDto;
import com.musicservice.model.User;
import com.musicservice.service.AuthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    @PostMapping("/health")
    public ResponseEntity<String> healthCheck() {
        System.out.println("working");
        return ResponseEntity.ok("Service is healthy");
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody UserRegistrationDto registrationDto) {
        User user = new User();
        user.setUsername(registrationDto.getUsername());
        user.setLogin(registrationDto.getLogin());
        // Здесь должно быть хеширование пароля
        user.setPasswordHash(registrationDto.getPassword());
        
        try {
            User registeredUser = authService.register(user);
            return ResponseEntity.ok(registeredUser);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error registering user: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        LoginResponse response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody String refreshToken) {
        AuthResponse authResponse = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(authResponse);
    }
}