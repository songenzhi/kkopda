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
        User savedUser = userService.createUser(user);

        return ResponseEntity.ok(new UserResponseDto(savedUser));
    }

    // 로그인
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

    // 사용자 정보 조회
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Integer id) {
        User user = userService.getUserById(id);

        return ResponseEntity.ok(new UserResponseDto(user));
    }

    // 사용자 정보 수정
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable Integer id, @RequestBody User user) {
        User updatedUser = userService.updateUser(id, user);
        return ResponseEntity.ok(new UserResponseDto(updatedUser));
    }

    // 사용자 정보 삭제
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Integer id) {

        userService.deleteUser(id);
    }
}