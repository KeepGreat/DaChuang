<template>
  <!-- 顶栏 -->
  <header class="practice-topbar">
    <div class="left">
      <button class="back-btn" @click="goBack">
        <el-icon class="back-icon"><ArrowLeft /></el-icon>
      </button>
      <div class="practice-info">
        <h1 class="practice-name">{{ practiceTitle }}</h1>
      </div>
    </div>
    <div class="right">
      <div class="timer" v-if="remainingTime">
        <el-icon class="timer-icon"><Timer /></el-icon>
        <span class="timer-text">{{ remainingTime }}</span>
      </div>
      <button class="user-btn" @click="goToProfile">
        <el-icon class="user-icon"><User /></el-icon>
      </button>
    </div>
  </header>
</template>

<script setup>
import { useRouter } from 'vue-router';
import { ArrowLeft, Timer, User } from '@element-plus/icons-vue';

const router = useRouter();

// Props 定义
const props = defineProps({
  practiceTitle: {
    type: String,
    default: '基础算法练习'
  },
  remainingTime: {
    type: String,
    default: '00:00'
  },
  userInfo: {
    type: Object,
    default: () => ({})
  }
});

// 返回上一页
function goBack() {
  router.back();
}

// 跳转到个人中心
function goToProfile() {
  router.push('/profile');
}
</script>

<style scoped>
.practice-topbar {
  height: 56px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 36px;
  background: rgba(255, 255, 255, 0.9);
  border-bottom: 1px solid #dbeafe;
  position: sticky;
  top: 0;
  z-index: 10;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.back-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  border: none;
  background: transparent;
  border-radius: 8px;
  cursor: pointer;
  color: #2563eb;
  transition: all .18s ease;
}

.back-btn:hover {
  background: rgba(37, 99, 235, 0.08);
  transform: translateX(-2px);
}

.back-icon {
  font-size: 20px;
}

.practice-info {
  display: flex;
  align-items: center;
}

.practice-name {
  font-size: 18px;
  font-weight: 700;
  margin: 0;
  background: linear-gradient(45deg, #2563eb, #1d4ed8);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.right {
  display: flex;
  align-items: center;
  gap: 24px;
}

.timer {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 12px;
  background: linear-gradient(180deg, #fff, #f0f9ff);
  border: 1px solid #bfdbfe;
  border-radius: 10px;
  font-weight: 600;
  color: #2563eb;
}

.timer-icon {
  font-size: 16px;
}

.timer-text {
  font-size: 14px;
}

.user-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  border: none;
  background: transparent;
  border-radius: 8px;
  cursor: pointer;
  color: #2563eb;
  transition: all .18s ease;
}

.user-btn:hover {
  background: rgba(37, 99, 235, 0.08);
  transform: translateY(-2px);
}

.user-icon {
  font-size: 20px;
}

/* 响应式设计 */
@media(max-width: 768px) {
  .practice-topbar {
    padding: 0 14px;
  }
  
  .practice-name {
    font-size: 16px;
  }
  
  .right {
    gap: 16px;
  }
  
  .timer {
    padding: 4px 8px;
  }
  
  .timer-text {
    font-size: 12px;
  }
}
</style>