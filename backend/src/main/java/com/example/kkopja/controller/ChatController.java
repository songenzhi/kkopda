package com.example.kkopja.controller;

import com.example.kkopja.dto.ChatMessageDto;
import com.example.kkopja.dto.ChatMessageResponseDto;
import com.example.kkopja.dto.ChatRoomDto;
import com.example.kkopja.entity.chat.ChatMessage;
import com.example.kkopja.entity.chat.ChatRoom;
import com.example.kkopja.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/lounge")
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate; // 동적 목적지 전송을 위한 템플릿

    // 1. 채팅방 입장 메시지 처리
//    @MessageMapping("/chat/{roomId}/enter")
//    public void enter(@DestinationVariable String roomId, ChatMessageDto message) {
//        chatService.saveMessage(roomId, message.getId(), message.getContent());
//
//        message.setSystem(false);
//
//        messagingTemplate.convertAndSend("/topic/chat/" + roomId, message);
//    }
    @MessageMapping("/chat/{roomId}/enter")
    public void enter(@DestinationVariable String roomId, ChatMessageDto message) {

        message.setSystem(true);
        message.setContent(message.getName() + "님이 입장하셨습니다.");
        messagingTemplate.convertAndSend("/topic/chat/" + roomId, message);
    }

    // 2. 일반 채팅 메시지 처리
    @MessageMapping("/chat/{roomId}/message")
    public void chat(@DestinationVariable String roomId, ChatMessageDto message) {
        System.out.println("Message in Room " + roomId + ": " + message.getContent());
        chatService.saveMessage(roomId, message.getId(), message.getContent());
        message.setSystem(false);
        messagingTemplate.convertAndSend("/topic/chat/" + roomId, message);
    }

    // 3. 전체 채팅방 목록 조회 (ChatRoomDto 리스트로 변환하여 반환)
    @GetMapping
    public ResponseEntity<List<ChatRoomDto>> rooms() {
        List<ChatRoom> chatRooms = chatService.findAllRooms();

        List<ChatRoomDto> dtos = chatRooms.stream()
                .map(room -> ChatRoomDto.builder()
                        .roomId(room.getRoomId())
                        .name(room.getName())
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    // 4. 특정 채팅방 상세 조회 (수정하신 코드 유지)
    @GetMapping("/rooms/{roomId}")
    public ResponseEntity<ChatRoomDto> getRoomById(@PathVariable String roomId) {

        ChatRoom chatRoom = chatService.findRoomById(roomId);
        ChatMessage chatMessage = chatService.findLastMessage(roomId);

        String previewContent = (chatMessage != null) ? chatMessage.getContent() : "아직 대화가 없습니다.";

        ChatRoomDto chatRoomDto = ChatRoomDto.builder()
                .roomId(chatRoom.getRoomId())
                .name(chatRoom.getName())
                .content(previewContent)
                .build();

        return ResponseEntity.ok(chatRoomDto);
    }

    @GetMapping("/rooms/{roomId}/message")
    public ResponseEntity<List<ChatMessageResponseDto>> getRoomMessages(@PathVariable String roomId) {
        List<ChatMessageResponseDto> messages = chatService.getMessagesByRoomId(roomId);
        return ResponseEntity.ok(messages);
    }


    // 5. 채팅방 생성 (생성된 방도 ChatRoomDto로 반환)
    @PostMapping("/rooms")
    public ResponseEntity<ChatRoomDto> create(@RequestParam String name) {
        ChatRoom chatRoom = chatService.createRoom(name);

        ChatRoomDto chatRoomDto = ChatRoomDto.builder()
                .roomId(chatRoom.getRoomId())
                .name(chatRoom.getName())
                .build();

        return ResponseEntity.ok(chatRoomDto);
    }

    // 6.채팅방 삭제
    @DeleteMapping("/{roomId}")
    public ResponseEntity<Void> deleteRoom(@PathVariable String roomId){
        chatService.deleteChatRoom(roomId);
        return ResponseEntity.ok().build();
    }

}