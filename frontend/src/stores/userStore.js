import { defineStore } from 'pinia';
import { ref } from 'vue';

// 🎯 세 번째 인자로 옵션 객체를 넣어줍니다!
export const useUserStore = defineStore(
  'user',
  () => {
    // 플러그인이 알아서 저장/복구하므로 null과 ''로 초기화하면 됩니다.
    const id = ref(null);
    const email = ref('');
    const nickname = ref('');
    const token = ref(null);
    const isLogin = ref(false);

    const $patch = (data) => {
      id.value = data.id;
      email.value = data.email;
      nickname.value = data.nickname;
      token.value = data.accessToken;
      isLogin.value = true;
    };

    const logout = () => {
      id.value = null;
      email.value = '';
      nickname.value = '';
      token.value = null;
      isLogin.value = false;
    };

    return { id, email, nickname, token, isLogin, $patch, logout };
  },
  {
    persist: {
      key: 'user', // 💡 이 키값이 sessionStorage의 키 이름과 일치해야 합니다!
      storage: sessionStorage,
    },
  },
);
