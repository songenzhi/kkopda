package com.example.kkopja.entity.chat;

import com.example.kkopja.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "chat_message")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY) // 지연 로딩: 성능 최적화를 위해 필수!
    @JoinColumn(name = "room_id")
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY) // 지연 로딩: 성능 최적화를 위해 필수!
    @JoinColumn(name = "user_id")
    private User user;

    private String content;

    private LocalDateTime createdAt;

    @PrePersist // db에 insert되기 직전에 현재 시간을 자동으로 기록
    public void prePersist(){
        this.createdAt = LocalDateTime.now();
    }
}
