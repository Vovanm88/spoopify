package com.musicservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "security.jwt.token")
public class JwtConfig {
    private String secretKey="your_very_long_and_very_secure_secret_key_that_is_at_least_256_bits_long_please_change_this_in_production_environment_and_store_securely_12345678901234567890";
    private long validityInMilliseconds;

    // Геттеры и сеттеры
    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public long getValidityInMilliseconds() {
        return validityInMilliseconds;
    }

    public void setValidityInMilliseconds(long validityInMilliseconds) {
        this.validityInMilliseconds = validityInMilliseconds;
    }
}