package com.example.kkopja.controller;

import com.example.kkopja.dto.CommunityCreateDto;
import com.example.kkopja.dto.CommunityResponseDto;
import com.example.kkopja.dto.CommunityUpdateDto;
import com.example.kkopja.entity.Community;
import com.example.kkopja.service.CommunityService;
import com.example.kkopja.service.CustomUserDetails;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/community")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class CommunityController {

    private final CommunityService communityService;

    // 커뮤니티 전체 조회
    @GetMapping
    public ResponseEntity<PagedModel<CommunityResponseDto>> getCommunities(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<Community> communityPage = communityService.findPage(page, size);

        Page<CommunityResponseDto> responsePage = communityPage.map(CommunityResponseDto::new);

        return ResponseEntity.ok(new PagedModel<>(responsePage));
    }

    // 커뮤니티 단일 조회
    @GetMapping("/{id}")
    public ResponseEntity<CommunityResponseDto> getCommunityById(@PathVariable Integer id) {
        Community community = communityService.getCommunity(id);
        return ResponseEntity.ok(new CommunityResponseDto(community));
    }

    // 커뮤니티 작성
    @PostMapping
    public ResponseEntity<?> createCommunity(@ModelAttribute CommunityCreateDto dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Integer loginUserId = userDetails.getUser().getId();

        try {
            Community savedCommunity = communityService.saveCommunity(dto, loginUserId);
            return ResponseEntity.ok(new CommunityResponseDto(savedCommunity));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("파일 업로드 중 문제가 발생했습니다.");
        }
    }

    // 커뮤니티 수정
    @PostMapping("/{id}")
    public ResponseEntity<?> updateCommunity(
            @PathVariable Integer id,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @ModelAttribute CommunityUpdateDto dto) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Integer loginUserId = userDetails.getUser().getId();

        try {
            Community updatedCommunity = communityService.updateCommunity(id, dto, loginUserId);
            return ResponseEntity.ok(new CommunityResponseDto(updatedCommunity));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("이미지 처리 중 문제가 발생했습니다.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    // 커뮤니티 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCommunity(
            @PathVariable Integer id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Integer loginUserId = userDetails.getUser().getId();
        communityService.deleteCommunity(id, loginUserId);

        return ResponseEntity.ok().build();
    }
}
