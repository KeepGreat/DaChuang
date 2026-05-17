<template>
  <div class="course-series-detail">
    <!-- AI助教悬浮球 -->
    <AIAssistantDrawer :material-ids="materialIds" />

    <!-- 返回按钮 -->
    <div class="back-bar">
      <el-button text type="primary" @click="goBack" :icon="ArrowLeft">
        返回课程列表
      </el-button>
    </div>

    <!-- 系列头部信息 -->
    <div class="series-header" v-if="seriesInfo">
      <div class="header-left">
        <h1 class="series-title">{{ seriesInfo.title }}</h1>
        <p class="series-description">{{ seriesInfo.description }}</p>

        <!-- 统计信息 -->
        <div class="series-stats">
          <el-tag type="success" size="large">
            <el-icon>
              <VideoPlay />
            </el-icon>
            {{ seriesInfo.videoCount }} 个视频
          </el-tag>
          <el-tag type="warning" size="large">
            <el-icon>
              <Document />
            </el-icon>
            {{ seriesInfo.pdfCount }} 个文档
          </el-tag>
          <el-tag type="info" size="large">
            共 {{ seriesInfo.totalCount }} 个内容
          </el-tag>
        </div>
      </div>

      <!-- 智能学伴按钮 -->
      <div class="header-right">
        <div class="ai-companion-btn" @click="goToAICompanion">
          <el-tooltip content="智能学伴" placement="left" effect="light">
            <div class="companion-icon-wrapper">
              <AIAssistantIcon :is-active="true" :is-online="true" />
              <span class="companion-text">智能学伴</span>
            </div>
          </el-tooltip>
        </div>
      </div>
    </div>

    <!-- 课程内容展示 -->
    <div class="contents-section">
      <h2>课程内容</h2>

      <div v-if="loading" class="loading-container">
        <el-skeleton :rows="5" animated />
      </div>

      <div v-else class="content-display">
        <!-- 内容展示区域 -->
        <div v-if="materialIds.length > 0" class="content-grid">
          <MaterialPlayer :material-ids="materialIds" />
        </div>

        <!-- 空状态 -->
        <div v-else-if="hasFetched" class="empty-state">
          <el-empty description="暂无课程内容" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import {
  ArrowLeft, VideoPlay, Document
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
// 导入 AI 助手图标组件
import AIAssistantIcon from '@/components/teaching/AIAssistantIcon.vue'
// 导入 AI 助教抽屉组件
import AIAssistantDrawer from '@/components/teaching/AIAssistantDrawer.vue'
import MaterialPlayer from '@/components/teaching/MaterialPlayer.vue'

const router = useRouter()
const route = useRoute()

// 响应式数据
const loading = ref(false)
const seriesInfo = ref(null)
const materialIds = ref([])
const hasFetched = ref(false)

// 返回上一页
const goBack = () => {
  router.go(-1)
}

// 跳转到智能学伴
const goToAICompanion = () => {
  const validMaterialIds = (materialIds.value || []).filter(id => id !== null && id !== undefined)
  router.push({
    path: '/ai-companion',
    query: {
      courseId: seriesInfo.value?.id,
      courseTitle: seriesInfo.value?.title,
      materialIds: JSON.stringify(validMaterialIds)
    }
  })
}

const parseMaterialIds = (value) => {
  if (!value) return []

  try {
    const parsed = typeof value === 'string' ? JSON.parse(value) : value
    if (!Array.isArray(parsed)) return []

    return parsed
      .map(id => Number(id))
      .filter(id => Number.isFinite(id) && id > 0)
  } catch (error) {
    console.error('解析 materialIds 失败:', error)
    return []
  }
}

const toCount = (value) => {
  const count = Number(value)
  return Number.isFinite(count) && count >= 0 ? count : 0
}

// 初始化页面数据（只消费父页面传递数据，不再重复请求）
const initPageData = () => {
  const {
    id,
    title,
    description,
    materialIds: materialIdsQuery,
    videoCount,
    pdfCount,
    totalCount
  } = route.query

  if (!id || !title) {
    ElMessage.error('未获取到课程信息，请返回列表页重新选择')
    hasFetched.value = true
    return
  }

  const parsedIds = parseMaterialIds(materialIdsQuery)
  materialIds.value = parsedIds

  seriesInfo.value = {
    id,
    title,
    description: description || '',
    videoCount: toCount(videoCount),
    pdfCount: toCount(pdfCount),
    totalCount: totalCount !== undefined ? toCount(totalCount) : parsedIds.length
  }

  hasFetched.value = true
}

onMounted(() => {
  initPageData()
})
</script>

<style scoped>
.course-series-detail {
  padding: 24px;
  background: #fff;
  min-height: 100%;
}

/* 返回栏 */
.back-bar {
  margin-bottom: 24px;
}

/* 系列头部 */
.series-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 24px;
  background: linear-gradient(135deg, var(--bg-primary-light), var(--primary-lightest));
  border-radius: 12px;
  margin-bottom: 32px;
}

.header-left {
  flex: 1;
}

.series-title {
  margin: 0 0 12px 0;
  font-size: 28px;
  color: var(--primary-dark);
  font-weight: 500;
}

.series-description {
  margin: 0 0 20px 0;
  font-size: 16px;
  color: var(--text-regular);
  line-height: 1.6;
  max-width: 600px;
}

.series-stats {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.series-stats .el-tag {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  font-size: 14px;
}

.header-right {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-left: 40px;
}

/* 智能学伴按钮样式 */
.ai-companion-btn {
  cursor: pointer;
}

.companion-icon-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 12px;
  width: 120px;
  height: 120px;
  padding: 15px;
  background: linear-gradient(135deg,
    rgba(239, 246, 255, 0.95) 0%,
    rgba(224, 242, 254, 0.95) 50%,
    rgba(239, 246, 255, 0.95) 100%);
  border-radius: 20px;
  box-shadow:
    0 4px 20px var(--primary-alpha-10),
    0 1px 3px rgba(59, 130, 246, 0.1),
    inset 0 1px 0 rgba(255, 255, 255, 0.8);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
  backdrop-filter: blur(10px);
  border: 1px solid var(--border-primary-lighter);
}

.companion-icon-wrapper:hover {
  transform: translateY(-6px) scale(1.05);
  box-shadow:
    0 10px 40px var(--primary-alpha-10),
    0 5px 15px rgba(59, 130, 246, 0.2),
    inset 0 1px 0 rgba(255, 255, 255, 1);
  background: linear-gradient(135deg,
    rgba(239, 246, 255, 1) 0%,
    rgba(224, 242, 254, 1) 50%,
    rgba(239, 246, 255, 1) 100%);
}

.companion-icon-wrapper::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg,
    var(--primary) 0%,
    var(--primary-light) 50%,
    var(--primary) 100%);
  background-size: 200% 100%;
  animation: shimmer-flow 3s linear infinite;
}

@keyframes shimmer-flow {
  0% {
    background-position: 0% 50%;
  }
  100% {
    background-position: 200% 50%;
  }
}

/* 添加呼吸光效果 */
.companion-icon-wrapper::after {
  content: '';
  position: absolute;
  inset: -2px;
  background: linear-gradient(45deg,
    transparent 30%,
    var(--primary-alpha-10) 50%,
    transparent 70%);
  border-radius: 22px;
  opacity: 0;
  animation: breathe 4s ease-in-out infinite;
  pointer-events: none;
}

@keyframes breathe {
  0%, 100% {
    opacity: 0;
    transform: scale(1);
  }
  50% {
    opacity: 0.6;
    transform: scale(1.05);
  }
}

.companion-text {
  font-size: 14px;
  font-weight: 500;
  color: var(--primary);
  margin-top: 8px;
}

/* 内容部分 */
.contents-section h2 {
  margin: 0 0 20px 0;
  font-size: 20px;
  color: var(--primary-dark);
  font-weight: 500;
}

/* 加载状态 */
.loading-container {
  padding: 40px;
}

/* 内容项 */
.content-items {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.content-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: #fff;
  border: 1px solid var(--border-light);
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.content-item:hover {
  border-color: var(--primary-light);
  box-shadow: 0 4px 12px var(--primary-alpha-10);
  transform: translateX(4px);
}

.content-item.completed {
  background: #f6ffed;
  border-color: #b7eb8f;
}

.content-item.locked {
  opacity: 0.6;
  cursor: not-allowed;
}

.content-item.locked:hover {
  transform: none;
  border-color: var(--border-light);
  box-shadow: none;
}

/* 序号和图标 */
.content-index {
  display: flex;
  align-items: center;
  gap: 8px;
  position: relative;
}

.index-number {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--bg-light);
  border-radius: 50%;
  font-weight: 500;
  color: var(--text-regular);
  font-size: 14px;
}

.content-icon {
  position: absolute;
  right: -16px;
  font-size: 20px;
  color: var(--primary);
}

/* 内容信息 */
.content-info {
  flex: 1;
}

.content-title {
  margin: 0 0 8px 0;
  font-size: 16px;
  font-weight: 500;
  color: var(--primary-dark);
}

.content-description {
  margin: 0 0 12px 0;
  font-size: 14px;
  color: var(--text-regular);
  line-height: 1.5;
}

.content-meta {
  display: flex;
  align-items: center;
  gap: 16px;
}

.duration,
.size {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: var(--text-secondary);
}

/* 内容状态 */
.content-status {
  display: flex;
  align-items: center;
  justify-content: center;
  min-width: 80px;
}

.completed-icon {
  font-size: 24px;
  color: #52c41a;
}

.lock-icon {
  font-size: 20px;
  color: var(--text-secondary);
}

.status-text {
  font-size: 14px;
  color: var(--text-secondary);
}

/* 内容展示区域 */
.action-bar {
  margin-bottom: 20px;
}

.content-grid {
  display: flex;
  flex-direction: column;
  gap: 24px;
}


.content-card {
  background: #fff;
  border: 1px solid var(--border-light);
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
}

.content-card:hover {
  box-shadow: 0 4px 16px var(--primary-alpha-10);
  transform: translateY(-2px);
  border-color: var(--primary-lighter);
}

.card-header {
  padding: 16px 20px;
  background: var(--bg-light);
  border-bottom: 1px solid var(--border-light);
}

.card-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 500;
  color: var(--primary-dark);
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.card-header h3 .title-text {
  flex: 1;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.card-header h3 .status-tag {
  flex-shrink: 0;
}

.card-body {
  padding: 0;
}

.video-wrapper {
  background: #000;
  position: relative;
  line-height: 0;
  aspect-ratio: 16/9;
  border-radius: 8px;
  overflow: hidden;
}

.video-wrapper video {
  display: block;
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.pdf-wrapper {
  background: #fff;
}

.pdf-wrapper iframe {
  display: block;
  width: 100%;
}

.loading-placeholder {
  padding: 20px;
  background: var(--bg-light);
}

.unknown-type {
  padding: 40px;
  text-align: center;
}

.empty-state {
  padding: 60px 20px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .course-series-detail {
    padding: 16px;
  }

  .series-header {
    flex-direction: column;
    align-items: stretch;
  }

  .header-right {
    margin-left: 0;
    margin-top: 20px;
    align-items: center;
  }

  .companion-icon-wrapper {
    padding: 16px;
    min-width: 70px;
  }

  .companion-text {
    font-size: 12px;
  }

  .series-title {
    font-size: 24px;
  }

  .content-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .card-header {
    padding: 12px 16px;
  }

  .card-header h3 {
    font-size: 14px;
  }
}


/* 不需要额外的响应式布局，因为现在每个卡片都占一行 */
</style>