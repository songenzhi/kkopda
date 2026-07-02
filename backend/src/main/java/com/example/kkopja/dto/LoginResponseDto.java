package com.example.kkopja.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponseDto {

    private String accessToken;
    private Integer id;
    private String email;
    private String nickname;
}