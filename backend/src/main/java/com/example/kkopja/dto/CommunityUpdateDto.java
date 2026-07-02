package com.example.kkopja.dto;

import org.springframework.web.multipart.MultipartFile;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommunityUpdateDto {
    private String title;
    private String description;
    private MultipartFile imageFile;   // 새롭게 바꿀 이미지 파일 (선택)
    private boolean imageDeleted;     // 기존 이미지를 아예 삭제했는지 여부
}