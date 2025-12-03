<template>
  <div class="resource-detail">
    <div v-if="resource" class="detail-container">
      <!-- 返回按钮 -->
      <div class="back-section">
        <el-button @click="goBack" :icon="ArrowLeft">返回资料列表</el-button>
      </div>

      <!-- 资源信息 -->
      <div class="resource-info">
        <div class="info-main">
          <div class="resource-icon">
            <el-icon :size="80">
              <Folder v-if="resource.type === 'folder'" />
              <Document v-else-if="resource.type === 'document'" />
              <VideoPlay v-else-if="resource.type === 'video'" />
              <Headphone v-else-if="resource.type === 'audio'" />
              <Picture v-else-if="resource.type === 'image'" />
              <Files v-else />
            </el-icon>
          </div>
          <div class="resource-details">
            <h1 class="resource-name">{{ resource.name }}</h1>
            <div class="resource-meta">
              <div class="meta-item">
                <span class="label">类型：</span>
                <el-tag :type="getTypeTagType(resource.type)">
                  {{ getTypeText(resource.type) }}
                </el-tag>
              </div>
              <div class="meta-item" v-if="resource.type !== 'folder'">
                <span class="label">大小：</span>
                <span>{{ formatFileSize(resource.size) }}</span>
              </div>
              <div class="meta-item">
                <span class="label">上传者：</span>
                <span>{{ resource.uploader }}</span>
              </div>
              <div class="meta-item">
                <span class="label">上传时间：</span>
                <span>{{ formatFullTime(resource.uploadTime) }}</span>
              </div>
              <div class="meta-item" v-if="resource.downloadCount">
                <span class="label">下载次数：</span>
                <span>{{ resource.downloadCount }}</span>
              </div>
            </div>
          </div>
        </div>
        <div class="info-actions">
          <el-button
            v-if="resource.type !== 'folder'"
            type="primary"
            size="large"
            @click="downloadResource"
          >
            <el-icon><Download /></el-icon>
            下载文件
          </el-button>
          <el-button size="large" @click="shareResource">
            <el-icon><Share /></el-icon>
            分享
          </el-button>
        </div>
      </div>

      <!-- 资源描述 -->
      <div v-if="resource.description" class="description-section">
        <h3>资源描述</h3>
        <p>{{ resource.description }}</p>
      </div>

      <!-- 标签 -->
      <div v-if="resource.tags && resource.tags.length > 0" class="tags-section">
        <h3>相关标签</h3>
        <div class="tag-list">
          <el-tag
            v-for="tag in resource.tags"
            :key="tag"
            size="large"
            type="info"
          >
            {{ tag }}
          </el-tag>
        </div>
      </div>

      <!-- 文件预览 -->
      <div v-if="resource.type !== 'folder'" class="preview-section">
        <h3>文件预览</h3>
        <div class="preview-container">
          <!-- 图片预览 -->
          <div v-if="resource.type === 'image'" class="image-preview">
            <el-image
              :src="resource.path"
              :preview-src-list="[resource.path]"
              fit="contain"
              style="max-width: 100%; max-height: 600px;"
            />
          </div>
          <!-- 视频预览 -->
          <div v-else-if="resource.type === 'video'" class="video-preview">
            <video
              :src="resource.path"
              controls
              style="width: 100%; max-height: 600px;"
            >
              您的浏览器不支持视频播放
            </video>
          </div>
          <!-- 音频预览 -->
          <div v-else-if="resource.type === 'audio'" class="audio-preview">
            <audio :src="resource.path" controls style="width: 100%;">
              您的浏览器不支持音频播放
            </audio>
          </div>
          <!-- PDF预览 -->
          <div v-else-if="isPDF(resource.name)" class="pdf-preview">
            <iframe
              :src="resource.path"
              style="width: 100%; height: 600px; border: none;"
            ></iframe>
          </div>
          <!-- 文本预览 -->
          <div v-else-if="isTextFile(resource.name)" class="text-preview">
            <el-input
              type="textarea"
              :value="textContent"
              :rows="20"
              readonly
              style="font-family: monospace;"
            />
          </div>
          <!-- 其他文件类型 -->
          <div v-else class="no-preview">
            <el-empty description="该文件类型不支持预览">
              <el-button type="primary" @click="downloadResource">
                下载查看
              </el-button>
            </el-empty>
          </div>
        </div>
      </div>

      <!-- 文件夹内容 -->
      <div v-if="resource.type === 'folder' && resource.children" class="folder-content">
        <h3>文件夹内容 ({{ resource.children.length }} 项)</h3>
        <el-table :data="resource.children" style="width: 100%">
          <el-table-column label="名称" min-width="300">
            <template #default="{ row }">
              <div class="file-info">
                <el-icon class="file-icon" :size="20">
                  <Folder v-if="row.type === 'folder'" />
                  <Document v-else-if="row.type === 'document'" />
                  <VideoPlay v-else-if="row.type === 'video'" />
                  <Picture v-else-if="row.type === 'image'" />
                  <Files v-else />
                </el-icon>
                <span>{{ row.name }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="类型" width="120">
            <template #default="{ row }">
              <el-tag size="small" :type="getTypeTagType(row.type)">
                {{ getTypeText(row.type) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="大小" width="120">
            <template #default="{ row }">
              {{ row.type === 'folder' ? '-' : formatFileSize(row.size) }}
            </template>
          </el-table-column>
          <el-table-column label="修改时间" width="180">
            <template #default="{ row }">
              {{ formatFullTime(row.uploadTime) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="150">
            <template #default="{ row }">
              <el-button
                v-if="row.type !== 'folder'"
                text
                type="primary"
                size="small"
                @click="downloadChildFile(row)"
              >
                下载
              </el-button>
            </template>
          </el-table-column>
        </el-table>
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
import { useResourceStore } from '@/store'
import { ElMessage } from 'element-plus'
import {
  ArrowLeft, Folder, Document, VideoPlay, Headphone,
  Picture, Files, Download, Share
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const resourceStore = useResourceStore()

// 响应式数据
const textContent = ref('')

// 计算属性
const resourceId = computed(() => route.params.resourceId)
const resource = computed(() => resourceStore.getResourceById(resourceId.value))

// 方法
const goBack = () => {
  router.back()
}

const downloadResource = () => {
  resourceStore.downloadResource(resourceId.value)
  ElMessage.success('开始下载')
}

const downloadChildFile = (file) => {
  ElMessage.success(`开始下载: ${file.name}`)
}

const shareResource = () => {
  ElMessage.info('分享功能开发中...')
}

const getTypeTagType = (type) => {
  const typeMap = {
    'folder': 'primary',
    'document': 'success',
    'video': 'warning',
    'audio': 'info',
    'image': 'danger',
    'archive': 'default'
  }
  return typeMap[type] || 'info'
}

const getTypeText = (type) => {
  const typeMap = {
    'folder': '文件夹',
    'document': '文档',
    'video': '视频',
    'audio': '音频',
    'image': '图片',
    'archive': '压缩包'
  }
  return typeMap[type] || '文件'
}

const formatFileSize = (size) => {
  return resourceStore.formatFileSize(size)
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

const isPDF = (filename) => {
  return filename.toLowerCase().endsWith('.pdf')
}

const isTextFile = (filename) => {
  const textExtensions = ['.txt', '.md', '.json', '.xml', '.csv', '.log']
  const ext = filename.toLowerCase().split('.').pop()
  return textExtensions.includes(`.${ext}`)
}

// 生命周期
onMounted(() => {
  if (resource.value && isTextFile(resource.value.name)) {
    // 模拟加载文本文件内容
    textContent.value = '这是一个文本文件的示例内容...\n\n文件内容加载功能开发中...'
  }
})
</script>

<style scoped>
.resource-detail {
  padding: 20px;
  background: #fff;
  min-height: calc(100vh - 60px);
}

.detail-container {
  max-width: 1200px;
  margin: 0 auto;
}

.back-section {
  margin-bottom: 20px;
}

.resource-info {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 30px;
  background: #f8f9fa;
  border-radius: 12px;
  margin-bottom: 30px;
}

.info-main {
  display: flex;
  gap: 30px;
  flex: 1;
}

.resource-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 120px;
  height: 120px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  color: #909399;
}

.resource-details {
  flex: 1;
}

.resource-name {
  margin: 0 0 20px 0;
  font-size: 28px;
  color: #2c3e50;
  font-weight: 600;
}

.resource-meta {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.meta-item {
  display: flex;
  align-items: center;
  font-size: 14px;
}

.meta-item .label {
  min-width: 80px;
  color: #606266;
  font-weight: 500;
}

.info-actions {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-left: 30px;
}

.description-section,
.tags-section,
.preview-section,
.folder-content {
  margin-bottom: 30px;
}

.description-section h3,
.tags-section h3,
.preview-section h3,
.folder-content h3 {
  margin: 0 0 15px 0;
  color: #303133;
  font-size: 18px;
  font-weight: 600;
}

.description-section p {
  color: #606266;
  font-size: 15px;
  line-height: 1.8;
  margin: 0;
}

.tag-list {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.preview-container {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 20px;
  min-height: 200px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.image-preview,
.video-preview {
  width: 100%;
  text-align: center;
}

.audio-preview {
  width: 100%;
  max-width: 600px;
}

.pdf-preview {
  width: 100%;
  height: 600px;
}

.text-preview {
  width: 100%;
}

.no-preview {
  width: 100%;
}

.file-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.file-icon {
  color: #909399;
}

.loading {
  padding: 20px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .resource-detail {
    padding: 10px;
  }

  .resource-info {
    flex-direction: column;
    gap: 20px;
    padding: 20px;
  }

  .info-main {
    flex-direction: column;
    gap: 20px;
  }

  .resource-icon {
    width: 80px;
    height: 80px;
  }

  .resource-icon :deep(.el-icon) {
    font-size: 40px !important;
  }

  .resource-name {
    font-size: 22px;
  }

  .info-actions {
    margin-left: 0;
    flex-direction: row;
  }

  .meta-item {
    flex-wrap: wrap;
  }

  .meta-item .label {
    min-width: auto;
    margin-right: 5px;
  }
}
</style>