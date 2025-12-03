<template>
  <div class="exam-detail">
    <div v-if="exam" class="detail-container">
      <!-- 返回按钮 -->
      <div class="back-section">
        <el-button @click="goBack" :icon="ArrowLeft">返回考试列表</el-button>
      </div>

      <!-- 考试信息 -->
      <div class="exam-info">
        <div class="info-header">
          <div class="exam-title-section">
            <h1 class="exam-title">{{ exam.title }}</h1>
            <div class="exam-badges">
              <el-tag :type="getTypeTagType(exam.type)" size="large">
                {{ exam.type === 'quiz' ? '测验' : '考试' }}
              </el-tag>
              <el-tag
                :type="getStatusTagType(exam.status)"
                size="large"
                :effect="exam.status === 'published' ? 'dark' : 'plain'"
              >
                {{ getStatusText(exam.status) }}
              </el-tag>
            </div>
          </div>
          <div class="exam-actions">
            <el-button
              v-if="exam.status === 'draft'"
              type="primary"
              @click="publishExam"
            >
              发布考试
            </el-button>
            <el-button
              v-if="exam.status === 'published'"
              type="warning"
              @click="endExam"
            >
              结束考试
            </el-button>
            <el-dropdown @command="handleAction">
              <el-button>
                更多操作<el-icon class="el-icon--right"><arrow-down /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="edit">编辑考试</el-dropdown-item>
                  <el-dropdown-item command="duplicate">复制考试</el-dropdown-item>
                  <el-dropdown-item command="export">导出成绩</el-dropdown-item>
                  <el-dropdown-item command="delete" divided>删除考试</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>
        <div class="exam-description">{{ exam.description }}</div>
      </div>

      <!-- 考试详情 -->
      <el-row :gutter="20">
        <el-col :span="16">
          <!-- 基本信息 -->
          <el-card class="info-card">
            <template #header>
              <h3>基本信息</h3>
            </template>
            <el-descriptions :column="2" border>
              <el-descriptions-item label="考试类型">
                {{ exam.type === 'quiz' ? '测验' : '考试' }}
              </el-descriptions-item>
              <el-descriptions-item label="考试时长">
                {{ exam.duration }} 分钟
              </el-descriptions-item>
              <el-descriptions-item label="总分">
                {{ exam.totalScore }} 分
              </el-descriptions-item>
              <el-descriptions-item label="及格分">
                {{ exam.passScore }} 分
              </el-descriptions-item>
              <el-descriptions-item label="尝试次数">
                {{ exam.attempts }} 次
              </el-descriptions-item>
              <el-descriptions-item label="时间限制">
                <el-tag :type="exam.timeLimit ? 'success' : 'info'">
                  {{ exam.timeLimit ? '是' : '否' }}
                </el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="随机顺序">
                <el-tag :type="exam.randomOrder ? 'success' : 'info'">
                  {{ exam.randomOrder ? '是' : '否' }}
                </el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="显示答案">
                <el-tag :type="exam.showAnswers ? 'success' : 'info'">
                  {{ exam.showAnswers ? '是' : '否' }}
                </el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="发布者" :span="2">
                {{ exam.publisher }}
              </el-descriptions-item>
              <el-descriptions-item label="发布时间" :span="2">
                {{ formatFullTime(exam.publishTime) }}
              </el-descriptions-item>
            </el-descriptions>
          </el-card>

          <!-- 考试时间 -->
          <el-card class="info-card">
            <template #header>
              <h3>考试时间</h3>
            </template>
            <div class="time-info">
              <div class="time-item">
                <span class="label">开始时间：</span>
                <span class="value">{{ formatFullTime(exam.startTime) }}</span>
              </div>
              <div class="time-item">
                <span class="label">结束时间：</span>
                <span class="value">{{ formatFullTime(exam.endTime) }}</span>
              </div>
              <div class="time-status">
                <el-progress
                  v-if="exam.status === 'published'"
                  :percentage="timeProgress"
                  :status="timeProgress >= 100 ? 'success' : null"
                />
                <span v-else class="status-text">
                  {{ exam.status === 'draft' ? '尚未发布' : '已结束' }}
                </span>
              </div>
            </div>
          </el-card>

          <!-- 题目列表 -->
          <el-card class="info-card">
            <template #header>
              <div class="card-header">
                <h3>题目列表 ({{ exam.questions.length }} 题)</h3>
                <el-button type="primary" size="small" @click="addQuestion">
                  添加题目
                </el-button>
              </div>
            </template>
            <div v-if="exam.questions.length === 0" class="empty-questions">
              <el-empty description="暂无题目">
                <el-button type="primary" @click="addQuestion">添加第一题</el-button>
              </el-empty>
            </div>
            <div v-else class="question-list">
              <div
                v-for="(question, index) in exam.questions"
                :key="question.id"
                class="question-item"
              >
                <div class="question-header">
                  <span class="question-index">第 {{ index + 1 }} 题</span>
                  <el-tag :type="getQuestionTypeTagType(question.type)">
                    {{ getQuestionTypeText(question.type) }}
                  </el-tag>
                  <span class="question-score">{{ question.score }} 分</span>
                </div>
                <div class="question-content">{{ question.question }}</div>
                <div v-if="question.type === 'single' || question.type === 'multiple'" class="question-options">
                  <div
                    v-for="option in question.options"
                    :key="option"
                    class="option-item"
                  >
                    <span class="option-label">{{ String.fromCharCode(65 + question.options.indexOf(option)) }}.</span>
                    <span class="option-text">{{ option }}</span>
                  </div>
                </div>
                <div class="question-actions">
                  <el-button text type="primary" size="small" @click="editQuestion(question)">
                    编辑
                  </el-button>
                  <el-button text type="danger" size="small" @click="deleteQuestion(index)">
                    删除
                  </el-button>
                </div>
              </div>
            </div>
          </el-card>
        </el-col>

        <el-col :span="8">
          <!-- 考试统计 -->
          <el-card class="stats-card">
            <template #header>
              <h3>考试统计</h3>
            </template>
            <div class="stats-content">
              <div class="stat-item">
                <div class="stat-number">{{ userResults.length }}</div>
                <div class="stat-label">参与人数</div>
              </div>
              <div class="stat-item">
                <div class="stat-number">{{ passRate }}%</div>
                <div class="stat-label">及格率</div>
              </div>
              <div class="stat-item">
                <div class="stat-number">{{ averageScore }}</div>
                <div class="stat-label">平均分</div>
              </div>
              <div class="stat-item">
                <div class="stat-number">{{ highestScore }}</div>
                <div class="stat-label">最高分</div>
              </div>
            </div>
          </el-card>

          <!-- 成绩排行榜 -->
          <el-card class="ranking-card">
            <template #header>
              <h3>成绩排行</h3>
            </template>
            <div v-if="ranking.length === 0" class="empty-ranking">
              <el-empty description="暂无考试记录" :image-size="100" />
            </div>
            <div v-else class="ranking-list">
              <div
                v-for="item in ranking"
                :key="item.id"
                class="ranking-item"
              >
                <div class="rank-badge" :class="getRankClass(item.rank)">
                  {{ item.rank }}
                </div>
                <div class="user-info">
                  <div class="user-name">{{ item.userName }}</div>
                  <div class="submit-time">{{ formatTime(item.submitTime) }}</div>
                </div>
                <div class="score-info">
                  <div class="score">{{ item.score }}</div>
                  <el-tag
                    :type="item.passStatus === 'passed' ? 'success' : 'danger'"
                    size="small"
                  >
                    {{ item.passStatus === 'passed' ? '通过' : '未通过' }}
                  </el-tag>
                </div>
              </div>
            </div>
          </el-card>

          <!-- 快速操作 -->
          <el-card class="actions-card">
            <template #header>
              <h3>快速操作</h3>
            </template>
            <div class="action-buttons">
              <el-button type="primary" @click="previewExam" style="width: 100%">
                <el-icon><View /></el-icon>
                预览考试
              </el-button>
              <el-button type="success" @click="batchGrade" style="width: 100%">
                <el-icon><Check /></el-icon>
                批量批改
              </el-button>
              <el-button type="info" @click="exportResults" style="width: 100%">
                <el-icon><Download /></el-icon>
                导出成绩
              </el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
    <div v-else class="loading">
      <el-skeleton :rows="5" animated />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useExamStore } from '@/store'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  ArrowLeft, ArrowDown, View, Check, Download
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const examStore = useExamStore()

// 计算属性
const examId = computed(() => route.params.examId)
const exam = computed(() => examStore.getExamById(examId.value))
const userResults = computed(() => examStore.getUserExamResults(examId.value))
const ranking = computed(() => examStore.getExamRanking(examId.value))

// 时间进度
const timeProgress = computed(() => {
  if (!exam.value || exam.value.status !== 'published') return 0
  const now = new Date()
  const start = new Date(exam.value.startTime)
  const end = new Date(exam.value.endTime)
  const total = end - start
  const current = now - start
  return Math.min(Math.max((current / total) * 100, 0), 100)
})

// 统计数据
const passRate = computed(() => {
  if (userResults.value.length === 0) return 0
  const passed = userResults.value.filter(r => r.passStatus === 'passed').length
  return Math.round((passed / userResults.value.length) * 100)
})

const averageScore = computed(() => {
  if (userResults.value.length === 0) return 0
  const total = userResults.value.reduce((sum, r) => sum + r.score, 0)
  return Math.round(total / userResults.value.length)
})

const highestScore = computed(() => {
  if (userResults.value.length === 0) return 0
  return Math.max(...userResults.value.map(r => r.score))
})

// 方法
const goBack = () => {
  router.back()
}

const publishExam = () => {
  examStore.publishExam(examId.value)
  ElMessage.success('考试已发布')
}

const endExam = () => {
  examStore.endExam(examId.value)
  ElMessage.success('考试已结束')
}

const handleAction = (command) => {
  switch (command) {
    case 'edit':
      ElMessage.info('编辑考试功能开发中...')
      break
    case 'duplicate':
      ElMessage.info('复制考试功能开发中...')
      break
    case 'export':
      exportResults()
      break
    case 'delete':
      ElMessageBox.confirm(
        '确定要删除这个考试吗？删除后无法恢复！',
        '删除确认',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
      ).then(() => {
        examStore.deleteExam(examId.value)
        ElMessage.success('删除成功')
        router.back()
      }).catch(() => {})
      break
  }
}

const addQuestion = () => {
  ElMessage.info('添加题目功能开发中...')
}

const editQuestion = (question) => {
  ElMessage.info('编辑题目功能开发中...')
}

const deleteQuestion = (index) => {
  ElMessageBox.confirm('确定要删除这道题吗？', '删除确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    // 删除题目逻辑
    ElMessage.success('题目已删除')
  }).catch(() => {})
}

const previewExam = () => {
  ElMessage.info('预览考试功能开发中...')
}

const batchGrade = () => {
  examStore.batchGrade(examId.value)
  ElMessage.success('批量批改完成')
}

const exportResults = () => {
  ElMessage.info('导出成绩功能开发中...')
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
    'draft': '草稿',
    'published': '进行中',
    'ended': '已结束'
  }
  return textMap[status] || '未知'
}

const getQuestionTypeTagType = (type) => {
  const typeMap = {
    'single': 'primary',
    'multiple': 'success',
    'essay': 'warning',
    'fill': 'info'
  }
  return typeMap[type] || 'info'
}

const getQuestionTypeText = (type) => {
  const textMap = {
    'single': '单选题',
    'multiple': '多选题',
    'essay': '简答题',
    'fill': '填空题'
  }
  return textMap[type] || '未知'
}

const getRankClass = (rank) => {
  if (rank === 1) return 'rank-gold'
  if (rank === 2) return 'rank-silver'
  if (rank === 3) return 'rank-bronze'
  return 'rank-normal'
}

const formatFullTime = (timeStr) => {
  const date = new Date(timeStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const formatTime = (timeStr) => {
  const date = new Date(timeStr)
  return date.toLocaleDateString()
}

// 生命周期
onMounted(() => {
  // 加载数据
})
</script>

<style scoped>
.exam-detail {
  padding: 20px;
  background: #f5f7fa;
  min-height: calc(100vh - 60px);
}

.detail-container {
  max-width: 1400px;
  margin: 0 auto;
}

.back-section {
  margin-bottom: 20px;
}

.exam-info {
  background: #fff;
  padding: 30px;
  border-radius: 12px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}

.info-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20px;
}

.exam-title-section {
  flex: 1;
}

.exam-title {
  margin: 0 0 15px 0;
  font-size: 28px;
  color: #2c3e50;
  font-weight: 600;
}

.exam-badges {
  display: flex;
  gap: 10px;
}

.exam-description {
  color: #606266;
  font-size: 16px;
  line-height: 1.8;
}

.info-card {
  margin-bottom: 20px;
}

.info-card :deep(.el-card__header) {
  padding: 20px;
  border-bottom: 1px solid #f0f2f5;
}

.info-card :deep(.el-card__body) {
  padding: 20px;
}

.info-card h3 {
  margin: 0;
  color: #303133;
  font-size: 18px;
  font-weight: 600;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.time-info {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.time-item {
  display: flex;
  align-items: center;
  gap: 10px;
}

.time-item .label {
  color: #606266;
  font-weight: 500;
  min-width: 80px;
}

.time-status {
  margin-top: 10px;
}

.status-text {
  color: #909399;
  font-size: 14px;
}

.question-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.question-item {
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
}

.question-header {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 10px;
}

.question-index {
  font-weight: 600;
  color: #303133;
}

.question-score {
  margin-left: auto;
  color: #409eff;
  font-weight: 600;
}

.question-content {
  color: #606266;
  margin-bottom: 15px;
  line-height: 1.6;
}

.question-options {
  margin-bottom: 15px;
}

.option-item {
  display: flex;
  gap: 10px;
  margin-bottom: 8px;
}

.option-label {
  color: #909399;
  font-weight: 500;
}

.question-actions {
  display: flex;
  gap: 10px;
}

.stats-card,
.ranking-card,
.actions-card {
  margin-bottom: 20px;
}

.stats-card :deep(.el-card__header),
.ranking-card :deep(.el-card__header),
.actions-card :deep(.el-card__header) {
  padding: 20px;
  border-bottom: 1px solid #f0f2f5;
}

.stats-card :deep(.el-card__body),
.ranking-card :deep(.el-card__body),
.actions-card :deep(.el-card__body) {
  padding: 20px;
}

.stats-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.stat-item {
  text-align: center;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 8px;
}

.stat-number {
  font-size: 28px;
  font-weight: 600;
  color: #409eff;
  margin-bottom: 5px;
}

.stat-label {
  color: #909399;
  font-size: 14px;
}

.ranking-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.ranking-item {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 8px;
}

.rank-badge {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  color: #fff;
}

.rank-gold {
  background: linear-gradient(135deg, #FFD700, #FFA500);
}

.rank-silver {
  background: linear-gradient(135deg, #C0C0C0, #808080);
}

.rank-bronze {
  background: linear-gradient(135deg, #CD7F32, #8B4513);
}

.rank-normal {
  background: #909399;
}

.user-info {
  flex: 1;
}

.user-name {
  font-weight: 500;
  color: #303133;
}

.submit-time {
  font-size: 12px;
  color: #909399;
  margin-top: 2px;
}

.score-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.score {
  font-size: 20px;
  font-weight: 600;
  color: #409eff;
}

.action-buttons {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.loading {
  padding: 20px;
}

.empty-questions,
.empty-ranking {
  padding: 40px;
  text-align: center;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .stats-content {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .exam-detail {
    padding: 10px;
  }

  .info-header {
    flex-direction: column;
    gap: 15px;
  }

  .exam-badges {
    flex-wrap: wrap;
  }

  .time-item {
    flex-direction: column;
    align-items: flex-start;
  }

  .question-header {
    flex-wrap: wrap;
    gap: 10px;
  }

  .stats-content {
    grid-template-columns: repeat(2, 1fr);
    gap: 10px;
  }

  .stat-item {
    padding: 10px;
  }

  .stat-number {
    font-size: 22px;
  }
}
</style>