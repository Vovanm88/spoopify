package com.musicservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.musicservice.model.Song;

@Repository
public interface SongRepository extends JpaRepository<Song, Long> {
    
    Optional<Song> findByTitle(String title);
    
    List<Song> findByArtist(String artist);
    
    List<Song> findByAlbum(String album);
    
    @Query("SELECT s FROM Song s WHERE " +
           "LOWER(s.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(s.artist) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(s.album) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Song> searchSongs(@Param("query") String query);
    
    @Query("SELECT s FROM Song s ORDER BY RANDOM() LIMIT 1")
    Optional<Song> findRandomSong();
    
    boolean existsByS3FilePath(String s3FilePath);
}