<template>
  <div class="edit-container">
    <h2>📝 리뷰 수정하기</h2>

    <div v-if="review" class="edit-form">
      <div class="rating-selector">
        <label>별점 수정</label>
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
          placeholder="리뷰 내용을 입력하세요"></textarea>
      </div>

      <div class="actions">
        <button class="save-btn" @click="handleUpdate">수정 완료</button>
        <button class="cancel-btn" @click="$router.back()">취소</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'; // ref 추가
import { useRoute, useRouter } from 'vue-router'; // useRouter 추가
import { getReview, updateReview } from '@/api/review';

const route = useRoute();
const router = useRouter();

// 초기값 설정
const review = ref({
  rating: 5,
  content: '',
});

// 1. 기존 리뷰 데이터 불러오기
const fetchReviewDetail = async () => {
  try {
    const { cafeId, id } = route.params;
    const res = await getReview(cafeId, id);
    review.value = res.data;
  } catch (error) {
    console.error('리뷰 불러오기 실패', error);
    alert('리뷰 정보를 가져올 수 없습니다.');
  }
};

// 2. 리뷰 업데이트 실행
const handleUpdate = async () => {
  if (review.value.content.length < 5) {
    return alert('리뷰 내용을 5자 이상 작성해주세요.');
  }

  try {
    // API 정의가 updateReview(cafeId, reviewId, data) 이므로:
    const { cafeId, id } = route.params;

    await updateReview(cafeId, id, {
      rating: review.value.rating,
      content: review.value.content,
    });

    alert('리뷰가 성공적으로 수정되었습니다! ✨');
    router.back();
  } catch (error) {
    console.error('리뷰 업데이트 실패', error);
    alert('수정 중 오류가 발생했습니다.');
  }
};

onMounted(() => {
  fetchReviewDetail();
});
</script>

<style scoped>
.edit-container {
  max-width: 600px;
  margin: 50px auto;
  padding: 30px;
  background: white;
  border-radius: 20px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.05);
}

h2 {
  color: #3d2b1f;
  margin-bottom: 20px;
}

.rating-selector,
.content-input {
  margin-bottom: 20px;
}

label {
  display: block;
  font-weight: 700;
  margin-bottom: 8px;
}

select,
textarea {
  width: 100%;
  padding: 12px;
  border: 1px solid #e2ddd9;
  border-radius: 12px;
  outline: none;
}

textarea {
  min-height: 150px;
  resize: none;
}

.actions {
  display: flex;
  gap: 10px;
}

.save-btn {
  flex: 1;
  background: #3d2b1f;
  color: white;
  border: none;
  padding: 15px;
  border-radius: 12px;
  font-weight: 700;
  cursor: pointer;
}

.cancel-btn {
  background: #f3f0ec;
  color: #8c7b6e;
  border: none;
  padding: 15px 25px;
  border-radius: 12px;
  cursor: pointer;
}
</style>
