package com.musicservice.dto;

import lombok.Data;

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