<template>
  <footer class="main-footer">
    <div class="footer-container">
      <div class="footer-top">
        <div class="footer-brand">
          <h2 class="footer-logo"><span>KKOPDA</span></h2>
          <p class="footer-desc">
            당신의 몰입을 방해하지 않는 최적의 공간을 연결합니다.
          </p>
        </div>
        <div class="footer-links">
          <div class="link-group">
            <span class="group-title">Service</span>
            <router-link to="/cafes">카페 탐색</router-link>
            <router-link to="/community">커뮤니티</router-link>
          </div>
          <div class="link-group">
            <span class="group-title">Support</span>
            <a href="#">이용약관</a>
            <a href="#">문의하기</a>
          </div>
        </div>
      </div>
      <div class="footer-bottom">
        <p class="copyright">© 2026 KKOPDA. Built for developers.</p>
        <div class="social-icons">
          <span>Github</span>
          <span>Instagram</span>
        </div>
      </div>
    </div>
  </footer>
</template>

<style scoped>
.main-footer {
  background-color: #3d2b1f;
  color: #f3f0ec;
  padding: 80px 0 40px;
  margin-top: auto; /* 페이지 내용이 적어도 아래에 고정 */
}

.footer-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
}

.footer-top {
  display: flex;
  justify-content: space-between;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  padding-bottom: 40px;
  margin-bottom: 40px;
}

.footer-logo {
  font-size: 24px;
  font-weight: 900;
  margin: 0 0 12px 0;
}

.footer-logo span {
  color: #ff9d6c;
}

.footer-desc {
  font-size: 14px;
  opacity: 0.6;
  max-width: none;
  line-height: 1.6;
  white-space: nowrap;
}

.footer-links {
  display: flex;
  gap: 80px;
}

.link-group {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.group-title {
  font-size: 12px;
  font-weight: 800;
  text-transform: uppercase;
  color: #ff9d6c;
  margin-bottom: 8px;
}

.link-group a {
  text-decoration: none;
  color: #f3f0ec;
  font-size: 14px;
  opacity: 0.7;
  transition: opacity 0.2s;
}

.link-group a:hover {
  opacity: 1;
}

.footer-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 13px;
  opacity: 0.5;
}

.social-icons {
  display: flex;
  gap: 20px;
}
</style>
