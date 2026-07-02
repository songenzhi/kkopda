<template>
  <div class="community-container">
    <div class="header-section">
      <h2>커뮤니티 게시판</h2>
      <button @click="$router.push('/community/create')" class="write-btn">
        글쓰기
      </button>
    </div>

    <div class="table-card">
      <table class="community-table">
        <thead>
          <tr>
            <th width="10%">번호</th>
            <th width="40%">제목</th>
            <th width="15%">작성자</th>
            <th width="15%">작성일</th>
            <th width="20%">관리</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(post, index) in communityPage.content" :key="post.id">
            <td>{{ (currentPage - 1) * pageSize + index + 1 }}</td>

            <td class="title-cell" @click="goToDetail(post.id)">
              {{ post.title }}
            </td>
            <td class="author-cell">{{ post.userNickname || '익명' }}</td>
            <td class="date-cell">{{ formatDate(post.createdAt) }}</td>

            <td class="manage-cell">
              <div class="btn-group">
                <button
                  @click.stop="goToEdit(post.id)"
                  class="edit-sm-btn"
                  v-if="isAuthor(post)">
                  수정
                </button>
                <button
                  @click.stop="handleToDelete(post.id)"
                  class="delete-sm-btn"
                  v-if="isAuthor(post)">
                  삭제
                </button>
              </div>
            </td>
          </tr>

          <tr
            v-if="!communityPage.content || communityPage.content.length === 0">
            <td colspan="5" class="no-data" align="center">
              등록된 게시글이 없습니다.
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div class="pagination-container" v-if="communityPage.totalPages > 0">
      <button
        class="page-btn"
        :disabled="currentPage === 1"
        @click="changePage(currentPage - 1)">
        이전
      </button>

      <button
        v-for="page in communityPage.totalPages"
        :key="page"
        class="page-number-btn"
        :class="{ active: currentPage === page }"
        @click="changePage(page)">
        {{ page }}
      </button>

      <button
        class="page-btn"
        :disabled="currentPage === communityPage.totalPages"
        @click="changePage(currentPage + 1)">
        다음
      </button>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import api from '@/api/axios';
import { deleteCommunity } from '@/api/community';
import { useUserStore } from '@/stores/userStore';

const router = useRouter();
const userStore = useUserStore();

const communityPage = ref({ content: [], totalPages: 0 });
const currentPage = ref(1);
const pageSize = ref(10);

const isAuthor = (post) => {
  return Number(userStore.id) === Number(post.userId);
};

// 🎯 2. 스프링 부트 3.x의 표준 페이징 응답 규격을 100% 흡수하는 매핑 로직으로 변경
const fetchCommunityData = async () => {
  try {
    const res = await api.get('/community', {
      params: {
        page: currentPage.value,
        size: pageSize.value,
      },
    });

    // 🎯 백엔드에서 PagedModel을 쓰면 알맹이 데이터가 무조건 'content'라는 이름으로 와!
    if (res.data) {
      communityPage.value = {
        content: res.data.content || [], // 💡 글 리스트 10개 쏙 꺼내기
        totalPages: res.data.page?.totalPages || 0, // 💡 전체 페이지 수 쏙 꺼내기
      };
    }
  } catch (error) {
    console.error(error);
    alert('정보 조회 실패');
  }
};

const changePage = (page) => {
  if (page < 1 || page > communityPage.value.totalPages) return;
  currentPage.value = page;
  fetchCommunityData();
};

const goToDetail = (id) => {
  router.push(`/community/${id}`);
};

const formatDate = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return date.toLocaleDateString();
};

const goToEdit = (id) => {
  router.push(`/community/edit/${id}`);
};

const handleToDelete = async (id) => {
  if (!confirm('정말 이 게시글을 삭제하시겠습니까?')) return;

  try {
    await deleteCommunity(id, userStore.id);
    alert('삭제되었습니다.');
    fetchCommunityData();
  } catch (error) {
    alert('삭제 실패');
  }
};

onMounted(() => {
  fetchCommunityData();
});
</script>

<style scoped>
.community-container {
  max-width: 1000px;
  margin: 50px auto;
  padding: 0 20px;
}

.header-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

h2 {
  color: #3d2b1f;
  margin: 0;
  font-size: 1.8rem;
  font-weight: 800;
}

.table-card {
  background: white;
  border-radius: 20px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.05);
  overflow: hidden;
  border: 1px solid #f3f0ec;
}

.community-table {
  width: 100%;
  border-collapse: collapse;
}

.community-table th {
  background-color: #fcfaf8;
  color: #8c7b6e;
  font-weight: 700;
  padding: 18px 12px;
  border-bottom: 2px solid #f3f0ec;
  font-size: 0.95rem;
}

.community-table td {
  padding: 16px 12px;
  border-bottom: 1px solid #f3f0ec;
  color: #5d4a3e;
  font-size: 0.95rem;
  vertical-align: middle;
}

.title-cell {
  text-align: left;
  font-weight: 600;
  color: #3d2b1f;
  cursor: pointer;
}

.title-cell:hover {
  text-decoration: underline;
}

.manage-cell {
  text-align: center;
}

.btn-group {
  display: flex;
  gap: 8px;
  justify-content: center;
}

.edit-sm-btn,
.delete-sm-btn {
  padding: 6px 12px;
  border-radius: 8px;
  font-size: 0.8rem;
  font-weight: 700;
  cursor: pointer;
  border: none;
  transition: all 0.2s;
}

.edit-sm-btn {
  background: #f3f0ec;
  color: #8c7b6e;
}

.edit-sm-btn:hover {
  background: #e2ddd9;
  color: #3d2b1f;
}

.delete-sm-btn {
  background: #fff0f0;
  color: #e57373;
}

.delete-sm-btn:hover {
  background: #ffe5e5;
  color: #d32f2f;
}

.write-btn {
  background: #3d2b1f;
  color: white;
  border: none;
  padding: 12px 25px;
  border-radius: 12px;
  font-weight: 700;
  cursor: pointer;
  transition:
    transform 0.2s,
    opacity 0.2s;
}

.write-btn:hover {
  opacity: 0.9;
  transform: translateY(-2px);
}

.no-data {
  padding: 80px 0;
  color: #c2b9b0;
  font-weight: 500;
}

.pagination-container {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 30px;
  gap: 6px;
}

.page-btn,
.page-number-btn {
  padding: 8px 14px;
  border-radius: 8px;
  font-size: 0.85rem;
  font-weight: 700;
  cursor: pointer;
  border: 1px solid #f3f0ec;
  background: white;
  color: #8c7b6e;
  transition: all 0.2s;
}

.page-btn:hover:not(:disabled),
.page-number-btn:hover:not(.active) {
  background: #fcfaf8;
  color: #3d2b1f;
  border-color: #e2ddd9;
}

.page-btn:disabled {
  background: #fcfaf8;
  color: #c2b9b0;
  cursor: not-allowed;
  border-color: #f3f0ec;
}

.page-number-btn.active {
  background: #3d2b1f;
  color: white;
  border-color: #3d2b1f;
}
</style>
