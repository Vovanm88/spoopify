package com.musicservice.controller;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

    @GetMapping(value = "/{rayId}", produces = "audio/mpeg")
    public ResponseEntity<StreamingResponseBody> streamSong(@PathVariable String rayId) {
        Long songId = streamingTokenService.getSongIdByToken(rayId);
        if (songId == null) {
            return ResponseEntity.notFound().build();
        }

        return songService.getSongById(songId)
            .<ResponseEntity<StreamingResponseBody>>map(song -> {
                try (InputStream inputStream = s3Service.getFileStream(song.getS3FilePath())) {
                    // Считываем весь файл в массив байтов, чтобы получить его полный размер
                    byte[] songData = inputStream.readAllBytes();
                    long contentLength = songData.length;
                    System.out.println("Requested file from S3: " + song.getS3FilePath());
                    System.out.println("Content length: " + contentLength + " bytes");
                    
                    StreamingResponseBody responseBody = outputStream -> {
                        try {
                            outputStream.write(songData);
                            outputStream.flush();
                            //System.out.println("Transferred full song of size: " + contentLength + " bytes");
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