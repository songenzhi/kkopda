<template>
  <div class="detail-container">
    <div class="bg-glow"></div>

    <section v-if="cafe" class="cafe-hero">
      <div class="hero-card">
        <div class="info-area">
          <span class="category">CAFE DETAIL</span>
          <h2 class="cafe-name">{{ cafe.name }}</h2>
          <p class="address">📍 {{ cafe.address }}</p>

          <div v-if="cafe.currentCongestion" class="congestion-status">
            <div class="badge" :class="cafe.currentCongestion.toLowerCase()">
              <span class="live-dot"></span>
              현재 혼잡도:
              <strong>{{ getCongestionLabel(cafe.currentCongestion) }}</strong>
            </div>
          </div>

          <div class="spec-tags">
            <span class="tag"
              >🔌 콘센트 {{ cafe.hasOutlet ? '있음' : '없음' }}</span
            >
            <span class="tag"
              >📶 와이파이 {{ cafe.wifi ? '제공' : '없음' }}</span
            >
            <span class="tag"
              >🚗 주차 {{ cafe.parking ? '가능' : '불가' }}</span
            >
          </div>

          <div class="vote-area">
            <div class="vote-header">
              <p class="vote-title">
                <span class="wave">👋</span> 지금 매장에 계신가요?
              </p>
              <p class="vote-subtitle">
                다른 분들을 위해 혼잡도를 제보해 주세요!
              </p>
            </div>
            <div class="vote-buttons">
              <button class="vote-btn relaxed" @click="submitVote('RELAXED')">
                <span class="icon">🟢</span>
                <span class="text">여유로워요</span>
              </button>
              <button class="vote-btn normal" @click="submitVote('NORMAL')">
                <span class="icon">🟡</span>
                <span class="text">보통이에요</span>
              </button>
              <button class="vote-btn crowded" @click="submitVote('CROWDED')">
                <span class="icon">🔴</span>
                <span class="text">혼잡해요</span>
              </button>
            </div>
          </div>

          <div class="admin-actions">
            <button class="edit-btn" @click="goToEdit">정보 수정하기</button>
            <button class="delete-btn" @click="handleDelete">삭제</button>
          </div>
        </div>

        <div class="visual-area">
          <div class="cafe-placeholder">
            <span>{{ cafe.name }}</span>
          </div>
        </div>
      </div>
    </section>

    <section class="review-section">
      <div class="section-header">
        <h3>
          사용자 리뷰 <span>({{ reviews.length }})</span>
        </h3>
        <button class="review-add-btn" @click="handleCreateReview">
          + 리뷰 작성
        </button>
      </div>

      <div v-if="reviews.length > 0" class="review-list">
        <div v-for="review in reviews" :key="review.id" class="review-card">
          <div class="review-info">
            <span class="user-name"
              >👤 {{ review.user?.nickname || '익명' }}</span
            >
            <span class="rating">⭐ {{ review.rating }}</span>
          </div>
          <p class="content">{{ review.content }}</p>

          <div class="review-footer">
            <span class="date">{{
              new Date(review.createdAt).toLocaleDateString()
            }}</span>

            <div v-if="review.user.id === userStore.id" class="review-actions">
              <button class="action-edit" @click="handleUpdateReview(review)">
                수정
              </button>
              <button
                class="action-delete"
                @click="handleDeleteReview(review.id)">
                삭제
              </button>
            </div>
          </div>
        </div>
      </div>

      <div v-else class="empty-review">
        <p>아직 등록된 리뷰가 없습니다. 첫 리뷰를 남겨보세요!</p>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import axios from 'axios';
import { getCafe, deleteCafe, updateCafe } from '@/api/cafe';
import { deleteReview, getReviewsByCafe, updateReview } from '@/api/review';
import { useUserStore } from '@/stores/userStore';

const userStore = useUserStore();
const route = useRoute();
const router = useRouter();
const cafe = ref(null);
const reviews = ref([]);

const fetchCafeDetail = async () => {
  try {
    const res = await getCafe(route.params.id);
    cafe.value = res.data;
  } catch (error) {
    console.error('데이터 로드 실패', error);
    alert('존재하지 않는 카페이거나 정보를 불러올 수 없습니다.');
    router.push('/cafes');
  }
};

const getCongestionLabel = (level) => {
  if (level === 'RELAXED') return '여유';
  if (level === 'NORMAL') return '보통';
  if (level === 'CROWDED') return '혼잡';
  return '알 수 없음';
};

const submitVote = async (level) => {
  // 1. localStorage 대신 'sessionStorage'에서 'user' 키로 저장된 데이터를 꺼냅니다.
  const sessionUser = sessionStorage.getItem('user');
  let token = null;

  if (sessionUser) {
    try {
      // Pinia persist 플러그인이 JSON 문자열로 저장하므로 파싱해서 token을 꺼냅니다.
      const parsed = JSON.parse(sessionUser);
      token = parsed.token;
    } catch (e) {
      console.error('세션 파싱 에러:', e);
    }
  }

  // 2. 만약 세션에 없다면 스토어 값에서 직접 꺼내옵니다. (안전하게 .value 방식 활용)
  if (!token && userStore.token) {
    token = userStore.token;
  }

  // 3. 최종 토큰 검증
  if (!token) {
    alert('로그인이 필요한 서비스입니다.');
    return;
  }

  try {
    const res = await axios.post(
      `http://localhost:8080/cafes/${route.params.id}/congestion-votes`,
      { congestionLevel: level },
      { headers: { Authorization: `Bearer ${token}` } },
    );

    alert(res.data);
    fetchCafeDetail();
  } catch (error) {
    console.error('투표 에러:', error);
    if (error.response?.status === 403) {
      alert('접근 권한이 없습니다. 백엔드 시큐리티 설정을 확인해 주세요!');
    } else if (error.response?.data) {
      alert(error.response.data);
    } else {
      alert('투표 중 오류가 발생했습니다.');
    }
  }
};

const goToEdit = () => {
  router.push(`/cafes/${route.params.id}/edit`);
};

const handleDelete = async () => {
  if (confirm('정말로 이 스팟을 삭제하시겠습니까?')) {
    try {
      await deleteCafe(route.params.id);
      alert('삭제되었습니다.');
      router.push('/cafes');
    } catch (error) {
      alert('삭제 실패');
    }
  }
};

const handleUpdateReview = (review) => {
  router.push(`/cafes/${route.params.id}/reviews/${review.id}/edit`);
};
const handleCreateReview = () => {
  router.push(`/cafes/${route.params.id}/reviews`);
};
const fetchReviews = async () => {
  try {
    const res = await getReviewsByCafe(route.params.id);
    reviews.value = res.data;
    console.log('userStore =', userStore);
    console.log('로그인 유저:', userStore.user);
    console.log('로그인 id:', userStore.user?.id);
    console.log('리뷰:', reviews.value);
    console.log('리뷰 작성자:', reviews.value[0]?.user);
    console.log('리뷰 작성자 id:', reviews.value[0]?.user?.id);
  } catch (error) {
    alert('리뷰 불러오기 실패');
  }
};
const handleDeleteReview = async (reviewId) => {
  if (!confirm('댓글을 삭제하시겠습니까?')) return;
  try {
    await deleteReview(route.params.id, reviewId);
    alert('댓글이 삭제되었습니다.');
    fetchReviews();
  } catch (error) {
    alert('댓글 삭제 실패');
  }
};

onMounted(() => {
  fetchCafeDetail();
  fetchReviews();
});
</script>

<style scoped>
/* 기존 CSS */
.detail-container {
  padding: 80px 20px;
  max-width: 1000px;
  margin: 0 auto;
}
.bg-glow {
  position: fixed;
  top: 0;
  right: 0;
  width: 500px;
  height: 500px;
  background: radial-gradient(
    circle,
    rgba(255, 157, 108, 0.1) 0%,
    transparent 70%
  );
  z-index: -1;
}
.hero-card {
  background: white;
  border-radius: 40px;
  padding: 60px;
  display: grid;
  grid-template-columns: 1.2fr 1fr;
  gap: 40px;
  box-shadow: 0 30px 60px rgba(61, 43, 31, 0.08);
}
.category {
  color: #bb4d39;
  font-weight: 800;
  font-size: 11px;
  letter-spacing: 3px;
}
.cafe-name {
  font-size: 42px;
  font-weight: 900;
  color: #3d2b1f;
  margin: 15px 0;
}
.address {
  color: #8c7b6e;
  margin-bottom: 20px;
  font-size: 17px;
}
.spec-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 35px;
}
.tag {
  background: #f3f0ec;
  padding: 10px 18px;
  border-radius: 12px;
  font-weight: 700;
  color: #5c4d43;
  font-size: 14px;
}

/* 💎 예뻐진 혼잡도 뱃지 디자인 */
.congestion-status {
  margin-bottom: 20px;
}
.badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  border-radius: 30px;
  font-size: 15px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.03);
}
.live-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  animation: pulse 1.5s infinite;
}
@keyframes pulse {
  0% {
    transform: scale(0.95);
    opacity: 1;
  }
  50% {
    transform: scale(1.3);
    opacity: 0.5;
  }
  100% {
    transform: scale(0.95);
    opacity: 1;
  }
}
.badge.relaxed {
  background: #f1f8f1;
  color: #2e7d32;
  border: 1px solid #c8e6c9;
}
.badge.relaxed .live-dot {
  background: #4caf50;
  box-shadow: 0 0 8px #4caf50;
}
.badge.normal {
  background: #fff8e1;
  color: #ef6c00;
  border: 1px solid #ffe082;
}
.badge.normal .live-dot {
  background: #ff9800;
  box-shadow: 0 0 8px #ff9800;
}
.badge.crowded {
  background: #ffebee;
  color: #c62828;
  border: 1px solid #ffcdd2;
}
.badge.crowded .live-dot {
  background: #f44336;
  box-shadow: 0 0 8px #f44336;
}

/* 💎 모던해진 투표 영역 디자인 */
.vote-area {
  background: linear-gradient(145deg, #ffffff, #fcfbfa);
  border: 1px solid #f3f0ec;
  padding: 25px;
  border-radius: 24px;
  margin-bottom: 35px;
  box-shadow: inset 0 2px 10px rgba(139, 123, 110, 0.02);
}
.vote-header {
  margin-bottom: 18px;
}
.vote-title {
  font-size: 16px;
  font-weight: 800;
  color: #3d2b1f;
  margin-bottom: 4px;
}
.vote-subtitle {
  font-size: 13px;
  color: #a39489;
}
.wave {
  display: inline-block;
  animation: wave 2.5s infinite;
  transform-origin: 70% 70%;
}
@keyframes wave {
  0% {
    transform: rotate(0deg);
  }
  10% {
    transform: rotate(14deg);
  }
  20% {
    transform: rotate(-8deg);
  }
  30% {
    transform: rotate(14deg);
  }
  40% {
    transform: rotate(-4deg);
  }
  50% {
    transform: rotate(10deg);
  }
  60% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(0deg);
  }
}

.vote-buttons {
  display: flex;
  gap: 12px;
}
.vote-btn {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  padding: 16px 10px;
  border: 2px solid transparent;
  border-radius: 18px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}
.vote-btn .icon {
  font-size: 22px;
}
.vote-btn .text {
  font-weight: 700;
  font-size: 14px;
}

.vote-btn.relaxed {
  background: #f9fbf9;
  color: #4caf50;
}
.vote-btn.relaxed:hover {
  background: #e8f5e9;
  border-color: #a5d6a7;
  transform: translateY(-4px);
  box-shadow: 0 8px 16px rgba(76, 175, 80, 0.12);
}
.vote-btn.normal {
  background: #fffdf9;
  color: #ef6c00;
}
.vote-btn.normal:hover {
  background: #fff3e0;
  border-color: #ffcc80;
  transform: translateY(-4px);
  box-shadow: 0 8px 16px rgba(255, 152, 0, 0.12);
}
.vote-btn.crowded {
  background: #fffafb;
  color: #c62828;
}
.vote-btn.crowded:hover {
  background: #ffebee;
  border-color: #ef9a9a;
  transform: translateY(-4px);
  box-shadow: 0 8px 16px rgba(244, 67, 54, 0.12);
}

/* 기존 관리자 버튼 및 리뷰 스타일 */
.admin-actions {
  display: flex;
  gap: 15px;
}
.edit-btn {
  background: #3d2b1f;
  color: white;
  border: none;
  padding: 14px 28px;
  border-radius: 15px;
  font-weight: 700;
  cursor: pointer;
  transition: 0.3s;
}
.edit-btn:hover {
  background: #bb4d39;
}
.delete-btn {
  background: none;
  border: 1px solid #e2ddd9;
  color: #a39489;
  padding: 14px 24px;
  border-radius: 15px;
  cursor: pointer;
}
.delete-btn:hover {
  color: #f44336;
  border-color: #f44336;
}
.cafe-placeholder {
  width: 100%;
  height: 100%;
  min-height: 300px;
  background: #f9f7f2;
  border-radius: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #bb4d39;
  font-weight: 900;
  font-size: 24px;
}

.review-section {
  margin-top: 60px;
}
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}
.section-header h3 {
  font-size: 24px;
  font-weight: 800;
  color: #3d2b1f;
}
.section-header h3 span {
  color: #bb4d39;
}
.review-add-btn {
  background: none;
  border: 2px solid #3d2b1f;
  padding: 8px 20px;
  border-radius: 12px;
  font-weight: 700;
  cursor: pointer;
  transition: 0.2s;
}
.review-add-btn:hover {
  background: #3d2b1f;
  color: white;
}
.empty-review {
  padding: 60px;
  text-align: center;
  background: rgba(255, 255, 255, 0.5);
  border-radius: 30px;
  border: 2px dashed #e2ddd9;
  color: #a39489;
}
.review-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}
.review-card {
  background: white;
  padding: 20px;
  border-radius: 20px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.02);
  border: 1px solid #f3f0ec;
}
.review-info {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
}
.user-name {
  font-weight: 700;
  color: #3d2b1f;
}
.rating {
  font-weight: 800;
  color: #bb4d39;
}
.content {
  color: #5c4d43;
  line-height: 1.5;
  margin-bottom: 10px;
}
.date {
  font-size: 12px;
  color: #a39489;
}
.review-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 10px;
  border-top: 1px solid #f3f0ec;
  padding-top: 10px;
}
.review-actions {
  display: flex;
  gap: 10px;
}
.action-edit,
.action-delete {
  background: none;
  border: none;
  font-size: 12px;
  font-weight: 600;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 6px;
  transition: 0.2s;
}
.action-edit {
  color: #8c7b6e;
}
.action-edit:hover {
  background: #f3f0ec;
  color: #3d2b1f;
}
.action-delete {
  color: #a39489;
}
.action-delete:hover {
  background: #fff0f0;
  color: #f44336;
}
</style>
