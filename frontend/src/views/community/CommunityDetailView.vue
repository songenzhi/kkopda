<template>
  <div class="detail-page">
    <div class="detail-container">
      <div class="detail-header">
        <div class="category">커뮤니티</div>
        <h2 class="title">{{ post.title }}</h2>
        <div class="post-info">
          <span class="author">{{ post.userNickname || '익명' }}</span>
          <span class="divider">|</span>
          <span class="date">{{ formatDate(post.createdAt) }}</span>
        </div>
      </div>

      <div class="detail-content">
        <div v-if="post.imageUrl" class="image-wrapper">
          <img
            :src="'http://localhost:8080' + post.imageUrl"
            alt="첨부 이미지"
            class="attached-image" />
        </div>

        <div class="text-content">
          {{ post.description }}
        </div>
      </div>

      <div class="actions">
        <button @click="$router.push('/community')" class="list-btn">
          목록으로
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { getCommunity, deleteCommunity } from '@/api/community';
import { useUserStore } from '@/stores/userStore';
import { onMounted, ref, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();

const post = ref({});
const postId = route.params.id; // URL에서 게시글 ID 추출

// 내가 쓴 글인지 확인 (수정/삭제 버튼 노출용)
// const isAuthor = computed(() => {
//   if (!userStore.user || !userStore.user.id) {
//     return false;
//   }
//   return userStore.user?.id === post.value.user?.id;
// });

// 데이터 조회
const fetchPost = async () => {
  try {
    const res = await getCommunity(postId); // ID를 인자로 넘겨줌
    post.value = res.data;
  } catch (error) {
    console.error(error);
    alert('게시글을 불러오지 못했습니다.');
    router.push('/community');
  }
};

// 삭제 로직
const handleDelete = async () => {
  if (!confirm('정말 삭제하시겠습니까?')) return;
  try {
    await deleteCommunity(postId);
    alert('삭제되었습니다.');
    router.push('/community');
  } catch (error) {
    alert('삭제 실패');
  }
};

// 수정 페이지 이동
const goToEdit = () => {
  router.push(`/community/edit/${postId}`);
};

// 날짜 포맷
const formatDate = (dateString) => {
  if (!dateString) return '';
  return new Date(dateString).toLocaleDateString();
};

onMounted(() => {
  fetchPost();
});
</script>

<style scoped>
.detail-page {
  padding: 60px 20px;
}

.detail-container {
  max-width: 800px;
  margin: 0 auto;
  background: white;
  border-radius: 20px;
  padding: 40px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.05);
  border: 1px solid #f3f0ec;
}

.detail-header {
  border-bottom: 2px solid #f3f0ec;
  padding-bottom: 20px;
  margin-bottom: 30px;
}

.category {
  color: #8c7b6e;
  font-size: 0.9rem;
  font-weight: 700;
  margin-bottom: 10px;
}

.title {
  color: #3d2b1f;
  font-size: 2rem;
  font-weight: 800;
  margin-bottom: 15px;
}

.post-info {
  color: #a69689;
  font-size: 0.95rem;
}

.divider {
  margin: 0 10px;
  color: #e2ddd9;
}

.detail-content {
  min-height: 300px;
  line-height: 1.8;
  color: #5d4a3e;
  font-size: 1.1rem;
  white-space: pre-wrap; /* 줄바꿈 유지 */
}

.actions {
  margin-top: 50px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.list-btn {
  background: #f3f0ec;
  color: #8c7b6e;
  border: none;
  padding: 12px 20px;
  border-radius: 12px;
  font-weight: 700;
  cursor: pointer;
}

.edit-group {
  display: flex;
  gap: 10px;
}

.edit-btn {
  background: #3d2b1f;
  color: white;
  border: none;
  padding: 12px 20px;
  border-radius: 12px;
  font-weight: 700;
  cursor: pointer;
}

.delete-btn {
  background: #fff0f0;
  color: #e57373;
  border: none;
  padding: 12px 20px;
  border-radius: 12px;
  font-weight: 700;
  cursor: pointer;
}

/* 기존 스타일은 그대로 두고, 하단에 추가해 줘 */

.image-wrapper {
  margin-bottom: 25px; /* 사진과 텍스트 사이 간격 */
  text-align: center; /* 사진 중앙 정렬 */
}

.attached-image {
  max-width: 100%; /* 이미지가 튀어나가지 않게 */
  max-height: 500px; /* 너무 세로로 긴 사진 방지 */
  border-radius: 12px; /* 모서리 둥글게 */
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05); /* 약간의 그림자 */
  object-fit: contain; /* 비율 유지하며 알맞게 들어가기 */
}

.text-content {
  white-space: pre-wrap; /* 줄바꿈 유지 */
}
</style>
