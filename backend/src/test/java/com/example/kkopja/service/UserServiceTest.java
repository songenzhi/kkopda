package com.example.kkopja.service;

import com.example.kkopja.entity.User;
import com.example.kkopja.repository.CommunityRepository;
import com.example.kkopja.repository.CongestionVoteRepository;
import com.example.kkopja.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private CommunityRepository communityRepository;

    @Mock
    private CongestionVoteRepository congestionVoteRepository; // 사용되진 않지만 생성자 주입 에러 방지용

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("회원가입 성공: 비밀번호가 암호화되어 저장되어야 한다")
    void createUser_Success() {
        // given
        User newUser = User.builder().email("test@test.com").password("rawPassword").build();

        given(passwordEncoder.encode("rawPassword")).willReturn("encodedPassword");
        given(userRepository.save(any(User.class))).willAnswer(invocation -> invocation.getArgument(0));

        // when
        User savedUser = userService.createUser(newUser);

        // then
        assertEquals("encodedPassword", savedUser.getPassword()); // 암호화된 비밀번호로 세팅되었는지 검증
        verify(passwordEncoder, times(1)).encode("rawPassword");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("로그인 성공")
    void login_Success() {
        // given
        String email = "test@test.com";
        String password = "myPassword";
        User mockUser = User.builder().email(email).password("encodedPassword").build();

        given(userRepository.findByEmail(email)).willReturn(Optional.of(mockUser));
        given(passwordEncoder.matches(password, "encodedPassword")).willReturn(true); // 비밀번호 일치 설정

        // when
        User result = userService.login(email, password);

        // then
        assertNotNull(result);
        assertEquals(email, result.getEmail());
    }

    @Test
    @DisplayName("로그인 실패 - 존재하지 않는 이메일일 경우")
    void login_Fail_UserNotFound() {
        // given
        String email = "notfound@test.com";
        given(userRepository.findByEmail(email)).willReturn(Optional.empty());

        // when & then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.login(email, "password");
        });

        assertEquals("사용자 없음", exception.getMessage());
        verify(passwordEncoder, never()).matches(anyString(), anyString()); // 유저가 없으면 비밀번호 검증은 시도조차 안 해야 함
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호가 틀렸을 경우")
    void login_Fail_PasswordMismatch() {
        // given
        String email = "test@test.com";
        String wrongPassword = "wrongPassword";
        User mockUser = User.builder().email(email).password("encodedPassword").build();

        given(userRepository.findByEmail(email)).willReturn(Optional.of(mockUser));
        given(passwordEncoder.matches(wrongPassword, "encodedPassword")).willReturn(false); // 불일치 설정

        // when & then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.login(email, wrongPassword);
        });

        assertEquals("비밀번호 틀림", exception.getMessage());
    }

    @Test
    @DisplayName("정보 수정 성공: 변경된 이메일과 닉네임만 반영되고 비밀번호는 유지된다")
    void updateUser_Success() {
        // given
        Integer userId = 1;
        // 기존 DB에 있던 회원 정보
        User existingUser = User.builder()
                .id(userId)
                .email("old@test.com")
                .nickname("구닉네임")
                .password("기존비밀번호")
                .build();

        // 프론트에서 넘어온 수정 데이터 (비밀번호는 null)
        User updateData = User.builder()
                .email("new@test.com")
                .nickname("새닉네임")
                .build();

        given(userRepository.findById(userId)).willReturn(Optional.of(existingUser));
        given(userRepository.findByEmail("new@test.com")).willReturn(Optional.empty()); // 중복 이메일 없음
        given(userRepository.save(any(User.class))).willAnswer(invocation -> invocation.getArgument(0));

        // when
        User updatedUser = userService.updateUser(userId, updateData);

        // then
        assertEquals("new@test.com", updatedUser.getEmail());
        assertEquals("새닉네임", updatedUser.getNickname());
        assertEquals("기존비밀번호", updatedUser.getPassword()); // 💡 핵심: 비밀번호가 보존되었는지 확인
    }

    @Test
    @DisplayName("정보 수정 실패 - 변경하려는 이메일이 이미 다른 사람에 의해 사용 중일 때")
    void updateUser_Fail_EmailAlreadyInUse() {
        // given
        Integer userId = 1;
        User existingUser = User.builder().id(userId).email("old@test.com").build();
        User updateData = User.builder().email("used@test.com").build();
        User anotherUser = User.builder().id(2).email("used@test.com").build(); // 이미 있는 다른 유저

        given(userRepository.findById(userId)).willReturn(Optional.of(existingUser));
        given(userRepository.findByEmail("used@test.com")).willReturn(Optional.of(anotherUser));

        // when & then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.updateUser(userId, updateData);
        });

        assertEquals("이미 사용 중인 이메일", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("회원 탈퇴 성공: 연관된 커뮤니티 데이터가 먼저 삭제되고 유저가 삭제된다")
    void deleteUser_Success() {
        // given
        Integer userId = 1;

        // when
        userService.deleteUser(userId);

        // then
        verify(communityRepository, times(1)).deleteByUserId(userId);
        verify(userRepository, times(1)).deleteById(userId);
    }
}