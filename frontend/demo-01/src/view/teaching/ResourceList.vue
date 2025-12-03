<template>
  <div class="resource-list">
    <div class="list-header">
      <h2>课程资料</h2>
    </div>

    <!-- 面包屑导航 -->
    <div class="breadcrumb-section">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item @click="goToRoot" class="breadcrumb-link">
          <el-icon><FolderOpened /></el-icon>
          全部文件
        </el-breadcrumb-item>
        <el-breadcrumb-item
          v-for="(folder, index) in currentPath"
          :key="index"
          @click="navigateToPath(index)"
          class="breadcrumb-link"
        >
          {{ folder }}
        </el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <!-- 筛选和搜索 -->
    <div class="filter-section">
      <div class="filter-left">
        <el-select v-model="typeFilter" placeholder="文件类型" style="width: 150px">
          <el-option label="全部" value="all"></el-option>
          <el-option label="文档" value="document"></el-option>
          <el-option label="视频" value="video"></el-option>
          <el-option label="音频" value="audio"></el-option>
          <el-option label="图片" value="image"></el-option>
          <el-option label="压缩包" value="archive"></el-option>
          <el-option label="文件夹" value="folder"></el-option>
        </el-select>
        <el-select v-model="sortBy" placeholder="排序方式" style="width: 150px">
          <el-option label="按名称" value="name"></el-option>
          <el-option label="按大小" value="size"></el-option>
          <el-option label="按上传时间" value="time"></el-option>
        </el-select>
      </div>
      <div class="search-box">
        <el-input
          v-model="searchQuery"
          placeholder="搜索资料..."
          style="width: 300px"
          clearable
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
      </div>
    </div>

    <!-- 工具栏 -->
    <div class="toolbar">
      <div class="view-toggle">
        <el-radio-group v-model="viewMode">
          <el-radio-button label="list">
            <el-icon><List /></el-icon>
          </el-radio-button>
          <el-radio-button label="grid">
            <el-icon><Grid /></el-icon>
          </el-radio-button>
        </el-radio-group>
      </div>
      <div class="selection-info" v-if="selectedResources.length > 0">
        <span>已选择 {{ selectedResources.length }} 项</span>
        <el-button text type="danger" @click="clearSelection">取消选择</el-button>
      </div>
    </div>

    <!-- 文件列表 -->
    <div class="resource-content">
      <div v-if="filteredResources.length === 0" class="empty-state">
        <el-empty description="暂无资料" />
      </div>
      <!-- 列表视图 -->
      <div v-else-if="viewMode === 'list'" class="list-view">
        <el-table
          :data="paginatedResources"
          @selection-change="handleSelectionChange"
          style="width: 100%"
        >
          <el-table-column type="selection" width="55" />
          <el-table-column label="名称" min-width="300">
            <template #default="{ row }">
              <div class="file-info" @click="handleFileClick(row)">
                <el-icon class="file-icon" :size="24">
                  <Folder v-if="row.type === 'folder'" />
                  <Document v-else-if="row.type === 'document'" />
                  <VideoPlay v-else-if="row.type === 'video'" />
                  <Picture v-else-if="row.type === 'image'" />
                  <Files v-else />
                </el-icon>
                <span class="file-name">{{ row.name }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="大小" width="120">
            <template #default="{ row }">
              {{ row.type === 'folder' ? '-' : formatFileSize(row.size) }}
            </template>
          </el-table-column>
          <el-table-column label="上传者" width="120" prop="uploader" />
          <el-table-column label="上传时间" width="180">
            <template #default="{ row }">
              {{ formatTime(row.uploadTime) }}
            </template>
          </el-table-column>
          <el-table-column label="下载次数" width="120" prop="downloadCount" />
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <el-button
                v-if="row.type !== 'folder'"
                text
                type="primary"
                size="small"
                @click="downloadResource(row.id)"
              >
                下载
              </el-button>
              <el-button
                v-if="row.type === 'folder'"
                text
                type="primary"
                size="small"
                @click="enterFolder(row)"
              >
                打开
              </el-button>
              <el-button text type="info" size="small" @click="showResourceDetail(row)">
                详情
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <!-- 网格视图 -->
      <div v-else class="grid-view">
        <div
          v-for="resource in paginatedResources"
          :key="resource.id"
          class="grid-item"
          :class="{ selected: isSelected(resource.id) }"
          @click="toggleSelection(resource.id)"
          @dblclick="handleFileClick(resource)"
        >
          <div class="item-checkbox">
            <el-checkbox
              :model-value="isSelected(resource.id)"
              @change="toggleSelection(resource.id)"
              @click.stop
            />
          </div>
          <div class="item-icon">
            <el-icon :size="48">
              <Folder v-if="resource.type === 'folder'" />
              <Document v-else-if="resource.type === 'document'" />
              <VideoPlay v-else-if="resource.type === 'video'" />
              <Picture v-else-if="resource.type === 'image'" />
              <Files v-else />
            </el-icon>
          </div>
          <div class="item-name" :title="resource.name">{{ resource.name }}</div>
          <div class="item-meta">
            <span v-if="resource.type !== 'folder'">{{ formatFileSize(resource.size) }}</span>
            <span>{{ resource.downloadCount }}次下载</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 分页 -->
    <div class="pagination-wrapper">
      <el-pagination
        v-model:current-page="currentPage"
        :page-size="pageSize"
        :total="filteredResources.length"
        layout="prev, pager, next, jumper, total"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useResourceStore } from '@/store'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Search, List, Grid, Folder, Document,
  VideoPlay, Picture, Files, FolderOpened, ArrowDown
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const resourceStore = useResourceStore()

// 响应式数据
const viewMode = ref('list')
const searchQuery = ref('')
const typeFilter = ref('all')
const sortBy = ref('time')
const currentPage = ref(1)
const pageSize = ref(20)
const selectedResources = ref([])
const currentPath = ref([])

// 计算属性
const courseId = computed(() => route.params.courseId)
const currentResources = computed(() => {
  if (currentPath.value.length === 0) {
    return resourceStore.getResourcesByCourseId(courseId.value)
  }
  return resourceStore.getCurrentFolderContent
})

const filteredResources = computed(() => {
  let result = [...currentResources.value]

  // 类型筛选
  if (typeFilter.value !== 'all') {
    result = result.filter(r => r.type === typeFilter.value)
  }

  // 搜索筛选
  if (searchQuery.value.trim()) {
    const query = searchQuery.value.toLowerCase()
    result = result.filter(r =>
      r.name.toLowerCase().includes(query) ||
      (r.description && r.description.toLowerCase().includes(query))
    )
  }

  // 排序
  result.sort((a, b) => {
    switch (sortBy.value) {
      case 'name':
        return a.name.localeCompare(b.name)
      case 'size':
        return (a.size || 0) - (b.size || 0)
      case 'time':
        return new Date(b.uploadTime) - new Date(a.uploadTime)
      default:
        return 0
    }
  })

  return result
})

const paginatedResources = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredResources.value.slice(start, end)
})

// 方法
const handleFileClick = (resource) => {
  if (resource.type === 'folder') {
    enterFolder(resource)
  } else {
    showResourceDetail(resource)
  }
}

const enterFolder = (folder) => {
  resourceStore.navigateToFolder(folder)
  currentPath.value.push(folder.name)
  currentPage.value = 1
}

const goToRoot = () => {
  resourceStore.setCurrentFolder(null)
  currentPath.value = []
  currentPage.value = 1
}

const navigateToPath = (index) => {
  currentPath.value = currentPath.value.slice(0, index + 1)
  // 这里应该根据路径导航到对应文件夹
  resourceStore.setCurrentFolder(null)
  currentPage.value = 1
}

const downloadResource = (resourceId) => {
  resourceStore.downloadResource(resourceId)
  ElMessage.success('开始下载')
}


const showResourceDetail = (resource) => {
  router.push(`/teaching/course/${courseId.value}/resource/${resource.id}`)
}


const handleSelectionChange = (selection) => {
  selectedResources.value = selection.map(item => item.id)
}

const toggleSelection = (resourceId) => {
  resourceStore.toggleResourceSelection(resourceId)
  selectedResources.value = resourceStore.selectedResources
}

const isSelected = (resourceId) => {
  return selectedResources.value.includes(resourceId)
}

const clearSelection = () => {
  resourceStore.clearSelection()
  selectedResources.value = []
}

const formatFileSize = (size) => {
  return resourceStore.formatFileSize(size)
}

const formatTime = (timeStr) => {
  const date = new Date(timeStr)
  return date.toLocaleString()
}

// 生命周期
onMounted(() => {
  resourceStore.clearSelection()
})
</script>

<style scoped>
.resource-list {
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

.breadcrumb-section {
  margin-bottom: 20px;
}

.breadcrumb-link {
  cursor: pointer;
}

.breadcrumb-link:hover {
  color: #409eff;
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

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.selection-info {
  display: flex;
  align-items: center;
  gap: 10px;
  color: #606266;
}

.resource-content {
  min-height: 400px;
}

.empty-state {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 300px;
}

.file-info {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
}

.file-info:hover .file-name {
  color: #409eff;
}

.file-icon {
  color: #909399;
}

.file-name {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.grid-view {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 20px;
}

.grid-item {
  position: relative;
  padding: 20px;
  background: #fff;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.grid-item:hover {
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
}

.grid-item.selected {
  border-color: #409eff;
  background: #ecf5ff;
}

.item-checkbox {
  position: absolute;
  top: 10px;
  left: 10px;
  opacity: 0;
  transition: opacity 0.3s;
}

.grid-item:hover .item-checkbox,
.grid-item.selected .item-checkbox {
  opacity: 1;
}

.item-icon {
  display: flex;
  justify-content: center;
  margin-bottom: 15px;
  color: #909399;
}

.item-name {
  text-align: center;
  margin-bottom: 8px;
  font-size: 14px;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.item-meta {
  display: flex;
  flex-direction: column;
  gap: 4px;
  font-size: 12px;
  color: #909399;
  text-align: center;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .resource-list {
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

  .grid-view {
    grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
    gap: 10px;
  }
}
</style>