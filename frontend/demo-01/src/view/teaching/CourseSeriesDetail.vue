<template>
  <div class="course-series-detail">
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

    <!-- 系列头部信息 -->
    <div class="series-header" v-if="seriesInfo">
      <div class="header-left">
        <h1 class="series-title">{{ seriesInfo.title }}</h1>
        <p class="series-description">{{ seriesInfo.description }}</p>

        <!-- 统计信息 -->
        <div class="series-stats">
          <el-tag type="success" size="large">
            <el-icon><VideoPlay /></el-icon>
            {{ seriesInfo.videoCount }} 个视频
          </el-tag>
          <el-tag type="warning" size="large">
            <el-icon><Document /></el-icon>
            {{ seriesInfo.pdfCount }} 个文档
          </el-tag>
          <el-tag type="info" size="large">
            共 {{ seriesInfo.contents.length }} 个内容
          </el-tag>
        </div>
      </div>

      <!-- 系列进度 -->
      <div class="header-right">
        <div class="progress-circle">
          <el-progress
            type="circle"
            :percentage="seriesInfo.progress"
            :width="120"
            :stroke-width="8"
          >
            <template #default="{ percentage }">
              <span class="progress-text">{{ percentage }}%</span>
            </template>
          </el-progress>
        </div>
        <p class="progress-desc">{{ seriesInfo.completedCount }}/{{ seriesInfo.contents.length }} 已完成</p>
      </div>
    </div>

    <!-- 课程内容列表 -->
    <div class="contents-section">
      <h2>课程内容</h2>

      <div v-if="loading" class="loading-container">
        <el-skeleton :rows="5" animated />
      </div>

      <div v-else class="content-items">
        <div
          v-for="(content, index) in seriesInfo?.contents"
          :key="content.id"
          class="content-item"
          :class="{
            completed: content.completed,
            locked: content.locked
          }"
          @click="enterContent(content)"
        >
          <!-- 序号和图标 -->
          <div class="content-index">
            <span class="index-number">{{ index + 1 }}</span>
            <el-icon class="content-icon">
              <VideoPlay v-if="content.type === 'video'" />
              <Document v-else-if="content.type === 'pdf'" />
              <Files v-else />
            </el-icon>
          </div>

          <!-- 内容信息 -->
          <div class="content-info">
            <h3 class="content-title">{{ content.title }}</h3>
            <p class="content-description">{{ content.description }}</p>

            <!-- 元信息 -->
            <div class="content-meta">
              <el-tag
                :type="content.type === 'video' ? 'success' : 'warning'"
                size="small"
                effect="light"
              >
                {{ content.type === 'video' ? '视频' : '文档' }}
              </el-tag>
              <span v-if="content.duration" class="duration">
                <el-icon><Clock /></el-icon>
                {{ content.duration }}
              </span>
              <span v-if="content.size" class="size">
                <el-icon><Folder /></el-icon>
                {{ content.size }}
              </span>
            </div>
          </div>

          <!-- 状态 -->
          <div class="content-status">
            <el-icon v-if="content.completed" class="completed-icon">
              <Check />
            </el-icon>
            <el-icon v-else-if="content.locked" class="lock-icon">
              <Lock />
            </el-icon>
            <span v-else class="status-text">未开始</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import {
  ArrowLeft, VideoPlay, Document, Files, Check, Lock, Clock, Folder
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()

// 响应式数据
const loading = ref(true)
const seriesInfo = ref(null)

// 返回上一页
const goBack = () => {
  router.go(-1)
}

// 进入内容
const enterContent = (content) => {
  if (content.locked) {
    ElMessage.warning('该内容尚未解锁')
    return
  }

  const courseId = route.params.id
  router.push(`/teaching/course/${courseId}/learn/content/${content.id}`)
}

// 初始化系列详情
const initSeriesDetail = () => {
  loading.value = true
  const seriesId = route.params.seriesId

  // 模拟系列详情数据
  setTimeout(() => {
    if (seriesId === 'series-1') {
      seriesInfo.value = {
        id: 'series-1',
        title: 'Python 基础入门',
        description: '从零开始学习 Python 编程语言的基础知识，包括语法、数据类型、控制流程等核心概念',
        videoCount: 5,
        pdfCount: 3,
        totalCount: 8,
        completedCount: 3,
        progress: 38,
        contents: [
          {
            id: 'content-1',
            title: '1. Python 简介',
            description: '了解 Python 的历史、特点和应用领域',
            type: 'video',
            duration: '15分钟',
            completed: true
          },
          {
            id: 'content-2',
            title: '2. 安装 Python 环境',
            description: '学习如何在不同操作系统上安装 Python',
            type: 'pdf',
            size: '2.3MB',
            completed: true
          },
          {
            id: 'content-3',
            title: '3. Python 基础语法',
            description: '掌握 Python 的基本语法规则',
            type: 'video',
            duration: '45分钟',
            completed: true
          },
          {
            id: 'content-4',
            title: '4. 变量和数据类型',
            description: '学习 Python 中的变量和各种数据类型',
            type: 'video',
            duration: '60分钟',
            completed: false
          },
          {
            id: 'content-5',
            title: '5. 数据类型练习',
            description: '通过实例练习 Python 数据类型的使用',
            type: 'pdf',
            size: '1.5MB',
            completed: false
          },
          {
            id: 'content-6',
            title: '6. 运算符和表达式',
            description: '学习 Python 中的各种运算符',
            type: 'video',
            duration: '40分钟',
            completed: false
          },
          {
            id: 'content-7',
            title: '7. 字符串操作',
            description: '掌握 Python 字符串的常用操作',
            type: 'video',
            duration: '35分钟',
            completed: false,
            locked: true
          },
          {
            id: 'content-8',
            title: '8. 基础语法总结',
            description: 'Python 基础语法知识点总结',
            type: 'pdf',
            size: '3.1MB',
            completed: false,
            locked: true
          }
        ]
      }
    } else {
      // 其他系列的默认数据
      seriesInfo.value = {
        id: seriesId,
        title: 'Python 进阶教程',
        description: '深入学习 Python 的高级特性和编程技巧',
        videoCount: 8,
        pdfCount: 5,
        totalCount: 13,
        completedCount: 0,
        progress: 0,
        contents: []
      }
    }
    loading.value = false
  }, 500)
}

onMounted(() => {
  initSeriesDetail()
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
  background: linear-gradient(135deg, #fff0f6, #ffd6e7);
  border-radius: 12px;
  margin-bottom: 32px;
}

.header-left {
  flex: 1;
}

.series-title {
  margin: 0 0 12px 0;
  font-size: 28px;
  color: #333;
  font-weight: 500;
}

.series-description {
  margin: 0 0 20px 0;
  font-size: 16px;
  color: #666;
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
  flex-direction: column;
  align-items: center;
  gap: 12px;
  margin-left: 40px;
}

.progress-circle .progress-text {
  font-size: 20px;
  font-weight: 500;
  color: #d63384;
}

.progress-desc {
  margin: 0;
  font-size: 14px;
  color: #666;
}

/* 内容部分 */
.contents-section h2 {
  margin: 0 0 20px 0;
  font-size: 20px;
  color: #333;
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
  border: 1px solid #e8e8e8;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.content-item:hover {
  border-color: #ff7ab1;
  box-shadow: 0 4px 12px rgba(255, 122, 177, 0.1);
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
  border-color: #e8e8e8;
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
  background: #f5f5f5;
  border-radius: 50%;
  font-weight: 500;
  color: #666;
  font-size: 14px;
}

.content-icon {
  position: absolute;
  right: -16px;
  font-size: 20px;
  color: #d63384;
}

/* 内容信息 */
.content-info {
  flex: 1;
}

.content-title {
  margin: 0 0 8px 0;
  font-size: 16px;
  font-weight: 500;
  color: #333;
}

.content-description {
  margin: 0 0 12px 0;
  font-size: 14px;
  color: #666;
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
  color: #999;
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
  color: #999;
}

.status-text {
  font-size: 14px;
  color: #999;
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

  .series-title {
    font-size: 24px;
  }

  .content-item {
    padding: 16px;
  }

  .content-icon {
    right: -12px;
    font-size: 18px;
  }
}
</style>