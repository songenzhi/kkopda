package com.example.kkopja.repository;

import com.example.kkopja.entity.chat.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Integer> {
    Optional<ChatRoom> findByRoomId(String roomId);

    void deleteByRoomId(String roomId);
}
