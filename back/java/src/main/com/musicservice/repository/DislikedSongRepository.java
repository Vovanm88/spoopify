package com.musicservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.musicservice.model.Song;
import com.musicservice.model.User;

@Repository
public interface DislikedSongRepository extends JpaRepository<Song, Long> {
    
    List<Song> findByUsersDisliked(User user);
    
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM Song s JOIN s.usersDisliked u WHERE s.id = :songId AND u.id = :userId")
    boolean isDislikedByUser(@Param("songId") Long songId, @Param("userId") Long userId);
    
    Optional<Song> findBySongIdAndUserDisliked(Long songId, User user);
    
    void deleteBySongIdAndUserDisliked(Long songId, User user);
    
    @Query("SELECT COUNT(s) FROM Song s JOIN s.usersDisliked u WHERE u.id = :userId")
    long countDislikedSongsByUser(@Param("userId") Long userId);
}