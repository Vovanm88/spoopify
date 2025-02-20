package com.musicservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.musicservice.model.Song;
import com.musicservice.model.User;

@Repository
public interface LikedSongRepository extends JpaRepository<Song, Long> {
    
    // Получить все лайкнутые песни пользователя
    List<Song> findAllByLikedByUsers(User user);
    
    // Проверить, лайкнул ли пользователь песню
    boolean existsByLikedByUsersAndId(User user, Long songId);
    
    // Получить количество лайков у песни
    @Query("SELECT COUNT(u) FROM Song s JOIN s.likedByUsers u WHERE s.id = :songId")
    long countLikesBySongId(@Param("songId") Long songId);
    
    // Получить последние N лайкнутых песен пользователя
    @Query("SELECT s FROM Song s JOIN s.likedByUsers u WHERE u = :user ORDER BY s.id DESC")
    List<Song> findLastLikedSongs(@Param("user") User user, @Param("limit") int limit);
    
    // Удалить лайк пользователя с песни
    void deleteByLikedByUsersAndId(User user, Long songId);
}