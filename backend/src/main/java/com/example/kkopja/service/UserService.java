package com.example.kkopja.service;

import com.example.kkopja.entity.User;
import com.example.kkopja.repository.CommunityRepository;
import com.example.kkopja.repository.CongestionVoteRepository;
import com.example.kkopja.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CommunityRepository communityRepository;
    private final CongestionVoteRepository congestionVoteRepository;

    // 회원가입
    public User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    // 로그인
    public User login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("비밀번호 틀림");
        }
        return user;
    }

    // 정보 조회
    public User getUserById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));
    }

    // 정보 수정 🚨 [이 메서드가 안전하게 변경되었습니다]
    @Transactional
    public User updateUser(Integer id, User user) {

        // 1. DB에서 기존 유저의 정보(비밀번호 포함)를 먼저 온전하게 가져옵니다.
        User existingUser = getUserById(id);

        // 2. 이메일을 변경하려고 하는 경우에만 중복 체크를 진행합니다.
        if (user.getEmail() != null && !existingUser.getEmail().equals(user.getEmail())) {
            userRepository.findByEmail(user.getEmail())
                    .ifPresent(u -> {
                        throw new RuntimeException("이미 사용 중인 이메일");
                    });
            existingUser.setEmail(user.getEmail());
        }

        // 3. 닉네임이 비어있지 않다면 새로운 닉네임으로 변경합니다.
        if (user.getNickname() != null) {
            existingUser.setNickname(user.getNickname());
        }

        // 💡 핵심: 프론트엔드에서 비밀번호가 넘어오지 않으므로(null),
        // existingUser.setPassword(user.getPassword()); 코드를 지워서 기존 비밀번호를 그대로 보존합니다!

        // 4. 변경된 닉네임/이메일만 반영하여 DB에 저장합니다.
        return userRepository.save(existingUser);
    }

    // 정보 삭제
    @Transactional
    public void deleteUser(Integer id) {
        communityRepository.deleteByUserId(id);
        userRepository.deleteById(id);
    }
}