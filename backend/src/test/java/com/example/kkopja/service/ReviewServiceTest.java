package com.example.kkopja.service;

import com.example.kkopja.entity.Cafe;
import com.example.kkopja.entity.Review;
import com.example.kkopja.entity.User;
import com.example.kkopja.repository.CafeRepository;
import com.example.kkopja.repository.ReviewRepository;
import com.example.kkopja.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Mockito 환경 구축
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CafeRepository cafeRepository;

    @InjectMocks
    private ReviewService reviewService; // Mock 객체들이 주입될 대상

    @Test
    @DisplayName("특정 카페의 리뷰 목록 조회 성공")
    void getReviews() {
        // given
        Integer cafeId = 1;
        Review review1 = Review.builder().content("좋아요").build();
        Review review2 = Review.builder().content("나빠요").build();
        given(reviewRepository.findByCafeId(cafeId)).willReturn(List.of(review1, review2));

        // when
        List<Review> result = reviewService.getReviews(cafeId);

        // then
        assertEquals(2, result.size());
        assertEquals("좋아요", result.get(0).getContent());
        verify(reviewRepository, times(1)).findByCafeId(cafeId);
    }

    @Test
    @DisplayName("리뷰 단일 조회 실패 - 존재하지 않는 ID일 때 예외 발생")
    void getReview_Fail_NotFound() {
        // given
        Integer reviewId = 99;
        given(reviewRepository.findById(reviewId)).willReturn(Optional.empty());

        // when & then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            reviewService.getReview(reviewId);
        });

        assertEquals("해당 리뷰를 찾을 수 없습니다. ID: " + reviewId, exception.getMessage());
    }

    @Test
    @DisplayName("리뷰 작성 성공 - 유저와 카페를 정상 조회하여 세팅 후 저장한다")
    void createReview_Success() {
        // given
        Integer userId = 1;
        Integer cafeId = 2;

        User mockUser = User.builder().id(userId).nickname("리뷰어").build();
        Cafe mockCafe = Cafe.builder().id(cafeId).name("스타벅스").build();
        Review inputReview = Review.builder().rating(5).content("커피가 맛있어요").build();

        given(userRepository.findById(userId)).willReturn(Optional.of(mockUser));
        given(cafeRepository.findById(cafeId)).willReturn(Optional.of(mockCafe));

        // save 호출 시 입력된 객체 그대로 반환하도록 세팅
        given(reviewRepository.save(any(Review.class))).willAnswer(invocation -> invocation.getArgument(0));

        // when
        Review result = reviewService.createReview(inputReview, userId, cafeId);

        // then
        assertNotNull(result);
        assertEquals(mockUser, result.getUser()); // 영속 유저 객체가 잘 바인딩되었는지 확인
        assertEquals(mockCafe, result.getCafe()); // 영속 카페 객체가 잘 바인딩되었는지 확인
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    @DisplayName("리뷰 수정 성공 - 작성자 본인일 경우 별점과 내용이 수정된다")
    void updateReview_Success() {
        // given
        Integer reviewId = 1;
        Integer userId = 1;

        User owner = User.builder().id(userId).build();
        Review existingReview = Review.builder().id(reviewId).user(owner).rating(3).content("보통이요").build();
        Review updateInfo = Review.builder().rating(5).content("최고로 변경합니다").build();

        given(reviewRepository.findById(reviewId)).willReturn(Optional.of(existingReview));
        given(reviewRepository.save(any(Review.class))).willReturn(existingReview);

        // when
        Review result = reviewService.updateReview(reviewId, updateInfo, userId);

        // then
        assertEquals(5, result.getRating());
        assertEquals("최고로 변경합니다", result.getContent());
        verify(reviewRepository, times(1)).save(existingReview);
    }

    @Test
    @DisplayName("리뷰 수정 실패 - 작성자가 아닌 다른 유저가 수정을 요청하면 예외가 발생한다")
    void updateReview_Fail_Unauthorized() {
        // given
        Integer reviewId = 1;
        Integer ownerId = 1;
        Integer hackerId = 999; // 💡 다른 유저

        User owner = User.builder().id(ownerId).build();
        Review existingReview = Review.builder().id(reviewId).user(owner).build();
        Review updateInfo = Review.builder().content("해킹 시도 콘텐츠").build();

        given(reviewRepository.findById(reviewId)).willReturn(Optional.of(existingReview));

        // when & then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            reviewService.updateReview(reviewId, updateInfo, hackerId);
        });

        assertEquals("본인이 작성한 리뷰만 수정할 수 있습니다.", exception.getMessage());
        verify(reviewRepository, never()).save(any(Review.class)); // 💡 저장은 절대 호출되면 안 됨
    }

    @Test
    @DisplayName("리뷰 삭제 성공 - 본인 작성 리뷰는 정상 삭제된다")
    void deleteReview_Success() {
        // given
        Integer reviewId = 1;
        Integer userId = 1;

        User owner = User.builder().id(userId).build();
        Review existingReview = Review.builder().id(reviewId).user(owner).build();

        given(reviewRepository.findById(reviewId)).willReturn(Optional.of(existingReview));

        // when
        reviewService.deleteReview(reviewId, userId);

        // then
        verify(reviewRepository, times(1)).delete(existingReview);
    }

    @Test
    @DisplayName("리뷰 삭제 실패 - 작성자가 아닐 경우 삭제되지 않고 예외가 발생한다")
    void deleteReview_Fail_Unauthorized() {
        // given
        Integer reviewId = 1;
        Integer ownerId = 1;
        Integer hackerId = 999;

        User owner = User.builder().id(ownerId).build();
        Review existingReview = Review.builder().id(reviewId).user(owner).build();

        given(reviewRepository.findById(reviewId)).willReturn(Optional.of(existingReview));

        // when & then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            reviewService.deleteReview(reviewId, hackerId);
        });

        assertEquals("본인이 작성한 리뷰만 삭제할 수 있습니다.", exception.getMessage());
        verify(reviewRepository, never()).delete(any(Review.class)); // 💡 삭제 메서드는 호출되지 않아야 함
    }
}