package com.example.kkopja.dto;

import com.example.kkopja.entity.User;
import lombok.Getter;

@Getter
public class UserResponseDto {
    private Long id;
    private String email;
    private String nickname;

    public UserResponseDto(User user) {
        this.id = Long.valueOf(user.getId());
        this.email = user.getEmail();
        this.nickname = user.getNickname();
    }
}