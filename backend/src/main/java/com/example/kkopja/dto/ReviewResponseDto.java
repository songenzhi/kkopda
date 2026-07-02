package com.example.kkopja.dto;

import com.example.kkopja.entity.Review;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ReviewResponseDto {
    private Long id;
    private String content;
    private Double rating;

    // 🌟 원본 User 엔티티 대신, 안전하게 빚어낸 UserResponseDto를 담아준다!
    private UserResponseDto user;
    private LocalDateTime createdAt;

    public ReviewResponseDto(Review review) {
        this.id = Long.valueOf(review.getId());
        this.content = review.getContent();
        this.rating = Double.valueOf(review.getRating());
        this.createdAt = review.getCreatedAt();

        // 리뷰 작성자 정보가 있다면 DTO로 변환해서 매핑!
        if (review.getUser() != null) {
            this.user = new UserResponseDto(review.getUser());
        }
    }
}