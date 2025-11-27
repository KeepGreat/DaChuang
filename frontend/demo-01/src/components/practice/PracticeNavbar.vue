<template>
  <!-- 顶栏 -->
  <header class="practice-topbar">
    <div class="left">
      <button class="back-btn" @click="goBack">
        <el-icon class="back-icon"><ArrowLeft /></el-icon>
      </button>
      <div class="practice-info">
        <h1 class="practice-name">{{ practiceName }}</h1>
      </div>
    </div>
    <div class="right">
      <div class="timer" v-if="showTimer">
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
import { ref, onMounted, onUnmounted } from 'vue';
import { useRouter } from 'vue-router';
import { ArrowLeft, Timer, User } from '@element-plus/icons-vue';

const router = useRouter();

// 练习名称（可根据实际情况从路由参数或API获取）
const practiceName = ref('基础算法练习');

// 截止时间（示例：当前时间 + 30分钟，实际项目中可从API获取）
const deadline = ref(new Date(Date.now() + 30 * 60 * 1000)); // 示例：当前时间后30分钟
const remainingTime = ref('30:00');
const showTimer = ref(true);
let timerInterval = null;

// 计算剩余秒数
const calculateRemainingSeconds = () => {
  const now = Date.now();
  const diff = deadline.value - now;
  return Math.max(0, Math.floor(diff / 1000));
};

// 格式化时间显示
const formatTime = (seconds) => {
  if (seconds <= 0) {
    return '00:00';
  }
  const mins = Math.floor(seconds / 60).toString().padStart(2, '0');
  const secs = (seconds % 60).toString().padStart(2, '0');
  return `${mins}:${secs}`;
};

// 更新剩余时间
const updateRemainingTime = () => {
  const seconds = calculateRemainingSeconds();
  remainingTime.value = formatTime(seconds);
  return seconds;
};

// 倒计时函数
const startTimer = () => {
  // 初始化剩余时间
  const initialSeconds = updateRemainingTime();
  
  if (initialSeconds <= 0) {
    console.log('练习时间已结束');
    return;
  }
  
  timerInterval = setInterval(() => {
    const seconds = updateRemainingTime();
    if (seconds <= 0) {
      // 时间结束，停止计时器
      clearInterval(timerInterval);
      timerInterval = null;
      // 可以添加时间结束的处理逻辑
      console.log('练习时间结束');
    }
  }, 1000);
};

// 组件挂载时开始计时
onMounted(() => {
  startTimer();
});

// 组件卸载时清除计时器
onUnmounted(() => {
  if (timerInterval) {
    clearInterval(timerInterval);
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
  border-bottom: 1px solid #f5dbe7;
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
  color: #a43b69;
  transition: all .18s ease;
}

.back-btn:hover {
  background: rgba(214, 51, 132, 0.08);
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
  color: #d63384;
  margin: 0;
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
  background: linear-gradient(180deg, #fff, #fff0f4);
  border: 1px solid #ffd6e7;
  border-radius: 10px;
  font-weight: 600;
  color: #a43b69;
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
  color: #a43b69;
  transition: all .18s ease;
}

.user-btn:hover {
  background: rgba(214, 51, 132, 0.08);
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