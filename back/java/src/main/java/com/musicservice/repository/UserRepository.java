package com.musicservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.musicservice.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Найти пользователя по имени пользователя
    Optional<User> findByUsername(String username);

    // Найти пользователя по логину
    Optional<User> findByLogin(String login);

    // Проверить, существует ли пользователь с данным именем пользователя
    boolean existsByUsername(String username);

    // Проверить, существует ли пользователь с данным логином
    boolean existsByLogin(String login);
}