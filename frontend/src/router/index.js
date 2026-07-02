import { createRouter, createWebHistory } from 'vue-router';
import HomeView from '../views/HomeView.vue';
import LoginView from '@/views/user/LoginView.vue';
import SignupView from '@/views/user/SignupView.vue';
import CafeListView from '@/views/cafe/CafeListView.vue';
import CafeDetailView from '@/views/cafe/CafeDetailView.vue';
import MyPageView from '@/views/user/MyPageView.vue';
import CafeCreateView from '@/views/cafe/CafeCreateView.vue';
import { useUserStore } from '@/stores/userStore';
import CafeEditView from '@/views/cafe/CafeEditView.vue';
import ReviewCreateView from '@/views/review/ReviewCreateView.vue';
import ReviewEditView from '@/views/review/ReviewEditView.vue';
import CommunityListView from '@/views/community/CommunityListView.vue';
import CommunityCreateView from '@/views/community/CommunityCreateView.vue';
import CommunityEditView from '@/views/community/CommunityEditView.vue';
import CommunityDetailView from '@/views/community/CommunityDetailView.vue';
import LoungeView from '@/views/lounge/LoungeView.vue';
import LoungeList from '@/views/lounge/LoungeList.vue';

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
    },
    {
      path: '/login',
      name: 'login',
      component: LoginView,
    },
    {
      path: '/signup',
      name: 'signup',
      component: SignupView,
    },
    {
      path: '/cafes',
      name: 'cafes',
      component: CafeListView,
    },
    {
      path: '/cafes/create',
      name: 'cafesCreate',
      component: CafeCreateView,
      // meta: { requiresAuth: true },
    },
    {
      path: '/cafes/:id/edit',
      name: 'cafesEdit',
      component: CafeEditView,
      // meta: { requiresAuth: true },
    },
    {
      path: '/cafes/:id',
      name: 'cafeDetail',
      component: CafeDetailView,
    },
    {
      path: '/cafes/:id/reviews',
      name: 'reviewCreate',
      component: ReviewCreateView,
    },
    {
      path: '/cafes/:cafeId/reviews/:id/edit',
      name: 'reviewEdit',
      component: ReviewEditView,
    },
    {
      path: '/community',
      name: 'community',
      component: CommunityListView,
    },
    {
      path: '/community/:id',
      name: 'communityDetail',
      component: CommunityDetailView,
    },
    {
      path: '/community/create',
      name: 'communityCreate',
      component: CommunityCreateView,
    },
    {
      path: '/community/edit/:id',
      name: 'communityEdit',
      component: CommunityEditView,
    },
    {
      path: '/lounge',
      name: 'Lounge',
      component: LoungeList, // 👈 여기가 ChatView로 되어있으면 채팅창이 떠버립니다!
    },
    // 2. /lounge/방번호 주소 -> 채팅창(ChatView) 화면 연결
    {
      path: '/lounge/:id', // :id는 방 번호 파라미터
      name: 'ChatView',
      component: LoungeView, // 👈 여기엔 채팅창이 들어가야 합니다.
    },
    {
      path: '/my',
      name: 'my',
      component: MyPageView,
      meta: { requiresAuth: true },
    },
  ],
});

router.beforeEach((to, from) => {
  const userStore = useUserStore();

  if (to.meta.requiresAuth && !userStore.isLogin) return '/login';
});

export default router;
