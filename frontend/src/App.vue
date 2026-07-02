<template>
  <div class="app-wrapper">
    <Header />

    <main class="content-area">
      <router-view />
    </main>

    <Footer />
  </div>
</template>

<script setup>
import Header from '@/components/common/AppHeader.vue';
import Footer from '@/components/common/AppFooter.vue';
</script>

<style>
/* 전역 스타일 */
.app-wrapper {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.content-area {
  flex: 1;
}
</style>
