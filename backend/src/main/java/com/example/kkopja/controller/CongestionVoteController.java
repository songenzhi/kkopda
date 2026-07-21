package com.example.kkopja.controller;

import com.example.kkopja.dto.CongestionVoteRequestDto;
import com.example.kkopja.service.CongestionVoteService;
import com.example.kkopja.service.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cafes")
public class CongestionVoteController {

    private final CongestionVoteService voteService;

    // 혼잡도 투표
    @PostMapping("/{cafeId}/congestion-votes")
    public ResponseEntity<String> vote(
            @PathVariable Integer cafeId,
            @RequestBody CongestionVoteRequestDto request
    ) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails user =
                (CustomUserDetails) auth.getPrincipal();

        Integer userId = user.getUser().getId();

        try {
            voteService.vote(cafeId, userId, request.getCongestionLevel());
            return ResponseEntity.ok("투표가 성공적으로 반영되었습니다.");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
