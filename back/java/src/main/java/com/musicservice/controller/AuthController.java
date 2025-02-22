package com.musicservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.musicservice.dto.AuthRequest;
import com.musicservice.dto.AuthResponse;
import com.musicservice.dto.UserDto;
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

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest authRequest) {
        AuthResponse authResponse = authService.login(authRequest);
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(UserDto us) {
        User user = new User();
        //System.out.println("Получен запрос на регистрацию пользователя: " + us.getUsername());
        System.out.println("Данные пользователя: " + us.toString());
        try {
            authService.register(user);
            System.out.println("Пользователь " + user.getUsername() + " успешно зарегистрирован");
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.out.println("Ошибка при регистрации пользователя " + user.getUsername() + ": " + e.getMessage());
            throw e;
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody String refreshToken) {
        AuthResponse authResponse = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(authResponse);
    }
}