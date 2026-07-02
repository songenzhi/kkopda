<template>
  <div class="mypage-container">
    <div class="bg-gradient"></div>

    <div class="profile-card">
      <div class="profile-header">
        <div class="avatar-wrapper">
          <div class="avatar-glow"></div>
          <div class="avatar">
            <span>{{ user.nickname?.charAt(0) || '👤' }}</span>
          </div>
        </div>

        <div class="title-section">
          <span class="eng-title">MY PROFILE</span>
          <h2 class="title">나의 <span>꼽다</span> 기록</h2>
          <div class="title-underline"></div>
          <p class="subtitle">당신의 소중한 몰입의 순간들을 관리하세요.</p>
        </div>
      </div>

      <div v-if="!isEdit" class="info-list">
        <div class="info-item">
          <label>닉네임</label>
          <div class="value">{{ user.nickname }}</div>
        </div>
        <div class="info-item">
          <label>이메일</label>
          <div class="value">{{ user.email }}</div>
        </div>
      </div>

      <div v-else class="info-list edit-mode">
        <div class="info-item">
          <label>닉네임 수정</label>
          <input v-model="editUser.nickname" placeholder="새 닉네임" />
        </div>
        <div class="info-item">
          <label>이메일 수정</label>
          <input v-model="editUser.email" disabled="true" />
        </div>
        <div class="info-item">
          <label>비밀번호 수정</label>
          <input
            type="password"
            v-model="editUser.password"
            placeholder="새 비밀번호" />
        </div>
      </div>

      <div class="btn-group">
        <button v-if="!isEdit" class="edit-btn" @click="startEdit">
          내 정보 수정
        </button>
        <button v-else class="save-btn" @click="handleUpdate">저장하기</button>

        <button class="delete-btn" @click="handleDelete">회원 탈퇴</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '@/stores/userStore';
import { getMyInfo, updateUser, deleteUser } from '@/api/user';

const router = useRouter();
const userStore = useUserStore();

// id의 초기값을 null로 설정
const user = ref({
  id: null,
  name: '',
  email: '',
});
const editUser = ref({});
const isEdit = ref(false);

onMounted(async () => {
  // 1. 유저 ID 가져오기 (수정됨: userStore.user.id -> userStore.id)
  // sessionStorage에서 직접 가져오거나 스토어에서 가져옵니다.
  const userId = userStore.id || sessionStorage.getItem('id');

  console.log('확인된 userId:', userId);

  // 2. userId가 정말 없는 경우 처리
  if (!userId) {
    alert('로그인 정보가 없습니다. 다시 로그인해주세요.');
    router.push('/login');
    return;
  }

  // 3. 데이터 요청
  try {
    const res = await getMyInfo(userId);
    user.value = res.data;
    editUser.value = { ...res.data };
  } catch (error) {
    console.error('데이터 가져오기 실패:', error);
    // 403 에러가 뜬다면 여기서 상세하게 확인 가능합니다.
    if (error.response && error.response.status === 403) {
      alert('권한이 없습니다. 다시 로그인해주세요.');
      userStore.logout(); // 권한이 없으면 스토어 비우기
      router.push('/login');
    }
  }
});

const startEdit = () => {
  isEdit.value = true;
  editUser.value = { ...user.value };
};

const handleUpdate = async () => {
  try {
    // 🚨 userStore.id 대신 user.value.id (또는 editUser.value.id) 사용!
    const res = await updateUser(user.value.id, editUser.value);
    user.value = res.data;
    isEdit.value = false;
    alert('정보가 성공적으로 수정되었습니다. ✨');
  } catch (error) {
    alert('수정에 실패했습니다.');
  }
};

const handleDelete = async () => {
  if (confirm('정말로 탈퇴하시겠습니까? 데이터는 복구되지 않습니다.')) {
    try {
      // 🚨 userStore.id 대신 user.value.id 사용!
      await deleteUser(user.value.id);

      userStore.logout();
      sessionStorage.clear(); // 탈퇴니까 지갑도 싹 비워줍니다.
      router.push('/login');
      alert('탈퇴가 완료되었습니다. 다음에 다시 만나요! 🔌');
    } catch (error) {
      alert('탈퇴 처리 중 오류가 발생했습니다.');
    }
  }
};
</script>

<style scoped>
/* 배경 및 컨테이너 */
.mypage-container {
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  width: 100%;
  overflow: hidden;
  background-color: #f9f7f2;
}

.bg-gradient {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 80%;
  height: 80%;
  background: radial-gradient(
    circle,
    rgba(255, 157, 108, 0.2) 0%,
    rgba(187, 77, 57, 0.05) 70%
  );
  filter: blur(60px);
  z-index: 0;
}

/* 카드 디자인 */
.profile-card {
  position: relative;
  z-index: 1;
  width: 440px;
  padding: 50px 40px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(15px);
  border-radius: 32px;
  box-shadow: 0 25px 50px rgba(61, 43, 31, 0.1);
  text-align: center;
}

/* ✨ 헤더 섹션 스타일 최신화 */
.profile-header {
  margin-bottom: 40px;
}

.avatar-wrapper {
  position: relative;
  width: 90px;
  height: 90px;
  margin: 0 auto 20px;
}

.avatar-glow {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: #ff9d6c;
  filter: blur(20px);
  opacity: 0.3;
  border-radius: 50%;
}

.avatar {
  position: relative;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #3d2b1f 0%, #1a110a 100%);
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 36px;
  color: #ff9d6c;
  z-index: 2;
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
}

.eng-title {
  font-size: 11px;
  letter-spacing: 3px;
  color: #bb4d39;
  font-weight: 800;
  margin-bottom: 4px;
  display: block;
}

.title {
  font-size: 32px;
  font-weight: 900;
  color: #3d2b1f;
  margin: 0;
}

.title span {
  color: #bb4d39;
}

.title-underline {
  width: 40px;
  height: 4px;
  background: #ff9d6c;
  margin: 12px auto;
  border-radius: 2px;
}

.subtitle {
  font-size: 14px;
  color: #a39489;
  line-height: 1.5;
}

/* 정보 리스트 */
.info-list {
  text-align: left;
  margin-bottom: 30px;
}

.info-item {
  margin-bottom: 20px;
}

.info-item input:disabled {
  background-color: #f5f5f5; /* 1. 연한 회색 배경으로 변경 */
  color: #999999; /* 2. 글자 색상을 흐리게 변경 */
  cursor: not-allowed; /* 3. 마우스 올렸을 때 금지(🚫) 아이콘 표시 */
}

.info-item label {
  display: block;
  font-size: 12px;
  font-weight: 700;
  color: #bb4d39;
  text-transform: uppercase;
  margin-bottom: 6px;
  padding-left: 4px;
}

.info-item .value {
  padding: 14px;
  background-color: #f3f0ec;
  border-radius: 12px;
  color: #3d2b1f;
  font-weight: 600;
}

.info-item input {
  width: 100%;
  padding: 14px;
  background-color: #fff;
  border: 2px solid #f3f0ec;
  border-radius: 12px;
  font-size: 15px;
  transition: all 0.3s ease;
  box-sizing: border-box;
}

.info-item input:focus {
  outline: none;
  border-color: #ff9d6c;
}

/* 버튼 그룹 */
.btn-group {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

button {
  width: 100%;
  padding: 16px;
  border-radius: 14px;
  font-size: 16px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.2s ease;
  border: none;
}

.edit-btn {
  background-color: #3d2b1f;
  color: white;
}

.edit-btn:hover {
  background-color: #2a1d15;
  transform: translateY(-2px);
}

.save-btn {
  background: linear-gradient(90deg, #bb4d39, #ff9d6c);
  color: white;
  box-shadow: 0 8px 20px rgba(187, 77, 57, 0.2);
}

.delete-btn {
  background-color: transparent;
  color: #a39489;
  font-size: 13px;
  text-decoration: underline;
  padding: 8px;
}

.delete-btn:hover {
  color: #bb4d39;
}
</style>
