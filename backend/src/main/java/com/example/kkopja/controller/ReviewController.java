package com.example.kkopja.controller;

import com.example.kkopja.dto.ReviewResponseDto;
import com.example.kkopja.entity.Review;
import com.example.kkopja.service.CustomUserDetails;
import com.example.kkopja.service.ReviewService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cafes/{cafeId}/reviews")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class ReviewController {

    private final ReviewService reviewService;

    // 리뷰 조회
    @GetMapping
    public ResponseEntity<List<ReviewResponseDto>> getReviewsByCafeId(@PathVariable Integer cafeId) {

        // 1. 서비스에서 리뷰 엔티티 리스트를 가져옴
        List<Review> reviews = reviewService.getReviews(cafeId);

        // 2. 리뷰 엔티티를 ReviewResponseDto로 싹 변환! (이때 유저 정보도 안전한 UserResponseDto로 자동 변환됨)
        List<ReviewResponseDto> responseList = reviews.stream()
                .map(ReviewResponseDto::new)
                .collect(Collectors.toList());

        // 3. 변환된 안전한 상자를 프론트엔드로 전달
        return ResponseEntity.ok(responseList);
    }

    // 🎯 단일 리뷰 조회
    @GetMapping("/{id}")
    public ResponseEntity<ReviewResponseDto> getReview(@PathVariable Integer cafeId, @PathVariable Integer id) {
        Review review = reviewService.getReview(id);
        return ResponseEntity.ok(new ReviewResponseDto(review));
    }

    // 🎯 리뷰 작성
    @PostMapping
    public ResponseEntity<ReviewResponseDto> createReview(@PathVariable Integer cafeId,
                                                          @AuthenticationPrincipal CustomUserDetails userDetails,
                                                          @RequestBody Review review) {


        if (userDetails  == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Integer loginUserId = userDetails.getUser().getId();

        Review savedReview = reviewService.createReview(review, loginUserId, cafeId);
        return ResponseEntity.ok(new ReviewResponseDto(savedReview));
    }

    // 🎯 리뷰 수정
    @PutMapping("/{id}")
    public ResponseEntity<ReviewResponseDto> updateReview(@PathVariable Integer cafeId, @PathVariable Integer id, @AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody Review review) {
        if (userDetails  == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Integer loginUserId = userDetails.getUser().getId();

        Review updatedReview = reviewService.updateReview(id, review, loginUserId);
        return ResponseEntity.ok(new ReviewResponseDto(updatedReview));
    }

    // 리뷰 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<ReviewResponseDto> deleteReview(@PathVariable Integer id, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Integer loginUserId = userDetails.getUser().getId();

        if (loginUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        reviewService.deleteReview(id, loginUserId);

        return ResponseEntity.ok().build();
    }
}
