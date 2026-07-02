package com.example.kkopja.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder // 빌더 패턴을 사용하면 객체 생성이 매우 편리합니다.
// 💡 엔티티 대신 사용할 DTO (ChatMessageResponseDto)
public class ChatMessageResponseDto {
    private String name;
    private String content;
    // ... 필요한 정보만 정의
}