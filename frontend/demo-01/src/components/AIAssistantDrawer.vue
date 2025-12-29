<template>
  <div class="ai-assistant-drawer-wrapper">
    <!-- 可拖动悬浮球 -->
    <div
      class="floating-ball"
      :class="{ hidden: isDrawerOpen }"
      :style="{ top: ballPosition.y + 'px', right: ballPosition.x + 'px' }"
      @mousedown="startDrag"
      @click="handleBallClick"
    >
      <div class="ball-inner">
        <el-icon class="chat-icon"><ChatDotRound /></el-icon>
      </div>
      <div class="pulse-ring"></div>
      <div class="pulse-ring delay"></div>
    </div>

    <!-- 侧边抽屉 -->
    <el-drawer
      v-model="isDrawerOpen"
      title="智能助教"
      direction="rtl"
      size="450px"
      :show-close="true"
      :modal="false"
      class="ai-drawer"
    >
      <AIAssistant />
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, onUnmounted } from 'vue'
import { ElDrawer, ElIcon } from 'element-plus'
import { ChatDotRound } from '@element-plus/icons-vue'
import AIAssistant from '@/components/AIAssistant.vue'

const isDrawerOpen = ref(false)
const ballPosition = ref({ x: 30, y: 200 })
const isDragging = ref(false)
const dragOffset = ref({ x: 0, y: 0 })
const hasMoved = ref(false)

const startDrag = (e) => {
  isDragging.value = true
  hasMoved.value = false
  const rect = e.target.closest('.floating-ball').getBoundingClientRect()
  dragOffset.value = {
    x: window.innerWidth - e.clientX - (window.innerWidth - rect.right),
    y: e.clientY - rect.top
  }
  document.addEventListener('mousemove', onDrag)
  document.addEventListener('mouseup', stopDrag)
}

const onDrag = (e) => {
  if (!isDragging.value) return
  hasMoved.value = true
  const newX = window.innerWidth - e.clientX - dragOffset.value.x + 45
  const newY = e.clientY - dragOffset.value.y
  ballPosition.value = {
    x: Math.max(10, Math.min(window.innerWidth - 100, newX)),
    y: Math.max(10, Math.min(window.innerHeight - 100, newY))
  }
}

const stopDrag = () => {
  isDragging.value = false
  document.removeEventListener('mousemove', onDrag)
  document.removeEventListener('mouseup', stopDrag)
}

const handleBallClick = () => {
  if (!hasMoved.value) {
    isDrawerOpen.value = true
  }
}

onUnmounted(() => {
  document.removeEventListener('mousemove', onDrag)
  document.removeEventListener('mouseup', stopDrag)
})
</script>

<style scoped>
.floating-ball {
  position: fixed;
  z-index: 1000;
  cursor: grab;
  transition: opacity 0.3s, transform 0.3s;
  width: 56px;
  height: 56px;
}

.ball-inner {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 15px rgba(59, 130, 246, 0.4);
  transition: all 0.3s ease;
  position: relative;
  z-index: 2;
}

.chat-icon {
  font-size: 28px;
  color: #fff;
}

.pulse-ring {
  position: absolute;
  top: 0;
  left: 0;
  width: 56px;
  height: 56px;
  border-radius: 50%;
  border: 2px solid rgba(59, 130, 246, 0.5);
  animation: pulse 2s ease-out infinite;
  z-index: 1;
}

.pulse-ring.delay {
  animation-delay: 1s;
}

@keyframes pulse {
  0% {
    transform: scale(1);
    opacity: 1;
  }
  100% {
    transform: scale(1.8);
    opacity: 0;
  }
}

.floating-ball:active {
  cursor: grabbing;
}

.floating-ball.hidden {
  opacity: 0;
  pointer-events: none;
  transform: scale(0.8);
}

.floating-ball:hover .ball-inner {
  transform: scale(1.1);
  box-shadow: 0 6px 20px rgba(59, 130, 246, 0.5);
}

:deep(.ai-drawer) {
  --el-drawer-padding-primary: 0;
}

:deep(.ai-drawer .el-drawer__header) {
  margin-bottom: 0;
  padding: 16px 20px;
  background: linear-gradient(135deg, var(--bg-primary-light), var(--primary-lightest));
  border-bottom: 1px solid var(--border-light);
}

:deep(.ai-drawer .el-drawer__title) {
  font-size: 18px;
  font-weight: 600;
  color: var(--primary-dark);
}

:deep(.ai-drawer .el-drawer__body) {
  padding: 0;
  height: calc(100% - 60px);
}
</style>
