<template>
  <div class="discussion-detail">
    <div v-if="discussion" class="discussion-container">
      <!-- 返回按钮 -->
      <div class="back-section">
        <el-button @click="goBack" :icon="ArrowLeft">返回讨论列表</el-button>
      </div>

      <!-- 讨论主贴 -->
      <div class="main-post">
        <div class="post-header">
          <div class="author-info">
            <el-avatar :size="50" :src="discussion.author.avatar">
              {{ discussion.author.name.charAt(0) }}
            </el-avatar>
            <div class="author-details">
              <h3 class="author-name">
                {{ discussion.author.name }}
                <el-tag v-if="discussion.author.role === 'teacher'" type="success" size="small">
                  老师
                </el-tag>
              </h3>
              <p class="post-time">{{ formatFullTime(discussion.publishTime) }}</p>
            </div>
          </div>
          <div class="post-actions">
            <el-tag v-if="discussion.isTop" type="danger">置顶</el-tag>
            <el-tag v-if="discussion.isLocked" type="info">已锁定</el-tag>
          </div>
        </div>
        <div class="post-content">
          <h1 class="post-title">{{ discussion.title }}</h1>
          <div class="post-body">{{ discussion.content }}</div>
          <div v-if="discussion.tags.length > 0" class="post-tags">
            <el-tag
              v-for="tag in discussion.tags"
              :key="tag"
              size="small"
              type="info"
            >
              {{ tag }}
            </el-tag>
          </div>
          <div v-if="discussion.attachments.length > 0" class="post-attachments">
            <h4>附件</h4>
            <div
              v-for="attachment in discussion.attachments"
              :key="attachment.name"
              class="attachment-item"
            >
              <el-icon><Paperclip /></el-icon>
              <span>{{ attachment.name }} ({{ attachment.size }})</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 回复列表 -->
      <div class="replies-section">
        <div class="replies-header">
          <h3>全部回复 ({{ discussion.replyCount }})</h3>
          <el-select v-model="sortOrder" placeholder="排序方式" size="small">
            <el-option label="最早回复" value="asc"></el-option>
            <el-option label="最新回复" value="desc"></el-option>
          </el-select>
        </div>

        <div v-if="replies.length === 0" class="no-replies">
          <el-empty description="暂无回复，快来发表第一个回复吧！" />
        </div>
        <div v-else class="reply-list">
          <div
            v-for="reply in sortedReplies"
            :key="reply.id"
            class="reply-item"
          >
            <el-avatar :size="40" :src="reply.authorAvatar">
              {{ reply.authorName.charAt(0) }}
            </el-avatar>
            <div class="reply-content">
              <div class="reply-header">
                <span class="reply-author">{{ reply.authorName }}</span>
                <el-tag v-if="reply.authorRole === 'teacher'" type="success" size="small">
                  老师
                </el-tag>
                <span class="reply-time">{{ formatFullTime(reply.publishTime) }}</span>
              </div>
              <div class="reply-text">{{ reply.content }}</div>
              <div class="reply-actions">
                <el-button
                  text
                  type="primary"
                  size="small"
                  @click="likeReply(reply.id)"
                >
                  <el-icon><Thumb /></el-icon>
                  {{ reply.likeCount }}
                </el-button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 回复输入框 -->
      <div v-if="!discussion.isLocked" class="reply-input-section">
        <h4>发表回复</h4>
        <el-input
          v-model="replyContent"
          type="textarea"
          :rows="4"
          placeholder="请输入你的回复..."
          maxlength="1000"
          show-word-limit
        />
        <div class="reply-submit">
          <el-button type="primary" @click="submitReply" :disabled="!replyContent.trim()">
            发表回复
          </el-button>
        </div>
      </div>
      <div v-else class="locked-notice">
        <el-alert
          title="该讨论已锁定，无法回复"
          type="info"
          :closable="false"
        />
      </div>
    </div>
    <div v-else class="loading">
      <el-skeleton :rows="5" animated />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useDiscussionStore } from '@/store'
import { ElMessage } from 'element-plus'
import { ArrowLeft, Paperclip, Thumb } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const discussionStore = useDiscussionStore()

// 响应式数据
const replyContent = ref('')
const sortOrder = ref('asc')

// 计算属性
const discussionId = computed(() => route.params.discussionId)
const discussion = computed(() => discussionStore.getDiscussionById(discussionId.value))
const replies = computed(() => discussionStore.replies[discussionId.value] || [])

const sortedReplies = computed(() => {
  const sorted = [...replies.value]
  return sortOrder.value === 'asc'
    ? sorted.sort((a, b) => new Date(a.publishTime) - new Date(b.publishTime))
    : sorted.sort((a, b) => new Date(b.publishTime) - new Date(a.publishTime))
})

// 方法
const goBack = () => {
  router.back()
}

const submitReply = () => {
  if (!replyContent.value.trim()) return

  const reply = {
    authorId: 'student1', // 应该从用户信息获取
    authorName: '学生',
    authorAvatar: '/avatars/student1.jpg',
    authorRole: 'student',
    content: replyContent.value
  }

  discussionStore.addReply(discussionId.value, reply)
  replyContent.value = ''
  ElMessage.success('回复发表成功')
}

const likeReply = (replyId) => {
  discussionStore.likeReply(discussionId.value, replyId, 'student1')
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

// 生命周期
onMounted(() => {
  if (discussionId.value) {
    discussionStore.setCurrentDiscussion(discussion.value)
  }
})
</script>

<style scoped>
.discussion-detail {
  padding: 20px;
  background: #fff;
  min-height: calc(100vh - 60px);
}

.back-section {
  margin-bottom: 20px;
}

.discussion-container {
  max-width: 1000px;
  margin: 0 auto;
}

.main-post {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 25px;
  margin-bottom: 30px;
}

.post-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20px;
}

.author-info {
  display: flex;
  gap: 15px;
}

.author-details {
  flex: 1;
}

.author-name {
  margin: 0 0 5px 0;
  font-size: 18px;
  color: #303133;
  display: flex;
  align-items: center;
  gap: 10px;
}

.post-time {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.post-content {
  margin-left: 65px;
}

.post-title {
  margin: 0 0 20px 0;
  font-size: 24px;
  color: #2c3e50;
  font-weight: 600;
}

.post-body {
  color: #606266;
  font-size: 16px;
  line-height: 1.8;
  white-space: pre-wrap;
  margin-bottom: 20px;
}

.post-tags {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.post-attachments {
  border-top: 1px solid #e4e7ed;
  padding-top: 15px;
}

.post-attachments h4 {
  margin: 0 0 10px 0;
  color: #606266;
  font-size: 14px;
}

.attachment-item {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #606266;
  font-size: 14px;
  margin-bottom: 5px;
}

.replies-section {
  margin-bottom: 30px;
}

.replies-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.replies-header h3 {
  margin: 0;
  color: #303133;
  font-size: 18px;
}

.reply-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.reply-item {
  display: flex;
  gap: 15px;
  padding: 20px;
  background: #fff;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
}

.reply-content {
  flex: 1;
}

.reply-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.reply-author {
  font-weight: 600;
  color: #303133;
}

.reply-time {
  color: #909399;
  font-size: 13px;
  margin-left: auto;
}

.reply-text {
  color: #606266;
  font-size: 15px;
  line-height: 1.6;
  margin-bottom: 10px;
}

.reply-actions {
  display: flex;
  gap: 15px;
}

.reply-input-section {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 25px;
}

.reply-input-section h4 {
  margin: 0 0 15px 0;
  color: #303133;
  font-size: 16px;
}

.reply-submit {
  margin-top: 15px;
  text-align: right;
}

.locked-notice {
  padding: 20px;
}

.loading {
  padding: 20px;
}

.no-replies {
  padding: 40px;
  text-align: center;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .discussion-detail {
    padding: 10px;
  }

  .main-post {
    padding: 15px;
  }

  .post-header {
    flex-direction: column;
    gap: 10px;
  }

  .post-content {
    margin-left: 0;
  }

  .replies-header {
    flex-direction: column;
    gap: 10px;
    align-items: flex-start;
  }

  .reply-item {
    padding: 15px;
  }
}
</style>