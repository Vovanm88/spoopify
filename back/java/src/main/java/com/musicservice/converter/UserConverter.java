package com.musicservice.converter;

import com.musicservice.dto.UserDto;
import com.musicservice.model.User;

public class UserConverter {

    public static UserDto toDto(User user) {
        if (user == null) {
            return null;
        }
        UserDto userDto = new UserDto();
        userDto.setId(user.getId().toString());
        userDto.setUsername(user.getUsername());
        userDto.setLogin(user.getLogin());
        userDto.setRole(user.getRole());
        userDto.setSessionToken(user.getSessionToken());
        userDto.setPasswordHash(user.getPasswordHash());
        return userDto;
    }

    public static User toEntity(UserDto userDto) {
        if (userDto == null) {
            return null;
        }
        User user = new User();
        user.setId(UUID.fromString(userDto.getId()));
        user.setUsername(userDto.getUsername());
        user.setLogin(userDto.getLogin());
        user.setRole(userDto.getRole());
        user.setSessionToken(userDto.getSessionToken());
        user.setPasswordHash(userDto.getPasswordHash());
        return user;
    }
}