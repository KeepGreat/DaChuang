<template>
  <div class="course-content-detail">
    <!-- 返回按钮 -->
    <div class="back-bar">
      <el-button
        text
        type="primary"
        @click="goBack"
        :icon="ArrowLeft"
      >
        返回课程列表
      </el-button>
    </div>

    <!-- 课程内容 -->
    <div class="content-container" v-if="!loading && courseInfo">
      <!-- 课程头部信息 -->
      <div class="content-header">
        <h1 class="content-title">{{ courseInfo.title }}</h1>
        <p class="content-description">{{ courseInfo.description }}</p>

        <!-- 课程标签 -->
        <div class="content-tags">
          <el-tag
            v-if="courseInfo.type === 'video'"
            type="success"
            size="large"
          >
            <el-icon><VideoPlay /></el-icon>
            视频课程
          </el-tag>
          <el-tag
            v-else-if="courseInfo.type === 'pdf'"
            type="warning"
            size="large"
          >
            <el-icon><Document /></el-icon>
            文档资料
          </el-tag>
        </div>
      </div>

      <!-- 视频播放器 -->
      <div v-if="courseInfo.type === 'video'" class="video-container">
        <div class="video-player">
          <video
            ref="videoPlayer"
            :src="courseInfo.videoUrl"
            controls
            width="100%"
            height="500"
            poster="data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iODAwIiBoZWlnaHQ9IjQ1MCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KICA8cmVjdCB3aWR0aD0iMTAwJSIgaGVpZ2h0PSIxMDAlIiBmaWxsPSIjMzMzIiAvPgogIDx0ZXh0IHg9IjUwJSIgeT0iNTAlIiBmb250LXNpemU9IjI0IiBmaWxsPSIjZmZmIiB0ZXh0LWFuY2hvcj0ibWlkZGxlIiBkeT0iLjNlbSI+5Zu+54mH5L2/55SoPC90ZXh0Pgo8L3N2Zz4="
            @loadedmetadata="onVideoLoaded"
            @timeupdate="onTimeUpdate"
            @ended="onVideoEnded"
          >
            您的浏览器不支持视频播放
          </video>
        </div>

        <!-- 视频控制栏 -->
        <div class="video-controls">
          <div class="playback-speed">
            <span>播放速度：</span>
            <el-select v-model="playbackSpeed" size="small" @change="changeSpeed">
              <el-option label="0.5x" :value="0.5" />
              <el-option label="0.75x" :value="0.75" />
              <el-option label="1x" :value="1" />
              <el-option label="1.25x" :value="1.25" />
              <el-option label="1.5x" :value="1.5" />
              <el-option label="2x" :value="2" />
            </el-select>
          </div>

          <div class="video-progress">
            <span>学习进度：{{ formatTime(currentTime) }} / {{ formatTime(duration) }}</span>
            <el-progress
              :percentage="videoProgress"
              :stroke-width="6"
              style="margin-top: 8px"
            />
          </div>
        </div>
      </div>

      <!-- PDF 查看器 -->
      <div v-else-if="courseInfo.type === 'pdf'" class="pdf-container">
        <div class="pdf-viewer">
          <iframe
            :src="courseInfo.pdfUrl"
            width="100%"
            height="800px"
            frameborder="0"
          ></iframe>
        </div>

        <!-- PDF 控制栏 -->
        <div class="pdf-controls">
          <el-button-group>
            <el-button @click="previousPage" :disabled="currentPage <= 1">
              <el-icon><ArrowLeft /></el-icon>
              上一页
            </el-button>
            <el-button>
              {{ currentPage }} / {{ totalPages }}
            </el-button>
            <el-button @click="nextPage" :disabled="currentPage >= totalPages">
              下一页
              <el-icon><ArrowRight /></el-icon>
            </el-button>
          </el-button-group>

          <el-button @click="downloadPdf">
            <el-icon><Download /></el-icon>
            下载PDF
          </el-button>
        </div>
      </div>

      <!-- 课程笔记 -->
      <div class="course-notes">
        <h3>课程笔记</h3>
        <el-input
          v-model="note"
          type="textarea"
          :rows="4"
          placeholder="在这里记录你的学习笔记..."
          @blur="saveNote"
        />
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-else-if="loading" class="loading-container">
      <el-skeleton :rows="10" animated />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import {
  ArrowLeft, ArrowRight, VideoPlay, Document, Download
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()

// 响应式数据
const loading = ref(true)
const courseInfo = ref(null)
const videoPlayer = ref(null)
const currentTime = ref(0)
const duration = ref(0)
const playbackSpeed = ref(1)
const videoProgress = ref(0)
const note = ref('')
const currentPage = ref(1)
const totalPages = ref(10)

// 返回上一页
const goBack = () => {
  router.go(-1)
}

// 视频加载完成
const onVideoLoaded = () => {
  duration.value = videoPlayer.value.duration
}

// 视频时间更新
const onTimeUpdate = () => {
  currentTime.value = videoPlayer.value.currentTime
  videoProgress.value = Math.round((currentTime.value / duration.value) * 100)

  // 保存播放进度
  localStorage.setItem(`video_progress_${route.params.contentId}`, currentTime.value)
}

// 视频播放结束
const onVideoEnded = () => {
  ElMessage.success('视频播放完成！')
  // 标记课程为已完成
  markCourseCompleted()
}

// 改变播放速度
const changeSpeed = (speed) => {
  videoPlayer.value.playbackRate = speed
}

// PDF 上一页
const previousPage = () => {
  if (currentPage.value > 1) {
    currentPage.value--
  }
}

// PDF 下一页
const nextPage = () => {
  if (currentPage.value < totalPages.value) {
    currentPage.value++
  }
}

// 下载PDF
const downloadPdf = () => {
  const link = document.createElement('a')
  link.href = courseInfo.value.pdfUrl
  link.download = courseInfo.value.title + '.pdf'
  link.click()
}

// 保存笔记
const saveNote = () => {
  localStorage.setItem(`course_note_${route.params.contentId}`, note.value)
  ElMessage.success('笔记已保存')
}

// 标记课程完成
const markCourseCompleted = () => {
  // 这里应该调用 store 方法更新课程状态
  ElMessage.success('恭喜完成本课程！')
}

// 格式化时间
const formatTime = (seconds) => {
  const minutes = Math.floor(seconds / 60)
  const remainingSeconds = Math.floor(seconds % 60)
  return `${minutes}:${remainingSeconds.toString().padStart(2, '0')}`
}

// 初始化课程信息
const initCourseInfo = () => {
  const contentId = route.params.contentId

  // 模拟课程数据
  const courses = {
    'content-1': {
      id: 'content-1',
      title: '1. Python 简介',
      description: '了解 Python 的历史、特点和应用领域',
      type: 'video',
      videoUrl: '/api/mock/video/python-intro.mp4'
    },
    'content-2': {
      id: 'content-2',
      title: '2. 安装 Python 环境',
      description: '学习如何在不同操作系统上安装 Python',
      type: 'pdf',
      pdfUrl: '/api/mock/pdf/python-install.pdf'
    },
    'content-3': {
      id: 'content-3',
      title: '3. Python 基础语法',
      description: '掌握 Python 的基本语法规则',
      type: 'video',
      videoUrl: '/api/mock/video/python-basic-syntax.mp4'
    },
    'content-4': {
      id: 'content-4',
      title: '4. 变量和数据类型',
      description: '学习 Python 中的变量和各种数据类型',
      type: 'video',
      videoUrl: '/api/mock/video/python-data-types.mp4'
    }
  }

  setTimeout(() => {
    courseInfo.value = courses[contentId] || null

    // 加载保存的笔记
    const savedNote = localStorage.getItem(`course_note_${contentId}`)
    if (savedNote) {
      note.value = savedNote
    }

    // 如果是视频，恢复播放进度
    if (courseInfo.value?.type === 'video') {
      const savedProgress = localStorage.getItem(`video_progress_${contentId}`)
      if (savedProgress) {
        setTimeout(() => {
          videoPlayer.value.currentTime = parseFloat(savedProgress)
        }, 1000)
      }
    }

    loading.value = false
  }, 500)
}

onMounted(() => {
  initCourseInfo()
})

onUnmounted(() => {
  // 保存当前播放进度
  if (videoPlayer.value && courseInfo.value?.type === 'video') {
    localStorage.setItem(`video_progress_${route.params.contentId}`, currentTime.value)
  }
})
</script>

<style scoped>
.course-content-detail {
  padding: 24px;
  background: #fff;
  min-height: 100%;
}

/* 返回栏 */
.back-bar {
  margin-bottom: 24px;
}

/* 内容容器 */
.content-container {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* 内容头部 */
.content-header {
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.content-title {
  margin: 0 0 8px 0;
  font-size: 28px;
  color: #333;
  font-weight: 500;
}

.content-description {
  margin: 0 0 16px 0;
  font-size: 16px;
  color: #666;
  line-height: 1.6;
}

.content-tags {
  display: flex;
  gap: 12px;
}

/* 视频容器 */
.video-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.video-player {
  background: #000;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.video-player video {
  display: block;
  width: 100%;
  height: auto;
}

.video-controls {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
}

.playback-speed {
  display: flex;
  align-items: center;
  gap: 8px;
}

.video-progress {
  flex: 1;
  margin-left: 24px;
}

/* PDF容器 */
.pdf-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.pdf-viewer {
  background: #fff;
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  overflow: hidden;
}

.pdf-controls {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
}

/* 课程笔记 */
.course-notes {
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
}

.course-notes h3 {
  margin: 0 0 12px 0;
  font-size: 18px;
  color: #333;
}

/* 加载状态 */
.loading-container {
  padding: 40px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .course-content-detail {
    padding: 16px;
  }

  .content-title {
    font-size: 24px;
  }

  .video-controls {
    flex-direction: column;
    gap: 16px;
  }

  .video-progress {
    margin-left: 0;
    width: 100%;
  }

  .pdf-controls {
    flex-direction: column;
    gap: 12px;
  }
}
</style>