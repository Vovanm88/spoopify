package com.musicservice.security;

import java.util.Base64;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import com.musicservice.config.JwtConfig;
import com.musicservice.exception.InvalidJwtAuthenticationException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class JwtTokenProvider {

    @Autowired(required = false)
    private JwtConfig jwtConfig;
    @Value("${security.jwt.token.secret-key:verySecretKey1234567890_12345678910_123456789}")
    private String secretKey;

    @Value("${security.jwt.token.expire-length:3600000}")
    private long validityInMilliseconds;

    @Autowired
    private UserDetailsService userDetailsService;

    @PostConstruct
    protected void init() {
        // Проверяем, что конфигурация не null и содержит валидные значения
        if (jwtConfig != null && jwtConfig.getSecretKey() != null && !jwtConfig.getSecretKey().isEmpty()) {
            this.secretKey = jwtConfig.getSecretKey();
            // Проверяем, что значение больше 0
            if (jwtConfig.getValidityInMilliseconds() > 0) {
                this.validityInMilliseconds = jwtConfig.getValidityInMilliseconds();
            }
        }
        
        // Проверяем, что secretKey не null перед кодированием
        if (secretKey == null || secretKey.isEmpty()) {
            throw new IllegalStateException("Secret key не может быть пустым");
        }
        
        // Кодируем ключ в Base64
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }
    
    public String createToken(String username) {
        Claims claims = Jwts.claims().setSubject(username);
    
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);
    
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes())) // Обновленный метод подписи
                .compact();
    }
    
    public String getUsername(String token) {
        return Jwts.parserBuilder() // Используем новый парсер
                .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
    
    

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(getUsername(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder() // Используем новый парсер
                    .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))
                    .build()
                    .parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidJwtAuthenticationException("Expired or invalid JWT token");
        }
    }
    
    public String createRefreshToken(String username) {
        Claims claims = Jwts.claims().setSubject(username);
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds * 2);
    
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes())) // Обновленный метод подписи
                .compact();
    }
    public boolean validateRefreshToken(String token) {
        return validateToken(token);
    }
    
    public String generateSessionToken() {
        return Base64.getEncoder().encodeToString((new Date().toString() + Math.random()).getBytes());
    }

}