<template>
  <div class="material-player-wrapper">
    <!-- 错误状态展示 -->
    <div v-if="hasError || !materialIds || materialIds.length === 0" class="error-view">
      <el-empty description="未收到资料信息或资料不存在" />
    </div>

    <div v-else v-loading="loading" class="player-container">
      <!-- 资料选择部分：只有当资料多于一个时显示 -->
      <div v-if="materialsList.length > 1" class="selection-section">
        <h4 class="section-title">资料选择</h4>
        <div class="selection-grid">
          <el-card
            v-for="item in materialsList"
            :key="item.id"
            :class="['material-card', { active: currentMaterialId === item.id }]"
            shadow="hover"
            @click="handleSelectMaterial(item)"
          >
            <div class="card-content">
              <div class="material-type">
                <el-tag :type="getTagType(item.type)" size="small">{{ item.type }}</el-tag>
              </div>
              <p class="material-desc">{{ item.description || '无描述信息' }}</p>
            </div>
          </el-card>
        </div>
      </div>

      <!-- 资料展示部分 -->
      <div class="display-section">
        <div v-if="currentMaterial" class="display-content">
          <div class="display-header">
            <h3 class="display-title">{{ currentMaterial.description || '正在展示资料' }}</h3>
          </div>

          <!-- 视频展示逻辑 -->
          <div v-if="currentMaterial.type && currentMaterial.type.startsWith('video')" class="video-wrapper">
            <video
              ref="videoPlayer"
              class="video-js vjs-default-skin vjs-big-play-centered"
              controls
              preload="auto"
              width="100%"
            >
              <p class="vjs-no-js">您的浏览器不支持视频播放，请启用JavaScript。</p>
            </video>
          </div>

          <!-- PDF展示逻辑 -->
          <div v-else-if="currentMaterial.type === 'application/pdf'" class="pdf-wrapper">
            <iframe
              v-if="currentFileUrl"
              :src="currentFileUrl"
              width="100%"
              height="600"
              frameborder="0"
            ></iframe>
            <div v-else class="loading-placeholder">
              <el-skeleton animated>
                <template #template>
                  <el-skeleton-item variant="rect" style="width: 100%; height: 600px;" />
                </template>
              </el-skeleton>
            </div>
          </div>

          <!-- 未知类型展示 -->
          <div v-else class="unknown-type">
            <el-empty description="该资料类型暂不支持预览" />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import videojs from 'video.js'
import 'video.js/dist/video-js.css'
import { getMaterials } from '@/api/modules/teaching/MaterialAPI'
import { getVideoContents, streamVideo } from '@/api/modules/teaching/VideoContentAPI'
import { getFileContents, downloadFile } from '@/api/modules/teaching/FileContentAPI'

const props = defineProps({
  materialIds: {
    type: Array,
    required: true,
    default: () => []
  }
})

// 响应式状态
const loading = ref(false)
const hasError = ref(false)
const materialsList = ref([])
const currentMaterialId = ref(null)
const currentFileUrl = ref(null)
const videoPlayer = ref(null)
const player = ref(null)

// 计算当前选中的资料详情
const currentMaterial = computed(() => {
  return materialsList.value.find(m => m.id === currentMaterialId.value)
})

// 初始化与加载逻辑
const initPlayer = async () => {
  if (!props.materialIds || props.materialIds.length === 0) {
    hasError.value = true
    return
  }

  loading.value = true
  hasError.value = false
  materialsList.value = []
  
  try {
    // 获取所有资料的详细信息
    const promises = props.materialIds.map(id => getMaterials(id))
    const responses = await Promise.all(promises)
    
    const validMaterials = []
    responses.forEach(res => {
      if (res.data && res.data.length > 0) {
        validMaterials.push(res.data[0])
      }
    })

    if (validMaterials.length === 0) {
      hasError.value = true
      return
    }

    materialsList.value = validMaterials
    // 默认选中第一个
    handleSelectMaterial(validMaterials[0])
  } catch (error) {
    console.error('加载资料信息失败:', error)
    ElMessage.error('加载资料信息失败')
    hasError.value = true
  } finally {
    loading.value = false
  }
}

// 切换资料
const handleSelectMaterial = async (material) => {
  if (currentMaterialId.value === material.id) return
  
  // 清理之前的播放器和URL
  disposeCurrentDisplay()
  
  currentMaterialId.value = material.id
  
  if (material.type && material.type.startsWith('video')) {
    loadVideo(material)
  } else if (material.type === 'application/pdf') {
    loadPdf(material)
  }
}

// 加载视频逻辑
const loadVideo = async (material) => {
  try {
    // 1. 获取视频内容信息（获取文件名）
    const res = await getVideoContents(null, material.id, null)
    if (!res.data || res.data.length === 0) {
      ElMessage.error('未找到视频内容信息')
      return
    }
    const fileName = res.data[0].name

    // 2. 获取视频流 URL
    const videoStreamRes = await streamVideo(fileName)
    const blob = new Blob([videoStreamRes.data], { type: material.type })
    const url = URL.createObjectURL(blob)
    currentFileUrl.value = url

    // 3. 初始化 video.js
    if (videoPlayer.value) {
      player.value = videojs(videoPlayer.value, {
        autoplay: false,
        controls: true,
        preload: 'auto',
        fluid: true,
        sources: [{ src: url, type: material.type }]
      })
    }
  } catch (error) {
    console.error('加载视频失败:', error)
    ElMessage.error('加载视频失败')
  }
}

// 加载PDF逻辑
const loadPdf = async (material) => {
  try {
    // 1. 获取文件内容信息 (获取ID用于下载)
    const res = await getFileContents(null, null, null, null, material.id)
    if (!res.data || res.data.length === 0) {
      ElMessage.error('未找到PDF文件信息')
      return
    }
    const fileId = res.data[0].id

    // 2. 下载文件并创建 Blob URL
    const response = await downloadFile(fileId)
    const blob = new Blob([response.data], { type: 'application/pdf' })
    currentFileUrl.value = URL.createObjectURL(blob)
  } catch (error) {
    console.error('加载PDF失败:', error)
    ElMessage.error('加载PDF失败')
  }
}

// 清理逻辑
const disposeCurrentDisplay = () => {
  // 销毁视频播放器
  if (player.value) {
    player.value.dispose()
    player.value = null
  }
  // 释放 Blob URL
  if (currentFileUrl.value) {
    URL.revokeObjectURL(currentFileUrl.value)
    currentFileUrl.value = null
  }
}

const getTagType = (type) => {
  if (type.startsWith('video')) return 'success'
  if (type.startsWith('application/pdf')) return 'danger'
  return 'info'
}

// 监听 props 变化
watch(() => props.materialIds, () => {
  initPlayer()
}, { deep: true })

onMounted(() => {
  initPlayer()
})

onUnmounted(() => {
  disposeCurrentDisplay()
})
</script>

<style scoped lang="scss">
.material-player-wrapper {
  padding: 16px;
  background-color: var(--bg-white);
  border-radius: 8px;
  min-height: 400px;
}

.player-container {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* 选择部分样式 */
.selection-section {
  .section-title {
    margin: 0 0 12px 0;
    color: var(--text-primary);
    font-size: 16px;
    font-weight: 600;
  }

  .selection-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
    gap: 12px;
  }

  .material-card {
    cursor: pointer;
    transition: all 0.3s ease;
    border: 1px solid var(--border-light);

    &:hover {
      transform: translateY(-2px);
      border-color: var(--primary-light);
    }

    &.active {
      border-color: var(--primary);
      background-color: var(--bg-primary-light);
      box-shadow: 0 2px 8px var(--primary-alpha-10);

      .material-desc {
        color: var(--primary);
      }
    }

    .card-content {
      padding: 4px;
      
      .material-type {
        margin-bottom: 8px;
      }

      .material-desc {
        margin: 0;
        font-size: 14px;
        color: var(--text-regular);
        overflow: hidden;
        text-overflow: ellipsis;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
      }
    }
  }
}

/* 展示部分样式 */
.display-section {
  .display-content {
    background-color: var(--bg-light);
    border-radius: 12px;
    padding: 20px;
    border: 1px solid var(--border-light);
  }

  .display-header {
    margin-bottom: 16px;
    padding-bottom: 12px;
    border-bottom: 1px solid var(--border-light);

    .display-title {
      margin: 0;
      font-size: 18px;
      color: var(--primary-dark);
      font-weight: 500;
    }
  }

  .video-wrapper {
    background-color: #000;
    border-radius: 8px;
    overflow: hidden;
    box-shadow: 0 4px 20px var(--primary-alpha-20);
  }

  .pdf-wrapper {
    background-color: #fff;
    border-radius: 8px;
    overflow: hidden;
    border: 1px solid var(--border-light);
  }

  .loading-placeholder {
    padding: 20px;
  }
}

.error-view {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 400px;
}

:deep(.vjs-big-play-centered .vjs-big-play-button) {
  background-color: var(--primary-alpha-80);
  border-color: var(--primary);
}

:deep(.el-card__body) {
  padding: 12px;
}
</style>
