<!-- src/components/TopNav.vue -->
<template>
  <header class="topbar">
    <div class="left">
      <div class="brand" @click="goHome">
        <div class="logo-dot">慧</div>
        <div class="brand-name">慧编未来</div>
      </div>
    </div>
    <nav class="nav">
      <button class="nav-link" @click="go('courses')">课程</button>
      <button class="nav-link" @click="go('practice')">练习</button>
      <button class="nav-link" @click="go('community')">社区</button>
      <button class="nav-ghost" @click="go('profile')">
        <span class="avatar">👤</span>
        <span class="nav-text">个人中心</span>
      </button>
    </nav>
  </header>
</template>

<script setup>
import { useRouter } from 'vue-router';
const router = useRouter();

function go(target) {
  if (target === "profile") router.push("/profile");
  else if (target === "courses") router.push("/coursesection");
  else if (target === "practice") router.push("/practiceIndex");
  else if (target === "community") router.push("/community");
}

function goHome() { router.push("/"); }
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

@media(max-width:900px){
  .nav-text { display: none; }
  .topbar { padding: 0 14px; height: 64px; }
}
</style>
