<template>
  <div class="task-list">
    <!-- 左侧任务列表 -->
    <div class="task-panel">
      <div class="panel-header">
        <h3>任务列表</h3>
        <div class="stats">
          <span>已完成: {{ completedCount }}/{{ totalCount }}</span>
        </div>
      </div>

      <!-- 筛选器 -->
      <div class="filter-section">
        <el-select v-model="statusFilter" placeholder="状态筛选" @change="filterTasks">
          <el-option label="全部" value="all"></el-option>
          <el-option label="待完成" value="pending"></el-option>
          <el-option label="已完成" value="completed"></el-option>
          <el-option label="已过期" value="expired"></el-option>
        </el-select>
        <el-select v-model="typeFilter" placeholder="类型筛选" @change="filterTasks">
          <el-option label="全部" value="all"></el-option>
          <el-option label="任务" value="task"></el-option>
          <el-option label="通知" value="notification"></el-option>
        </el-select>
      </div>

      <!-- 搜索框 -->
      <div class="search-section">
        <el-input
          v-model="searchText"
          placeholder="搜索任务..."
          clearable
          @input="handleSearch"
          @clear="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
      </div>

      <div class="task-items">
        <div
          v-for="(task, index) in filteredTasks"
          :key="index"
          class="task-item"
          :class="{
            completed: task.status === 'completed',
            expired: task.status === 'expired',
            notification: task.type === 'notification'
          }"
          @click="selectTask(index)"
        >
          <div class="task-header">
            <div class="task-title">
              <el-icon v-if="task.type === 'notification'"><Bell /></el-icon>
              <h4>{{ task.title }}</h4>
            </div>
            <el-tag
              :type="getPriorityType(task.priority)"
              size="small"
            >
              {{ getPriorityText(task.priority) }}
            </el-tag>
          </div>

          <div class="task-description">{{ task.description }}</div>

          <div class="task-meta">
            <span class="publisher">
              <el-icon><User /></el-icon>
              {{ task.publisher }}
            </span>
            <span class="publish-time">
              <el-icon><Clock /></el-icon>
              {{ formatTime(task.publishTime) }}
            </span>
          </div>

          <div class="task-footer">
            <div class="deadline" v-if="task.deadline">
              <el-icon><Calendar /></el-icon>
              截止: {{ formatDeadline(task.deadline) }}
            </div>
            <div class="task-status">
              <el-tag :type="getStatusType(task.status)" size="small">
                {{ getStatusText(task.status) }}
              </el-tag>
            </div>
          </div>

          <!-- 进度条 -->
          <div class="progress-section" v-if="task.type === 'task'">
            <el-progress
              :percentage="task.completionRate || 0"
              :status="task.completionRate === 100 ? 'success' : null"
              :stroke-width="6"
            />
          </div>

          <!-- 标签 -->
          <div class="task-tags">
            <el-tag
              v-for="tag in task.tags"
              :key="tag"
              size="small"
              type="info"
            >
              {{ tag }}
            </el-tag>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-if="filteredTasks.length === 0" class="empty-state">
        <el-icon size="60"><Document /></el-icon>
        <p>暂无任务</p>
      </div>
    </div>

    <!-- 右侧任务详情 -->
    <div class="detail-panel">
      <div v-if="selectedTask !== null" class="task-detail">
        <el-card class="detail-card">
          <template #header>
            <div class="detail-header">
              <h4>{{ selectedTask.title }}</h4>
              <el-button
                v-if="selectedTask.type === 'task' && selectedTask.status === 'pending'"
                type="primary"
                @click="startTask"
              >
                开始任务
              </el-button>
            </div>
          </template>

          <div class="detail-content">
            <div class="detail-section">
              <h5>任务描述</h5>
              <p>{{ selectedTask.description }}</p>
            </div>

            <div class="detail-section" v-if="selectedTask.deadline">
              <h5>截止时间</h5>
              <p class="deadline-text">
                <el-icon><Calendar /></el-icon>
                {{ selectedTask.deadline }}
                <el-tag
                  :type="isOverdue(selectedTask.deadline) ? 'danger' : 'success'"
                  size="small"
                  class="ml-2"
                >
                  {{ isOverdue(selectedTask.deadline) ? '已过期' : '正常' }}
                </el-tag>
              </p>
            </div>

            <div class="detail-section">
              <h5>优先级</h5>
              <el-tag :type="getPriorityType(selectedTask.priority)">
                {{ getPriorityText(selectedTask.priority) }}
              </el-tag>
            </div>

            <div class="detail-section" v-if="selectedTask.attachments.length > 0">
              <h5>附件</h5>
              <div class="attachments">
                <div
                  v-for="attachment in selectedTask.attachments"
                  :key="attachment.name"
                  class="attachment-item"
                  @click="downloadAttachment(attachment)"
                >
                  <el-icon><Document /></el-icon>
                  <div class="attachment-info">
                    <span class="attachment-name">{{ attachment.name }}</span>
                    <span class="attachment-size">{{ formatFileSize(attachment.size) }}</span>
                  </div>
                </div>
              </div>
            </div>

            <div class="detail-section" v-if="selectedTask.tags.length > 0">
              <h5>标签</h5>
              <div class="tags">
                <el-tag
                  v-for="tag in selectedTask.tags"
                  :key="tag"
                  type="info"
                >
                  {{ tag }}
                </el-tag>
              </div>
            </div>

            <!-- 任务进度 -->
            <div class="detail-section" v-if="selectedTask.type === 'task'">
              <h5>完成进度</h5>
              <el-progress
                :percentage="selectedTask.completionRate || 0"
                :format="(percentage) => `${percentage}%`"
              :stroke-width="8"
              text-inside
              class="progress-large"
              @click="updateProgress"
              style="cursor: pointer"
            />
              <p class="progress-tip" v-if="selectedTask.completionRate === 0">
                点击进度条更新完成度
              </p>
            </div>
          </div>
        </el-card>
      </div>

      <!-- 空状态 -->
      <div v-else class="empty-detail">
        <el-icon size="60"><Document /></el-icon>
        <p>请选择任务查看详情</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import {
  Bell, Calendar, Clock, Document, Search, User
} from '@element-plus/icons-vue'
import { ElCard, ElButton, ElTag, ElSelect, ElInput, ElProgress, ElMessage } from 'element-plus'
import { useTaskStore } from '@/store'

const router = useRouter()
const route = useRoute()

// 使用Pinia store
const taskStore = useTaskStore()

const selectedTask = ref(null)
const statusFilter = ref('all')
const typeFilter = ref('all')
const searchText = ref('')

// 计算当前课程的作业列表
const tasks = computed(() => {
  return taskStore.getTasksByCourseId(route.params.id)
})

// 计算统计
const totalCount = computed(() => tasks.value.length)
const completedCount = computed(() =>
  tasks.value.filter(t => t.status === 'completed').length
)

// 过滤后的任务
const filteredTasks = computed(() => {
  let result = [...tasks.value]

  // 状态筛选
  if (statusFilter.value !== 'all') {
    result = result.filter(task => task.status === statusFilter.value)
  }

  // 类型筛选
  if (typeFilter.value !== 'all') {
    result = result.filter(task => task.type === typeFilter.value)
  }

  // 搜索筛选
  if (searchText.value.trim()) {
    const term = searchText.value.toLowerCase()
    result = taskStore.searchTasks(term, route.params.id)
  }

  return result
})

// 选择任务
const selectTask = (index) => {
  selectedTask.value = filteredTasks.value[index]
}

// 开始任务
const startTask = () => {
  router.push(`/teaching/course/${route.params.id}/task/${selectedTask.value.id}`)
}

// 处理搜索
const handleSearch = () => {
  // 延迟执行搜索
  clearTimeout(handleSearch.timer)
  handleSearch.timer = setTimeout(() => {
    // searchTasks会自动触发过滤
  }, 300)
}

// 更新任务进度
const updateProgress = () => {
  if (!selectedTask.value || selectedTask.value.type !== 'task') return

  ElMessageBox.prompt('请输入完成度（0-100）', '更新进度', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputValue: selectedTask.value.completionRate || 0,
    inputPattern: /^(100|[1-9]\d?|0)$/,
    inputErrorMessage: '请输入0-100之间的数字'
  }).then(({ value }) => {
    const progress = parseInt(value)
    taskStore.updateTaskCompletionRate(selectedTask.value.id, progress)
    selectedTask.value.completionRate = progress

    if (progress === 100) {
      taskStore.updateTaskStatus(selectedTask.value.id, 'completed')
      ElMessage.success('任务已完成！')
    }
  }).catch(() => {
    // 用户取消
  })
}

// 下载附件
const downloadAttachment = (attachment) => {
  // 模拟下载
  ElMessage.success(`开始下载: ${attachment.name}`)
}

// 判断是否过期
const isOverdue = (deadline) => {
  return new Date(deadline) < new Date()
}

// 格式化时间
const formatTime = (time) => {
  const date = new Date(time)
  const now = new Date()
  const diff = now - date
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))
  const hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60))

  if (days > 0) return `${days}天前`
  if (hours > 0) return `${hours}小时前`
  return '刚刚'
}

// 格式化截止时间
const formatDeadline = (deadline) => {
  const date = new Date(deadline)
  const now = new Date()
  const diff = date - now
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))
  const hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60))

  if (diff < 0) return '已过期'
  if (days > 0) return `${days}天后`
  if (hours > 0) return `${hours}小时后`
  return '即将截止'
}

// 格式化文件大小
const formatFileSize = (size) => {
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(size) / Math.log(k))
  return parseFloat((size / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

// 获取优先级类型
const getPriorityType = (priority) => {
  const types = {
    high: 'danger',
    medium: 'warning',
    low: 'info'
  }
  return types[priority] || 'info'
}

// 获取优先级文本
const getPriorityText = (priority) => {
  const texts = {
    high: '高',
    medium: '中',
    low: '低'
  }
  return texts[priority] || '低'
}

// 获取状态类型
const getStatusType = (status) => {
  const types = {
    pending: 'warning',
    completed: 'success',
    expired: 'danger'
  }
  return types[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const texts = {
    pending: '待完成',
    completed: '已完成',
    expired: '已过期'
  }
  return texts[status] || '未知'
}

// 过滤任务
const filterTasks = () => {
  // 计算属性会自动更新
}

onMounted(() => {
  // 检查过期的任务
  taskStore.checkExpiredTasks()
})
</script>

<style scoped>
.task-list {
  display: flex;
  gap: 20px;
  height: 100%;
  padding: 20px;
}

/* 左侧任务列表面板 */
.task-panel {
  flex: 0 0 350px;
  background: rgba(255,255,255,0.8);
  border-radius: 12px;
  padding: 20px;
  overflow-y: auto;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #f5dbe7;
}

.panel-header h3 {
  margin: 0;
  color: #d63384;
  font-size: 18px;
  font-weight: 700;
}

.stats {
  color: #9b7a88;
  font-size: 13px;
}

.filter-section {
  display: flex;
  gap: 10px;
  margin-bottom: 15px;
}

.filter-section .el-select {
  flex: 1;
}

.search-section {
  margin-bottom: 20px;
}

.task-items {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.task-item {
  padding: 16px;
  background: white;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid #ffd6e7;
  position: relative;
}

.task-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(214, 51, 132, 0.1);
}

.task-item.completed {
  border-color: #67c23a;
  background: linear-gradient(135deg, #f0f9ff 0%, #e0f7fa 100%);
}

.task-item.expired {
  border-color: #f56c6c;
  background: linear-gradient(135deg, #fef0f0 0%, #fde2e2 100%);
}

.task-item.notification {
  border-left: 4px solid #e6a23c;
}

.task-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 10px;
}

.task-title {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
}

.task-title h4 {
  margin: 0;
  font-size: 15px;
  color: #7b2a53;
  font-weight: 600;
}

.task-description {
  font-size: 13px;
  color: #9b7a88;
  line-height: 1.4;
  margin-bottom: 10px;
}

.task-meta {
  display: flex;
  align-items: center;
  gap: 20px;
  font-size: 12px;
  color: #9b7a88;
}

.task-meta span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.task-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 10px;
}

.deadline {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #f56c6c;
}

.progress-section {
  margin-top: 12px;
}

.task-tags {
  display: flex;
  gap: 5px;
  margin-top: 12px;
  flex-wrap: wrap;
}

/* 右侧详情面板 */
.detail-panel {
  flex: 1;
  background: rgba(255,255,255,0.8);
  border-radius: 12px;
  padding: 20px;
  overflow-y: auto;
}

.task-detail {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.detail-card,
.attachment-card {
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.05);
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.detail-header h4 {
  margin: 0;
  color: #d63384;
  font-size: 18px;
  font-weight: 700;
}

.detail-content {
  color: #6b3b52;
}

.detail-section {
  margin-bottom: 20px;
}

.detail-section h5 {
  margin: 0 0 10px 0;
  color: #7b2a53;
  font-size: 16px;
  font-weight: 600;
}

.detail-section p {
  margin: 0;
  font-size: 14px;
  line-height: 1.6;
}

.deadline-text {
  display: flex;
  align-items: center;
  gap: 8px;
}

.attachments {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.attachment-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px;
  background: rgba(255, 247, 247, 0.5);
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
}

.attachment-item:hover {
  background: rgba(255, 247, 247, 0.8);
}

.attachment-info {
  display: flex;
  flex-direction: column;
}

.attachment-name {
  font-size: 14px;
  color: #333;
}

.attachment-size {
  font-size: 12px;
  color: #999;
}

.tags {
  display: flex;
  gap: 5px;
  flex-wrap: wrap;
}

.progress-large {
  margin: 10px 0;
}

.progress-tip {
  font-size: 12px;
  color: #999;
  margin-top: 5px;
}

.ml-2 {
  margin-left: 8px;
}

/* 空状态 */
.empty-state,
.empty-detail {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #9b7a88;
}

.empty-state .el-icon,
.empty-detail .el-icon {
  margin-bottom: 20px;
  opacity: 0.5;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .task-list {
    flex-direction: column;
  }

  .task-panel {
    flex: none;
    width: 100%;
  }
}
</style>