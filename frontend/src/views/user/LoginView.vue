<template>
  <div class="login-container">
    <div class="bg-gradient"></div>

    <div class="login-box">
      <div class="logo-area">
        <span class="logo-icon">🔌</span>
        <h2 class="title">KKOPDA</h2>
        <p class="subtitle">당신의 몰입을 위한 공간을 찾으세요.</p>
      </div>

      <form @submit.prevent="handleLogin">
        <div class="input-group">
          <label>이메일</label>
          <input
            type="email"
            v-model="email"
            placeholder="example@user.com"
            required />
        </div>

        <div class="input-group">
          <label>비밀번호</label>
          <input
            type="password"
            v-model="password"
            placeholder="••••••••"
            required />
        </div>

        <button class="login-btn">로그인</button>
      </form>

      <div class="signup-link">
        계정이 없으신가요?
        <router-link to="/signup">회원가입</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useUserStore } from '@/stores/userStore';
import { useRouter } from 'vue-router';
import { login } from '@/api/user';

const router = useRouter();
const userStore = useUserStore();

const email = ref('');
const password = ref('');

const handleLogin = async () => {
  try {
    const requestData = {
      email: email.value,
      password: password.value,
    };

    const response = await login(requestData);

    // 🚨🚨🚨 [이 부분이 수정되었습니다!] 🚨🚨🚨
    // 데이터(data)가 아니라 응답 헤더(headers)에서 Authorization 값을 꺼내옵니다.
    const authHeader = response.headers['authorization']; 
    
    // 'Bearer '라는 글자가 붙어있으면 떼어내고 순수 토큰만 남깁니다.
    const token = authHeader ? authHeader.replace('Bearer ', '') : null;

    // 만약 헤더에도 없고 바디에 있다면 바디에서 꺼내도록 최후의 보루도 남겨둡니다.
    const finalToken = token || response.data.accessToken || response.data.token;
    // 🚨🚨🚨🚨🚨🚨🚨🚨🚨🚨🚨🚨🚨🚨🚨🚨🚨🚨

    // 찾아낸 토큰과 ID를 지갑에 쏙 넣습니다.
    sessionStorage.setItem('accessToken', finalToken);
    sessionStorage.setItem('id', response.data.id);
    
    // Vue 화면(UI) 업데이트를 위해 스토어에도 저장
    userStore.$patch({
      id: response.data.id,
      email: response.data.email,
      nickname: response.data.nickname,
      accessToken: finalToken, 
    });

    alert('로그인 성공!');
    router.push('/'); 
  } catch (error) {
    console.error('로그인 실패:', error);
    alert('로그인에 실패했습니다. 이메일과 비밀번호를 확인해주세요.');
  }
};
</script>
<style scoped>
/* 폰트 및 전체 배경 */
.login-container {
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  width: 100%;
  overflow: hidden;
  background-color: #f9f7f2; /* 부드러운 샌드톤 배경 */
}

/* 이미지에서 본 주황색 그라데이션 배경 요소 */
.bg-gradient {
  position: absolute;
  top: -10%;
  right: -10%;
  width: 60%;
  height: 80%;
  background: linear-gradient(135deg, #ff9d6c 0%, #bb4d39 100%);
  filter: blur(80px);
  opacity: 0.4;
  z-index: 0;
  border-radius: 50%;
}

.login-box {
  position: relative;
  z-index: 1;
  width: 400px;
  padding: 40px;
  background-color: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  border-radius: 24px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.logo-area {
  text-align: center;
  margin-bottom: 30px;
}

.logo-icon {
  font-size: 40px;
  display: block;
  margin-bottom: 10px;
}

.title {
  font-size: 28px;
  font-weight: 800;
  color: #3d2b1f; /* 진한 브라운/네이비 톤 */
  margin: 0;
}

.subtitle {
  font-size: 14px;
  color: #8c7b6e;
  margin-top: 8px;
}

.input-group {
  margin-bottom: 20px;
  display: flex;
  flex-direction: column;
}

.input-group label {
  margin-bottom: 8px;
  font-size: 13px;
  font-weight: 600;
  color: #5c4d43;
  padding-left: 4px;
}

.input-group input {
  padding: 14px;
  background-color: #f3f0ec;
  border: 1px solid transparent;
  border-radius: 12px;
  font-size: 15px;
  transition: all 0.3s ease;
}

.input-group input:focus {
  outline: none;
  background-color: #fff;
  border-color: #ff9d6c;
  box-shadow: 0 0 0 4px rgba(255, 157, 108, 0.1);
}

.login-btn {
  width: 100%;
  padding: 16px;
  margin-top: 10px;
  background: linear-gradient(90deg, #bb4d39, #ff9d6c);
  color: white;
  border: none;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 700;
  cursor: pointer;
  transition:
    transform 0.2s ease,
    box-shadow 0.2s ease;
}

.login-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(187, 77, 57, 0.3);
}

.signup-link {
  margin-top: 25px;
  text-align: center;
  font-size: 14px;
  color: #8c7b6e;
}

.signup-link a {
  color: #bb4d39;
  font-weight: 700;
  text-decoration: none;
  margin-left: 8px;
}

.signup-link a:hover {
  text-decoration: underline;
}
</style>
