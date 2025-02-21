package com.musicservice.controller;

import com.musicservice.dto.SongDto;
import com.musicservice.model.Song;
import com.musicservice.model.User;
import com.musicservice.service.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/songs")
public class SongController {

    @Autowired
    private SongService songService;

    @GetMapping
    public ResponseEntity<List<SongDto>> getAllSongs() {
        List<Song> songs = songService.getAllSongs();
        List<SongDto> songDtos = songs.stream()
                .map(song -> new SongDto(
                        song.getId().toString(),
                        song.getTitle(),
                        song.getArtist(),
                        song.getAlbum(),
                        song.getDescription(),
                        song.getS3Bucket(),
                        song.getS3FilePath()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(songDtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SongDto> getSongById(@PathVariable UUID id) {
        return songService.getSongById(id)
                .map(song -> new SongDto(
                        song.getId().toString(),
                        song.getTitle(),
                        song.getArtist(),
                        song.getAlbum(),
                        song.getDescription(),
                        song.getS3Bucket(),
                        song.getS3FilePath()))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<Void> likeSong(@AuthenticationPrincipal User user, @PathVariable UUID id) {
        songService.likeSong(user, id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/dislike")
    public ResponseEntity<Void> dislikeSong(@AuthenticationPrincipal User user, @PathVariable UUID id) {
        songService.dislikeSong(user, id);
        return ResponseEntity.ok().build();
    }
}