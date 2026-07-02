import axios from 'axios';
import { useUserStore } from '@/stores/userStore';

const api = axios.create({
  baseURL: 'http://localhost:8080',
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json',
  },
});

// 요청 인터셉터: 모든 요청에 JWT 토큰을 자동으로 포함
api.interceptors.request.use(
  (config) => {
    const token = sessionStorage.getItem('accessToken');
    console.log('현재 요청 경로:', config.url);
    console.log('보내는 토큰:', token); // 👈 여기서 token이 null인지 확인해보세요!
    // "undefined" 또는 "null" 문자열이 들어가는 것을 방지
    if (token && token !== 'undefined' && token !== 'null') {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  },
);

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (
      error.response &&
      (error.response.status === 401 || error.response.status === 403)
    ) {
      const userStore = useUserStore();

      userStore.logout(); // ⭐ Pinia 상태 변경
      sessionStorage.clear(); // 저장된 데이터 제거

      if (window.location.pathname !== '/login') {
        alert('세션이 만료되었거나 접근 권한이 없습니다. 다시 로그인해주세요.');
        window.location.href = '/login';
      }
    }

    return Promise.reject(error);
  },
);

export default api;
