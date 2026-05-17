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
      :size="drawerSize"
      :show-close="true"
      :modal="false"
      :lock-scroll="false"
      class="ai-drawer"
    >
      <div class="drawer-content">
        <div class="drawer-resizer" @mousedown="startResize"></div>
        <AIAssistant :material-ids="props.materialIds" />
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { ElDrawer, ElIcon } from 'element-plus'
import { ChatDotRound } from '@element-plus/icons-vue'
import AIAssistant from '@/components/teaching/AIAssistant.vue'

const props = defineProps({
  materialIds: {
    type: Array,
    default: () => []
  }
})

const DESKTOP_MIN_DRAWER_WIDTH = 420
const MOBILE_MIN_DRAWER_WIDTH = 320
const MAX_DRAWER_WIDTH = 900

const getMinDrawerWidth = () => {
  return window.innerWidth <= 768 ? MOBILE_MIN_DRAWER_WIDTH : DESKTOP_MIN_DRAWER_WIDTH
}

const getInitialDrawerWidth = () => {
  const preferred = Math.round(window.innerWidth * 0.42)
  const minWidth = Math.min(getMinDrawerWidth(), Math.max(280, window.innerWidth - 24))
  return Math.min(Math.max(preferred, minWidth), Math.min(MAX_DRAWER_WIDTH, window.innerWidth - 24))
}

const clampDrawerWidth = (width) => {
  const minWidth = Math.min(getMinDrawerWidth(), Math.max(280, window.innerWidth - 24))
  const maxWidthByViewport = Math.max(minWidth, Math.min(MAX_DRAWER_WIDTH, window.innerWidth - 24))
  return Math.min(Math.max(width, minWidth), maxWidthByViewport)
}

const drawerWidth = ref(getInitialDrawerWidth())
const drawerSize = ref(`${drawerWidth.value}px`)

const isDrawerOpen = ref(false)
const ballPosition = ref({ x: 30, y: 200 })
const isDragging = ref(false)
const dragOffset = ref({ x: 0, y: 0 })
const hasMoved = ref(false)
const isResizing = ref(false)

const updateDrawerSize = (width) => {
  drawerWidth.value = clampDrawerWidth(width)
  drawerSize.value = `${drawerWidth.value}px`
}

const startResize = (e) => {
  e.preventDefault()
  isResizing.value = true
  document.body.style.userSelect = 'none'
  document.addEventListener('mousemove', onResize)
  document.addEventListener('mouseup', stopResize)
}

const onResize = (e) => {
  if (!isResizing.value) return
  const nextWidth = window.innerWidth - e.clientX
  updateDrawerSize(nextWidth)
}

const stopResize = () => {
  if (!isResizing.value) return
  isResizing.value = false
  document.body.style.userSelect = ''
  document.removeEventListener('mousemove', onResize)
  document.removeEventListener('mouseup', stopResize)
}

const handleViewportResize = () => {
  updateDrawerSize(drawerWidth.value)
}

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

onMounted(() => {
  window.addEventListener('resize', handleViewportResize)
})

onUnmounted(() => {
  document.removeEventListener('mousemove', onDrag)
  document.removeEventListener('mouseup', stopDrag)
  document.removeEventListener('mousemove', onResize)
  document.removeEventListener('mouseup', stopResize)
  window.removeEventListener('resize', handleViewportResize)
  document.body.style.userSelect = ''
})
</script>

<style scoped>
.drawer-content {
  position: relative;
  height: 100%;
}

.drawer-resizer {
  position: absolute;
  left: 0;
  top: 0;
  width: 10px;
  height: 100%;
  cursor: ew-resize;
  z-index: 10;
}

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
  overflow: hidden;
}

:deep(.ai-drawer .el-drawer) {
  position: relative;
}
</style>
