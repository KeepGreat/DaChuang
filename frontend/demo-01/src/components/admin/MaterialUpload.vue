<template>
  <div class="material-upload">
    <!-- 错误界面：未获取到 courseId -->
    <div v-if="!courseId" class="error-view">
      <el-result icon="error" title="参数错误" sub-title="未获取到课程ID，无法创建资料">
        <template #extra>
          <el-button type="primary" @click="$emit('back')">返回</el-button>
        </template>
      </el-result>
    </div>

    <div v-else class="upload-container">
      <!-- 第一部分：Material 字段编辑 -->
      <el-card class="form-card" shadow="never">
        <template #header>
          <div class="card-header">
            <span>{{ createdMaterial ? '资料已创建' : '第一步：完善资料信息' }}</span>
          </div>
        </template>
        <el-form :model="materialForm" label-width="100px" :disabled="!!createdMaterial">
          <el-form-item label="课程ID">
            <el-input v-model="materialForm.courseId" disabled />
          </el-form-item>
          <el-form-item label="资料类型">
            <el-input v-model="materialForm.type" placeholder="上传文件后自动识别" disabled />
          </el-form-item>
          <el-form-item label="资料描述">
            <el-input
              v-model="materialForm.description"
              type="textarea"
              :rows="3"
              placeholder="请输入资料描述"
            />
          </el-form-item>
          <el-form-item v-if="!createdMaterial">
            <el-button type="primary" @click="handleCreateMaterial" :loading="creating">
              创建资料
            </el-button>
          </el-form-item>
        </el-form>
      </el-card>

      <!-- 第二部分：资料文件上传 -->
      <transition name="el-fade-in">
        <el-card v-if="createdMaterial" class="upload-card" shadow="never">
          <template #header>
            <div class="card-header">
              <span>第二步：上传资料文件</span>
            </div>
          </template>

          <div class="upload-section">
            <el-upload
              class="drag-uploader"
              drag
              action="#"
              :auto-upload="false"
              :on-change="handleFileChange"
              :limit="1"
              :file-list="fileList"
            >
              <el-icon class="el-icon--upload"><upload-filled /></el-icon>
              <div class="el-upload__text">
                将文件拖到此处，或 <em>点击上传</em>
              </div>
              <template #tip>
                <div class="el-upload__tip">
                  支持 PDF 文档或视频文件
                </div>
              </template>
            </el-upload>

            <div v-if="selectedFile" class="file-info">
              <div class="info-item">
                <span class="label">文件名：</span>
                <span class="value">{{ selectedFile.name }}</span>
              </div>
              <div class="info-item">
                <span class="label">大小：</span>
                <span class="value">{{ formatSize(selectedFile.size) }}</span>
              </div>
              <div class="info-item">
                <span class="label">识别类型：</span>
                <el-tag size="small" :type="tagType">{{ materialForm.type }}</el-tag>
              </div>
            </div>

            <div v-if="isUploading" class="progress-box">
              <el-progress :percentage="uploadProgress" :status="uploadProgress === 100 ? 'success' : ''" />
              <p class="progress-text">{{ uploadStatusText }}</p>
            </div>

            <div class="upload-actions">
              <el-button
                type="success"
                :disabled="!selectedFile || isUploading"
                @click="handleStartUpload"
              >
                {{ isUploading ? '正在上传...' : '上传文件' }}
              </el-button>
            </div>
          </div>
        </el-card>
      </transition>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { UploadFilled } from '@element-plus/icons-vue'
import { addMaterial, updateMaterial } from '@/api/modules/teaching/MaterialAPI'
import { uploadFile } from '@/api/modules/teaching/FileContentAPI'
import { addVideoContent } from '@/api/modules/teaching/VideoContentAPI'
import { uploadChunk, checkChunk, mergeChunk } from '@/api/modules/teaching/VideoChunkAPI'

const props = defineProps({
  courseId: {
    type: [Number, String],
    required: true
  }
})

const emit = defineEmits(['back', 'success'])

// Material 状态
const materialForm = reactive({
  type: '',
  description: '',
  courseId: props.courseId
})
const creating = ref(false)
const createdMaterial = ref(null)

// 文件上传状态
const selectedFile = ref(null)
const fileList = ref([])
const isUploading = ref(false)
const uploadProgress = ref(0)
const uploadStatusText = ref('')
const chunkSize = 512 * 1024 * 1024; // 512MB as per VideoUploader.vue

const tagType = computed(() => {
  if (materialForm.type.startsWith('video')) return 'success'
  if (materialForm.type === 'application/pdf') return 'danger'
  return 'info'
})

// 创建 Material 记录
const handleCreateMaterial = async () => {
  creating.value = true
  try {
    const res = await addMaterial({
      type: 'pending', // 初始类型，上传文件后更新
      description: materialForm.description,
      courseId: props.courseId
    })
    createdMaterial.value = res.data
    ElMessage.success('资料信息创建成功，请上传文件')
  } catch (error) {
    ElMessage.error('创建资料失败: ' + (error.message || '未知错误'))
  } finally {
    creating.value = false
  }
}

// 文件选择回调
const handleFileChange = (file) => {
  selectedFile.value = file.raw
  materialForm.type = file.raw.type || 'unknown'
  
  // 如果是视频，尝试解析时长
  if (file.raw.type.startsWith('video/')) {
    const video = document.createElement('video')
    video.preload = 'metadata'
    video.onloadedmetadata = () => {
      selectedFile.value.duration = Math.round(video.duration)
      URL.revokeObjectURL(video.src)
    }
    video.src = URL.createObjectURL(file.raw)
  }
}

// 开始上传流程
const handleStartUpload = async () => {
  if (!selectedFile.value || !createdMaterial.value) return

  isUploading.value = true
  uploadProgress.value = 0
  uploadStatusText.value = '正在初始化上传...'

  try {
    const file = selectedFile.value
    const matId = createdMaterial.value.id

    // 首先更新 Material 的类型
    await updateMaterial({
      id: matId,
      type: file.type,
      description: materialForm.description,
      courseId: props.courseId
    })

    if (file.type === 'application/pdf') {
      await performPdfUpload(file, matId)
    } else if (file.type.startsWith('video/')) {
      await performVideoUpload(file, matId)
    } else {
      ElMessage.warning('暂不支持该类型文件的预览展示，仅作为普通文件上传')
      await performPdfUpload(file, matId) // 默认走普通文件上传
    }

    ElMessage.success('资料文件上传成功！')
    emit('success', createdMaterial.value)
  } catch (error) {
    console.error('上传失败:', error)
    ElMessage.error('上传失败: ' + (error.message || '网络异常'))
  } finally {
    isUploading.value = false
  }
}

// PDF/普通文件上传逻辑 (参考 FileTest.vue)
const performPdfUpload = async (file, matId) => {
  uploadStatusText.value = '正在上传文件内容...'
  const fileContent = {
    type: file.type,
    name: file.name,
    size: file.size,
    matId: matId
  }
  
  await uploadFile(fileContent, file)
  uploadProgress.value = 100
}

// 视频分片上传逻辑 (参考 VideoUploader.vue)
const performVideoUpload = async (file, matId) => {
  // 1. 添加视频内容记录
  uploadStatusText.value = '正在创建视频记录...'
  const videoContent = {
    type: file.type,
    name: file.name,
    size: file.size,
    duration: file.duration || 0,
    matId: matId
  }
  const addRes = await addVideoContent(videoContent)
  const videoId = addRes.data.id
  const videoName = addRes.data.name

  // 2. 分片上传
  const totalChunks = Math.ceil(file.size / chunkSize)
  uploadStatusText.value = `准备上传分片 (共 ${totalChunks} 片)...`

  for (let i = 0; i < totalChunks; i++) {
    const start = i * chunkSize
    const end = Math.min(start + chunkSize, file.size)
    const chunkFile = file.slice(start, end)
    
    const chunkJSON = JSON.stringify({
      number: i + 1,
      total: totalChunks,
      size: end - start,
      videoId: videoId
    })

    uploadStatusText.value = `正在上传分片 ${i + 1}/${totalChunks}...`
    await uploadChunk(chunkJSON, chunkFile)
    await checkChunk(videoId, i + 1)
    
    uploadProgress.value = Math.round(((i + 1) / totalChunks) * 90) // 预留10%给合并
  }

  // 3. 合并分片
  uploadStatusText.value = '正在合并分片，请稍候...'
  await mergeChunk({
    id: videoId,
    name: videoName
  })
  uploadProgress.value = 100
}

const formatSize = (size) => {
  if (!size) return '0 B'
  const units = ['B', 'KB', 'MB', 'GB']
  let i = 0
  while (size >= 1024 && i < units.length - 1) {
    size /= 1024
    i++
  }
  return `${size.toFixed(2)} ${units[i]}`
}
</script>

<style scoped lang="scss">
.material-upload {
  padding: 20px;
  background-color: var(--bg-white);
  border-radius: 8px;

  .upload-container {
    display: flex;
    flex-direction: column;
    gap: 24px;
    max-width: 800px;
    margin: 0 auto;
  }

  .card-header {
    font-weight: 600;
    color: var(--primary);
    border-left: 4px solid var(--primary);
    padding-left: 12px;
  }

  .drag-uploader {
    width: 100%;
    
    :deep(.el-upload-dragger) {
      border-color: var(--primary-lighter);
      background-color: var(--bg-light);
      
      &:hover {
        border-color: var(--primary);
      }
    }
    
    .el-icon--upload {
      color: var(--primary-light);
    }
  }

  .file-info {
    margin-top: 16px;
    padding: 12px;
    background-color: var(--bg-primary-grey);
    border-radius: 4px;
    font-size: 14px;

    .info-item {
      margin-bottom: 8px;
      display: flex;
      align-items: center;

      .label {
        color: var(--text-regular);
        width: 80px;
      }
      .value {
        color: var(--text-primary);
        font-weight: 500;
      }
    }
  }

  .progress-box {
    margin: 20px 0;
    
    .progress-text {
      font-size: 12px;
      color: var(--text-placeholder);
      margin-top: 8px;
      text-align: center;
    }
  }

  .upload-actions {
    display: flex;
    justify-content: center;
    margin-top: 20px;
  }

  .error-view {
    padding: 40px;
  }
}

:deep(.el-form-item__label) {
  font-weight: 500;
}

:deep(.el-button--primary) {
  background-color: var(--primary);
  border-color: var(--primary);
  
  &:hover {
    background-color: var(--primary-hover);
    border-color: var(--primary-hover);
  }
}
</style>
