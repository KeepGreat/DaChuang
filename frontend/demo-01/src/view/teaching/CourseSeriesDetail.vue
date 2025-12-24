<template>
  <div class="course-series-detail">
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
        <div v-if="contentList.length > 0" class="content-grid">
          <div v-for="(item, index) in contentList" :key="index" class="content-card">
            <!-- 内容标题 -->
            <div class="card-header">
              <h3>
                <span class="title-text">{{ item.title || `内容 ${index + 1}` }}</span>
                <el-tag :type="item.type === 'video' ? 'success' : 'info'" size="small">
                  {{ item.type === 'video' ? '视频' : 'PDF文档' }}
                </el-tag>
              </h3>
            </div>

            <!-- 内容主体 -->
            <div class="card-body">
              <!-- 视频播放器 -->
              <div v-if="item.type === 'video'" class="video-wrapper">
                <video v-if="fileUrls[item.id]" :src="fileUrls[item.id]" controls preload="metadata">
                  您的浏览器不支持视频播放
                </video>
                <div v-else class="loading-placeholder">
                  <el-skeleton animated>
                    <template #template>
                      <el-skeleton-item variant="rect" style="width: 100%; height: 300px;" />
                    </template>
                  </el-skeleton>
                </div>
              </div>

              <!-- PDF 查看器 -->
              <div v-else-if="item.type === 'pdf'" class="pdf-wrapper">
                <iframe v-if="fileUrls[item.id]" :src="fileUrls[item.id]" width="100%" height="600" frameborder="0"></iframe>
                <div v-else class="loading-placeholder">
                  <el-skeleton animated>
                    <template #template>
                      <el-skeleton-item variant="rect" style="width: 100%; height: 600px;" />
                    </template>
                  </el-skeleton>
                </div>
              </div>

              <!-- 未知类型 -->
              <div v-else class="unknown-type">
                <el-empty description="不支持的资料类型" />
              </div>
            </div>
          </div>
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
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import {
  ArrowLeft, VideoPlay, Document
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
// 导入 Pinia store
import { useTeachingStore } from '@/store'
// 导入 AI 助手图标组件
import AIAssistantIcon from '@/components/AIAssistantIcon.vue'
// 导入文件下载 API
import { downloadFile } from '@/api/modules/teaching/FileContentAPI'

const router = useRouter()
const route = useRoute()

// 使用 Pinia store
const teachingStore = useTeachingStore()

// 响应式数据
const loading = teachingStore.loading
const seriesInfo = ref(null)
const contentList = ref([])
const hasFetched = ref(false)
const fileUrls = ref({}) // 存储文件的 blob URL

// 返回上一页
const goBack = () => {
  router.go(-1)
}

// 跳转到智能学伴
const goToAICompanion = () => {
  router.push({
    path: '/ai-companion',
    query: {
      courseId: seriesInfo.value?.id,
      courseTitle: seriesInfo.value?.title
    }
  })
}

// 加载文件内容并生成 blob URL
const loadFileContent = async (item) => {
  if (!item.fileId) {
    console.warn('资料没有关联的文件:', item.title)
    return
  }

  // 如果已经加载过，直接返回
  if (fileUrls.value[item.id]) {
    return
  }

  try {
    const response = await downloadFile(item.fileId)
    const blob = new Blob([response.data], { type: item.fileType || 'application/octet-stream' })
    fileUrls.value[item.id] = window.URL.createObjectURL(blob)
  } catch (err) {
    console.error('加载文件失败:', err)
    ElMessage.error(`加载文件 "${item.title}" 失败`)
  }
}

// 组件卸载时释放 blob URL
onUnmounted(() => {
  Object.values(fileUrls.value).forEach(url => {
    if (url) window.URL.revokeObjectURL(url)
  })
})


// 初始化页面数据（统一管理顶部汇总和课程内容）
const initPageData = async () => {
  // 检查路由中是否有传递的必要参数
  const { id, title, description } = route.query

  if (id && title) {
    // 如果路由中有必要参数，直接使用
    seriesInfo.value = {
      id: id,
      title: title,
      description: description || '',  // description可选
      videoCount: 0,  // 初始为0，会根据实际内容计算
      pdfCount: 0,    // 初始为0，会根据实际内容计算
      totalCount: 0   // 初始为0，会根据实际内容计算
    }

    // 使用传递的ID获取内容
    await loadSeriesContent(id)
  } else {
    // 如果没有传递必要参数，显示错误
    ElMessage.error('未获取到课程信息，请返回列表页重新选择')
  }
}

// 加载系列内容
const loadSeriesContent = async (seriesId) => {
  try {
    // 调用 store 的方法获取课程内容
    ElMessage.info('正在获取课程内容数据...')

    // 获取当前课程ID（从路由参数中获取）
    const courseId = route.params.id

    // 调用 teachingStore 的 fetchMaterials 方法
    const response = await teachingStore.fetchMaterials(seriesId, courseId)

    if (response.code >= 200 && response.code < 300 && response.data && response.data.length > 0) {
      const contentData = response.data
      contentList.value = contentData

      // 统计视频和PDF数量
      const videoCount = contentData.filter(item => item.type === 'video').length
      const pdfCount = contentData.filter(item => item.type === 'pdf').length

      // 更新系列信息
      seriesInfo.value.videoCount = videoCount
      seriesInfo.value.pdfCount = pdfCount
      seriesInfo.value.totalCount = contentData.length

      // 加载所有文件内容
      await Promise.all(contentData.map(item => loadFileContent(item)))

      ElMessage.success('课程数据加载完成！')
    } else if (response.code >= 200 && response.data && response.data.length === 0) {
      // 没有数据但请求成功
      ElMessage.warning('该课程系列暂无内容')
    }

    hasFetched.value = true
  } catch (error) {
    console.error('加载内容失败:', error)
    ElMessage.error('加载课程内容失败，请重试')

    // store 已经处理了错误，包括返回模拟数据
    contentList.value = teachingStore.getContentBySeriesId(seriesId)
    hasFetched.value = true
  }
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
    rgba(240, 253, 244, 0.95) 0%,
    rgba(236, 253, 245, 0.95) 25%,
    rgba(240, 253, 250, 0.95) 50%,
    rgba(240, 253, 244, 0.95) 100%);
  border-radius: 20px;
  box-shadow:
    0 4px 20px rgba(34, 197, 94, 0.2),
    0 1px 3px rgba(6, 182, 212, 0.1),
    inset 0 1px 0 rgba(255, 255, 255, 0.8);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(34, 197, 94, 0.1);
}

.companion-icon-wrapper:hover {
  transform: translateY(-6px) scale(1.05);
  box-shadow:
    0 10px 40px rgba(34, 197, 94, 0.3),
    0 5px 15px rgba(6, 182, 212, 0.2),
    inset 0 1px 0 rgba(255, 255, 255, 1);
  background: linear-gradient(135deg,
    rgba(240, 253, 244, 1) 0%,
    rgba(236, 253, 245, 1) 25%,
    rgba(240, 253, 250, 1) 50%,
    rgba(240, 253, 244, 1) 100%);
}

.companion-icon-wrapper::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg,
    #22c55e 0%,
    #06b6d4 25%,
    #fbbf24 50%,
    #22c55e 75%,
    #06b6d4 100%);
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
    rgba(34, 197, 94, 0.3) 50%,
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
  color: #333;
  margin-top: 8px;
  background: linear-gradient(135deg, #22c55e, #06b6d4, #fbbf24);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
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