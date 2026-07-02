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

    // 전체 조회 수정
    @GetMapping
    public ResponseEntity<PagedModel<CommunityResponseDto>> getCommunities(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<Community> communityPage = communityService.findPage(page, size);

        Page<CommunityResponseDto> responsePage = communityPage.map(CommunityResponseDto::new);

        return ResponseEntity.ok(new PagedModel<>(responsePage));
    }

    // 단일 조회 수정
    @GetMapping("/{id}")
    public ResponseEntity<CommunityResponseDto> getCommunityById(@PathVariable Integer id) {
        Community community = communityService.getCommunity(id);
        return ResponseEntity.ok(new CommunityResponseDto(community));
    }

    // 작성
    @PostMapping
    public ResponseEntity<?> createCommunity(@ModelAttribute CommunityCreateDto dto) { // 🚨 @RequestBody 대신 @ModelAttribute 사용!

        // ⭐ SecurityContext에서 현재 로그인한 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // 방법 2 (추천): JwtAuthenticationFilter에서 아예 CustomUserDetails를 셋팅해뒀다면 캐스팅해서 사용
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Integer loginUserId = userDetails.getUser().getId();

        try {
            // 서비스에 DTO와 유저 ID 전달
            Community savedCommunity = communityService.saveCommunity(dto, loginUserId);
            return ResponseEntity.ok(new CommunityResponseDto(savedCommunity));

        } catch (IOException e) {
            // 파일 저장하다가 내 컴퓨터 디스크 문제 등으로 에러가 났을 때의 처리
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("파일 업로드 중 문제가 발생했습니다.");
        }
    }

    // 🎯 수정 (파일 업로드 및 삭제 로직 반영)
    @PostMapping("/{id}")
    public ResponseEntity<?> updateCommunity( // 💡 성공 시 DTO, 실패 시 에러 메시지(String)를 주기 위해 와일드카드(?) 사용
                                              @PathVariable Integer id,
                                              @AuthenticationPrincipal CustomUserDetails userDetails,
                                              @ModelAttribute CommunityUpdateDto dto) { // 🚨 @RequestBody 대신 @ModelAttribute 사용!


        // 1. 로그인 안 한 유저 컷!
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Integer loginUserId = userDetails.getUser().getId();

        try {
            // 2. 서비스에 글 번호, 수정할 데이터(DTO), 유저 ID를 넘겨서 처리
            Community updatedCommunity = communityService.updateCommunity(id, dto, loginUserId);

            // 3. 무사히 수정되었다면 화면에 보여줄 응답 DTO로 변환해서 리턴
            return ResponseEntity.ok(new CommunityResponseDto(updatedCommunity));

        } catch (IOException e) {
            // 🚨 파일 저장/삭제 중 내 컴퓨터 디스크 문제 등으로 에러가 났을 때
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("이미지 처리 중 문제가 발생했습니다.");
        } catch (RuntimeException e) {
            // 🚨 본인 글이 아니거나 글이 없는 등 서비스에서 던진 에러 처리
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    // 🎯 삭제 (작성자 확인 및 응답 형태 개선!)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCommunity(
            @PathVariable Integer id,
            @AuthenticationPrincipal CustomUserDetails userDetails) { // 💡 프론트에서 보낸 유저 ID 받기

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Integer loginUserId = userDetails.getUser().getId();

        // 서비스에 글 번호와 '삭제하려는 사람의 번호(userId)'를 같이 넘겨줌!
        communityService.deleteCommunity(id, loginUserId);

        // 에러 없이 무사히 지워졌다면 깔끔하게 200 OK 상태 코드 반환!
        return ResponseEntity.ok().build();
    }
}
