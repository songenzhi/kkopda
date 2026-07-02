package com.example.kkopja.service;

import com.example.kkopja.dto.ChatMessageResponseDto;
import com.example.kkopja.entity.chat.ChatMessage;
import com.example.kkopja.entity.chat.ChatRoom;
import com.example.kkopja.entity.User;
import com.example.kkopja.repository.ChatMessageRepository;
import com.example.kkopja.repository.ChatRoomRepository;
import com.example.kkopja.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;

    // 모든 방 목록 조회
    public List<ChatRoom> findAllRooms() {
        return new ArrayList<>(chatRoomRepository.findAll());
    }

    // 특정 방 조회 (입장 시 방 존재 여부 검증용)
    public ChatRoom findRoomById(String roomId) {
        return chatRoomRepository.findByRoomId(roomId).orElse(null);
    }

    // 방 만들기
    @Transactional
    public ChatRoom createRoom(String name) {
        String roomId = UUID.randomUUID().toString();
        ChatRoom room = ChatRoom.builder()
                .roomId(roomId)
                .name(name)
                .build();

        chatRoomRepository.save(room);
        return room;
    }

    @Transactional
    public void saveMessage(String roomId, Integer userId, String content) {

        ChatRoom room = chatRoomRepository.findByRoomId(roomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 방입니다."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        ChatMessage chatMessage = ChatMessage.builder()
                .chatRoom(room)
                .user(user)
                .content(content)
                .build();

        chatMessageRepository.save(chatMessage);
    }

    // 1. 방 목록(Lounge)에서 보여줄 '마지막 메시지'용
    public ChatMessage findLastMessage(String roomId) {
        return chatMessageRepository.findTopByChatRoomRoomIdOrderByCreatedAtDesc(roomId)
                .orElse(null);
    }

    // 2. 채팅방 입장 시 보여줄 '전체 대화 내역'용
    public List<ChatMessageResponseDto> getMessagesByRoomId(String roomId) {
        List<ChatMessage> messages = chatMessageRepository.findByChatRoomRoomIdOrderByCreatedAtAsc(roomId);

        return messages.stream()
                .map(msg -> ChatMessageResponseDto.builder()
                        .name(msg.getUser().getNickname()) // 엔티티에서 필요한 데이터만 추출
                        .content(msg.getContent())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteChatRoom(String roomId){
        chatRoomRepository.deleteByRoomId(roomId);
    }
}
