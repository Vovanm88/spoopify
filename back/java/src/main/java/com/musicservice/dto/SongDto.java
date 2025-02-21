package com.musicservice.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SongDto {
    private String id;
    private String title;
    private String artist;
    private String album;
    private String description;
    private String s3Bucket;
    private String s3FilePath;
}