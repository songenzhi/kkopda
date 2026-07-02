import { createApp } from 'vue';
import { createPinia } from 'pinia';
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate';
import App from './App.vue';
import router from './router'; // 👈 1. 라우터 파일을 가져와야 합니다. (경로는 프로젝트에 맞게)

const pinia = createPinia();
pinia.use(piniaPluginPersistedstate);

const app = createApp(App);

app.use(pinia);
app.use(router); // 🎯 2. 가장 중요! 주석 처리되어 있다면 주석을 해제하세요.

app.mount('#app');
