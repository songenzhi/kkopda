package com.example.kkopja.entity.chat;


import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "chat_room")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String roomId;

    private String name;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default // 빌더 패턴 사용 시 리스트 초기화를 위해 필요
    private List<ChatMessage> messages = new ArrayList<>();

    public void updateName(String name) {
        this.name = name;
    }

    public void addMessage(ChatMessage message) {
        this.messages.add(message);

        if (message.getChatRoom() != this) {
            message.setChatRoom(this);
        }
    }
}
