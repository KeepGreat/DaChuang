<template>
  <div class="task-detail">
    <div class="task-container">
      <!-- 任务详情 -->
      <div class="task-header">
        <h1>{{ task.title }}</h1>
        <div class="task-meta">
          <el-tag :type="getPriorityType(task.priority)" size="large">
            {{ getPriorityText(task.priority) }}
          </el-tag>
          <el-tag :type="getStatusType(task.status)" size="large">
            {{ getStatusText(task.status) }}
          </el-tag>
          <el-tag v-if="task.type === 'notification'" type="warning" size="large">
            通知
          </el-tag>
        </div>
      </div>

      <div class="task-content">
        <!-- 发布信息 -->
        <el-card class="info-card">
          <div class="info-row">
            <span class="label">发布者：</span>
            <span>{{ task.publisher }}</span>
          </div>
          <div class="info-row">
            <span class="label">发布时间：</span>
            <span>{{ task.publishTime }}</span>
          </div>
          <div class="info-row" v-if="task.deadline">
            <span class="label">截止时间：</span>
            <span>{{ task.deadline }}</span>
            <el-tag
              :type="isOverdue(task.deadline) ? 'danger' : 'success'"
              size="small"
              class="ml-2"
            >
              {{ isOverdue(task.deadline) ? '已过期' : '正常' }}
            </el-tag>
          </div>
        </el-card>

        <!-- 任务描述 -->
        <el-card class="description-card">
          <template #header>
            <h3>任务描述</h3>
          </template>
          <div class="description-content">
            {{ task.description }}
          </div>
        </el-card>

        <!-- 任务要求 -->
        <el-card v-if="task.requirements" class="requirements-card">
          <template #header>
            <h3>任务要求</h3>
          </template>
          <div class="requirements-content" v-html="task.requirements"></div>
        </el-card>

        <!-- 任务进度 -->
        <el-card v-if="task.type === 'task'" class="progress-card">
          <template #header>
            <h3>完成进度</h3>
            <el-button
              type="primary"
              size="small"
              @click="updateProgress"
            >
              更新进度
            </el-button>
          </template>
          <el-progress
            :percentage="task.completionRate || 0"
            :status="task.completionRate === 100 ? 'success' : null"
            :stroke-width="10"
            text-inside
          />
          <div class="progress-stats">
            <span>当前进度：{{ task.completionRate || 0 }}%</span>
            <span v-if="task.completionRate === 100" class="completed-text">
              <el-icon><Check /></el-icon>
              已完成
            </span>
          </div>
        </el-card>

        <!-- 附件 -->
        <el-card v-if="task.attachments && task.attachments.length > 0" class="attachment-card">
          <template #header>
            <h3>附件下载</h3>
          </template>
          <div class="attachment-list">
            <div
              v-for="(attachment, index) in task.attachments"
              :key="index"
              class="attachment-item"
              @click="downloadAttachment(attachment)"
            >
              <el-icon class="attachment-icon">
                <Document v-if="attachment.name.endsWith('.pdf')" />
                <Files v-else-if="['zip', 'rar', '7z'].includes(attachment.name.split('.').pop())" />
                <VideoPlay v-else-if="['mp4', 'avi', 'mov'].includes(attachment.name.split('.').pop())" />
                <Picture v-else />
              </el-icon>
              <div class="attachment-info">
                <div class="attachment-name">{{ attachment.name }}</div>
                <div class="attachment-meta">
                  <span class="attachment-size">{{ formatFileSize(attachment.size) }}</span>
                  <span class="download-count" v-if="attachment.downloadCount">
                    下载 {{ attachment.downloadCount }} 次
                  </span>
                </div>
              </div>
            </div>
          </div>
        </el-card>

        <!-- 标签 -->
        <el-card v-if="task.tags && task.tags.length > 0" class="tags-card">
          <template #header>
            <h3>相关标签</h3>
          </template>
          <div class="tag-list">
            <el-tag
              v-for="tag in task.tags"
              :key="tag"
              size="large"
              type="info"
            >
              {{ tag }}
            </el-tag>
          </div>
        </el-card>

        <!-- 完成状态 -->
        <el-card v-if="task.completeTime" class="status-card">
          <template #header>
            <h3>完成情况</h3>
          </template>
          <div class="completion-info">
            <div class="completion-icon">
              <el-icon size="48" color="#67c23a"><CircleCheck /></el-icon>
            </div>
            <div class="completion-text">
              <p class="success-text">任务已完成！</p>
              <p class="complete-time">完成时间：{{ task.completeTime }}</p>
            </div>
          </div>
        </el-card>
      </div>
    </div>

    <!-- 更新进度对话框 -->
    <el-dialog
      v-model="progressDialogVisible"
      title="更新任务进度"
      width="400px"
      :before-close="handleCloseDialog"
    >
      <el-form :model="progressForm" label-width="80px">
        <el-form-item label="完成度">
          <el-slider
            v-model="progressForm.completionRate"
            :min="0"
            :max="100"
            :step="5"
            show-input
            show-stops
          />
          <span style="margin-left: 10px">{{ progressForm.completionRate }}%</span>
        </el-form-item>
        <el-form-item label="备注">
          <el-input
            v-model="progressForm.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入完成情况备注（选填）"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="progressDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitProgress">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  Check, Bell, CircleCheck, Document, Files, VideoPlay, Picture
} from '@element-plus/icons-vue'
import { ElCard, ElTag, ElButton, ElIcon, ElMessage, ElDialog, ElForm, ElFormItem, ElSlider } from 'element-plus'
import { useTaskStore } from '@/store'

const route = useRoute()
const router = useRouter()
const taskStore = useTaskStore()

// 获取任务详情
const task = computed(() => {
  const taskId = route.params.taskId
  const found = taskStore.getTaskById(taskId)
  return found || {}
})

// 进度更新对话框
const progressDialogVisible = ref(false)
const progressForm = ref({
  completionRate: 0,
  remark: ''
})

// 判断是否过期
const isOverdue = (deadline) => {
  return deadline && new Date(deadline) < new Date()
}

// 更新进度对话框
const updateProgress = () => {
  progressForm.value.completionRate = task.value.completionRate || 0
  progressForm.value.remark = ''
  progressDialogVisible.value = true
}

// 提交进度
const submitProgress = () => {
  taskStore.updateTaskCompletionRate(task.value.id, progressForm.value.completionRate)

  if (progressForm.value.completionRate === 100) {
    taskStore.updateTaskStatus(task.value.id, 'completed')
    ElMessage.success('任务已完成！')
  } else {
    ElMessage.success('进度更新成功')
  }

  progressDialogVisible.value = false

  // 更新本地数据
  if (progressForm.value.remark) {
    taskStore.updateTask(task.value.id, {
      lastUpdate: new Date().toISOString(),
      remark: progressForm.value.remark
    })
  }
}

// 关闭对话框前的处理
const handleCloseDialog = () => {
  // 可以在这里添加保存草稿的逻辑
  return true
}

// 下载附件
const downloadAttachment = (attachment) => {
  // 模拟下载
  ElMessage.success(`开始下载: ${attachment.name}`)

  // 更新下载次数
  const attachments = task.value.attachments || []
  const index = attachments.findIndex(a => a.url === attachment.url)
  if (index !== -1) {
    attachments[index].downloadCount++
    taskStore.updateTask(task.value.id, { attachments })
  }
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

// 格式化文件大小
const formatFileSize = (size) => {
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(size) / Math.log(k))
  return parseFloat((size / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}
</script>

<style scoped>
.task-detail {
  padding: 20px;
  background: #fff6fb;
  min-height: 100vh;
}

.task-container {
  max-width: 1000px;
  margin: 0 auto;
}

.task-header {
  background: white;
  padding: 30px;
  border-radius: 12px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}

.task-header h1 {
  margin: 0 0 16px 0;
  color: #2c3e50;
  font-size: 28px;
  font-weight: 700;
}

.task-meta {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.task-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.info-card,
.description-card,
.requirements-card,
.progress-card,
.attachment-card,
.tags-card,
.status-card {
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}

.el-card :deep(.el-card__header) {
  border-bottom: 1px solid #f0f2f5;
  padding-bottom: 15px;
}

.el-card :deep(.el-card__header h3) {
  margin: 0;
  color: #303133;
  font-size: 18px;
  font-weight: 600;
}

.info-row {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
}

.info-row:last-child {
  margin-bottom: 0;
}

.info-row .label {
  font-weight: 600;
  color: #606266;
  min-width: 80px;
  font-size: 14px;
}

.description-content {
  color: #606266;
  font-size: 15px;
  line-height: 1.8;
  white-space: pre-wrap;
}

.requirements-content {
  color: #606266;
  font-size: 15px;
  line-height: 1.8;
}

.requirements-content :deep(ul) {
  padding-left: 20px;
  margin: 0;
}

.requirements-content :deep(li) {
  margin-bottom: 8px;
}

.progress-stats {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 15px;
  font-size: 14px;
}

.completed-text {
  color: #67c23a;
  display: flex;
  align-items: center;
  gap: 5px;
}

.attachment-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 15px;
}

.attachment-item {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.attachment-item:hover {
  background: #e9ecef;
  transform: translateY(-2px);
}

.attachment-icon {
  font-size: 24px;
  color: #409eff;
}

.attachment-info {
  flex: 1;
  min-width: 0;
}

.attachment-name {
  font-weight: 500;
  color: #303133;
  margin-bottom: 5px;
}

.attachment-meta {
  display: flex;
  gap: 10px;
  font-size: 12px;
  color: #909399;
}

.tag-list {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.completion-info {
  display: flex;
  align-items: center;
  gap: 20px;
}

.completion-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: linear-gradient(135deg, #f0f9ff, #e0f7fa);
}

.completion-text {
  flex: 1;
}

.success-text {
  font-size: 18px;
  font-weight: 600;
  color: #67c23a;
  margin-bottom: 8px;
}

.complete-time {
  color: #909399;
  font-size: 14px;
}

.ml-2 {
  margin-left: 8px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .task-container {
    padding: 10px;
  }

  .task-header {
    padding: 20px;
  }

  .task-header h1 {
    font-size: 24px;
  }

  .attachment-list {
    grid-template-columns: 1fr;
  }

  .progress-stats {
    flex-direction: column;
    gap: 10px;
    text-align: center;
  }

  .completion-info {
    flex-direction: column;
    text-align: center;
  }
}
</style>