<template>
  <div class="course-list">
    <!-- 课程系列列表头部 -->
    <div class="list-header">
      <h2>课程列表</h2>
    </div>

    <!-- 课程系列列表 -->
    <div class="courses-container">
      <div v-if="teachingStore.loading" class="loading-container">
        <el-skeleton :rows="5" animated />
      </div>

      <div v-else-if="courseSeries.length === 0" class="empty-container">
        <el-empty description="暂无课程内容" />
      </div>

      <div v-else class="course-items">
        <div v-for="(series, index) in courseSeries" :key="series.id" class="course-item" @click="enterSeries(series)">
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
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  Folder, ArrowRight
} from '@element-plus/icons-vue'
// 导入 Pinia store
import { useTeachingStore } from '@/store'

const router = useRouter()

// 使用 Pinia store
const teachingStore = useTeachingStore()
const courseSeries = ref([])

// 进入课程系列
const enterSeries = (series) => {
  // 跳转到课程系列详情页，只传递必要的参数
  const courseId = router.currentRoute.value.params.id

  // 使用 query 参数传递必要的系列信息
  router.push({
    path: `/teaching/course/${courseId}/learn/series/${series.id}`,
    query: {
      id: series.id,
      title: series.title,
      description: series.description
    }
  })
}

// 初始化课程系列列表
const initCourseSeries = async () => {
  try {
    // 获取当前课程ID
    const courseId = router.currentRoute.value.params.id

    // 调用 store 的方法获取课程数据
    const response = await teachingStore.fetchCourses(courseId)

    if (response.code === 200) {
      ElMessage.success(response.message || '课程列表加载成功')
      console.log('课程数据:', response.data)
      // 将返回的课程数据作为课程系列
      courseSeries.value = response.data.map(course => ({
        id: course.id,
        title: course.name,
        description: course.description
      }))
    } else {
      ElMessage.error(response.message || '加载课程列表失败')
    }
  } catch (error) {
    console.error('加载课程列表失败:', error)
    ElMessage.error('加载课程列表失败，请重试')
  }
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
  border-bottom: 1px solid var(--border-light);
}

.list-header h2 {
  margin: 0;
  font-size: 24px;
  color: var(--primary-dark);
  font-weight: 500;
}

.header-info {
  display: flex;
  align-items: center;
  gap: 16px;
}

.course-count {
  color: var(--text-regular);
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
  border: 1px solid var(--border-light);
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.course-item:hover {
  border-color: var(--primary-light);
  box-shadow: 0 4px 12px var(--primary-alpha-10);
  transform: translateY(-2px);
}

.course-item.completed {
  background: var(--success-alpha-10);
  border-color: var(--success);
}

/* 课程图标 */
.course-icon {
  width: 60px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, var(--bg-primary-light), var(--primary-lightest));
  border-radius: 12px;
  color: var(--primary);
  flex-shrink: 0;
}

.course-item.completed .course-icon {
  background: linear-gradient(135deg, #f6ffed, #d9f7be);
  color: var(--success);
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
  color: var(--primary-dark);
}

.course-description {
  margin: 0;
  font-size: 14px;
  color: var(--text-regular);
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
  color: var(--text-regular);
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
  color: var(--text-secondary);
}

.arrow-icon {
  font-size: 20px;
  color: var(--text-secondary);
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