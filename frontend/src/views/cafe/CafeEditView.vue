<template>
  <div class="create-container">
    <div class="bg-glow"></div>

    <div class="create-card">
      <div class="card-header">
        <div class="tag-wrapper">
          <span class="tag">EDIT SPOT</span>
        </div>
        <h2 class="title">카페 정보 <span>수정하기</span></h2>
        <p class="subtitle">변경된 정보를 최신 상태로 업데이트 해주세요.</p>
      </div>

      <form v-if="cafe.name" @submit.prevent="handleUpdate" class="create-form">
        <div class="input-section">
          <label class="section-label">카페 이름</label>
          <div class="input-wrapper">
            <input
              v-model="cafe.name"
              type="text"
              placeholder="카페 이름을 입력하세요"
              required
              class="premium-input"
              disabled="true" />
          </div>
        </div>

        <div class="input-section">
          <label class="section-label">상세 주소</label>
          <div class="input-wrapper">
            <input
              v-model="cafe.address"
              type="text"
              placeholder="📍 위치 정보를 입력해주세요"
              required
              class="premium-input"
              disabled="true" />
          </div>
        </div>

        <div class="options-section">
          <label class="section-label">제공 서비스 수정</label>
          <div class="checkbox-grid">
            <label class="premium-check" :class="{ active: cafe.hasOutlet }">
              <input type="checkbox" v-model="cafe.hasOutlet" />
              <div class="check-content">
                <span class="icon">🔌</span>
                <span class="text">콘센트</span>
              </div>
            </label>
            <label class="premium-check" :class="{ active: cafe.wifi }">
              <input type="checkbox" v-model="cafe.wifi" />
              <div class="check-content">
                <span class="icon">📡</span>
                <span class="text">와이파이</span>
              </div>
            </label>
            <label class="premium-check" :class="{ active: cafe.parking }">
              <input type="checkbox" v-model="cafe.parking" />
              <div class="check-content">
                <span class="icon">🚗</span>
                <span class="text">주차장</span>
              </div>
            </label>
          </div>
        </div>

        <div class="footer-btns">
          <button type="submit" class="premium-submit-btn">
            수정 완료하기
          </button>
          <button type="button" class="text-cancel-btn" @click="router.back()">
            변경 취소
          </button>
        </div>
      </form>

      <div v-else class="loading-container">
        <div class="loader"></div>
        <p>카페 정보를 불러오고 있습니다...</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { getCafe, updateCafe } from '@/api/cafe';

const route = useRoute();
const router = useRouter();

// 초기값 설정
const cafe = ref({
  name: '',
  address: '',
  hasOutlet: false,
  wifi: false,
  parking: false,
});

// 1. 페이지가 열리면 기존 데이터를 가져와서 폼에 채웁니다.
const fetchCafeData = async () => {
  try {
    const res = await getCafe(route.params.id);
    cafe.value = res.data;
  } catch (error) {
    console.error(error);
    alert('정보를 불러오지 못했습니다.');
    router.back();
  }
};

// 2. 수정 버튼 클릭 시 실행
const handleUpdate = async () => {
  try {
    const updateData = {
      name: cafe.value.name,
      address: cafe.value.address,
      hasOutlet: cafe.value.hasOutlet,
      wifi: cafe.value.wifi,
      parking: cafe.value.parking,
    };

    // 1. 백엔드 서버에 수정 요청을 보냄
    await updateCafe(route.params.id, updateData);

    // 2. 사용자에게 성공 알림 제공
    alert('카페 정보가 성공적으로 수정되었습니다.');

    // 3. 수정 완료 후 해당 카페의 상세 페이지로 자동 이동!
    router.push(`/cafes/${route.params.id}`);
  } catch (error) {
    console.error('수정 실패:', error);
    alert('정보 수정에 실패했습니다. 다시 시도해 주세요.');
  }
};

onMounted(() => {
  fetchCafeData();
});
</script>

<style scoped>
/* 1. 기본 레이아웃 (등록 페이지와 동일) */
.create-container {
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #fcfaf7;
  padding: 40px 20px;
  overflow: hidden;
}

.bg-glow {
  position: absolute;
  top: -10%;
  right: -10%;
  width: 500px;
  height: 500px;
  background: radial-gradient(
    circle,
    rgba(187, 77, 57, 0.08) 0%,
    transparent 70%
  );
  z-index: 0;
}

.create-card {
  position: relative;
  z-index: 1;
  width: 100%;
  max-width: 460px;
  background: #ffffff;
  padding: 40px;
  border-radius: 40px;
  box-shadow: 0 30px 60px rgba(61, 43, 31, 0.12);
}

/* 2. 헤더 스타일 */
.card-header {
  text-align: center;
  margin-bottom: 35px;
}
.tag {
  background: #fff0ed;
  color: #bb4d39;
  font-size: 10px;
  font-weight: 900;
  letter-spacing: 2px;
  padding: 5px 12px;
  border-radius: 100px;
}
.title {
  font-size: 26px;
  color: #3d2b1f;
  font-weight: 900;
  margin-top: 12px;
}
.title span {
  color: #bb4d39;
}
.subtitle {
  color: #a39489;
  font-size: 14px;
  margin-top: 8px;
}

/* 3. 입력 필드 스타일 */
.input-section {
  margin-bottom: 25px;
}
.section-label {
  display: block;
  font-size: 13px;
  font-weight: 800;
  color: #3d2b1f;
  margin-bottom: 10px;
  padding-left: 5px;
}
.premium-input {
  width: 100%; /* 부모 너비를 꽉 채우되 */
  box-sizing: border-box; /* 중요! 패딩과 테두리를 너비 안에 포함시킵니다 */

  padding: 18px 25px; /* 안쪽 여백 */
  border-radius: 20px;
  border: 1px solid #f3efec;
  background: #f9f7f5;
  font-size: 15px;
  transition: all 0.3s;

  /* 혹시 모를 브라우저 기본 스타일 제거 */
  display: block;
  outline: none;
}
.premium-input:focus {
  outline: none;
  border-color: #ff9d6c;
  background: white;
  box-shadow: 0 10px 20px rgba(255, 157, 108, 0.08);
}

/* 4. 서비스 체크박스 (등록 페이지 스타일 계승) */
.checkbox-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
  margin-bottom: 35px;
}
.premium-check input {
  display: none;
}
.check-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 15px 10px;
  background: #f9f7f5;
  border-radius: 20px;
  border: 2px solid transparent;
  transition: all 0.3s;
  cursor: pointer;
}
.premium-check.active .check-content {
  background: #fffcfb;
  border-color: #ff9d6c;
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(255, 157, 108, 0.15);
}
.icon {
  font-size: 20px;
}
.text {
  font-size: 12px;
  font-weight: 700;
  color: #8c7b6e;
}
.premium-check.active .text {
  color: #bb4d39;
}

/* 5. 버튼 그룹 */
.footer-btns {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.premium-submit-btn {
  width: 100%;
  padding: 18px;
  border-radius: 20px;
  border: none;
  background: linear-gradient(90deg, #bb4d39, #ff9d6c);
  color: white;
  font-size: 16px;
  font-weight: 900;
  cursor: pointer;
  box-shadow: 0 10px 25px rgba(187, 77, 57, 0.25);
  transition: all 0.3s;
}
.premium-submit-btn:hover {
  transform: scale(1.02);
}
.text-cancel-btn {
  background: none;
  border: none;
  color: #a39489;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  text-decoration: underline;
  text-underline-offset: 4px;
}

/* 6. 로딩 애니메이션 */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 40px 0;
  color: #a39489;
}
.loader {
  width: 30px;
  height: 30px;
  border: 3px solid #f3f0ec;
  border-top: 3px solid #bb4d39;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 15px;
}
.input-wrapper {
  width: 100%; /* 인풋을 감싸는 박스도 가득 차게 */
  display: block; /* flex가 걸려있다면 정렬이 꼬일 수 있으니 block으로 */
}
@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}
</style>
