<template>
  <div class="ai-assistant-icon" :class="{ active: isActive }">
    <!-- 背景光晕效果 -->
    <div class="glow-background" v-if="isActive"></div>

    <!-- AI头像图片 -->
    <img src="../assets/AI.png" alt="AI Assistant" class="ai-avatar" />

    <!-- 悬浮的小点装饰 -->
    <div class="floating-dots" v-if="isActive">
      <div class="dot dot-1"></div>
      <div class="dot dot-2"></div>
      <div class="dot dot-3"></div>
      <div class="dot dot-4"></div>
    </div>

    <!-- 状态指示器 -->
    <div class="status-indicator" :class="{ online: isOnline }">
      <span class="status-dot"></span>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'

const props = defineProps({
  isActive: {
    type: Boolean,
    default: true
  },
  isOnline: {
    type: Boolean,
    default: true
  }
})

// 鼠标交互效果
const isHovered = ref(false)
</script>

<style scoped>
.ai-assistant-icon {
  position: relative;
  width: 90px;
  height: 90px;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f0fdf4 0%, #ecfdf5 25%, #f0fdfa 50%, #f0fdf4 100%);
  border-radius: 15px;
  box-shadow:
    0 4px 15px rgba(34, 197, 94, 0.1),
    0 1px 3px rgba(6, 182, 212, 0.05);
  overflow: visible;
}

.ai-assistant-icon:hover {
  transform: scale(1.05);
  box-shadow:
    0 8px 25px rgba(34, 197, 94, 0.2),
    0 3px 10px rgba(6, 182, 212, 0.15);
}

.ai-avatar {
  width: 72%;
  height: 72%;
  object-fit: contain;
  border-radius: 50%;
  filter: drop-shadow(0 2px 6px rgba(34, 197, 94, 0.15));
  transition: all 0.3s ease;
  z-index: 2;
  animation: avatar-float 4s ease-in-out infinite;
}

@keyframes avatar-float {
  0%, 100% {
    transform: translateY(0) scale(1);
  }
  25% {
    transform: translateY(-3px) scale(1.02);
  }
  50% {
    transform: translateY(-1px) scale(1.015);
  }
  75% {
    transform: translateY(-2px) scale(1.01);
  }
}

.ai-assistant-icon:hover .ai-avatar {
  filter: drop-shadow(0 4px 12px rgba(34, 197, 94, 0.3));
  animation-duration: 2s;
}

/* 背景光晕效果 */
.glow-background {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 110px;
  height: 110px;
  background: radial-gradient(
    circle,
    rgba(34, 197, 94, 0.2) 0%,
    rgba(6, 182, 212, 0.12) 35%,
    rgba(251, 191, 36, 0.06) 60%,
    rgba(34, 197, 94, 0) 80%
  );
  border-radius: 50%;
  animation: active-glow 3s ease-in-out infinite;
  pointer-events: none;
}

@keyframes active-glow {
  0%, 100% {
    opacity: 0.5;
    transform: translate(-50%, -50%) scale(1);
  }
  33% {
    opacity: 0.7;
    transform: translate(-50%, -50%) scale(1.1);
  }
  66% {
    opacity: 0.6;
    transform: translate(-50%, -50%) scale(1.05);
  }
}

/* 动态光环 */
.ai-assistant-icon.active::before {
  content: '';
  position: absolute;
  top: -3px;
  left: -3px;
  right: -3px;
  bottom: -3px;
  background: linear-gradient(
    90deg,
    rgba(34, 197, 94, 0.4),
    rgba(6, 182, 212, 0.4),
    rgba(251, 191, 36, 0.4),
    rgba(34, 197, 94, 0.4)
  );
  background-size: 300% 100%;
  border-radius: 18px;
  z-index: 0;
  animation: shimmer-flow 3s linear infinite;
  opacity: 0.7;
}

@keyframes shimmer-flow {
  0% {
    background-position: 0% 50%;
  }
  100% {
    background-position: 300% 50%;
  }
}

/* 悬浮的小点装饰 */
.floating-dots {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
}

.dot {
  position: absolute;
  border-radius: 50%;
  opacity: 0.7;
}

.dot-1 {
  width: 5px;
  height: 5px;
  top: 15%;
  left: 15%;
  background: #22c55e;
  box-shadow: 0 0 8px rgba(34, 197, 94, 0.5);
  animation: orbit-float-1 4s ease-in-out infinite;
}

.dot-2 {
  width: 4px;
  height: 4px;
  top: 20%;
  right: 15%;
  background: #06b6d4;
  box-shadow: 0 0 7px rgba(6, 182, 212, 0.5);
  animation: orbit-float-2 3.5s ease-in-out infinite;
}

.dot-3 {
  width: 5px;
  height: 5px;
  bottom: 20%;
  right: 15%;
  background: #22c55e;
  box-shadow: 0 0 8px rgba(34, 197, 94, 0.5);
  animation: orbit-float-3 4.5s ease-in-out infinite;
}

.dot-4 {
  width: 4px;
  height: 4px;
  bottom: 15%;
  left: 15%;
  background: #06b6d4;
  box-shadow: 0 0 7px rgba(6, 182, 212, 0.5);
  animation: orbit-float-4 3s ease-in-out infinite;
}

@keyframes orbit-float-1 {
  0%, 100% {
    transform: translate(0, 0) scale(1);
    opacity: 0.7;
  }
  25% {
    transform: translate(5px, -8px) scale(1.3);
    opacity: 1;
  }
  50% {
    transform: translate(-3px, -5px) scale(0.9);
    opacity: 0.8;
  }
  75% {
    transform: translate(-5px, -3px) scale(1.1);
    opacity: 0.9;
  }
}

@keyframes orbit-float-2 {
  0%, 100% {
    transform: translate(0, 0) scale(1);
    opacity: 0.7;
  }
  25% {
    transform: translate(-6px, -6px) scale(1.2);
    opacity: 1;
  }
  50% {
    transform: translate(4px, -4px) scale(0.8);
    opacity: 0.8;
  }
  75% {
    transform: translate(6px, -2px) scale(1.15);
    opacity: 0.95;
  }
}

@keyframes orbit-float-3 {
  0%, 100% {
    transform: translate(0, 0) scale(1);
    opacity: 0.7;
  }
  25% {
    transform: translate(-4px, 6px) scale(0.85);
    opacity: 0.85;
  }
  50% {
    transform: translate(-6px, 4px) scale(1.25);
    opacity: 1;
  }
  75% {
    transform: translate(3px, 6px) scale(1.05);
    opacity: 0.9;
  }
}

@keyframes orbit-float-4 {
  0%, 100% {
    transform: translate(0, 0) scale(1);
    opacity: 0.7;
  }
  25% {
    transform: translate(5px, 5px) scale(1.1);
    opacity: 0.9;
  }
  50% {
    transform: translate(-4px, 6px) scale(0.9);
    opacity: 0.8;
  }
  75% {
    transform: translate(6px, 3px) scale(1.2);
    opacity: 1;
  }
}

/* 状态指示器 */
.status-indicator {
  position: absolute;
  bottom: 5px;
  right: 5px;
  width: 18px;
  height: 18px;
  border-radius: 50%;
  background: #e2e8f0;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2px solid #fff;
  box-shadow:
    0 2px 6px rgba(0, 0, 0, 0.1),
    0 0 0 1px rgba(34, 197, 94, 0.1);
  z-index: 3;
  transition: all 0.3s ease;
}

.status-indicator.online {
  background: linear-gradient(135deg, #22c55e, #16a34a);
  box-shadow:
    0 2px 6px rgba(34, 197, 94, 0.3),
    0 0 0 2px rgba(34, 197, 94, 0.2);
}

.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #fff;
  opacity: 0.9;
}

.status-indicator.online .status-dot {
  animation: gentle-pulse 3s ease-in-out infinite;
}

@keyframes gentle-pulse {
  0%, 100% {
    opacity: 0.9;
    transform: scale(1);
  }
  50% {
    opacity: 1;
    transform: scale(1.15);
  }
}

/* 激活状态样式 */
.ai-assistant-icon.active .glow-background {
  animation-duration: 2s;
}

.ai-assistant-icon.active .dot {
  animation-duration: 3s;
}

.ai-assistant-icon.active .ai-avatar {
  animation-duration: 4s;
}

/* 响应式调整 */
@media (max-width: 640px) {
  .ai-assistant-icon {
    width: 70px;
    height: 70px;
    border-radius: 12px;
  }

  .ai-avatar {
    width: 70%;
    height: 70%;
  }

  .glow-background {
    width: 85px;
    height: 85px;
  }

  .dot {
    width: 3px;
    height: 3px;
  }

  .dot-1, .dot-3 {
    width: 3px;
    height: 3px;
  }

  .status-indicator {
    width: 14px;
    height: 14px;
    border-width: 1.5px;
  }

  .status-dot {
    width: 6px;
    height: 6px;
  }
}
</style>