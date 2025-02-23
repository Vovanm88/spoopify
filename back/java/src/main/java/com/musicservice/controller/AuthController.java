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
import com.musicservice.exception.AuthenticationFailedException;
import com.musicservice.exception.UserAlreadyExistsException;
import com.musicservice.exception.UserNotFoundException;
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
        user.setPasswordHash(registrationDto.getPassword());
        System.out.println("Registering new user:");
        System.out.println("Username: " + user.getUsername());
        System.out.println("Login: " + user.getLogin());
        try {
            User registeredUser = authService.register(user);
            return ResponseEntity.ok(registeredUser);
        }catch(UserAlreadyExistsException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error registering user: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        try{
            LoginResponse response = authService.login(loginRequest);
            System.out.println("User login attempt:");
            System.out.println("Email: " + loginRequest.getEmail());
            System.out.println("Login successful");
            return ResponseEntity.ok(response);
        }catch(UserNotFoundException e){
            System.out.println("User not found: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody String refreshToken) {
        try {
            AuthResponse authResponse = authService.refreshToken(refreshToken);
            return ResponseEntity.ok(authResponse);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (AuthenticationFailedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}