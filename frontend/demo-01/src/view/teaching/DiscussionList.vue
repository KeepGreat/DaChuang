<template>
  <div class="discussion-list">
    <div class="list-header">
      <h2>课程讨论</h2>
    </div>

    <!-- 筛选和搜索 -->
    <div class="filter-section">
      <div class="filter-left">
        <el-select v-model="tagFilter" placeholder="按标签筛选" style="width: 150px">
          <el-option label="全部" value="all"></el-option>
          <el-option v-for="tag in availableTags" :key="tag" :label="tag" :value="tag"></el-option>
        </el-select>
        <el-select v-model="statusFilter" placeholder="按状态筛选" style="width: 150px">
          <el-option label="全部" value="all"></el-option>
          <el-option label="置顶" value="top"></el-option>
          <el-option label="普通" value="normal"></el-option>
          <el-option label="已锁定" value="locked"></el-option>
        </el-select>
      </div>
      <div class="search-box">
        <el-input
          v-model="searchQuery"
          placeholder="搜索讨论..."
          style="width: 300px"
          clearable
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
      </div>
    </div>

    <!-- 讨论列表 -->
    <div class="discussion-content">
      <div v-if="filteredDiscussions.length === 0" class="empty-state">
        <el-empty description="暂无讨论内容" />
      </div>
      <div v-else class="discussion-items">
        <div
          v-for="discussion in filteredDiscussions"
          :key="discussion.id"
          class="discussion-item"
          @click="enterDiscussion(discussion.id)"
        >
          <div class="item-header">
            <div class="title-section">
              <h3 class="discussion-title">
                <el-tag v-if="discussion.isTop" type="danger" size="small">置顶</el-tag>
                <el-tag v-if="discussion.isLocked" type="info" size="small">已锁定</el-tag>
                {{ discussion.title }}
              </h3>
              <div class="discussion-meta">
                <span class="author">{{ discussion.author.name }}</span>
                <span class="publish-time">{{ formatTime(discussion.publishTime) }}</span>
                <el-tag v-if="discussion.author.role === 'teacher'" type="success" size="small">
                  老师
                </el-tag>
              </div>
            </div>
            <div class="stats-section">
              <div class="stat-item">
                <el-icon><ChatDotRound /></el-icon>
                <span>{{ discussion.replyCount }}</span>
              </div>
              <div class="stat-item">
                <el-icon><View /></el-icon>
                <span>{{ discussion.viewCount }}</span>
              </div>
            </div>
          </div>
          <div class="item-content">
            <p class="discussion-preview">{{ getPreview(discussion.content) }}</p>
            <div class="discussion-tags" v-if="discussion.tags.length > 0">
              <el-tag
                v-for="tag in discussion.tags"
                :key="tag"
                size="small"
                type="info"
              >
                {{ tag }}
              </el-tag>
            </div>
          </div>
          <div v-if="discussion.attachments.length > 0" class="item-footer">
            <el-icon><Paperclip /></el-icon>
            <span>{{ discussion.attachments.length }}个附件</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 分页 -->
    <div class="pagination-wrapper">
      <el-pagination
        v-model:current-page="currentPage"
        :page-size="pageSize"
        :total="filteredDiscussions.length"
        layout="prev, pager, next, jumper"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useDiscussionStore } from '@/store'
import { ElMessage } from 'element-plus'
import { Search, ChatDotRound, View, Paperclip } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const discussionStore = useDiscussionStore()

// 响应式数据
const searchQuery = ref('')
const tagFilter = ref('all')
const statusFilter = ref('all')
const currentPage = ref(1)
const pageSize = ref(10)

// 计算属性
const courseId = computed(() => route.params.courseId)
const discussions = computed(() => discussionStore.getDiscussionsByCourseId(courseId.value))

// 获取所有可用标签
const availableTags = computed(() => {
  const tags = new Set()
  discussions.value.forEach(d => {
    d.tags.forEach(tag => tags.add(tag))
  })
  return Array.from(tags)
})

// 筛选后的讨论列表
const filteredDiscussions = computed(() => {
  let result = discussions.value

  // 按状态筛选
  if (statusFilter.value === 'top') {
    result = result.filter(d => d.isTop)
  } else if (statusFilter.value === 'normal') {
    result = result.filter(d => !d.isTop && !d.isLocked)
  } else if (statusFilter.value === 'locked') {
    result = result.filter(d => d.isLocked)
  }

  // 按标签筛选
  if (tagFilter.value !== 'all') {
    result = result.filter(d => d.tags.includes(tagFilter.value))
  }

  // 搜索筛选
  if (searchQuery.value.trim()) {
    const query = searchQuery.value.toLowerCase()
    result = result.filter(d =>
      d.title.toLowerCase().includes(query) ||
      d.content.toLowerCase().includes(query) ||
      d.author.name.toLowerCase().includes(query)
    )
  }

  return result
})

// 方法
const enterDiscussion = (discussionId) => {
  // 增加查看次数
  discussionStore.incrementViewCount(discussionId)
  router.push(`/teaching/course/${courseId.value}/discussion/${discussionId}`)
}


const formatTime = (timeStr) => {
  const date = new Date(timeStr)
  const now = new Date()
  const diff = now - date

  if (diff < 60000) {
    return '刚刚'
  } else if (diff < 3600000) {
    return `${Math.floor(diff / 60000)}分钟前`
  } else if (diff < 86400000) {
    return `${Math.floor(diff / 3600000)}小时前`
  } else if (diff < 604800000) {
    return `${Math.floor(diff / 86400000)}天前`
  } else {
    return date.toLocaleDateString()
  }
}

const getPreview = (content) => {
  return content.length > 150 ? content.substring(0, 150) + '...' : content
}

// 生命周期
onMounted(() => {
  // 可以在这里加载更多数据
})
</script>

<style scoped>
.discussion-list {
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
  color: #2c3e50;
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
  background: #f5f7fa;
  border-radius: 8px;
}

.filter-left {
  display: flex;
  gap: 10px;
}

.discussion-content {
  min-height: 400px;
}

.discussion-items {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.discussion-item {
  padding: 20px;
  background: #fff;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.discussion-item:hover {
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
  transform: translateY(-2px);
}

.item-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 10px;
}

.title-section {
  flex: 1;
}

.discussion-title {
  margin: 0 0 8px 0;
  font-size: 18px;
  color: #303133;
  display: flex;
  align-items: center;
  gap: 8px;
}

.discussion-meta {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 13px;
  color: #909399;
}

.stats-section {
  display: flex;
  gap: 20px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 5px;
  color: #909399;
  font-size: 14px;
}

.item-content {
  margin-bottom: 10px;
}

.discussion-preview {
  color: #606266;
  font-size: 14px;
  line-height: 1.6;
  margin: 0 0 10px 0;
}

.discussion-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.item-footer {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 13px;
  color: #909399;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}

.empty-state {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 300px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .discussion-list {
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

  .item-header {
    flex-direction: column;
    gap: 10px;
  }

  .stats-section {
    width: 100%;
    justify-content: flex-start;
  }
}
</style>