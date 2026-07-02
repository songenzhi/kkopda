package com.example.kkopja.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatRoomDto {
    private String roomId;
    private String name;
    private String content;
    // 필요하다면 추가할 수 있는 필드들
    // private int userCount; // 현재 방에 있는 인원수
    // private String createdBy; // 방 개설자
}