<template>
  <div class="course-section-editor">
    <div v-if="!courseSectionId" class="selection-view">
      <el-card class="selection-card">
        <template #header>
          <div class="card-header">
            <span>选择课程</span>
          </div>
        </template>
        <el-table
          :data="courseSections"
          @row-click="handleSelectCourseSection"
          style="width: 100%"
          :header-cell-style="{ background: 'var(--bg-primary-lighter)', color: 'var(--text-primary)' }"
          :row-style="{ cursor: 'pointer' }"
        >
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="name" label="课程名称" />
          <el-table-column prop="description" label="描述" show-overflow-tooltip />
          <el-table-column label="类型" width="120">
            <template #default="{ row }">
              {{ getTypeNameById(row.courseSectionTypeId) }}
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </div>

    <div v-else class="editor-view">
      <el-card class="editor-card">
        <el-tabs v-model="activeTab" class="editor-tabs">
          <el-tab-pane label="课程信息" name="info">
            <div class="editor-content">
              <div class="section-form">
                <h3 class="section-title">基本信息</h3>
                <el-form :model="courseSectionForm" label-width="100px" class="form">
                  <el-form-item label="课程名称">
                    <el-input
                      v-model="courseSectionForm.name"
                      placeholder="请输入课程名称"
                      style="width: 300px"
                    />
                  </el-form-item>
                  <el-form-item label="课程描述">
                    <el-input
                      v-model="courseSectionForm.description"
                      type="textarea"
                      :rows="3"
                      placeholder="请输入课程描述"
                      style="width: 300px"
                    />
                  </el-form-item>
                  <el-form-item label="课程类型">
                    <el-select
                      v-model="courseSectionForm.courseSectionTypeId"
                      placeholder="请选择课程类型"
                      style="width: 300px"
                    >
                      <el-option
                        v-for="type in courseSectionTypes"
                        :key="type.id"
                        :label="type.name"
                        :value="type.id"
                      />
                    </el-select>
                  </el-form-item>
                  <el-form-item>
                    <el-button
                      type="primary"
                      @click="handleUpdateCourseSection"
                      style="background: var(--primary); border-color: var(--primary)"
                    >
                      保存修改
                    </el-button>
                  </el-form-item>
                </el-form>
              </div>

              <div class="section-courses">
                <div class="section-title-row">
                  <h3 class="section-title">课时列表</h3>
                  <el-button
                    type="primary"
                    @click="handleOpenCreateCourseDialog"
                    style="background: var(--primary); border-color: var(--primary)"
                  >
                    新增课时
                  </el-button>
                </div>
                <el-table
                  :data="courses"
                  row-key="id"
                  style="width: 100%"
                  :header-cell-style="{ background: 'var(--bg-primary-lighter)', color: 'var(--text-primary)' }"
                >
                  <el-table-column prop="name" label="课时名称" />
                  <el-table-column prop="description" label="描述" show-overflow-tooltip />
                  <el-table-column label="相关资料" width="120">
                    <template #default="{ row }">
                      <el-button
                        type="primary"
                        size="small"
                        plain
                        @click="handleManageMaterials(row.id)"
                        style="border-color: var(--primary); color: var(--primary)"
                      >
                        管理
                      </el-button>
                    </template>
                  </el-table-column>
                  <el-table-column label="操作" width="150" fixed="right">
                    <template #default="{ row }">
                      <el-button
                        type="primary"
                        size="small"
                        link
                        @click="handleEditCourse(row)"
                        style="color: var(--primary)"
                      >
                        编辑
                      </el-button>
                      <el-button
                        type="danger"
                        size="small"
                        link
                        @click="handleDeleteCourse(row.id)"
                        style="color: var(--danger)"
                      >
                        删除
                      </el-button>
                    </template>
                  </el-table-column>
                </el-table>
              </div>

              <div class="section-practices">
                <div class="section-title-row">
                  <h3 class="section-title">练习列表</h3>
                  <el-button
                    type="primary"
                    @click="handleOpenCreatePracticeDialog"
                    style="background: var(--primary); border-color: var(--primary)"
                  >
                    新建练习
                  </el-button>
                </div>
                <el-table
                  :data="practices"
                  style="width: 100%"
                  :header-cell-style="{ background: 'var(--bg-primary-lighter)', color: 'var(--text-primary)' }"
                >
                  <el-table-column prop="name" label="练习名称" />
                  <el-table-column prop="questionNum" label="问题数量" width="120" />
                  <el-table-column label="类型" width="120">
                    <template #default="{ row }">
                      {{ getPracticeTypeNameById(row.practiceTypeId) }}
                    </template>
                  </el-table-column>
                  <el-table-column prop="createdAt" label="创建时间" width="180" />
                  <el-table-column prop="expiredAt" label="过期时间" width="180" />
                  <el-table-column label="所属课程" width="150">
                    <template #default="{ row }">
                      {{ getCourseNameById(getPracticeCourseId(row.id)) }}
                    </template>
                  </el-table-column>
                  <el-table-column label="操作" width="150" fixed="right">
                    <template #default="{ row }">
                      <el-button
                        type="primary"
                        size="small"
                        @click="handleEditPractice(row)"
                        style="background: var(--primary); border-color: var(--primary); color: white"
                      >
                        编辑
                      </el-button>
                      <el-button
                        type="danger"
                        size="small"
                        @click="handleDeletePractice(row.id)"
                        style="background: var(--danger); border-color: var(--danger); color: white"
                      >
                        删除
                      </el-button>
                    </template>
                  </el-table-column>
                </el-table>
                <div v-if="practices.length === 0" class="empty-tip">暂无练习数据</div>
              </div>
            </div>
          </el-tab-pane>

          <el-tab-pane label="知识图谱" name="graph" lazy>
            <KnowledgeGraph :course-section-id="Number(courseSectionId)" />
          </el-tab-pane>
        </el-tabs>
      </el-card>
    </div>

    <el-dialog
      v-model="courseDialogVisible"
      :title="dialogMode === 'edit' ? '编辑课时' : '编辑练习'"
      width="500px"
    >
      <!-- 编辑课程 -->
      <el-form v-if="dialogMode === 'edit'" :model="courseForm" label-width="100px">
        <el-form-item label="课时名称">
          <el-input v-model="courseForm.name" placeholder="请输入课时名称" />
        </el-form-item>
        <el-form-item label="课时描述">
          <el-input
            v-model="courseForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入课时描述"
          />
        </el-form-item>
      </el-form>
      
      <el-form v-else :model="practiceForm" label-width="100px">
        <el-form-item label="练习名称">
          <el-input v-model="practiceForm.name" placeholder="请输入练习名称" />
        </el-form-item>
        <el-form-item label="问题数量">
          <el-input-number v-model="practiceForm.questionNum" min="1" placeholder="请输入问题数量" />
        </el-form-item>
        <el-form-item label="练习类型">
          <el-select v-model="practiceForm.practiceTypeId" placeholder="请选择练习类型" style="width: 100%">
            <el-option
              v-for="type in practiceTypes"
              :key="type.id"
              :label="type.name"
              :value="type.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="过期时间">
          <el-date-picker
            v-model="practiceForm.expiredAt"
            type="datetime"
            placeholder="选择过期时间"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="courseDialogVisible = false">取消</el-button>
        <el-button
          type="primary"
          @click="handleSaveDialog"
          style="background: var(--primary); border-color: var(--primary)"
        >
          确定
        </el-button>
      </template>
    </el-dialog>

    <PracticeUploader
      v-model:visible="createPracticeDialogVisible"
      :course-section-id="Number(courseSectionId)"
      @success="handlePracticeCreated"
    />

    <CourseUploader
      v-model:visible="createCourseDialogVisible"
      :course-section-id="Number(courseSectionId)"
      @success="handleCourseCreated"
    />
  </div>
</template>

<script setup>
import { ref, reactive, computed, nextTick, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { teacherStore } from '@/store/modules/teacher/teacherStore'
import {
  getCourseSections,
  updateCourseSection,
  getCourseSectionTypes
} from '@/api/modules/teaching/CourseSectionAPI'
import {
  getCourses,
  updateCourse,
  deleteCourse
} from '@/api/modules/teaching/CourseAPI'
import {
  deleteKnowledgeGraphByCourseSectionId
} from '@/api/modules/teaching/KnowledgeGraphAPI'
import {
  getPracticeIndexes
} from '@/api/modules/practice/practiceIndex'
import {
  getPractices,
  updatePractice,
  deletePracticeById,
  getPracticeTypes
} from '@/api/modules/practice/practice'
import PracticeUploader from '@/components/admin/PracticeUploader.vue'
import CourseUploader from '@/components/admin/CourseUploader.vue'
import KnowledgeGraph from '@/components/admin/KnowledgeGraph.vue'

const store = teacherStore()
const router = useRouter()

const courseSections = ref([])
const courseSectionTypes = ref([])
const courses = ref([])
const practices = ref([])
const practiceIndexes = reactive(new Map())
const practiceTypes = ref([])

const courseSectionId = computed(() => store.chosenCourseSectionId)

const courseSectionForm = ref({
  id: 0,
  name: '',
  description: '',
  courseSectionTypeId: null,
  teacherId: ''
})

const courseDialogVisible = ref(false)
const dialogMode = ref('edit')
const courseForm = ref({
  id: 0,
  name: '',
  description: '',
  sectionId: 0
})

const practiceForm = ref({
  id: 0,
  name: '',
  questionNum: 0,
  practiceTypeId: 0,
  createdAt: '',
  expiredAt: null
})

const currentEditingId = ref(0)

const createPracticeDialogVisible = ref(false)
const createCourseDialogVisible = ref(false)
const activeTab = ref('info')

watch(activeTab, async (value) => {
  if (value === 'graph') {
    await nextTick()
    window.dispatchEvent(new Event('resize'))
  }
})

const getTypeNameById = (typeId) => {
  const type = courseSectionTypes.value.find(t => t.id === typeId)
  return type ? type.name : '未知类型'
}

const loadCourseSections = async () => {
  try {
    const response = await getCourseSections()
    courseSections.value = response.data || []
  } catch (error) {
    ElMessage.error('加载课程失败')
  }
}

const loadCourseSectionTypes = async () => {
  try {
    const response = await getCourseSectionTypes()
    courseSectionTypes.value = response.data || []
  } catch (error) {
    ElMessage.error('加载课程类型失败')
  }
}

const loadCourseSectionDetail = async () => {
  if (!courseSectionId.value) return

  try {
    const response = await getCourseSections(courseSectionId.value)
    const sectionData = response.data?.[0]
    if (sectionData) {
      courseSectionForm.value = {
        id: sectionData.id,
        name: sectionData.name,
        description: sectionData.description,
        courseSectionTypeId: sectionData.courseSectionTypeId,
        teacherId: sectionData.teacherId
      }
    }
  } catch (error) {
    ElMessage.error('加载课程详情失败')
  }
}

const loadCourses = async () => {
  if (!courseSectionId.value) return

  try {
    const response = await getCourses(null, null, courseSectionId.value)
    courses.value = response.data || []
  } catch (error) {
    ElMessage.error('加载课时列表失败')
  }
}

const loadPracticeIndexes = async () => {
  if (!courseSectionId.value) return

  try {
    const response = await getPracticeIndexes({ courseSectionId: courseSectionId.value })
    const indexes = response.data || []
    practiceIndexes.clear()
    indexes.forEach(index => {
      practiceIndexes.set(index.practiceId, index)
    })
    
    const practiceIds = indexes.map(index => index.practiceId)
    if (practiceIds.length > 0) {
      loadPractices(practiceIds)
    } else {
      practices.value = []
    }
  } catch (error) {
    ElMessage.error('加载练习索引失败')
  }
}

const loadPractices = async (practiceIds) => {
  try {
    // 这里需要根据实际API调整，可能需要循环调用或批量获取
    const practicePromises = practiceIds.map(id => getPractices(id))
    const responses = await Promise.all(practicePromises)
    
    const practiceData = []
    responses.forEach((response, index) => {
      const practice = response.data?.[0]
      if (practice) {
        practiceData.push(practice)
      }
    })
    
    practices.value = practiceData
  } catch (error) {
    ElMessage.error('加载练习失败')
  }
}

const getCourseNameById = (courseId) => {
  if (!courseId || courseId === 0) return '-'
  const course = courses.value.find(c => c.id === courseId)
  return course ? course.name : '-'
}

const getPracticeCourseId = (practiceId) => {
  const index = practiceIndexes.get(practiceId)
  return index ? index.courseId : 0
}

const loadPracticeTypes = async () => {
  try {
    const response = await getPracticeTypes()
    practiceTypes.value = response.data || []
  } catch (error) {
    ElMessage.error('加载练习类型失败')
  }
}

const getPracticeTypeNameById = (typeId) => {
  const type = practiceTypes.value.find(t => t.id === typeId)
  return type ? type.name : '未知类型'
}

const handleOpenCreatePracticeDialog = () => {
  createPracticeDialogVisible.value = true
}

const handleOpenCreateCourseDialog = () => {
  createCourseDialogVisible.value = true
}

const handlePracticeCreated = () => {
  loadPracticeIndexes()
}

const handleCourseCreated = () => {
  loadCourses()
}

const handleSelectCourseSection = (row) => {
  store.chooseCourseSection(row.id)
  loadCourseSectionDetail()
  loadCourses()
  loadPracticeIndexes()
}

const handleManageMaterials = (courseId) => {
  store.chooseCourse(courseId)
  router.push('/teacher/me')
}

const handleBackToSelection = () => {
  store.chooseCourseSection(0)
  practices.value = []
  practiceIndexes.clear()
}

const handleUpdateCourseSection = async () => {
  try {
    await updateCourseSection({
      id: courseSectionForm.value.id,
      name: courseSectionForm.value.name,
      description: courseSectionForm.value.description,
      courseSectionTypeId: courseSectionForm.value.courseSectionTypeId
    })
    ElMessage.success('更新成功')
    loadCourseSections()
  } catch (error) {
    ElMessage.error('更新失败')
  }
}

const handleEditCourse = (row) => {
  dialogMode.value = 'edit'
  courseForm.value = {
    id: row.id,
    name: row.name,
    description: row.description,
    sectionId: row.sectionId
  }
  currentEditingId.value = row.id
  courseDialogVisible.value = true
}

const handleDeleteCourse = async (id) => {
  try {
    await ElMessageBox.confirm(
      '确定要删除该课时吗？该操作会调用课程系列知识图谱删除接口，且不可恢复。',
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    if (courseSectionId.value) {
      await deleteKnowledgeGraphByCourseSectionId(Number(courseSectionId.value))
    }

    await deleteCourse(id)
    
    ElMessage.success('课时删除成功')
    loadCourses()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除课时失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

const handleEditPractice = (practice) => {
  store.choosePractice(practice.id)
  router.push('/teacher/pe')
}

const handleDeletePractice = async (id) => {
  try {
    await deletePracticeById(id)
    ElMessage.success('删除成功')
    practices.value = practices.value.filter(p => p.id !== id)
    practiceIndexes.delete(id)
  } catch (error) {
    ElMessage.error('删除失败')
  }
}

const handleSaveDialog = async () => {
  try {
    if (dialogMode.value === 'edit') {
      await updateCourse({
        id: courseForm.value.id,
        name: courseForm.value.name,
        description: courseForm.value.description,
        sectionId: courseForm.value.sectionId
      })
      ElMessage.success('更新成功')
      loadCourses()
    } else if (dialogMode.value === 'practice') {
      await updatePractice({
        id: practiceForm.value.id,
        name: practiceForm.value.name,
        questionNum: practiceForm.value.questionNum,
        practiceTypeId: practiceForm.value.practiceTypeId,
        expiredAt: practiceForm.value.expiredAt
      })
      ElMessage.success('更新成功')
      const practice = practices.value.find(p => p.id === currentEditingId.value)
      if (practice) {
        practice.name = practiceForm.value.name
        practice.questionNum = practiceForm.value.questionNum
        practice.practiceTypeId = practiceForm.value.practiceTypeId
        practice.expiredAt = practiceForm.value.expiredAt
      }
    }
    courseDialogVisible.value = false
  } catch (error) {
    ElMessage.error('更新失败')
  }
}

onMounted(() => {
  loadCourseSections()
  loadCourseSectionTypes()
  loadPracticeTypes()
  if (courseSectionId.value) {
    loadCourseSectionDetail()
    loadCourses()
    loadPracticeIndexes()
  }
})
</script>

<style scoped lang="scss">
.course-section-editor {
  padding: 20px;
  background: var(--bg-light);
  min-height: 100vh;
}

.selection-card,
.editor-card {
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(37, 99, 235, 0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
  color: var(--text-primary);
}

.editor-content {
  display: flex;
  flex-direction: column;
  gap: 30px;
}

.editor-tabs :deep(.el-tabs__item) {
  font-size: 16px;
}

.section-form,
.section-courses,
.section-practices {
  background: var(--bg-white);
  border-radius: 8px;
  padding: 20px;
  border: 1px solid var(--border-light);
}

.section-title {
  margin: 0 0 20px 0;
  font-size: 18px;
  font-weight: 600;
  color: var(--text-primary);
  padding-bottom: 10px;
  border-bottom: 2px solid var(--primary-lighter);
}

.section-title-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.form {
  max-width: 500px;
}

.empty-tip {
  text-align: center;
  padding: 40px;
  color: var(--text-placeholder);
  font-size: 14px;
}

:deep(.el-table) {
  border-radius: 4px;
  overflow: hidden;
}

:deep(.el-table__header th) {
  font-weight: 600;
}

:deep(.el-dialog__header) {
  background: var(--bg-primary-lighter);
  padding: 20px;
}

:deep(.el-dialog__title) {
  color: var(--text-primary);
  font-weight: 600;
}

:deep(.el-form-item__label) {
  color: var(--text-regular);
}

:deep(.el-input__wrapper) {
  &:hover {
    box-shadow: 0 0 0 1px var(--primary-lighter) inset;
  }

  &.is-focus {
    box-shadow: 0 0 0 1px var(--primary) inset;
  }
}

:deep(.el-textarea__inner) {
  &:hover {
    border-color: var(--primary-lighter);
  }

  &:focus {
    border-color: var(--primary);
  }
}
</style>
