<template>
  <div class="review-form-card">
    <h3 class="form-title">💬 리뷰 남기기</h3>

    <form @submit.prevent="handleReviewSubmit">
      <div class="rating-selector">
        <label>만족도</label>
        <select v-model="review.rating">
          <option :value="5">⭐⭐⭐⭐⭐ (5.0)</option>
          <option :value="4">⭐⭐⭐⭐ (4.0)</option>
          <option :value="3">⭐⭐⭐ (3.0)</option>
          <option :value="2">⭐⭐ (2.0)</option>
          <option :value="1">⭐ (1.0)</option>
        </select>
      </div>

      <div class="content-input">
        <textarea
          v-model="review.content"
          placeholder="이 카페의 분위기나 콘센트 위치는 어떤가요? (최소 5자)"
          required></textarea>
      </div>

      <button type="submit" class="submit-btn">리뷰 등록하기</button>
    </form>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { createReview } from '@/api/review';
import { useUserStore } from '@/stores/userStore';

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();

const review = ref({
  rating: 5,
  content: '',
});

const handleReviewSubmit = async () => {
  console.log('현재 스토어 유저 정보:', userStore.user);

  // 1. 로그인 체크
  // 🎯 .user를 빼고 직접 .id와 .isLogin에 접근합니다!
  if (!userStore.isLogin || !userStore.id) {
    return alert('로그인이 필요한 서비스입니다. 🔐');
  }
  const userId = userStore.id; // userStore.id로 바로 접근
  const cafeId = route.params.id;

  // 2. 입력값 검증
  if (review.value.content.length < 5) {
    return alert('리뷰 내용을 조금 더 자세히 작성해주세요! (최소 5자)');
  }

  // 3. 서버의 @RequestBody(Review 엔티티)에 담길 순수 데이터만 생성
  const payload = {
    content: review.value.content,
    rating: review.value.rating,
    // 컨트롤러에서 cafeId와 userId를 별도로 받으므로 여기서는 뺍니다.
  };

  try {
    // 💡 API 함수를 (cafeId, userId, payload) 순서로 호출하도록 수정
    await createReview(cafeId, userId, payload);

    alert('리뷰가 소중하게 등록되었습니다! ✨');

    review.value.content = '';
    review.value.rating = 5;
    router.push(`/cafes/${cafeId}`);
  } catch (error) {
    console.error('리뷰 등록 에러:', error);
    alert('리뷰 등록 중 오류가 발생했습니다.');
  }
};
</script>

<style scoped>
.review-form-card {
  background: #fdfaf7;
  padding: 30px;
  border-radius: 25px;
  border: 1px solid #eee;
}

.form-title {
  font-size: 18px;
  font-weight: 800;
  color: #3d2b1f;
  margin-bottom: 20px;
}

.rating-selector {
  margin-bottom: 15px;
}

.rating-selector label {
  display: block;
  font-size: 13px;
  font-weight: 700;
  margin-bottom: 8px;
  color: #bb4d39;
}

.rating-selector select {
  width: 100%;
  padding: 10px;
  border-radius: 12px;
  border: 1px solid #e2ddd9;
  outline: none;
}

textarea {
  width: 100%;
  min-height: 120px;
  padding: 15px;
  border-radius: 15px;
  border: 1px solid #e2ddd9;
  resize: none;
  font-family: inherit;
  margin-bottom: 15px;
}

textarea:focus {
  border-color: #ff9d6c;
  outline: none;
}

.submit-btn {
  width: 100%;
  background: #3d2b1f;
  color: white;
  padding: 15px;
  border-radius: 15px;
  border: none;
  font-weight: 800;
  cursor: pointer;
  transition: 0.2s;
}

.submit-btn:hover {
  background: #bb4d39;
  transform: translateY(-2px);
}
</style>
