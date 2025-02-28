package com.musicservice.service;

import java.time.LocalDateTime;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.musicservice.dto.AuthResponse;
import com.musicservice.dto.LoginRequest;
import com.musicservice.dto.LoginResponse;
import com.musicservice.exception.AuthenticationFailedException;
import com.musicservice.exception.UserAlreadyExistsException;
import com.musicservice.exception.UserNotFoundException;
import com.musicservice.model.User;
import com.musicservice.model.UserRole;
import com.musicservice.repository.UserRepository;
import com.musicservice.security.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    public User register(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new UserAlreadyExistsException("User with username " + user.getUsername() + " already exists");
        }
        if (userRepository.existsByLogin(user.getLogin())) {
            throw new UserAlreadyExistsException("User with login " + user.getLogin() + " already exists");
        }
        
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        user.setRole(UserRole.USER);
        user.setCreatedAt(LocalDateTime.now());
        
        return userRepository.save(user);
    }

    public LoginResponse login(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );
            
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String accessToken = jwtTokenProvider.createToken(userDetails.getUsername());
            
            User user = userRepository.findByLogin(loginRequest.getEmail())
                    .orElseThrow(() -> new UserNotFoundException("User not found"));
            user.setLastLoginAt(LocalDateTime.now());
            user.setSessionToken(accessToken);
            userRepository.save(user);
            
            return LoginResponse.builder()
                    .token(accessToken)
                    //.user(user)
                    .build();
        } catch (AuthenticationException e) {
            throw new AuthenticationFailedException("Invalid credentials");
        }
    }

    public AuthResponse refreshToken(String refreshToken) {
        if (!jwtTokenProvider.validateRefreshToken(refreshToken)) {
            throw new AuthenticationFailedException("Invalid refresh token");
        }

        String username = jwtTokenProvider.getUsername(refreshToken);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        String newAccessToken = jwtTokenProvider.createToken(username);
        String newRefreshToken = jwtTokenProvider.createRefreshToken(username);

        user.setSessionToken(newAccessToken);
        userRepository.save(user);

        return AuthResponse.builder()
                .token(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    
    public void resetPassword(String username, String oldPassword, String newPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));

        // Проверяем старый пароль
        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            throw new AuthenticationFailedException("Неверный текущий пароль");
        }

        // Устанавливаем новый пароль
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}