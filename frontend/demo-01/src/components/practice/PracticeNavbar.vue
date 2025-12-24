<template>
  <!-- 练习页面导航栏组件 -->
  <header class="practice-topbar">
    <!-- 左侧区域：返回按钮和练习标题 -->
    <div class="left">
      <!-- 返回按钮 -->
      <button class="back-btn" @click="goBack">
        <el-icon class="back-icon"><ArrowLeft /></el-icon>
      </button>
      <!-- 练习信息 -->
      <div class="practice-info">
        <h1 class="practice-name">{{ practiceTitle }}</h1>
      </div>
    </div>

    <!-- 右侧区域：单题作答切换和计时器 -->
    <div class="right">
      <!-- 单题作答模式切换开关 -->
      <div class="single-question-toggle">
        <span class="toggle-label">单题作答</span>
        <el-switch
          v-model="singleQuestionModeLocal"
          @change="handleSingleQuestionToggle"
          active-color="#2563eb"
          inactive-color="#bfdbfe"
        />
      </div>

      <!-- 计时器显示（当有剩余时间时） -->
      <div class="timer" :class="{ 'timer-urgent': isTimeUrgent }" v-if="remainingTime">
        <el-icon class="timer-icon"><Timer /></el-icon>
        <span class="timer-text">{{ remainingTime }}</span>
      </div>
    </div>
  </header>
</template>

<script setup>
// 导入依赖
import { useRouter } from "vue-router";
import { ref, watch, onMounted, onUnmounted, computed } from "vue";
import { ArrowLeft, Timer } from "@element-plus/icons-vue";

// 初始化路由
const router = useRouter();

// =============== 组件属性与事件 ===============
// 定义组件事件
const emit = defineEmits(["update:singleQuestionMode", "time-up"]);

// 定义组件属性
const props = defineProps({
  /**
   * 练习标题
   */
  practiceTitle: {
    type: String,
    default: "基础算法练习",
  },

  /**
   * 截止时间
   */
  deadline: {
    type: [Date, String],
    default: null,
  },

  /**
   * 用户信息
   */
  userInfo: {
    type: Object,
    default: () => ({}),
  },

  /**
   * 单题作答模式（父组件传递）
   */
  singleQuestionMode: {
    type: Boolean,
    default: false,
  },
});

// =============== 状态管理 ===============
// 本地单题作答模式状态（用于双向绑定）
const singleQuestionModeLocal = ref(props.singleQuestionMode);

// 倒计时相关状态
const remainingTime = ref('30:00');
const isTimeUrgent = ref(false); // 是否紧急（少于30分钟）
let timerInterval = null;

// =============== 计算属性 ===============
// 计算剩余秒数
const calculateRemainingSeconds = () => {
  if (!props.deadline) {
    // 如果没有设置截止时间，默认30分钟
    return 30 * 60;
  }
  
  const now = Date.now();
  const deadlineTime = props.deadline instanceof Date ? props.deadline.getTime() : new Date(props.deadline).getTime();
  const diff = deadlineTime - now;
  return Math.max(0, Math.floor(diff / 1000));
};

// 格式化时间显示
const formatTime = (seconds) => {
  if (seconds <= 0) {
    return '00:00';
  }
  
  // 计算天数
  const days = Math.floor(seconds / (24 * 60 * 60));
  
  // 如果剩余时间大于1天，显示天数
  if (days > 0) {
    return `${days}天`;
  }
  
  // 否则显示时:分:秒
  const hours = Math.floor(seconds / 3600).toString().padStart(2, '0');
  const mins = Math.floor((seconds % 3600) / 60).toString().padStart(2, '0');
  const secs = (seconds % 60).toString().padStart(2, '0');
  
  // 如果小时为0，只显示分:秒
  if (hours === '00') {
    return `${mins}:${secs}`;
  }
  
  return `${hours}:${mins}:${secs}`;
};

// 更新剩余时间
const updateRemainingTime = () => {
  const seconds = calculateRemainingSeconds();
  remainingTime.value = formatTime(seconds);
  
  // 判断是否少于30分钟（1800秒）
  isTimeUrgent.value = seconds > 0 && seconds < 1800;
  
  return seconds;
};

// 启动倒计时
const startTimer = () => {
  // 清除可能存在的旧计时器
  if (timerInterval) {
    clearInterval(timerInterval);
  }
  
  // 初始化剩余时间
  const initialSeconds = updateRemainingTime();

  if (initialSeconds <= 0) {
    console.log('练习时间已结束');
    emit('time-up');
    return;
  }

  timerInterval = setInterval(() => {
    const seconds = updateRemainingTime();
    if (seconds <= 0) {
      // 时间结束，停止计时器
      clearInterval(timerInterval);
      timerInterval = null;
      console.log('练习时间结束');
      emit('time-up');
    }
  }, 1000);
};

// =============== 监听与响应 ===============
// 监听父组件传递的单题作答模式变化，同步到本地状态
watch(
  () => props.singleQuestionMode,
  (newVal) => {
    singleQuestionModeLocal.value = newVal;
  },
);

// =============== 生命周期钩子 ===============
// 组件挂载时启动倒计时
onMounted(() => {
  startTimer();
});

// 组件卸载时清理计时器
onUnmounted(() => {
  if (timerInterval) {
    clearInterval(timerInterval);
    timerInterval = null;
  }
});

// =============== 事件处理函数 ===============
/**
 * 返回上一页
 */
function goBack() {
  // 使用明确的路由导航而不是 router.back()，避免多个导航事件同时触发
  router.push('/pracindex');
}

/**
 * 处理单题作答模式切换
 * @param {boolean} value - 切换后的值
 */
function handleSingleQuestionToggle(value) {
  emit("update:singleQuestionMode", value);
}
</script>

<style scoped>
.practice-topbar {
  height: 56px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 36px;
  background: rgba(var(--bg-white-rgb), 0.9);
  border-bottom: 1px solid var(--border-primary-lighter);
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
  color: var(--primary);
  transition: all 0.18s ease;
}

.back-btn:hover {
  background: var(--primary-alpha-10);
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
  background: var(--gradient-brand);
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
  background: linear-gradient(180deg, var(--bg-white), var(--bg-primary-light));
  border: 1px solid var(--border-primary-lighter);
  border-radius: 10px;
  font-weight: 600;
  color: var(--primary);
}

.timer-urgent {
  background: linear-gradient(180deg, var(--bg-white), #fef2f2);
  border: 1px solid #fecaca;
  color: #dc2626;
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
  color: var(--primary);
  transition: all 0.18s ease;
}

.user-btn:hover {
  background: var(--primary-alpha-10);
  transform: translateY(-2px);
}

.user-icon {
  font-size: 20px;
}

.single-question-toggle {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 12px;
  background: linear-gradient(180deg, var(--bg-white), var(--bg-primary-light));
  border: 1px solid var(--border-primary-lighter);
  border-radius: 10px;
  font-weight: 600;
  color: var(--primary);
}

.toggle-label {
  font-size: 14px;
}

/* 响应式设计 */
@media (max-width: 768px) {
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
