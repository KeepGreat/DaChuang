<template>
  <div class="course-list">
    <!-- 课程系列列表头部 -->
    <div class="list-header">
      <h2>课程系列</h2>
      <div class="header-info">
        <span class="course-count">共 {{ courseSeries.length }} 个系列</span>
      </div>
    </div>

    <!-- 课程系列列表 -->
    <div class="courses-container">
      <div v-if="loading" class="loading-container">
        <el-skeleton :rows="5" animated />
      </div>

      <div v-else-if="courseSeries.length === 0" class="empty-container">
        <el-empty description="暂无课程内容" />
      </div>

      <div v-else class="course-items">
        <div
          v-for="(series, index) in courseSeries"
          :key="series.id"
          class="course-item"
          @click="enterSeries(series)"
        >
          <!-- 系列图标 -->
          <div class="course-icon">
            <el-icon :size="32">
              <Folder />
            </el-icon>
          </div>

          <!-- 系列信息 -->
          <div class="course-info">
            <h3 class="course-title">{{ series.title }}</h3>
            <p class="course-description">{{ series.description }}</p>

            <!-- 内容统计 -->
            <div class="content-stats">
              <el-tag size="small" effect="light" type="success">
                <el-icon><VideoPlay /></el-icon>
                {{ series.videoCount }} 个视频
              </el-tag>
              <el-tag size="small" effect="light" type="warning">
                <el-icon><Document /></el-icon>
                {{ series.pdfCount }} 个文档
              </el-tag>
              <el-tag size="small" effect="light" type="info">
                共 {{ series.totalCount }} 个内容
              </el-tag>
            </div>

            <!-- 系列进度 -->
            <div class="course-progress">
              <el-progress
                :percentage="series.progress"
                :stroke-width="4"
                :show-text="false"
              />
              <span class="progress-text">{{ series.completedCount }}/{{ series.totalCount }} 已完成</span>
            </div>
          </div>

          <!-- 系列状态 -->
          <div class="course-status">
            <el-icon class="arrow-icon">
              <ArrowRight />
            </el-icon>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  VideoPlay, Document, Files, Check, Lock, Folder, ArrowRight
} from '@element-plus/icons-vue'

const router = useRouter()

// 响应式数据
const loading = ref(false)
const courseSeries = ref([])

// 进入课程系列
const enterSeries = (series) => {
  // 跳转到课程系列详情页
  const courseId = router.currentRoute.value.params.id
  router.push(`/teaching/course/${courseId}/learn/series/${series.id}`)
}

// 初始化课程系列列表
const initCourseSeries = () => {
  loading.value = true

  // 模拟课程系列数据
  setTimeout(() => {
    courseSeries.value = [
      {
        id: 'series-1',
        title: 'Python 基础入门',
        description: '从零开始学习 Python 编程语言的基础知识',
        videoCount: 5,
        pdfCount: 3,
        totalCount: 8,
        completedCount: 3,
        progress: 38
      },
      {
        id: 'series-2',
        title: 'Python 进阶教程',
        description: '深入学习 Python 的高级特性和编程技巧',
        videoCount: 8,
        pdfCount: 5,
        totalCount: 13,
        completedCount: 0,
        progress: 0
      },
      {
        id: 'series-3',
        title: 'Python Web 开发',
        description: '使用 Python 进行 Web 应用开发的完整指南',
        videoCount: 12,
        pdfCount: 8,
        totalCount: 20,
        completedCount: 5,
        progress: 25
      },
      {
        id: 'series-4',
        title: 'Python 数据分析',
        description: '掌握使用 Python 进行数据分析的核心技能',
        videoCount: 10,
        pdfCount: 6,
        totalCount: 16,
        completedCount: 0,
        progress: 0
      }
    ]
    loading.value = false
  }, 500)
}

onMounted(() => {
  initCourseSeries()
})
</script>

<style scoped>
.course-list {
  padding: 24px;
  background: #fff;
  min-height: 100%;
}

/* 列表头部 */
.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.list-header h2 {
  margin: 0;
  font-size: 24px;
  color: #333;
  font-weight: 500;
}

.header-info {
  display: flex;
  align-items: center;
  gap: 16px;
}

.course-count {
  color: #666;
  font-size: 14px;
}

/* 课程容器 */
.courses-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* 加载状态 */
.loading-container {
  padding: 20px;
}

/* 空状态 */
.empty-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 300px;
}

/* 课程项 */
.course-item {
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

.course-item:hover {
  border-color: #ff7ab1;
  box-shadow: 0 4px 12px rgba(255, 122, 177, 0.1);
  transform: translateY(-2px);
}

.course-item.completed {
  background: #f6ffed;
  border-color: #52c41a;
}

/* 课程图标 */
.course-icon {
  width: 60px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #fff0f6, #ffd6e7);
  border-radius: 12px;
  color: #d63384;
  flex-shrink: 0;
}

.course-item.completed .course-icon {
  background: linear-gradient(135deg, #f6ffed, #d9f7be);
  color: #52c41a;
}

/* 课程信息 */
.course-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.course-title {
  margin: 0;
  font-size: 16px;
  font-weight: 500;
  color: #333;
}

.course-description {
  margin: 0;
  font-size: 14px;
  color: #666;
  line-height: 1.5;
}

/* 内容统计 */
.content-stats {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  margin: 8px 0;
}

.content-stats .el-tag {
  display: flex;
  align-items: center;
  gap: 4px;
}

/* 课程进度 */
.course-progress {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-top: 4px;
}

.progress-text {
  font-size: 12px;
  color: #666;
  min-width: 40px;
}

/* 课程状态 */
.course-status {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
}

.completed-icon {
  font-size: 24px;
  color: #52c41a;
}

.lock-icon {
  font-size: 20px;
  color: #999;
}

.arrow-icon {
  font-size: 20px;
  color: #999;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .course-list {
    padding: 16px;
  }

  .course-item {
    padding: 16px;
  }

  .course-icon {
    width: 50px;
    height: 50px;
  }

  .course-icon .el-icon {
    font-size: 24px;
  }
}
</style>