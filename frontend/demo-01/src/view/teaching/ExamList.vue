<template>
  <div class="exam-list">
    <div class="list-header">
      <h2>考试中心</h2>
    </div>

    <!-- 筛选和搜索 -->
    <div class="filter-section">
      <div class="filter-left">
        <el-select v-model="statusFilter" placeholder="状态筛选" style="width: 150px">
          <el-option label="全部" value="all"></el-option>
          <el-option label="未开始" value="draft"></el-option>
          <el-option label="进行中" value="published"></el-option>
          <el-option label="已结束" value="ended"></el-option>
        </el-select>
        <el-select v-model="typeFilter" placeholder="类型筛选" style="width: 150px">
          <el-option label="全部" value="all"></el-option>
          <el-option label="测验" value="quiz"></el-option>
          <el-option label="考试" value="exam"></el-option>
        </el-select>
      </div>
      <div class="search-box">
        <el-input
          v-model="searchQuery"
          placeholder="搜索考试..."
          style="width: 300px"
          clearable
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
      </div>
    </div>

    <!-- 考试统计 -->
    <div class="stats-section">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-icon" style="background: linear-gradient(135deg, var(--primary), var(--primary-dark))">
                <el-icon size="24"><Document /></el-icon>
              </div>
              <div class="stat-info">
                <h3>{{ examStats.total }}</h3>
                <p>全部考试</p>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-icon" style="background: linear-gradient(135deg, var(--primary-light), var(--primary))">
                <el-icon size="24"><Clock /></el-icon>
              </div>
              <div class="stat-info">
                <h3>{{ examStats.pending || 0 }}</h3>
                <p>待参加</p>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-icon" style="background: linear-gradient(135deg, #4facfe, #00f2fe)">
                <el-icon size="24"><Promotion /></el-icon>
              </div>
              <div class="stat-info">
                <h3>{{ examStats.published }}</h3>
                <p>已发布</p>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-icon" style="background: linear-gradient(135deg, #43e97b, #38f9d7)">
                <el-icon size="24"><CircleCheck /></el-icon>
              </div>
              <div class="stat-info">
                <h3>{{ examStats.completed || 0 }}</h3>
                <p>已完成</p>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 考试列表 -->
    <div class="exam-content">
      <div v-if="filteredExams.length === 0" class="empty-state">
        <el-empty description="暂无考试" />
      </div>
      <div v-else class="exam-grid">
        <div
          v-for="exam in paginatedExams"
          :key="exam.id"
          class="exam-card"
          @click="enterExam(exam.id)"
        >
          <div class="card-header">
            <el-tag :type="getTypeTagType(exam.type)" size="small">
              {{ exam.type === 'quiz' ? '测验' : '考试' }}
            </el-tag>
            <el-tag v-if="exam.status === 'published'" type="success" size="small">
              可参加
            </el-tag>
          </div>
          <div class="card-content">
            <h3 class="exam-title">{{ exam.title }}</h3>
            <p class="exam-desc">{{ exam.description }}</p>
            <div class="exam-meta">
              <div class="meta-item">
                <el-icon><Clock /></el-icon>
                <span>{{ exam.duration }}分钟</span>
              </div>
              <div class="meta-item">
                <el-icon><Document /></el-icon>
                <span>{{ exam.totalScore }}分</span>
              </div>
              <div class="meta-item">
                <el-icon><User /></el-icon>
                <span>{{ exam.publisher }}</span>
              </div>
            </div>
          </div>
          <div class="card-footer">
            <div class="exam-time">
              <span class="time-label">考试时间：</span>
              <span class="time-value">{{ formatTime(exam.startTime) }} - {{ formatTime(exam.endTime) }}</span>
            </div>
            <div class="exam-status">
              <el-tag
                :type="getStatusTagType(exam.status)"
                :effect="exam.status === 'published' ? 'dark' : 'plain'"
              >
                {{ getStatusText(exam.status) }}
              </el-tag>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 分页 -->
    <div class="pagination-wrapper">
      <el-pagination
        v-model:current-page="currentPage"
        :page-size="pageSize"
        :total="filteredExams.length"
        layout="prev, pager, next, jumper, total"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useExamStore } from '@/store'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Search, Document, Promotion, CircleCheck,
  Clock, User
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const examStore = useExamStore()

// 响应式数据
const searchQuery = ref('')
const statusFilter = ref('all')
const typeFilter = ref('all')
const currentPage = ref(1)
const pageSize = ref(12)

// 计算属性
const courseId = computed(() => route.params.courseId)
const exams = computed(() => examStore.getExamsByCourseId(courseId.value))
const examStats = computed(() => examStore.getExamStats(courseId.value))

const filteredExams = computed(() => {
  let result = [...exams.value]

  // 状态筛选
  if (statusFilter.value !== 'all') {
    result = result.filter(e => e.status === statusFilter.value)
  }

  // 类型筛选
  if (typeFilter.value !== 'all') {
    result = result.filter(e => e.type === typeFilter.value)
  }

  // 搜索筛选
  if (searchQuery.value.trim()) {
    const query = searchQuery.value.toLowerCase()
    result = result.filter(e =>
      e.title.toLowerCase().includes(query) ||
      e.description.toLowerCase().includes(query) ||
      e.publisher.toLowerCase().includes(query)
    )
  }

  return result
})

const paginatedExams = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredExams.value.slice(start, end)
})

// 方法
const enterExam = (examId) => {
  router.push(`/teaching/course/${courseId.value}/exam/${examId}`)
}


const getTypeTagType = (type) => {
  return type === 'quiz' ? 'success' : 'warning'
}

const getStatusTagType = (status) => {
  const typeMap = {
    'draft': 'info',
    'published': 'success',
    'ended': 'info'
  }
  return typeMap[status] || 'info'
}

const getStatusText = (status) => {
  const textMap = {
    'draft': '未开始',
    'published': '进行中',
    'ended': '已结束'
  }
  return textMap[status] || '未知'
}

const formatTime = (timeStr) => {
  const date = new Date(timeStr)
  return date.toLocaleString('zh-CN', {
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 生命周期
onMounted(() => {
  // 加载数据
})
</script>

<style scoped>
.exam-list {
  padding: 20px;
  background: #fff;
  min-height: calc(100vh - 60px);
}

.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.list-header h2 {
  margin: 0;
  color: var(--primary-dark);
  font-size: 24px;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.filter-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 15px;
  background: var(--bg-light);
  border-radius: 8px;
}

.filter-left {
  display: flex;
  gap: 10px;
}

.stats-section {
  margin-bottom: 30px;
}

.stat-card {
  cursor: pointer;
  transition: transform 0.3s;
}

.stat-card:hover {
  transform: translateY(-2px);
}

.stat-card :deep(.el-card__body) {
  padding: 20px;
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 15px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.stat-info h3 {
  margin: 0 0 5px 0;
  font-size: 24px;
  color: var(--primary-dark);
}

.stat-info p {
  margin: 0;
  font-size: 14px;
  color: var(--text-regular);
}

.exam-content {
  min-height: 400px;
}

.empty-state {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 300px;
}

.exam-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(380px, 1fr));
  gap: 20px;
}

.exam-card {
  padding: 20px;
  background: #fff;
  border: 1px solid var(--border-light);
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s;
}

.exam-card:hover {
  box-shadow: 0 4px 20px var(--primary-alpha-10);
  transform: translateY(-2px);
  border-color: var(--primary-lighter);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.card-content {
  margin-bottom: 20px;
}

.exam-title {
  margin: 0 0 10px 0;
  font-size: 18px;
  color: var(--primary-dark);
  font-weight: 600;
}

.exam-desc {
  color: var(--text-regular);
  font-size: 14px;
  line-height: 1.6;
  margin: 0 0 15px 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.exam-meta {
  display: flex;
  gap: 20px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 5px;
  color: var(--text-secondary);
  font-size: 13px;
}

.card-footer {
  border-top: 1px solid var(--border-light);
  padding-top: 15px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.exam-time {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.time-label {
  font-size: 12px;
  color: var(--text-secondary);
}

.time-value {
  font-size: 13px;
  color: var(--text-regular);
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .exam-list {
    padding: 10px;
  }

  .filter-section {
    flex-direction: column;
    gap: 15px;
  }

  .filter-left {
    width: 100%;
    justify-content: space-between;
  }

  .search-box {
    width: 100%;
  }

  .search-box :deep(.el-input) {
    width: 100% !important;
  }

  .stats-section :deep(.el-col) {
    margin-bottom: 10px;
  }

  .exam-grid {
    grid-template-columns: 1fr;
  }

  .exam-meta {
    flex-wrap: wrap;
    gap: 10px;
  }

  .card-footer {
    flex-direction: column;
    gap: 10px;
    align-items: flex-start;
  }
}
</style>