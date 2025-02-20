package com.musicservice.dto;

import lombok.Data;

@Data
public class UserDto {
    private String id;
    private String username;
    private String login;
    private String role;
    //private String passwordHash;
    private String sessionToken;
}