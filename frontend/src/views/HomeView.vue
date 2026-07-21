<template>
  <div class="cafe-map-page">
    <header class="list-hero">
      <div class="hero-content">
        <span class="tag">PREMIUM PLUG SPOTS</span>
        <h2 class="title">최적의 <span>지도 탐색</span></h2>
        <div class="search-container">
          <div class="search-bar">
            <input
              type="text"
              v-model="searchQuery"
              placeholder="카페 이름을 검색하세요"
              @keyup.enter="searchPlaces"
            />
            <button class="search-btn" @click="searchPlaces">🔍 검색</button>
          </div>
        </div>
      </div>
    </header>

    <main class="map-container-wrapper">
      <div class="map-card">
        <div id="map" ref="mapContainer" class="kakao-map"></div>

        <transition name="slide-up">
          <div v-if="selectedPlace" class="place-info-card">
            <h3 class="place-name">{{ selectedPlace.name }}</h3>
            <p class="place-addr">📍 {{ selectedPlace.address }}</p>
            <div class="card-actions">
              <!-- 🎯 버튼 텍스트와 클릭 이벤트를 길찾기로 변경 -->
              <button class="action-main-btn" @click="goToDirections">
                🗺️ 카카오맵 길찾기
              </button>
            </div>
          </div>
        </transition>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import api from '@/api/axios';

const router = useRouter();
const mapContainer = ref(null);
const searchQuery = ref('');
const selectedPlace = ref(null);

let map = null;
let markers = [];
let selectedMarker = null;

const initMap = () => {
  window.kakao.maps.load(() => {
    map = new window.kakao.maps.Map(mapContainer.value, {
      center: new window.kakao.maps.LatLng(37.5665, 126.978),
      level: 4,
    });

    window.kakao.maps.event.addListener(map, 'click', (mouseEvent) => {
      const latlng = mouseEvent.latLng;
      const lat = latlng.getLat();
      const lng = latlng.getLng();

      const ps = new window.kakao.maps.services.Places();

      ps.categorySearch(
        'CE7', // 카페
        (data, status) => {
          if (
            status !== window.kakao.maps.services.Status.OK ||
            data.length === 0
          ) {
            selectedPlace.value = null;

            if (selectedMarker) {
              selectedMarker.setMap(null);
              selectedMarker = null;
            }
            return;
          }

          const cafe = data[0];
          const markerPosition = new window.kakao.maps.LatLng(cafe.y, cafe.x);

          if (selectedMarker) {
            selectedMarker.setMap(null);
          }

          selectedMarker = new window.kakao.maps.Marker({
            position: markerPosition,
            map: map,
          });

          selectedPlace.value = {
            name: cafe.place_name,
            address: cafe.road_address_name || cafe.address_name,
            kakaoId: cafe.id,
            lat: Number(cafe.y),
            lng: Number(cafe.x),
          };
        },
        {
          location: latlng,
          radius: 30,
          sort: window.kakao.maps.services.SortBy.DISTANCE,
        },
      );
    });

    findMyPosition();
  });
};

const findMyPosition = () => {
  if (navigator.geolocation) {
    navigator.geolocation.getCurrentPosition(
      (position) => {
        const lat = position.coords.latitude;
        const lon = position.coords.longitude;
        map.panTo(new window.kakao.maps.LatLng(lat, lon));
        fetchNearbyCafes(lat, lon);
      },
      (error) => console.error('위치 에러:', error),
    );
  }
};

const fetchNearbyCafes = async (lat, lon) => {
  try {
    const response = await api.get(
      `/cafes/nearby?lat=${lat}&lon=${lon}&radius=500`,
    );

    const cafes = Array.isArray(response.data)
      ? response.data
      : response.data.content || [];

    if (!Array.isArray(cafes)) {
      console.error('카페 데이터가 배열 형태가 아닙니다:', cafes);
      return;
    }

    markers.forEach((marker) => marker.setMap(null));
    markers = [];

    cafes.forEach((cafe) => {
      const imageSrc = cafe.hasOutlet
        ? 'https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/markerStar.png'
        : 'https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/marker_red.png';

      const markerImage = new window.kakao.maps.MarkerImage(
        imageSrc,
        new window.kakao.maps.Size(24, 24),
      );
      const markerPosition = new window.kakao.maps.LatLng(
        cafe.latitude,
        cafe.longitude,
      );

      const marker = new window.kakao.maps.Marker({
        position: markerPosition,
        image: markerImage,
      });

      marker.setMap(map);
      markers.push(marker);

      window.kakao.maps.event.addListener(marker, 'click', () => {
        selectedPlace.value = {
          name: cafe.name,
          address: cafe.address,
          kakaoId: cafe.kakaoId,
          lat: cafe.latitude,
          lng: cafe.longitude,
        };
        map.panTo(markerPosition);
      });
    });
  } catch (error) {
    console.error('카페 로드 실패:', error);
  }
};

const searchPlaces = () => {
  const ps = new window.kakao.maps.services.Places();
  ps.keywordSearch(searchQuery.value, (data, status) => {
    if (status === window.kakao.maps.services.Status.OK) {
      const place = data[0];
      selectedPlace.value = {
        kakaoId: place.id,
        name: place.place_name,
        address: place.road_address_name || place.address_name,
        lat: place.y,
        lng: place.x,
      };

      const targetLatLng = new window.kakao.maps.LatLng(place.y, place.x);
      map.panTo(targetLatLng);

      if (selectedMarker) {
        selectedMarker.setMap(null);
      }

      selectedMarker = new window.kakao.maps.Marker({
        position: targetLatLng,
        map: map,
      });
    }
  });
};

// 🎯 기존 goToCreate를 카카오맵 길찾기로 연결하는 goToDirections로 변경
const goToDirections = () => {
  if (!selectedPlace.value) return;

  const { name, lat, lng } = selectedPlace.value;
  // 카카오맵 목적지 링크 형식: https://map.kakao.com/link/to/이름,위도,경도
  const kakaoMapUrl = `https://map.kakao.com/link/to/${name},${lat},${lng}`;

  // 새 탭에서 카카오맵 열기
  window.open(kakaoMapUrl, '_blank');
};

onMounted(() => initMap());
</script>

<style scoped>
/* 기존 스타일 그대로 유지됨 (생략 없이 원본 그대로 복사해두시면 됩니다) */
.cafe-map-page {
  padding: 80px 20px;
  max-width: 1300px;
  margin: 0 auto;
  min-height: 100vh;
  position: relative;
}

.bg-glow {
  position: fixed;
  top: 0;
  right: 0;
  width: 600px;
  height: 600px;
  background: radial-gradient(
    circle,
    rgba(255, 157, 108, 0.1) 0%,
    transparent 70%
  );
  z-index: -1;
}

.list-hero {
  margin-bottom: 60px;
  text-align: center;
}

.tag {
  color: #bb4d39;
  font-weight: 800;
  font-size: 11px;
  letter-spacing: 3px;
  display: block;
  margin-bottom: 15px;
}

.title {
  font-size: 48px;
  font-weight: 900;
  color: #3d2b1f;
  line-height: 1.2;
  margin-bottom: 30px;
}

.title span {
  color: #bb4d39;
  position: relative;
  z-index: 1;
}

.title span::after {
  content: '';
  position: absolute;
  bottom: 8px;
  left: 0;
  width: 100%;
  height: 8px;
  background: rgba(255, 157, 108, 0.3);
  z-index: -1;
}

.search-container {
  display: flex;
  justify-content: center;
}

.search-bar {
  display: flex;
  background: white;
  padding: 8px;
  border-radius: 20px;
  box-shadow: 0 15px 40px rgba(61, 43, 31, 0.08);
  width: 100%;
  max-width: 600px;
  border: 1px solid rgba(243, 240, 236, 0.8);
}

.search-bar input {
  flex: 1;
  border: none;
  padding: 15px 25px;
  font-size: 16px;
  outline: none;
  background: transparent;
}

.search-btn {
  background: #bb4d39;
  color: white;
  border: none;
  padding: 0 30px;
  border-radius: 15px;
  font-weight: 700;
  cursor: pointer;
  transition: background 0.2s;
}

.search-btn:hover {
  background: #a33f2c;
}

.map-container-wrapper {
  margin-top: 20px;
}

.map-card {
  background: white;
  border-radius: 30px;
  padding: 20px;
  box-shadow: 0 30px 60px rgba(61, 43, 31, 0.1);
  border: 1px solid #f3f0ec;
  position: relative;
}

.kakao-map {
  width: 100%;
  height: 600px;
  border-radius: 20px;
  overflow: hidden;
  position: relative;
}

.place-info-card {
  position: absolute;
  bottom: 40px;
  left: 50%;
  transform: translateX(-50%);
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  padding: 24px;
  border-radius: 24px;
  box-shadow: 0 20px 40px rgba(61, 43, 31, 0.15);
  z-index: 10;
  width: 90%;
  max-width: 400px;
  text-align: center;
  border: 1px solid rgba(243, 240, 236, 0.8);
}

.place-name {
  font-size: 22px;
  font-weight: 900;
  color: #3d2b1f;
  margin: 0 0 8px 0;
}

.place-addr {
  font-size: 14px;
  color: #8c7b6e;
  margin: 0 0 24px 0;
  font-weight: 500;
}

.card-actions {
  width: 100%;
}

.action-main-btn {
  background: #bb4d39;
  color: white;
  border: none;
  padding: 16px;
  border-radius: 16px;
  font-size: 16px;
  font-weight: 800;
  width: 100%;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.16, 1, 0.3, 1);
  box-shadow: 0 10px 20px rgba(187, 77, 57, 0.2);
}

.action-main-btn:hover {
  background: #a33f2c;
  transform: translateY(-3px);
  box-shadow: 0 15px 25px rgba(187, 77, 57, 0.3);
}

.action-main-btn:active {
  transform: translateY(0);
  box-shadow: 0 5px 10px rgba(187, 77, 57, 0.2);
}

.slide-up-enter-active,
.slide-up-leave-active {
  transition: all 0.4s cubic-bezier(0.16, 1, 0.3, 1);
}

.slide-up-enter-from,
.slide-up-leave-to {
  opacity: 0;
  transform: translate(-50%, 30px);
}
</style>
