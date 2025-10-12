<template>
  <div class="sidebar-container">
    <!-- Logo和名称部分 -->
    <div class="logo-section">
      <div class="logo-circle"></div>
      <span class="logo-name">慧编未来</span>
    </div>
    
    <!-- 导航菜单部分 -->
    <div class="nav-menu">
      <div class="nav-item" @click="handleClick('teaching')">
        <el-icon class="nav-icon"><Document /></el-icon>
        <span class="nav-text">编程教学</span>
      </div>
      
      <div class="nav-item" @click="handleClick('practice')">
        <el-icon class="nav-icon"><Edit /></el-icon>
        <span class="nav-text">练习实践</span>
      </div>
      
      <div class="nav-item" @click="handleClick('profile')">
        <el-icon class="nav-icon"><User /></el-icon>
        <span class="nav-text">个人中心</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { Document, Edit, User } from '@element-plus/icons-vue';
import { useRouter } from 'vue-router';

const router = useRouter();
const handleClick = (item) => {
  switch (item) {
    case 'practice' : {router.push('/pracindex'); sessionStorage.clear() ; break;}
    case 'teaching' : {router.push('/coursesection'); sessionStorage.clear(); break;}
  }
}
</script>

<style scoped>
.sidebar-container {
  height: 100vh;
  width: 220px;
  background-color: #1890ff;
  display: flex;
  flex-direction: column;
  padding: 20px 0;
  box-sizing: border-box;
}

/* Logo部分样式 */
.logo-section {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 40px;
  padding: 0 20px;
}

.logo-circle {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: radial-gradient(circle, #ffffff 0%, #1890ff 100%);
  margin-right: 12px;
}

.logo-name {
  font-size: 20px;
  font-weight: bold;
  color: transparent;
  background-clip: text;
  -webkit-background-clip: text;
  background-image: linear-gradient(to right, #ffffff, #ffffff);
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.3);
}

/* 导航菜单样式 */
.nav-menu {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.nav-item {
  display: flex;
  align-items: center;
  padding: 20px 30px;
  cursor: pointer;
  transition: all 0.3s ease;
  flex: 1;
  justify-content: center;
  position: relative;
}

.nav-item:hover {
  background-color: rgba(255, 255, 255, 0.1);
}

.nav-item:active {
  background-color: rgba(255, 255, 255, 0.2);
}

.nav-item.selected {
  background-color: #1677ff;
  border-right: 3px solid #ffffff;
}

.nav-icon {
  font-size: 24px;
  margin-right: 12px;
  color: #ffffff;
}

.nav-text {
  font-size: 16px;
  font-weight: bold;
  color: transparent;
  background-clip: text;
  -webkit-background-clip: text;
  background-image: linear-gradient(to right, #ffffff, #ffffff);
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.3);
}
</style>