<template>
  <div class="sidebar" :class="{ collapsed }">
    <!-- Logo -->
    

    <!-- Menu -->
    <div class="menu">
      <div class="menu-item" @click="go('teaching')">
        📘 <span v-if="!collapsed">编程教学</span>
      </div>

      <div class="menu-item" @click="go('practice')">
        🧪 <span v-if="!collapsed">练习实践</span>
      </div>

      <div class="menu-item" @click="go('profile')">
        👤 <span v-if="!collapsed">个人中心</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from "vue-router";
defineProps({ collapsed: Boolean });

const router = useRouter();
const go = (page) => {
  sessionStorage.clear();
  switch(page) {
    case 'profile': router.push('/profile'); break;
    case 'practice': router.push('/practiceIndex'); break;
    case 'teaching': router.push('/coursesection'); break;
  }
};

</script>

<style scoped>
/* --- 侧栏容器 --- */
.sidebar {
  height: 100%;
  width: 220px;
  display: flex;
  flex-direction: column;
  padding: 22px 14px;
  transition: all 0.3s ease;

  /* 柔和粉白渐变 */
  background: linear-gradient(180deg, #ffeef4, #ffffff);
  
  /* 与右侧连接自然：柔边阴影，不突兀 */
  border-right: 1px solid #f3d7e5;
  box-shadow: 2px 0 10px rgba(255, 182, 193, 0.15);

  /* 圆角（更现代，像AI课程平台） */
  border-top-right-radius: 18px;
  border-bottom-right-radius: 18px;
}

/* 折叠效果 */
.sidebar.collapsed {
  width: 70px;
  padding: 22px 10px;
}

/* --- Logo --- */
.logo-section {
  display: flex;
  align-items: center;
  padding-left: 6px;
  margin-bottom: 40px;
}

.logo-circle {
  width: 34px;
  height: 34px;
  border-radius: 12px;
  background: linear-gradient(135deg, #ffb6d9, #ffd6e8);
  box-shadow: 0 2px 6px rgba(255, 105, 180, 0.25);
}

.logo-name {
  margin-left: 12px;
  font-size: 18px;
  font-weight: bold;
  color: #d63384;
}

/* --- Menu --- */
.menu {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.menu-item {
  padding: 12px 14px;
  border-radius: 10px;
  font-size: 15px;
  color: #b2246e;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 12px;
  transition: 0.22s ease;
}

/* 鼠标悬停 — 柔和粉白光 */
.menu-item:hover {
  background: #ffe6f1;
  box-shadow: inset 0 0 8px rgba(255, 182, 193, 0.3);
  transform: translateX(4px);
}

/* 折叠状态图标居中 */
.sidebar.collapsed .menu-item {
  justify-content: center;
  padding: 12px 0;
}
</style>
