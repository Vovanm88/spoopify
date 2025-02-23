package com.musicservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.util.pattern.PathPatternParser;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("file:src/main/resources/static/")
                .addResourceLocations("classpath:/static/")
                .addResourceLocations("classpath:/static/");
    }

    @SuppressWarnings("unused")
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.setPatternParser(new PathPatternParser());
        configurer.addPathPrefix("/v1", c -> true);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")  // Изменено с /* на /**
                .allowedOrigins("http://localhost:8080")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH")
                .allowedHeaders("Origin", "Content-Type", "Accept", "Authorization", "X-Requested-With", "*")
                .exposedHeaders("Authorization")  // Добавлено для JWT
                .maxAge(3600L) 
                .allowCredentials(true);
    }
}