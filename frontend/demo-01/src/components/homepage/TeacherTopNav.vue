<template>
  <header class="topbar">
    <div class="left">
      <div class="brand" @click="goHome">
        <div class="logo-dot">慧</div>
        <div class="brand-name">慧编未来</div>
      </div>
    </div>
    <nav class="nav">
      <button class="nav-link" @click="go('courses')">课程管理</button>
      <button class="nav-link" @click="go('practice')">练习管理</button>
      <div
        class="user-menu-wrapper"
        @mouseenter="showUserMenu = true"
        @mouseleave="showUserMenu = false"
      >
        <!-- <button class="nav-ghost" @click="go('learningTime')"> -->
        <button class="nav-ghost">
          <span class="avatar">👤</span>
          <span class="nav-text">个人中心</span>
        </button>
        <button v-if="showUserMenu" class="logout-btn" @click.stop="handleLogout">退出登录</button>
      </div>
    </nav>
  </header>
</template>

<script setup>
import { useRouter } from 'vue-router';
import { ref } from 'vue';
import { useUserStore } from '@/store/modules/auth/user';
const router = useRouter();
const userStore = useUserStore();
const showUserMenu = ref(false);

function go(target) {
  if (target === "learningTime") router.push("/learningTime");
  else if (target === "courses") router.push("/teacher/cs");
  else if (target === "practice") router.push("/teacher/pd");
}

function goHome() { router.push("/teacher"); }

function handleLogout() {
  userStore.logout();
  showUserMenu.value = false;
  router.push('/login');
}
</script>

<style scoped>
.topbar {
  height: 64px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 36px;
  background: rgba(255, 255, 255, 0.9);
  border-bottom: 1px solid #dddbf5;
  position: sticky;
  top: 0;
  z-index: 10;
}

.brand {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
}

.logo-dot {
  width: 42px;
  height: 42px;
  border-radius: 10px;
  background: linear-gradient(135deg,#e4f1ff,#9393ff);
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  color: #4940ff;
  box-shadow: 0 6px 18px rgba(214,51,132,0.08);
}

.brand-name {
  font-weight: 700;
  color: #4b93f1;
  font-size: 18px;
}

.nav {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-menu-wrapper {
  position: relative;
  display: inline-flex;
}

.nav-link {
  background: transparent;
  border: 0;
  padding: 8px 12px;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 600;
  color: #3b6ca4;
  transition: all .18s ease;
}

.nav-link:hover {
  background: rgba(214,51,132,0.08);
  color: rgb(51, 149, 214);
  transform: translateY(-2px);
}

.nav-ghost {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 10px;
  border-radius: 10px;
  background: linear-gradient(180deg,#fff,#f0f1ff);
  border: 1px solid #cbd4ff;
  cursor: pointer;
  transition: all .18s ease;
}

.nav-ghost:hover {
  transform: translateY(-4px);
  box-shadow: 0 6px 18px rgba(51, 75, 214, 0.08);
}

.logout-btn {
  position: absolute;
  top: 100%;
  right: 0;
  white-space: nowrap;
  border: 1px solid #cbd4ff;
  background: #ffffff;
  color: #3b6ca4;
  border-radius: 8px;
  padding: 8px 12px;
  cursor: pointer;
  box-shadow: 0 8px 20px rgba(51, 75, 214, 0.12);
  transition: all .18s ease;
  z-index: 20;
}

.logout-btn:hover {
  background: #f0f5ff;
  color: #2f5f98;
}

@media(max-width:900px){
  .nav-text { display: none; }
  .topbar { padding: 0 14px; height: 64px; }
}
</style>
