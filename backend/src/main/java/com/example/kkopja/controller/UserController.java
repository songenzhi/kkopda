package com.example.kkopja.controller;

import com.example.kkopja.dto.LoginRequestDto;
import com.example.kkopja.dto.LoginResponseDto;
import com.example.kkopja.dto.UserResponseDto;
import com.example.kkopja.entity.User;
import com.example.kkopja.service.UserService;
import com.example.kkopja.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<UserResponseDto> createUser(@RequestBody User user) {
        // (참고) 입력받을 때도 원래는 UserRequestDto를 쓰는 게 좋지만,
        // 일단 나가는 데이터(응답)의 무한루프와 보안을 먼저 해결할게!
        User savedUser = userService.createUser(user);
        return ResponseEntity.ok(new UserResponseDto(savedUser));
    }

    // 로그인 (주석 처리된 부분)
//    @PostMapping("/login")
//    public ResponseEntity<UserResponseDto> login(@RequestBody User user) {
//        User loginUser = userService.login(user.getEmail(), user.getPassword());
//        return ResponseEntity.ok(new UserResponseDto(loginUser));
//    }
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto request) {

        User loginUser = userService.login(
                request.getEmail(),
                request.getPassword()
        );

        String token = jwtUtil.generateToken(
                loginUser.getId(),
                loginUser.getEmail()
        );

        LoginResponseDto response = new LoginResponseDto(
                token,
                loginUser.getId(),
                loginUser.getEmail(),
                loginUser.getNickname()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Integer id) {
        User user = userService.getUserById(id);

        // 엔티티 대신 안전한 DTO 상자에 담아서 비밀번호는 쏙 빼고 전달!
        return ResponseEntity.ok(new UserResponseDto(user));
    }

    // 사용자 수정
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable Integer id, @RequestBody User user) {
        User updatedUser = userService.updateUser(id, user);
        return ResponseEntity.ok(new UserResponseDto(updatedUser));
    }

    // 사용자 삭제
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Integer id) {

        userService.deleteUser(id);
    }
}