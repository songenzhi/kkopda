<template>
  <div class="signup-container">
    <div class="bg-gradient"></div>

    <div class="signup-box">
      <div class="logo-area">
        <span class="logo-icon">✨</span>
        <h2 class="title">회원가입</h2>
        <p class="subtitle">더 많은 플러그 스팟을 기록해보세요.</p>
      </div>

      <form @submit.prevent="handleSignup">
        <div class="input-group">
          <label>닉네임</label>
          <input
            type="text"
            v-model="nickname"
            placeholder="멋진 이름을 지어주세요"
            required />
        </div>

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
            placeholder="8자 이상 입력하세요"
            required />
        </div>

        <button class="signup-btn">회원가입 완료</button>
      </form>

      <div class="login-link">
        이미 계정이 있으신가요?
        <router-link to="/login">로그인하기</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { signup } from '@/api/user';

const router = useRouter();

const nickname = ref('');
const email = ref('');
const password = ref('');

const handleSignup = async () => {
  try {
    const res = await signup({
      nickname: nickname.value,
      email: email.value,
      password: password.value,
    });
    alert('회원가입을 축하합니다! 🎉');
    router.push('/login');
  } catch (error) {
    alert('정보를 다시 확인해주세요.');
  }
};
</script>

<style scoped>
/* 전체 컨테이너 및 배경 */
.signup-container {
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  width: 100%;
  overflow: hidden;
  background-color: #f9f7f2;
}

/* 배경 그라데이션 블러 (로그인과 통일) */
.bg-gradient {
  position: absolute;
  bottom: -10%;
  left: -10%;
  width: 60%;
  height: 80%;
  background: linear-gradient(135deg, #ff9d6c 0%, #bb4d39 100%);
  filter: blur(80px);
  opacity: 0.4;
  z-index: 0;
  border-radius: 50%;
}

.signup-box {
  position: relative;
  z-index: 1;
  width: 420px;
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
  color: #3d2b1f;
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

.signup-btn {
  width: 100%;
  padding: 16px;
  margin-top: 10px;
  background: linear-gradient(90deg, #ff9d6c, #bb4d39);
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

.signup-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(187, 77, 57, 0.3);
}

.login-link {
  margin-top: 25px;
  text-align: center;
  font-size: 14px;
  color: #8c7b6e;
}

.login-link a {
  color: #bb4d39;
  font-weight: 700;
  text-decoration: none;
  margin-left: 8px;
}

.login-link a:hover {
  text-decoration: underline;
}
</style>
