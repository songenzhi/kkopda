package com.example.kkopja.service;

import com.example.kkopja.dto.CommunityCreateDto;
import com.example.kkopja.dto.CommunityUpdateDto;
import com.example.kkopja.entity.Community;
import com.example.kkopja.entity.User;
import com.example.kkopja.repository.CommunityRepository;
import com.example.kkopja.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommunityServiceTest {

    @Mock
    private CommunityRepository communityRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CommunityService communityService;

    @Test
    @DisplayName("커뮤니티 리스트 페이징 조회 성공")
    void findPage() {
        // given
        int page = 1;
        int size = 10;
        PageRequest pageRequest = PageRequest.of(page - 1, size);

        Community community = Community.builder().title("테스트 제목").build();
        Page<Community> mockPage = new PageImpl<>(List.of(community));

        given(communityRepository.findAllByOrderByIdDesc(pageRequest)).willReturn(mockPage);

        // when
        Page<Community> result = communityService.findPage(page, size);

        // then
        assertEquals(1, result.getContent().size());
        assertEquals("테스트 제목", result.getContent().get(0).getTitle());
    }

    @Test
    @DisplayName("커뮤니티 단일 조회 성공")
    void getCommunity_Success() {
        // given
        Integer communityId = 1;
        Community mockCommunity = Community.builder().title("단일 조회").build();
        given(communityRepository.findById(communityId)).willReturn(Optional.of(mockCommunity));

        // when
        Community result = communityService.getCommunity(communityId);

        // then
        assertNotNull(result);
        assertEquals("단일 조회", result.getTitle());
    }

    @Test
    @DisplayName("커뮤니티 작성 성공 (이미지가 있는 경우)")
    void saveCommunity_WithImage() throws IOException {
        // given
        Integer userId = 1;
        User mockUser = User.builder().id(userId).nickname("작성자").build();

        // DTO와 파일 Mocking
        CommunityCreateDto dto = mock(CommunityCreateDto.class);
        MultipartFile mockFile = mock(MultipartFile.class);

        given(userRepository.findById(userId)).willReturn(Optional.of(mockUser));
        given(dto.getTitle()).willReturn("새 글 제목");
        given(dto.getDescription()).willReturn("새 글 내용");
        given(dto.getImageFile()).willReturn(mockFile);

        // 파일 업로드 관련 Mock 설정 (실제 디스크에 저장하지 않도록 함)
        given(mockFile.isEmpty()).willReturn(false);
        given(mockFile.getOriginalFilename()).willReturn("test-image.png");
        doNothing().when(mockFile).transferTo(any(File.class)); // 💡 실제 파일 쓰기 방지

        // 레포지토리 저장 시 임의의 엔티티 반환 설정
        Community savedCommunity = Community.builder().title("새 글 제목").imageUrl("/uploads/mock-uuid.png").build();
        given(communityRepository.save(any(Community.class))).willReturn(savedCommunity);

        // when
        Community result = communityService.saveCommunity(dto, userId);

        // then
        assertNotNull(result);
        assertEquals("새 글 제목", result.getTitle());
        verify(mockFile, times(1)).transferTo(any(File.class));
        verify(communityRepository, times(1)).save(any(Community.class));
    }

    @Test
    @DisplayName("커뮤니티 수정 실패 - 작성자가 아닌 유저가 수정을 시도할 때")
    void updateCommunity_Fail_Unauthorized() {
        // given
        Integer communityId = 1;
        Integer ownerId = 1;
        Integer hackerId = 999; // 💡 글쓴이가 아닌 다른 유저

        User owner = User.builder().id(ownerId).build();
        Community mockCommunity = Community.builder().user(owner).title("원본 제목").build();
        CommunityUpdateDto dto = mock(CommunityUpdateDto.class);

        given(communityRepository.findById(communityId)).willReturn(Optional.of(mockCommunity));

        // when & then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            communityService.updateCommunity(communityId, dto, hackerId);
        });

        assertEquals("남의 글은 수정할 수 없습니다!", exception.getMessage());
        // 예외가 터졌으므로 save는 호출되지 않아야 함
        verify(communityRepository, never()).save(any(Community.class));
    }

    @Test
    @DisplayName("커뮤니티 삭제 성공")
    void deleteCommunity_Success() {
        // given
        Integer communityId = 1;
        Integer loginUserId = 1;

        User owner = User.builder().id(loginUserId).build();
        Community mockCommunity = Community.builder().user(owner).build();

        given(communityRepository.findById(communityId)).willReturn(Optional.of(mockCommunity));

        // when
        communityService.deleteCommunity(communityId, loginUserId);

        // then
        verify(communityRepository, times(1)).delete(mockCommunity);
    }
}