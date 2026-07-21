package com.example.kkopja.service;

import com.example.kkopja.dto.ChatMessageResponseDto;
import com.example.kkopja.entity.User;
import com.example.kkopja.entity.chat.ChatMessage;
import com.example.kkopja.entity.chat.ChatRoom;
import com.example.kkopja.repository.ChatMessageRepository;
import com.example.kkopja.repository.ChatRoomRepository;
import com.example.kkopja.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class) // Mockito 사용을 위한 설정
class ChatServiceTest {

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ChatService chatService; // Mock 객체들이 주입될 실제 테스트 대상

    @Test
    @DisplayName("방 생성 테스트: UUID가 발급되고 저장이 호출되어야 한다")
    void createRoom() {
        // given (준비)
        String roomName = "테스트 방";

        // when (실행)
        ChatRoom createdRoom = chatService.createRoom(roomName);

        // then (검증)
        assertNotNull(createdRoom.getRoomId()); // UUID가 정상적으로 들어갔는지 확인
        assertEquals(roomName, createdRoom.getName());

        // chatRoomRepository의 save 메서드가 정확히 1번 호출되었는지 검증
        verify(chatRoomRepository, times(1)).save(any(ChatRoom.class));
    }

    @Test
    @DisplayName("메시지 저장 성공 테스트")
    void saveMessage_Success() {
        // given
        String roomId = "uuid-1234";
        Integer userId = 1;
        String content = "안녕하세요!";

        ChatRoom mockRoom = ChatRoom.builder().roomId(roomId).name("테스트 방").build();
        User mockUser = User.builder().id(userId).nickname("테스터").build();

        // Mock 객체들의 동작 정의 (findBy... 가 호출되면 이렇게 반환해라)
        given(chatRoomRepository.findByRoomId(roomId)).willReturn(Optional.of(mockRoom));
        given(userRepository.findById(userId)).willReturn(Optional.of(mockUser));

        // when
        chatService.saveMessage(roomId, userId, content);

        // then
        verify(chatMessageRepository, times(1)).save(any(ChatMessage.class));
    }

    @Test
    @DisplayName("메시지 저장 실패 테스트: 존재하지 않는 방일 경우 예외 발생")
    void saveMessage_Fail_RoomNotFound() {
        // given
        String roomId = "invalid-uuid";
        Integer userId = 1;

        // 방을 찾을 때 빈값(Optional.empty)을 반환하도록 설정
        given(chatRoomRepository.findByRoomId(roomId)).willReturn(Optional.empty());

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            chatService.saveMessage(roomId, userId, "테스트 메시지");
        });

        assertEquals("존재하지 않는 방입니다.", exception.getMessage());
        // 방이 없어서 예외가 터졌으므로 UserRepository와 MessageRepository는 호출되지 않아야 함
        verify(userRepository, times(0)).findById(any());
        verify(chatMessageRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("특정 방의 메시지 목록 조회: Entity가 Dto로 잘 변환되어야 한다")
    void getMessagesByRoomId() {
        // given
        String roomId = "uuid-1234";
        User mockUser = User.builder().nickname("개발자").build();
        ChatRoom mockRoom = ChatRoom.builder().roomId(roomId).build();

        ChatMessage msg1 = ChatMessage.builder().content("첫 메시지").user(mockUser).chatRoom(mockRoom).build();
        ChatMessage msg2 = ChatMessage.builder().content("두번째 메시지").user(mockUser).chatRoom(mockRoom).build();

        given(chatMessageRepository.findByChatRoomRoomIdOrderByCreatedAtAsc(roomId))
                .willReturn(List.of(msg1, msg2));

        // when
        List<ChatMessageResponseDto> result = chatService.getMessagesByRoomId(roomId);

        // then
        assertEquals(2, result.size());
        assertEquals("첫 메시지", result.get(0).getContent());
        assertEquals("개발자", result.get(0).getName()); // User 엔티티에서 닉네임을 잘 가져왔는지 확인
    }

    @Test
    @DisplayName("채팅방 삭제 테스트")
    void deleteChatRoom() {
        // given
        String roomId = "uuid-1234";

        // when
        chatService.deleteChatRoom(roomId);

        // then
        verify(chatRoomRepository, times(1)).deleteByRoomId(roomId);
    }
}