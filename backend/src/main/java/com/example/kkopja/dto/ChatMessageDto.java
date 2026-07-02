package com.example.kkopja.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageDto {
    private Integer id;
    private String roomId;
    private String name;
    private String content;

    @JsonProperty("isSystem") // 프론트엔드에 전달될 JSON 키값을 명시적으로 지정
    private boolean isSystem;
}
