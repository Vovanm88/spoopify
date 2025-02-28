package com.musicservice.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.musicservice.dto.SongDto;
import com.musicservice.model.Song;
import com.musicservice.repository.UserRepository;
import com.musicservice.service.S3Service;
import com.musicservice.service.SongService;
import com.musicservice.service.StreamingTokenService;

@RestController
@RequestMapping("/api/songs")
public class SongController {

    @Autowired
    private S3Service s3Service;
    
    @Autowired
    private SongService songService;
    
    @Autowired
    private UserRepository userRepository;
    
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/all")
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

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<SongDto> getSongById(@PathVariable Long id) {
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

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}/download")
    public ResponseEntity<?> downloadSong(@PathVariable Long id) {
        System.out.println("Download request received for song ID: " + id);
        return songService.getSongById(id)
                .map(song -> {
                    //song.getS3Bucket()+"/"
                    try (InputStream inputStream = s3Service.getFileStream(song.getS3FilePath())) {
                        byte[] content = inputStream.readAllBytes();
                        return ResponseEntity.ok()
                                .header("Content-Disposition", "attachment; filename=\"" + song.getTitle() + ".mp3\"")
                                .body(content);
                    } catch (IOException e) {
                        return ResponseEntity.status(500).build();
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }


        @Autowired
    private StreamingTokenService streamingTokenService;

    @SuppressWarnings("unused")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}/stream")
    public ResponseEntity<String> getStreamToken(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        return songService.getSongById(id)
            .map(song -> {
                String username = userDetails.getUsername();
                Long userId = userRepository.findByUsername(username)
                    .map(user -> user.getId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
                String rayId = streamingTokenService.generateToken(userId, id);
                return ResponseEntity.ok(rayId);
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{id}/like")
    public ResponseEntity<?> likeSong(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long id) {
        String username = userDetails.getUsername();
        return userRepository.findByUsername(username)
            .map(user -> {
                songService.likeSong(user, id);
                return ResponseEntity.ok().build();
            })
            .orElse(ResponseEntity.badRequest().build());
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{id}/dislike")
    public ResponseEntity<?> dislikeSong(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long id) {
        String username = userDetails.getUsername();
        return userRepository.findByUsername(username)
                .map(user -> {
                    songService.dislikeSong(user, id);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.badRequest().build());
    }
    @GetMapping("/health")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Song service is up and running");
    }
}