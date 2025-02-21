package com.musicservice.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.musicservice.dto.AuthRequest;
import com.musicservice.dto.AuthResponse;
import com.musicservice.exception.UserNotFoundException;
import com.musicservice.model.User;
import com.musicservice.repository.UserRepository;
import com.musicservice.security.JwtTokenProvider;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import com.musicservice.exception.AuthenticationFailedException;
import com.musicservice.exception.UserAlreadyExistsException;
import com.musicservice.model.UserRole;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse login(AuthRequest authRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.getLogin(),
                            authRequest.getPasswordHash()
                    )
            );
            
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String accessToken = jwtTokenProvider.createToken(userDetails.getUsername());
            String refreshToken = jwtTokenProvider.createRefreshToken(userDetails.getUsername());

            // Обновляем время последнего входа
            User user = userRepository.findByLogin(authRequest.getLogin())
                    .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
            user.setLastLoginAt(LocalDateTime.now());
            user.setSessionToken(accessToken);
            userRepository.save(user);
            
            return AuthResponse.builder()
                    .token(accessToken)
                    .refreshToken(refreshToken)
                    .build();
        } catch (AuthenticationException e) {
            throw new AuthenticationFailedException("Неверные учетные данные");
        }
    }

    public void register(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new UserAlreadyExistsException("Пользователь с именем " + user.getUsername() + " уже существует");
        }
        
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        user.setRole(UserRole.USER);
        
        userRepository.save(user);
    }
}