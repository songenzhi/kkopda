<template>
  <div class="create-container">
    <div class="bg-glow"></div>

    <div class="create-card">
      <div class="card-header">
        <div class="tag-wrapper">
          <span class="tag">NEW SPOT</span>
        </div>
        <h2 class="title">새로운 <span>플러그 스팟</span> 등록</h2>
        <p class="subtitle">지도 검색으로 카페 정보를 불러오세요.</p>
      </div>

      <div class="search-section">
        <div class="search-bar-wrapper">
          <input
            type="text"
            v-model="searchKeyword"
            placeholder="카페 이름을 검색하세요"
            @keyup.enter="searchPlaces"
            class="premium-input"
          />
          <button @click="searchPlaces" class="search-icon-btn">🔍</button>
        </div>

        <transition name="fade">
          <ul v-if="searchResults.length > 0" class="search-results-floating">
            <li
              v-for="(place, index) in searchResults"
              :key="index"
              @click="selectPlace(place)"
            >
              <div class="result-info">
                <span class="place-name">{{ place.place_name }}</span>
                <span class="place-addr">{{
                  place.road_address_name || place.address_name
                }}</span>
              </div>
              <span class="plus-icon">+</span>
            </li>
          </ul>
        </transition>
      </div>

      <form @submit.prevent="addCafe" class="create-form">
        <transition name="slide">
          <div class="selected-info-card" v-if="cafe.name">
            <div class="card-top">
              <div class="info-label">SELECTED PLACE</div>
              <button type="button" class="reset-btn" @click="resetSelection">
                변경
              </button>
            </div>
            <div class="selected-content">
              <strong>{{ cafe.name }}</strong>
              <p>📍 {{ cafe.address }}</p>
            </div>
          </div>
        </transition>

        <div class="options-section">
          <label class="section-label">제공 서비스</label>
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
          <button
            type="submit"
            class="premium-submit-btn"
            :disabled="!cafe.name || !cafe.kakaoId"
          >
            등록하기
          </button>
          <button type="button" class="text-cancel-btn" @click="router.back()">
            나중에 할래요
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { checkCafeExists, createCafe } from '@/api/cafe';

const router = useRouter();
const route = useRoute();
const searchKeyword = ref('');
const searchResults = ref([]);

// 🎯 수정 포인트 1: 전송할 데이터 폼에 백엔드가 원하는 latitude, longitude 자리 추가
const cafe = ref({
  name: route.query.name || '', // 👈 주소창에 name이 있으면 할당
  address: route.query.address || '', // 👈 주소창에 address가 있으면 할당
  latitude: parseFloat(route.query.lat) || 0, // 👈 주소창에 lat이 있으면 실수로 변환 후 할당
  longitude: parseFloat(route.query.lng) || 0, // 👈 주소창에 lng이 있으면 실수로 변환 후 할당
  hasOutlet: false,
  wifi: false,
  parking: false,
  kakaoId: route.query.kakaoId || '',
});

const closeSearch = (e) => {
  if (!e.target.closest('.search-section')) {
    searchResults.value = [];
  }
};

const resetSelection = () => {
  cafe.value.name = '';
  cafe.value.address = '';
  cafe.value.latitude = 0;
  cafe.value.longitude = 0;
  cafe.value.kakaoId = '';
  searchKeyword.value = '';
};

onMounted(() => {
  window.addEventListener('click', closeSearch);
  // ⭐ 추가: 만약 지도에서 이미 카페 이름이 넘겨져 왔다면 검색창 텍스트 필드도 채워줍니다.
  if (route.query.name) {
    searchKeyword.value = route.query.name;
  }
});

onUnmounted(() => {
  window.removeEventListener('click', closeSearch);
});

const searchPlaces = () => {
  if (!searchKeyword.value.trim()) return;

  if (!window.kakao || !window.kakao.maps) {
    alert('카카오 지도 SDK를 불러오지 못했습니다.');
    return;
  }

  window.kakao.maps.load(() => {
    const ps = new window.kakao.maps.services.Places();

    ps.keywordSearch(
      searchKeyword.value,
      (data, status) => {
        if (status === window.kakao.maps.services.Status.OK) {
          searchResults.value = data;
        } else if (status === window.kakao.maps.services.Status.ZERO_RESULT) {
          alert('카페 검색 결과가 없습니다.');
          searchResults.value = [];
        } else {
          alert('검색 서비스에 문제가 발생했습니다.');
        }
      },
      {
        category_group_code: 'CE7', // ☕ 카페만
      },
    );
  });
};

const selectPlace = async (place) => {
  try {
    const res = await checkCafeExists(place.id);

    if (res.status === 200 && res.data && res.data.id) {
      alert(
        '이미 우리 리스트에 등록된 카페입니다! 상세 페이지로 이동할게요. ✨',
      );
      router.push(`/cafes/${res.data.id}`);
      return;
    }

    // 🎯 수정 포인트 2: 카카오 검색 결과(place)에서 위경도를 꺼내 폼에 저장
    cafe.value.name = place.place_name;
    cafe.value.address = place.road_address_name || place.address_name;
    cafe.value.kakaoId = place.id;
    cafe.value.latitude = parseFloat(place.y); // 위도 추가 (카카오 API는 y가 위도)
    cafe.value.longitude = parseFloat(place.x); // 경도 추가 (카카오 API는 x가 경도)

    searchResults.value = [];
    searchKeyword.value = place.place_name;
  } catch (error) {
    console.error('중복 체크 중 에러 발생:', error);

    cafe.value.name = place.place_name;
    cafe.value.address = place.road_address_name || place.address_name;
    cafe.value.kakaoId = place.id;
    cafe.value.latitude = parseFloat(place.y); // 에러 났을 때도 위도 챙기기
    cafe.value.longitude = parseFloat(place.x); // 에러 났을 때도 경도 챙기기

    searchResults.value = [];
  }
};

const addCafe = async () => {
  try {
    const res = await createCafe(cafe.value);
    const newId = res.data.id;

    if (newId) {
      alert('성공적으로 등록되었습니다! ✨');
      router.push(`/cafes/${newId}`);
    }
  } catch (error) {
    console.error('등록 실패 사유:', error.response);

    const errorMessage = error.response?.data?.message;

    if (errorMessage === 'ALREADY_EXISTS') {
      alert('이미 우리 리스트에 등록된 카페입니다! ❌');
    } else {
      // 진짜 서버 에러(DB 다운, 코드 에러 등)일 때는 다른 알림을 띄워줍니다.
      alert(
        '등록 중 알 수 없는 서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.',
      );
    }
  }
};
</script>
<style scoped>
/* 배경 글로우 효과 */
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

/* 카드 메인 디자인 */
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

.card-header {
  text-align: center;
  margin-bottom: 35px;
}

.tag-wrapper {
  margin-bottom: 12px;
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
  margin-bottom: 8px;
}

.title span {
  color: #bb4d39;
}

.subtitle {
  color: #a39489;
  font-size: 14px;
}

/* 프리미엄 검색바 */
.search-section {
  position: relative;
  margin-bottom: 25px;
}

.search-bar-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.premium-input {
  width: 100%;
  padding: 18px 25px;
  border-radius: 20px;
  border: 1px solid #f3efec;
  background: #f9f7f5;
  font-size: 15px;
  transition: all 0.3s;
}

.premium-input:focus {
  outline: none;
  border-color: #ff9d6c;
  background: white;
  box-shadow: 0 10px 20px rgba(255, 157, 108, 0.1);
}

.search-icon-btn {
  position: absolute;
  right: 15px;
  background: none;
  border: none;
  cursor: pointer;
  font-size: 18px;
}

/* 결과 리스트 플로팅 디자인 */
.search-results-floating {
  position: absolute;
  top: 70px;
  left: 0;
  right: 0;
  background: white;
  border-radius: 24px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
  z-index: 100;
  max-height: 280px;
  overflow-y: auto;
  border: 1px solid #f3f0ec;
  padding: 10px;
}

.search-results-floating li {
  padding: 15px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-radius: 16px;
  cursor: pointer;
  transition: background 0.2s;
}

.search-results-floating li:hover {
  background: #fcf6f2;
}

.result-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.place-name {
  font-weight: 800;
  font-size: 14px;
  color: #3d2b1f;
}

.place-addr {
  font-size: 12px;
  color: #a39489;
}

.plus-icon {
  color: #bb4d39;
  font-weight: bold;
  font-size: 20px;
}

/* 선택된 정보 카드 */
.selected-info-card {
  background: linear-gradient(135deg, #3d2b1f 0%, #5c4d43 100%);
  padding: 20px;
  border-radius: 24px;
  margin-bottom: 30px;
  color: white;
}

.info-label {
  font-size: 9px;
  font-weight: 800;
  letter-spacing: 1px;
  opacity: 0.6;
  margin-bottom: 8px;
}

.selected-content strong {
  display: block;
  font-size: 18px;
  margin-bottom: 4px;
}

.selected-content p {
  font-size: 12px;
  opacity: 0.8;
}

/* 체크박스 커스텀 디자인 */
.section-label {
  display: block;
  font-size: 13px;
  font-weight: 800;
  color: #3d2b1f;
  margin-bottom: 15px;
}

.checkbox-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
  margin-bottom: 35px;
}

.premium-check {
  cursor: pointer;
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
}

.icon {
  font-size: 20px;
}
.text {
  font-size: 12px;
  font-weight: 700;
  color: #8c7b6e;
}

.premium-check.active .check-content {
  background: #fffcfb;
  border-color: #ff9d6c;
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(255, 157, 108, 0.15);
}

.premium-check.active .text {
  color: #bb4d39;
}

/* 하단 버튼 */
.footer-btns {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.premium-submit-btn {
  width: 100%;
  padding: 20px;
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

.premium-submit-btn:hover:not(:disabled) {
  transform: scale(1.02);
  box-shadow: 0 15px 30px rgba(187, 77, 57, 0.35);
}

.premium-submit-btn:disabled {
  background: #e0dcd9;
  box-shadow: none;
  cursor: not-allowed;
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

/* 애니메이션 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.slide-enter-active {
  transition: all 0.4s ease-out;
}
.slide-enter-from {
  opacity: 0;
  transform: translateY(20px);
}
/* 선택 카드 상단 정렬 */
.card-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.reset-btn {
  background: rgba(255, 255, 255, 0.2);
  border: none;
  color: white;
  font-size: 10px;
  padding: 4px 8px;
  border-radius: 6px;
  cursor: pointer;
}

.reset-btn:hover {
  background: rgba(255, 255, 255, 0.3);
}

/* 검색 결과창 스크롤바 예쁘게 */
.search-results-floating::-webkit-scrollbar {
  width: 6px;
}
.search-results-floating::-webkit-scrollbar-thumb {
  background-color: #e0dcd9;
  border-radius: 10px;
}
.search-results-floating::-webkit-scrollbar-track {
  background: transparent;
}

/* 검색 중일 때 인풋 스타일 변경 (선택사항) */
.premium-input:disabled {
  opacity: 0.6;
  cursor: wait;
}
</style>
