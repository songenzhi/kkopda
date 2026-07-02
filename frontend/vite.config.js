import { fileURLToPath, URL } from 'node:url';
import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';

export default defineConfig({
  plugins: [vue()],
  define: {
    global: 'window',
  },
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
    },
  },
  // 🎯 [여기부터 추가!] 서버 설정: /api 요청을 백엔드로 포워딩
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080', // 백엔드(Spring Boot) 주소! 포트가 다르다면 맞춰주세요.
        changeOrigin: true,
      },
    },
  },
});
