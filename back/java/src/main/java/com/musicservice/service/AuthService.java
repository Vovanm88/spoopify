package com.musicservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.musicservice.dto.AuthRequest;
import com.musicservice.dto.AuthResponse;
import com.musicservice.exception.UserNotFoundException;
import com.musicservice.model.User;
import com.musicservice.repository.UserRepository;
import com.musicservice.security.JwtTokenProvider;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    public AuthResponse login(AuthRequest authRequest) {
        // Аутентификация пользователя
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getLogin(), authRequest.getPasswordHash()));
        
        // Получение пользователя
        UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getLogin());
        String token = jwtTokenProvider.createToken(userDetails.getUsername(), userDetails.getAuthorities().toString());
        String refreshToken = jwtTokenProvider.createRefreshToken(userDetails.getUsername());

        return new AuthResponse(token, refreshToken);
    }

    public void register(User user) {
        // Проверка существования пользователя
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new UserNotFoundException("Пользователь с таким именем уже существует");
        }
        userRepository.save(user);
    }
}