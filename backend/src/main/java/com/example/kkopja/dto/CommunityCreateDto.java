package com.example.kkopja.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class CommunityCreateDto {

    private String title;
    private String description;
    private MultipartFile imageFile;
}
