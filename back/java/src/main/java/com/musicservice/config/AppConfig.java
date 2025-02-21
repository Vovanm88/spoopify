package com.musicservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {
    @Value("${recommendation-service.url:http://localhost:8080}")
    private String recommendationServiceUrl;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}