<template>
  <header class="main-header">
    <div class="header-container">
      <div class="logo-section" @click="goHome">
        <span class="logo-icon">🔌</span>
        <h1 class="logo-text"><span>KKOPDA</span></h1>
      </div>

      <nav class="main-nav">
        <router-link to="/cafes" class="nav-item">카페 탐색</router-link>
        <router-link to="/community" class="nav-item">커뮤니티</router-link>
        <router-link to="/lounge" class="nav-item">라운지</router-link>
        <router-link :to="{ name: 'my' }" class="nav-item"
          >마이페이지</router-link
        >
      </nav>

      <div class="user-actions">
        <template v-if="userStore.isLogin">
          <div class="user-profile">
            <div class="header-avatar">
              {{ userStore.nickname?.charAt(0) }}
            </div>
            <span class="user-name">
              <strong>{{ userStore.nickname }}</strong
              >님
            </span>
          </div>
          <button class="logout-btn" @click="handleLogout">로그아웃</button>
        </template>
        <template v-else>
          <router-link to="/login" class="login-link">로그인</router-link>
          <router-link to="/signup" class="signup-btn">시작하기</router-link>
        </template>
      </div>
    </div>
  </header>
</template>

<script setup>
import { useUserStore } from '@/stores/userStore';
import { useRouter } from 'vue-router';

const userStore = useUserStore();
const router = useRouter();

const goHome = () => router.push('/');

const handleLogout = () => {
  if (confirm('로그아웃 하시겠습니까?')) {
    userStore.logout();
    router.push('/login');
  }
};
</script>

<style scoped>
.main-header {
  position: sticky;
  top: 0;
  z-index: 1000;
  background-color: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(15px);
  border-bottom: 1px solid rgba(187, 77, 57, 0.1);
  height: 80px;
}

.header-container {
  max-width: 1200px;
  height: 100%;
  margin: 0 auto;
  padding: 0 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

/* 로고 스타일 */
.logo-section {
  display: flex;
  align-items: center;
  cursor: pointer;
  transition: transform 0.2s ease;
}

.logo-section:hover {
  transform: scale(1.02);
}

.logo-icon {
  font-size: 24px;
  margin-right: 8px;
}

.logo-text {
  font-size: 22px;
  font-weight: 900;
  color: #3d2b1f;
  margin: 0;
}

.logo-text span {
  color: #bb4d39;
}

/* 메뉴 스타일 */
.main-nav {
  display: flex;
  gap: 40px;
}

.nav-item {
  text-decoration: none;
  color: #8c7b6e;
  font-weight: 700;
  font-size: 16px;
  transition: all 0.3s ease;
  padding: 8px 0;
  border-bottom: 2px solid transparent;
}

.nav-item:hover,
.nav-item.router-link-active {
  color: #bb4d39;
  border-bottom: 2px solid #bb4d39;
}

/* 유저 섹션 */
.user-actions {
  display: flex;
  align-items: center;
  gap: 16px;
}

.user-profile {
  display: flex;
  align-items: center;
  gap: 10px;
}

.header-avatar {
  width: 32px;
  height: 32px;
  background: linear-gradient(135deg, #ff9d6c, #bb4d39);
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  color: white;
  font-size: 14px;
  font-weight: 700;
}

.user-name {
  font-size: 14px;
  color: #3d2b1f;
}

.logout-btn {
  background: none;
  border: 1px solid #e2ddd9;
  color: #8c7b6e;
  padding: 6px 12px;
  border-radius: 8px;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.logout-btn:hover {
  border-color: #bb4d39;
  color: #bb4d39;
}

.login-link {
  text-decoration: none;
  color: #8c7b6e;
  font-weight: 600;
  font-size: 14px;
}

.signup-btn {
  text-decoration: none;
  background: #3d2b1f;
  color: white;
  padding: 10px 20px;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 700;
  transition: transform 0.2s;
}

.signup-btn:hover {
  transform: translateY(-2px);
}
</style>
