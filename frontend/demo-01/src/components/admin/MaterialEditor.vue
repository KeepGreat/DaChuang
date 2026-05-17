<template>
  <div class="material-editor">
    <div class="header-section">
      <div class="course-info">
        <h2 class="title">资料管理</h2>
        <div class="course-detail" v-if="courseInfo">
          <span class="label">当前课程:</span>
          <span class="value">{{ courseInfo.name }}</span>
          <el-tag size="small" type="info" class="course-id-tag">ID: {{ chosenCourseId }}</el-tag>
        </div>
      </div>
      <div class="actions">
        <el-button type="primary" class="create-btn" @click="handleCreate">
          <el-icon><Plus /></el-icon>新建资料
        </el-button>
      </div>
    </div>

    <el-card class="table-card">
      <el-table
        v-loading="loading"
        :data="materials"
        style="width: 100%"
        :header-cell-style="{ background: 'var(--bg-primary-lighter)', color: 'var(--text-primary)' }"
        row-key="id"
      >
        <el-table-column prop="type" label="类型" width="150">
          <template #default="{ row }">
            <el-tag :type="getTagType(row.type)" size="small">{{ row.type }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" show-overflow-tooltip />
        <el-table-column label="内容信息" width="250">
          <template #default="{ row: material }">
            <div v-if="material.type && material.type.startsWith('video')">
              <div v-for="content in getContentByMaterialId(material)" :key="content.id" class="content-info">
                <div><el-icon><Monitor /></el-icon> 大小: {{ formatSize(content.size) }}</div>
                <div><el-icon><Clock /></el-icon> 时长: {{ formatDuration(content.duration) }}</div>
              </div>
            </div>
            <div v-else-if="material.type && material.type.startsWith('application')">
              <div v-for="content in getContentByMaterialId(material)" :key="content.id" class="content-info">
                <div><el-icon><Document /></el-icon> 大小: {{ formatSize(content.size) }}</div>
              </div>
            </div>
            <div v-else class="empty-info">- 无内容信息 -</div>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column prop="updatedAt" label="更新时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.updatedAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="225" fixed="right">
          <template #default="{ row }">
            <el-button type="info" size="small" link @click="handleView(row)">查看</el-button>
            <!-- <el-button type="primary" size="small" link @click="handleEdit(row)">编辑</el-button> -->
            <el-button type="danger" size="small" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 资料展示对话框 -->
    <el-dialog
      v-model="viewDialogVisible"
      title="资料预览"
      width="80%"
      destroy-on-close
    >
      <MaterialPlayer :material-ids="selectedMaterialIds" />
      <template #footer>
        <el-button @click="viewDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 资料上传对话框 -->
    <el-dialog
      v-model="uploadDialogVisible"
      title="新建资料"
      width="600px"
      destroy-on-close
    >
      <MaterialUpload :course-id="chosenCourseId" @success="handleUploadSuccess" @back="uploadDialogVisible = false" />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Monitor, Clock, Document } from '@element-plus/icons-vue'
import { teacherStore } from '@/store/modules/teacher/teacherStore'
import MaterialPlayer from '@/components/teaching/MaterialPlayer.vue'
import MaterialUpload from '@/components/admin/MaterialUpload.vue'
import { getCourses } from '@/api/modules/teaching/CourseAPI'
import { getMaterials, deleteMaterial } from '@/api/modules/teaching/MaterialAPI'
import { getVideoContents, deleteVideoContent } from '@/api/modules/teaching/VideoContentAPI'
import { getFileContents, deleteFile } from '@/api/modules/teaching/FileContentAPI'

const router = useRouter()
const store = teacherStore()

const chosenCourseId = computed(() => store.chosenCourseId)
const courseInfo = ref(null)
const materials = ref([])
const loading = ref(false)

const viewDialogVisible = ref(false)
const uploadDialogVisible = ref(false)
const selectedMaterialIds = ref([])

const videoContentsMap = reactive(new Map())
const fileContentsMap = reactive(new Map())

// 检查课程ID并跳转
const checkCourseId = () => {
  if (!chosenCourseId.value || chosenCourseId.value === 0) {
    ElMessage.warning('请先选择课程')
    router.back()
    return false
  }
  return true
}

// 获取课程详情
const fetchCourseDetail = async () => {
  try {
    const res = await getCourses(chosenCourseId.value)
    if (res.data && res.data.length > 0) {
      courseInfo.value = res.data[0]
    }
  } catch (error) {
    ElMessage.error('获取课程信息失败')
  }
}

// 获取资料列表
const fetchMaterialsList = async () => {
  loading.value = true
  try {
    const res = await getMaterials(null, null, null, null, null, null, chosenCourseId.value)
    materials.value = res.data || []
  } catch (error) {
    ElMessage.error('获取资料列表失败')
  } finally {
    loading.value = false
  }
}

// 加载视频内容详情
const loadVideoContent = async (matId) => {
  try {
    const response = await getVideoContents(null, matId, null)
    const data = response.data || []
    videoContentsMap.set(matId, data)
    return data
  } catch (error) {
    console.error('加载视频内容失败', error)
    return []
  }
}

// 加载文件内容详情
const loadFileContent = async (matId) => {
  try {
    const response = await getFileContents(null, null, null, null, matId)
    const data = response.data || []
    fileContentsMap.set(matId, data)
    return data
  } catch (error) {
    console.error('加载文件内容失败', error)
    return []
  }
}

const getVideoContentsByMatId = async (matId) => {
  if (videoContentsMap.has(matId)) {
    return videoContentsMap.get(matId) || []
  }
  return loadVideoContent(matId)
}

const getFileContentsByMatId = async (matId) => {
  if (fileContentsMap.has(matId)) {
    return fileContentsMap.get(matId) || []
  }
  return loadFileContent(matId)
}

// 获取内容详情
const getContentByMaterialId = (material) => {
  const matId = material.id
  if (material.type && material.type.startsWith('video')) {
    if (!videoContentsMap.has(matId)) {
      loadVideoContent(matId)
    }
    return videoContentsMap.get(matId) || []
  } else if (material.type && material.type.startsWith('application')) {
    if (!fileContentsMap.has(matId)) {
      loadFileContent(matId)
    }
    return fileContentsMap.get(matId) || []
  }
  return []
}

// 格式化大小
const formatSize = (size) => {
  if (!size) return '0 B'
  const units = ['B', 'KB', 'MB', 'GB']
  let unitIndex = 0
  let currentSize = size
  while (currentSize >= 1024 && unitIndex < units.length - 1) {
    currentSize /= 1024
    unitIndex++
  }
  return `${currentSize.toFixed(2)} ${units[unitIndex]}`
}

// 格式化时长
const formatDuration = (duration) => {
  if (!duration) return '00:00'
  const minutes = Math.floor(duration / 60)
  const seconds = Math.floor(duration % 60)
  return `${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`
}

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleString()
}

// 获取标签类型
const getTagType = (type) => {
  if (type.startsWith('video')) return 'success'
  if (type.startsWith('application/pdf')) return 'danger'
  if (type.startsWith('application')) return 'warning'
  return 'info'
}

// 查看资料处理
const handleView = (row) => {
  selectedMaterialIds.value = [row.id]
  viewDialogVisible.value = true
}

// 新建资料处理
const handleCreate = () => {
  uploadDialogVisible.value = true
}

// 上传成功处理
const handleUploadSuccess = () => {
  uploadDialogVisible.value = false
  fetchMaterialsList()
}

// 编辑处理 (暂不实现具体逻辑)
const handleEdit = (row) => {
  ElMessage.info(`编辑资料: ${row.id}`)
}

// 删除处理
const handleDelete = (row) => {
  const matId = row?.id
  if (!matId) {
    ElMessage.error('资料ID无效，无法删除')
    return
  }

  ElMessageBox.confirm('确定要删除该资料吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      if (row.type?.startsWith('application')) {
        const fileContents = await getFileContentsByMatId(matId)
        const fileIds = fileContents
          .map(item => item?.id)
          .filter(id => Number.isInteger(id) && id > 0)

        await Promise.all(fileIds.map(id => deleteFile(id)))
      } else if (row.type?.startsWith('video')) {
        const videoContents = await getVideoContentsByMatId(matId)
        const videoIds = videoContents
          .map(item => item?.id)
          .filter(id => Number.isInteger(id) && id > 0)

        await Promise.all(videoIds.map(id => deleteVideoContent(id)))
      }

      await deleteMaterial(matId)
      videoContentsMap.delete(matId)
      fileContentsMap.delete(matId)
      ElMessage.success('删除成功')
      fetchMaterialsList()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

onMounted(() => {
  if (checkCourseId()) {
    fetchCourseDetail()
    fetchMaterialsList()
  }
})
</script>

<style scoped lang="scss">
.material-editor {
  padding: 24px;
  background-color: var(--bg-light);
  min-height: calc(100vh - 40px);

  .header-section {
    display: flex;
    justify-content: space-between;
    align-items: flex-end;
    margin-bottom: 24px;
    padding-bottom: 16px;
    border-bottom: 1px solid var(--border-light);

    .course-info {
      .title {
        margin: 0 0 8px 0;
        font-size: 24px;
        color: var(--text-primary);
        font-weight: 600;
      }

      .course-detail {
        display: flex;
        align-items: center;
        gap: 8px;
        font-size: 14px;

        .label {
          color: var(--text-regular);
        }

        .value {
          color: var(--primary);
          font-weight: 500;
        }

        .course-id-tag {
          margin-left: 4px;
        }
      }
    }

    .create-btn {
      background-color: var(--primary);
      border-color: var(--primary);
      
      &:hover {
        background-color: var(--primary-hover);
        border-color: var(--primary-hover);
      }

      .el-icon {
        margin-right: 4px;
      }
    }
  }

  .table-card {
    border-radius: 8px;
    border: none;
    box-shadow: 0 4px 12px var(--primary-alpha-10);

    :deep(.el-table) {
      --el-table-header-bg-color: var(--bg-primary-lighter);
      
      th.el-table__cell {
        font-weight: 600;
      }
    }

    .content-info {
      font-size: 13px;
      color: var(--text-regular);
      display: flex;
      flex-direction: column;
      gap: 4px;

      .el-icon {
        vertical-align: middle;
        margin-right: 4px;
        color: var(--primary-light);
      }
    }

    .empty-info {
      color: var(--text-placeholder);
      font-style: italic;
    }
  }
}
</style>
