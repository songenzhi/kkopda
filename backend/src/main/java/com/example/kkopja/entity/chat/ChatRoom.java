package com.example.kkopja.entity.chat;


import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "chat_room")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String roomId;

    private String name;

    @Builder.Default
    private int participants = 0; // 프론트엔드 참여자 수 UI를 위한 기본값 설정

    // 💡 수정된 부분: 양방향 매핑 및 부모가 자식을 관리하도록 설정
    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default // 빌더 패턴 사용 시 리스트 초기화를 위해 필요
    private List<ChatMessage> messages = new ArrayList<>();
}
