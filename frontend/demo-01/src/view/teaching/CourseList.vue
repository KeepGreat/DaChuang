<template>
  <div class="course-list">
    <div class="list-header">
      <h2>教学内容</h2>
    </div>

    <!-- 课时与资料树 -->
    <div class="courses-container">
      <div v-if="teachingStore.loading" class="loading-container">
        <el-skeleton :rows="5" animated />
      </div>

      <div v-else-if="course.length === 0" class="empty-container">
        <el-empty description="暂无课程内容" />
      </div>

      <div v-else class="tree-wrapper">
        <el-tree
          :data="course"
          node-key="key"
          default-expand-all
          :expand-on-click-node="false"
          :highlight-current="true"
          @node-click="handleNodeClick"
        >
          <template #default="{ data }">
            <div class="tree-node" :class="{ 'is-course-node': data.nodeType === 'course', 'is-material-node': data.nodeType === 'material' }">
              <div class="node-left">
                <el-icon class="node-icon" :size="18">
                  <Folder v-if="data.nodeType === 'course'" />
                  <Document v-else />
                </el-icon>
                <span class="node-title">{{ data.label }}</span>
                <el-tag v-if="data.nodeType === 'material'" size="small" :type="getMaterialTagType(data.materialType)">
                  {{ getMaterialTypeLabel(data.materialType) }}
                </el-tag>
              </div>
              <div class="node-right" v-if="data.nodeType === 'course'">
                <span class="material-count">{{ data.children?.length || 0 }} 个资料</span>
                <el-icon class="arrow-icon"><ArrowRight /></el-icon>
              </div>
            </div>
          </template>
        </el-tree>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  Folder, ArrowRight, Document
} from '@element-plus/icons-vue'
// 导入 Pinia store
import { useTeachingStore } from '@/store'
import { getMaterials } from '@/api/modules/teaching/MaterialAPI'

const router = useRouter()

// 使用 Pinia store
const teachingStore = useTeachingStore()
const course = ref([])

// 跳转到课程详情
const enterCourseDetail = (courseNode) => {
  const courseSectionId = router.currentRoute.value.params.id
  const validMaterialIds = (courseNode.materialIds || []).filter(id => id !== null && id !== undefined)

  router.push({
    path: `/teaching/course/${courseSectionId}/learn/series/${courseNode.id}`,
    query: {
      id: courseNode.id,
      title: courseNode.title,
      description: courseNode.description,
      materialIds: JSON.stringify(validMaterialIds),
      videoCount: String(courseNode.videoCount || 0),
      pdfCount: String(courseNode.pdfCount || 0),
      totalCount: String(validMaterialIds.length)
    }
  })
}

const handleNodeClick = (data) => {
  if (data.nodeType === 'course') {
    enterCourseDetail(data)
    return
  }

  if (data.nodeType === 'material' && data.courseId) {
    const targetCourse = course.value.find(item => item.id === data.courseId)
    if (targetCourse) {
      enterCourseDetail(targetCourse)
    }
  }
}

const getMaterialTypeLabel = (type) => {
  if (type?.startsWith('video')) return '视频'
  if (type === 'application/pdf') return '图文'
  return '资料'
}

const getMaterialTagType = (type) => {
  if (type?.startsWith('video')) return 'success'
  if (type === 'application/pdf') return 'warning'
  return 'info'
}

// 初始化课程系列列表
const initCourseSeries = async () => {
  try {
    // 获取当前课程章节ID
    const courseSectionId = router.currentRoute.value.params.id

    // 调用 store 的方法获取课程数据
    const response = await teachingStore.fetchCourses(courseSectionId)

    if (response.code === 200) {
      ElMessage.success(response.message || '课程列表加载成功')

      const courseList = Array.isArray(response.data) ? response.data : []
      const courseWithMaterials = await Promise.all(
        courseList.map(async (courseItem) => {
          let materials = []
          try {
            const materialResponse = await getMaterials(null, null, null, null, null, null, courseItem.id)
            materials = Array.isArray(materialResponse?.data) ? materialResponse.data : []
          } catch (err) {
            console.error(`课程 ${courseItem.id} 资料加载失败:`, err)
          }

          const materialIds = materials.map(item => item.id).filter(Boolean)
          const videoCount = materials.filter(item => item.type && item.type.startsWith('video')).length
          const pdfCount = materials.filter(item => item.type === 'application/pdf').length

          const courseNode = {
            key: `course-${courseItem.id}`,
            nodeType: 'course',
            id: courseItem.id,
            label: courseItem.name,
            title: courseItem.name,
            description: courseItem.description || '',
            materialIds,
            videoCount,
            pdfCount,
            children: []
          }

          courseNode.children = materials.map(material => ({
            key: `material-${courseItem.id}-${material.id}`,
            nodeType: 'material',
            id: material.id,
            label: material.description && material.description.trim()
              ? material.description.trim()
              : `资料 ${material.id}`,
            materialType: material.type,
            courseId: courseItem.id
          }))

          return courseNode
        })
      )

      course.value = courseWithMaterials
    } else {
      ElMessage.error(response.message || '加载课程列表失败')
    }
  } catch (error) {
    console.error('加载课程列表失败:', error)
    ElMessage.error('加载课程列表失败，请重试')
  }
}

onMounted(() => {
  initCourseSeries()
})
</script>

<style scoped>
.course-list {
  padding: 24px;
  background: #fff;
  min-height: 100%;
}

/* 列表头部 */
.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid var(--border-light);
}

.list-header h2 {
  margin: 0;
  font-size: 24px;
  color: var(--primary-dark);
  font-weight: 500;
}

.header-info {
  display: flex;
  align-items: center;
  gap: 16px;
}

.course-count {
  color: var(--text-regular);
  font-size: 14px;
}

/* 课程容器 */
.courses-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.tree-wrapper {
  border: 1px solid var(--border-light);
  border-radius: 12px;
  padding: 8px 0;
  background: #fff;
}

.tree-wrapper :deep(.el-tree-node__content) {
  min-height: 58px;
  padding-right: 12px;
}

.tree-wrapper :deep(.el-tree-node__children .el-tree-node__content) {
  min-height: 52px;
}

.tree-node {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 6px 10px;
  border-radius: 8px;
}

.tree-node:hover {
  background: var(--bg-primary-light);
}

.node-left {
  display: flex;
  align-items: center;
  gap: 10px;
  min-width: 0;
}

.node-icon {
  color: var(--primary);
  flex-shrink: 0;
}

.node-title {
  color: var(--primary-dark);
  font-size: 15px;
  font-weight: 500;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.is-material-node .node-title {
  font-size: 14px;
  font-weight: 400;
  color: var(--text-regular);
}

.node-right {
  display: flex;
  align-items: center;
  gap: 10px;
  color: var(--text-secondary);
  flex-shrink: 0;
}

.material-count {
  font-size: 12px;
}

/* 加载状态 */
.loading-container {
  padding: 20px;
}

/* 空状态 */
.empty-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 300px;
}

.arrow-icon {
  font-size: 20px;
  color: var(--text-secondary);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .course-list {
    padding: 16px;
  }

  .tree-wrapper :deep(.el-tree-node__content) {
    min-height: 52px;
    padding-right: 8px;
  }

  .node-title {
    font-size: 14px;
  }

  .is-material-node .node-title {
    font-size: 13px;
  }

  .material-count {
    display: none;
  }
}
</style>