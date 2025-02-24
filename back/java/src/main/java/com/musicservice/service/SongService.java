package com.musicservice.service;

import java.util.List;
import java.util.Optional;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.musicservice.model.Song;
import com.musicservice.model.User;
import com.musicservice.repository.DislikedSongRepository;
import com.musicservice.repository.LikedSongRepository;
import com.musicservice.repository.SongRepository;

@Service
public class SongService {

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private LikedSongRepository likedSongRepository;

    @Autowired
    private DislikedSongRepository dislikedSongRepository;

    public Optional<Song> getSongById(Long id) {
        return songRepository.findById(id);
    }

    public List<Song> getAllSongs() {
        return songRepository.findAll();
    }

    public Song addSong(Song song) {
        return songRepository.save(song);
    }

    public Song updateSong(Long id, Song songDetails) {
        return songRepository.findById(id).map(song -> {
            song.setTitle(songDetails.getTitle());
            song.setArtist(songDetails.getArtist());
            song.setAlbum(songDetails.getAlbum());
            song.setDescription(songDetails.getDescription());
            song.setS3Bucket(songDetails.getS3Bucket());
            song.setS3FilePath(songDetails.getS3FilePath());
            return songRepository.save(song);
        }).orElseThrow(() -> new RuntimeException("Song not found"));
    }

    public void deleteSong(Long id) {
        songRepository.deleteById(id);
    }
    public void likeSong(User user, Long songId) {
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new RuntimeException("Song not found"));
                
        // Проверяем, не стоит ли уже дислайк
        if (dislikedSongRepository.existsByDislikedByUsersAndId(user, songId)) {
            song.getDislikedByUsers().remove(user);
            user.getDislikedSongs().remove(song);
        }
        
        // Проверяем, не стоит ли уже лайк
        if (!likedSongRepository.existsByLikedByUsersAndId(user, songId)) {
            song.getLikedByUsers().add(user);
            user.getLikedSongs().add(song);
            songRepository.save(song);
        }
    }
    @Transactional
    public void dislikeSong(User user, Long songId) {
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new RuntimeException("Song not found"));
                
        // Проверяем, не стоит ли уже лайк
        if (likedSongRepository.existsByLikedByUsersAndId(user, songId)) {
            song.getLikedByUsers().remove(user);
            user.getLikedSongs().remove(song);
        }
        
        // Проверяем, не стоит ли уже дислайк
        if (!dislikedSongRepository.existsByDislikedByUsersAndId(user, songId)) {
            song.getDislikedByUsers().add(user);
            user.getDislikedSongs().add(song);
            songRepository.save(song);
        }
    }
    
    public List<Song> getLikedSongsByUser(User user) {
        return likedSongRepository.findAllByLikedByUsers(user);
    }

    public List<Song> getDislikedSongsByUser(User user) {
        return dislikedSongRepository.findAllByDislikedByUsers(user);
    }
}