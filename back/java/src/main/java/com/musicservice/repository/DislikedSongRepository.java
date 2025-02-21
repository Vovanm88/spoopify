package com.musicservice.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.musicservice.model.Song;
import com.musicservice.model.User;

@Repository
public interface DislikedSongRepository extends JpaRepository<Song, UUID> {
    
    // Получить все дизлайкнутые песни пользователя
    List<Song> findAllByDislikedByUsers(User user);
    
    // Проверить, дизлайкнул ли пользователь песню
    boolean existsByDislikedByUsersAndId(User user, UUID songId);
    
    // Получить количество дизлайков у песни
    @Query("SELECT COUNT(u) FROM Song s JOIN s.dislikedByUsers u WHERE s.id = :songId")
    long countDislikesBySongId(@Param("songId") UUID songId);
    
    // Получить последние N дизлайкнутых песен пользователя
    @Query("SELECT s FROM Song s JOIN s.dislikedByUsers u WHERE u = :user ORDER BY s.id DESC LIMIT :limit")
    List<Song> findLastDislikedSongs(@Param("user") User user, @Param("limit") int limit);
    
    // Удалить дизлайк пользователя с песни
    void deleteByDislikedByUsersAndId(User user, UUID songId);
}