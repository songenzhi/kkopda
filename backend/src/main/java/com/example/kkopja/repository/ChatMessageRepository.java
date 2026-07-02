package com.example.kkopja.repository;

import com.example.kkopja.entity.chat.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    Optional<ChatMessage> findTopByChatRoomRoomIdOrderByCreatedAtDesc(String roomId);

    List<ChatMessage> findByChatRoomRoomIdOrderByCreatedAtAsc(String roomId);
}
