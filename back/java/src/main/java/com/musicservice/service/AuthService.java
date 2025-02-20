package com.musicservice.service;

import com.musicservice.dto.AuthRequest;
import com.musicservice.dto.AuthResponse;
import com.musicservice.dto.UserDto;
import com.musicservice.exception.UserNotFoundException;
import com.musicservice.model.User;
import com.musicservice.repository.UserRepository;
import com.musicservice.security.JwtTokenProvider;
import com.musicservice.exception.InvalidJwtAuthenticationException;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public AuthResponse login(AuthRequest authRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
            );

            User user = (User) authentication.getPrincipal();
            String token = jwtTokenProvider.createToken(user.getUsername(), user.getRoles());

            return new AuthResponse(token, user.toDto());
        } catch (AuthenticationException e) {
            throw new UserNotFoundException("Invalid email or password");
        }
    }

    public UserDto register(UserDto userDto) {
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }

        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setPasswordHash(passwordEncoder.encode(userDto.getPassword()));
        user.setRole("USER");

        userRepository.save(user);

        return user.toDto();
    }

    
    public AuthResponse refreshToken(String oldToken) {
        if (!jwtTokenProvider.validateToken(oldToken)) {
            throw new InvalidJwtAuthenticationException("Expired or invalid JWT token");
        }

        String username = jwtTokenProvider.getUsername(oldToken);
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        String newToken = jwtTokenProvider.createToken(user.getUsername(), user.getRoles());
        return new AuthResponse(newToken, user.toDto());
    }

}