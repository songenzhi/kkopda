package com.example.kkopja.service;

import com.example.kkopja.entity.Cafe;
import com.example.kkopja.entity.Review;
import com.example.kkopja.entity.User;
import com.example.kkopja.repository.CafeRepository;
import com.example.kkopja.repository.ReviewRepository;
import com.example.kkopja.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final CafeRepository cafeRepository;

    // 리뷰 조회
    public List<Review> getReviews(Integer cafeId) {
        // findAll() 대신 특정 ID로 찾는 메소드 호출
        return reviewRepository.findByCafeId(cafeId);
    }

    // 서비스 클래스 안에 추가
    public Review getReview(Integer id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 리뷰를 찾을 수 없습니다. ID: " + id));
    }

    // 리뷰 작성
    public Review createReview(Review review, Integer userId, Integer cafeId) {
        // 1. 프론트에서 온 ID로 실제 DB 유저를 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다."));

        // 2. 프론트에서 온 ID로 실제 DB 카페를 조회
        Cafe cafe = cafeRepository.findById(cafeId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 카페입니다."));

        // 3. 조회해온 '진짜(영속 상태)' 객체들을 리뷰에 다시 세팅
        review.setUser(user);
        review.setCafe(cafe);

        return reviewRepository.save(review);
    }

    // 리뷰 수정
    public Review updateReview(Integer id, Review review, Integer userId) {

        Review existingReview = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("리뷰를 찾을 수 없습니다."));

        if(!existingReview.getUser().getId().equals(userId)){
            throw new RuntimeException("본인이 작성한 리뷰만 수정할 수 있습니다.");
        }

        existingReview.setRating(review.getRating());
        existingReview.setContent(review.getContent());

        return reviewRepository.save(existingReview);
    }

    // 리뷰 삭제
    public void deleteReview(Integer id,Integer userId) {
        Review existingReview = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("리뷰를 찾을 수 없습니다."));

        if(!existingReview.getUser().getId().equals(userId)){
            throw new RuntimeException("본인이 작성한 리뷰만 삭제할 수 있습니다.");
        }

        reviewRepository.delete(existingReview);
    }
}
