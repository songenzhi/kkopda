import api from './axios';

// 1. 특정 카페의 리뷰 목록 조회
export const getReviewsByCafe = (cafeId) => {
  return api.get(`/cafes/${cafeId}/reviews`);
};

// 2. 리뷰 작성
export const createReview = (cafeId, userId, data) => {
  return api.post(`/cafes/${cafeId}/reviews`, data, {
    params: { userId: userId },
  });
};

// 3. 단일 리뷰 조회
export const getReview = (cafeId, reviewId) => {
  return api.get(`/cafes/${cafeId}/reviews/${reviewId}`);
};

// 4. 리뷰 업데이트
 export const updateReview = (cafeId, reviewId, data) => {
  return api.put(`/cafes/${cafeId}/reviews/${reviewId}`, data);
};

// 5. 리뷰 삭제
export const deleteReview = (cafeId, reviewId) => {
  return api.delete(`/cafes/${cafeId}/reviews/${reviewId}`);
};
