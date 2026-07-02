<!-- <template>
  <div class="review-container">
    <div v-if="reviews.length > 0" class="review-grid">
      <div v-for="review in reviews" :key="review.id" class="review-card">
        <h4>{{ review.user.nickname }}</h4>

        <p>{{ review.content }}</p>

        <span class="rating">⭐ {{ review.rating }}</span>

        <div class="btn-group" v-if="isAuthor(review)">
          <button
            class="edit-sm-btn"
            @click.stop="goToEdit(review.cafe.id, review.id)">
            수정
          </button>

          <button
            class="delete-sm-btn"
            @click.stop="handleDelete(review.cafe.id, review.id)">
            삭제
          </button>
        </div>
      </div>
    </div>

    <div v-else class="empty-state">
      검색 결과가 없거나 리뷰가 존재하지 않습니다.
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { getReviewsByCafe, deleteReview } from '@/api/review';
import { useUserStore } from '@/stores/userStore';

const router = useRouter();
const route = useRoute();
const userStore = useUserStore();

const reviews = ref([]);

const isAuthor = (review) => {
  // 1. 데이터가 완전히 들어오기 전에는 false 반환
  if (!userStore.id || !review.user || !review.user.id) {
    return false;
  }

  // 2. 두 값을 명시적으로 문자열로 변환하여 비교 (가장 안전)
  const loginUserId = String(userStore.id);
  const reviewUserId = String(review.user.id);

  return loginUserId === reviewUserId;
};

const goToEdit = (cafeId, reviewId) => {
  router.push(`/cafes/${cafeId}/reviews/edit/${reviewId}`);
};

const handleDelete = async (cafeId, reviewId) => {
  if (!confirm('이 리뷰를 정말 삭제하시겠습니까?')) return;

  try {
    await deleteReview(cafeId, reviewId);
    alert('리뷰가 삭제되었습니다.');
    fetchReviews();
  } catch (error) {
    console.error(error);
    alert('리뷰 삭제에 실패했습니다.');
  }
};

const fetchReviews = async () => {
  try {
    const cafeId = route.params.id;

    const res = await getReviewsByCafe(cafeId);

    reviews.value = Array.isArray(res.data) ? res.data : res.data.content || [];

    console.log('UserStore ID:', userStore.id, typeof userStore.id);
    console.log(
      '첫 번째 리뷰 작성자 ID:',
      reviews.value[0]?.user?.id,
      typeof reviews.value[0]?.user?.id,
    );
  } catch (error) {
    console.error(error);
  }
};

onMounted(() => {
  fetchReviews();
});
</script>

<style scoped>
.review-container {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.review-grid {
  display: grid;
  gap: 20px;
}

.review-card {
  padding: 20px;
  background: white;
  border-radius: 15px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.rating {
  display: block;
  margin-top: 10px;
}

.btn-group {
  margin-top: 15px;
  display: flex;
  gap: 10px;
}

.edit-sm-btn,
.delete-sm-btn {
  padding: 8px 14px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
}

.edit-sm-btn {
  background: #f3f0ec;
}

.delete-sm-btn {
  background: #ff7675;
  color: white;
}

.empty-state {
  text-align: center;
  color: #999;
  padding: 50px;
}
</style> -->
