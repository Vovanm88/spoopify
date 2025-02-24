package com.musicservice.controller;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import com.musicservice.service.S3Service;
import com.musicservice.service.SongService;
import com.musicservice.service.StreamingTokenService;

@RestController
@RequestMapping("/stream")
public class StreamingController {
    
    @Autowired
    private S3Service s3Service;
    
    @Autowired
    private SongService songService;
    
    @Autowired
    private StreamingTokenService streamingTokenService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/{rayId}", produces = "audio/mpeg")
    public ResponseEntity<StreamingResponseBody> streamSong(@PathVariable String rayId) {
        Long songId = streamingTokenService.getSongIdByToken(rayId);
        if (songId == null) {
            return ResponseEntity.notFound().build();
        }

        return songService.getSongById(songId)
            .<ResponseEntity<StreamingResponseBody>>map(song -> {
                try {
                    InputStream inputStream = s3Service.getFileStream(song.getS3FilePath());
                    long contentLength = inputStream.available();
                    
                    StreamingResponseBody responseBody = outputStream -> {
                        try (InputStream is = inputStream) {
                            byte[] buffer = new byte[4096];
                            int bytesRead;
                            while ((bytesRead = is.read(buffer)) != -1) {
                                outputStream.write(buffer, 0, bytesRead);
                                outputStream.flush();
                            }
                        } catch (IOException e) {
                            throw new RuntimeException("Error streaming song: " + e.getMessage());
                        }
                    };
                    
                    return ResponseEntity.ok()
                            .contentType(MediaType.parseMediaType("audio/mpeg"))
                            .contentLength(contentLength)
                            .header("Content-Disposition", "inline; filename=\"" + song.getTitle() + ".mp3\"")
                            .body(responseBody);
                } catch (IOException e) {
                    throw new RuntimeException("Error preparing stream: " + e.getMessage());
                }
            })
            .orElse(ResponseEntity.notFound().build());
    }
}