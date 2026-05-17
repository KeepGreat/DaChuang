<template>
  <div class="course-section-editor">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1>课程管理</h1>
      <p>管理和编辑课程信息</p>
    </div>

    <!-- 统计数据展示部分 -->
    <section class="statistics-section">
      <h2>数据概览</h2>
      <div class="stats-grid">
        <div class="stat-card">
          <div class="stat-icon total-icon"></div>
          <div class="stat-content">
            <div class="stat-value">{{ statistics.total }}</div>
            <div class="stat-label">课程总数</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon type-icon"></div>
          <div class="stat-content">
            <div class="stat-value">{{ statistics.typeCount }}</div>
            <div class="stat-label">课程类型数量</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon recent-icon"></div>
          <div class="stat-content">
            <div class="stat-value">{{ statistics.recentCreated }}</div>
            <div class="stat-label">本周新增课程</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon my-icon"></div>
          <div class="stat-content">
            <div class="stat-value">{{ statistics.mySections }}</div>
            <div class="stat-label">我创建的课程</div>
          </div>
        </div>
      </div>

      <!-- 按类型统计 -->
      <div class="type-stats">
        <h3>按类型统计</h3>
        <div class="type-stats-grid">
          <div v-for="typeStat in statistics.byType" :key="typeStat.id" class="type-stat-item">
            <div class="type-stat-name">{{ typeStat.name }}</div>
            <div class="type-stat-count">{{ typeStat.count }}</div>
          </div>
        </div>
      </div>
    </section>

    <!-- 表格分页展示部分 -->
    <section class="table-section">
      <h2>课程列表</h2>
      <div class="table-header">
        <button class="create-btn" @click="openCreateDialog">创建课程</button>
      </div>
      <div class="table-container">
        <div v-if="loading" class="loading-overlay">
          <div class="loading-spinner"></div>
          <p>加载中...</p>
        </div>
        <el-table
          v-else
          :data="courseSections"
          row-key="id"
          :expand-row-keys="expandedRows"
          @expand-change="handleExpandChange"
          border
          style="width: 100%"
        >
          <el-table-column type="expand">
            <template #default="{ row }">
              <div class="sub-table-container">
                <div v-if="courseLoading[row.id]" class="sub-loading">
                  <div class="loading-spinner"></div>
                  <p>加载课程中...</p>
                </div>
                <el-table
                  v-else
                  :data="row.courses || []"
                  border
                  style="width: 100%"
                >
                  <el-table-column prop="name" label="课程名称" />
                  <el-table-column prop="description" label="课程描述" show-overflow-tooltip />
                  <el-table-column label="操作">
                    <template #default="{ row: course }">
                      <el-button size="small" @click="handleCourseEdit(course)">
                        编辑
                      </el-button>
                    </template>
                  </el-table-column>
                </el-table>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="name" label="课程名称" />
          <el-table-column prop="description" label="课程描述" show-overflow-tooltip />
          <el-table-column
            label="课程类型"
            :formatter="(row) => getTypeName(row.courseSectionTypeId)"
          />
          <el-table-column prop="teacherId" label="教师ID" width="120" />
          <el-table-column label="操作" width="160">
            <template #default="{ row }">
              <el-button size="small" @click="handleSectionEdit(row)">
                编辑
              </el-button>
              <el-button size="small" type="danger" @click="openDeleteDialog(row)">
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 分页控件 -->
      <div class="pagination">
        <button 
          class="pagination-btn" 
          :disabled="currentPage === 1" 
          @click="changePage(currentPage - 1)">
          上一页
        </button>
        <span class="pagination-info">
          第 {{ currentPage }} 页 / 共 {{ totalPages }} 页
        </span>
        <button 
          class="pagination-btn" 
          :disabled="currentPage === totalPages" 
          @click="changePage(currentPage + 1)">
          下一页
        </button>
      </div>
    </section>

    <!-- 编辑对话框 -->
    <div v-if="showEditDialog" class="dialog-overlay" @click="closeEditDialog">
      <div class="dialog-content" @click.stop>
        <div class="dialog-header">
          <h3>编辑课程</h3>
          <button class="close-btn" @click="closeEditDialog">×</button>
        </div>
        <form class="dialog-form" @submit.prevent="handleEdit">
          <div class="form-group">
            <label for="edit-name">课程名称 *</label>
            <input 
              id="edit-name" 
              v-model="editForm.name" 
              type="text" 
              placeholder="请输入课程名称"
              required
            >
          </div>
          <div class="form-group">
            <label for="edit-description">课程描述</label>
            <textarea 
              id="edit-description" 
              v-model="editForm.description" 
              placeholder="请输入课程描述"
              rows="3"
            ></textarea>
          </div>
          <div class="form-group">
            <label>课程类型 *</label>
            <div class="checkbox-group">
              <label 
                v-for="type in courseSectionTypes" 
                :key="type.id" 
                class="checkbox-item"
              >
                <input 
                  v-model="editForm.courseSectionTypeId" 
                  :value="type.id" 
                  type="radio"
                  name="editSectionType"
                >
                <span>{{ type.name }}</span>
              </label>
            </div>
          </div>
          <div class="dialog-actions">
            <button type="button" class="cancel-btn" @click="closeEditDialog" :disabled="submitting">取消</button>
            <button type="submit" class="confirm-btn" :disabled="submitting">
              {{ submitting ? '保存中...' : '保存' }}
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- 创建对话框 -->
    <div v-if="showCreateDialog" class="dialog-overlay" @click="closeCreateDialog">
      <div class="dialog-content" @click.stop>
        <div class="dialog-header">
          <h3>新建课程</h3>
          <button class="close-btn" @click="closeCreateDialog">×</button>
        </div>
        <form class="dialog-form" @submit.prevent="handleCreate">
          <div class="form-group">
            <label for="create-name">课程名称 *</label>
            <input 
              id="create-name" 
              v-model="createForm.name" 
              type="text" 
              placeholder="请输入课程名称"
              required
            >
          </div>
          <div class="form-group">
            <label for="create-description">课程描述</label>
            <textarea 
              id="create-description" 
              v-model="createForm.description" 
              placeholder="请输入课程描述"
              rows="3"
            ></textarea>
          </div>
          <div class="form-group">
            <label>课程类型 *</label>
            <div class="checkbox-group">
              <label 
                v-for="type in courseSectionTypes" 
                :key="type.id" 
                class="checkbox-item"
              >
                <input 
                  v-model="createForm.courseSectionTypeId" 
                  :value="type.id" 
                  type="radio"
                  name="createSectionType"
                >
                <span>{{ type.name }}</span>
              </label>
            </div>
          </div>
          <div class="dialog-actions">
            <button type="button" class="cancel-btn" @click="closeCreateDialog" :disabled="submitting">取消</button>
            <button type="submit" class="confirm-btn" :disabled="submitting">
              {{ submitting ? '创建中...' : '创建课程' }}
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- 删除确认对话框 -->
    <div v-if="showDeleteDialog" class="dialog-overlay" @click="closeDeleteDialog">
      <div class="dialog-content dialog-small" @click.stop>
        <div class="dialog-header">
          <h3>确认删除</h3>
          <button class="close-btn" @click="closeDeleteDialog" :disabled="deleting">×</button>
        </div>
        <div class="dialog-body">
          <p>确定要删除课程"{{ deleteTarget?.name }}"吗？</p>
          <p class="warning-text">此操作不可恢复！</p>
          <div v-if="deleting" class="delete-progress">
            <p class="progress-title">{{ deleteProgressText || '正在准备删除任务...' }}</p>
            <div class="progress-log">
              <p v-for="(log, idx) in deleteLogs" :key="`${idx}-${log}`">{{ log }}</p>
            </div>
          </div>
        </div>
        <div class="dialog-actions">
          <button class="cancel-btn" @click="closeDeleteDialog" :disabled="deleting">取消</button>
          <button class="delete-confirm-btn" @click="handleDelete" :disabled="deleting">
            {{ deleting ? '删除中...' : '确认删除' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { 
  getCourseSectionsPage, 
  addCourseSection, 
  updateCourseSection, 
  deleteCourseSection,
  getCourseSectionTypes 
} from '@/api/modules/teaching/CourseSectionAPI';
import { deleteCourse, getCourses } from '@/api/modules/teaching/CourseAPI';
import { deleteFile, getFileContents } from '@/api/modules/teaching/FileContentAPI';
import {
  deleteKnowledgeGraphEdge,
  deleteKnowledgeGraphNode,
  deleteKnowledgeGraphNodeIndex,
  getKnowledgeGraphEdges,
  getKnowledgeGraphNodeIndexes,
  getKnowledgeGraphNodes
} from '@/api/modules/teaching/KnowledgeGraphAPI';
import { deleteMaterial, getMaterials } from '@/api/modules/teaching/MaterialAPI';
import { deleteVideoContent, getVideoContents } from '@/api/modules/teaching/VideoContentAPI';
import { deletePracticeById } from '@/api/modules/practice/practice';
import { getPracticeIndexes } from '@/api/modules/practice/practiceIndex';
import { deleteQuestionIndexByIds, getQuestionIndexes } from '@/api/modules/practice/questionIndex';
import { useUserStore } from '@/store';
import { teacherStore } from '@/store/modules/teacher/teacherStore';

// 统计数据
const statistics = reactive({
  total: 0,
  typeCount: 0,
  recentCreated: 0,
  mySections: 0,
  byType: []
});

const userStore = useUserStore();
const router = useRouter();
const store = teacherStore();

// 课程列表
const courseSections = ref([]);
const currentPage = ref(1);
const pageSize = ref(10);
const totalPages = ref(1);

// 加载状态
const loading = ref(false);
const submitting = ref(false);

// 课程类型列表
const courseSectionTypes = ref([]);

// 新建表单
const createForm = reactive({
  name: '',
  description: '',
  courseSectionTypeId: null
});

// 编辑表单
const editForm = reactive({
  id: null,
  name: '',
  description: '',
  courseSectionTypeId: null
});

// 对话框状态
const showEditDialog = ref(false);
const showCreateDialog = ref(false);
const showDeleteDialog = ref(false);
const deleteTarget = ref(null);
const deleting = ref(false);
const deleteProgressText = ref('');
const deleteLogs = ref([]);

// 展开行状态
const expandedRows = ref([]);
const courseLoading = ref({});

// 获取统计数据
const fetchStatistics = async () => {
  try {
    loading.value = true;
    
    // 获取所有课程用于统计
    const sectionsResponse = await getCourseSectionsPage(1, 1000);
    const sections = sectionsResponse.data.records || [];
    
    statistics.total = sections.length;
    statistics.typeCount = courseSectionTypes.value.length;
    
    // 计算本周新增（这里假设后端返回的数据包含创建时间字段）
    const now = new Date();
    const weekAgo = new Date(now.getTime() - 7 * 24 * 60 * 60 * 1000);
    statistics.recentCreated = sections.filter(section => {
      // 如果后端返回createTime字段，使用它来判断
      if (section.createTime) {
        const createTime = new Date(section.createTime);
        return createTime >= weekAgo;
      }
      // 如果没有创建时间，暂时返回0
      return false;
    }).length;
    
    // 计算我创建的课程（这里需要根据实际的用户ID来判断）
    const currentTeacherId = localStorage.getItem('teacherId') || 'current_teacher_id';
    statistics.mySections = sections.filter(section => 
      section.teacherId === currentTeacherId
    ).length;
    
    // 按类型统计
    const typeMap = {};
    sections.forEach(section => {
      const typeId = section.courseSectionTypeId;
      if (!typeMap[typeId]) {
        typeMap[typeId] = 0;
      }
      typeMap[typeId]++;
    });
    
    statistics.byType = courseSectionTypes.value.map(type => ({
      id: type.id,
      name: type.name,
      count: typeMap[type.id] || 0
    }));
  } catch (error) {
    console.error('获取统计数据失败:', error);
  } finally {
    loading.value = false;
  }
};

// 获取课程列表
const fetchCourseSections = async () => {
  try {
    loading.value = true;
    
    const response = await getCourseSectionsPage(currentPage.value, pageSize.value);
    courseSections.value = response.data.records || [];    
    // 根据后端返回的数据结构计算总页数
    // 如果后端返回total字段，使用它；否则根据数据长度计算
    if (response.total !== undefined) {
      totalPages.value = Math.ceil(response.total / pageSize.value);
    } else {
      totalPages.value = Math.ceil(courseSections.value.length / pageSize.value);
    }
  } catch (error) {
    console.error('获取课程列表失败:', error);
    courseSections.value = [];
    totalPages.value = 1;
  } finally {
    loading.value = false;
  }
};

// 获取课程类型列表
const fetchCourseSectionTypes = async () => {
  try {
    const response = await getCourseSectionTypes();
    courseSectionTypes.value = response.data || [];
  } catch (error) {
    console.error('获取课程类型列表失败:', error);
  }
};

// 获取类型名称
const getTypeName = (typeId) => {
  if (typeId === undefined || typeId === null) {
    return '-';
  }
  const type = courseSectionTypes.value.find(t => t.id === typeId);
  return type ? type.name : '-';
};

// 分页切换
const changePage = (page) => {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page;
    fetchCourseSections();
  }
};

// 打开创建对话框
const openCreateDialog = () => {
  showCreateDialog.value = true;
};

// 关闭创建对话框
const closeCreateDialog = () => {
  showCreateDialog.value = false;
  createForm.name = '';
  createForm.description = '';
  createForm.courseSectionTypeId = null;
};

// 打开编辑对话框
const openEditDialog = (section) => {
  editForm.id = section.id;
  editForm.name = section.name;
  editForm.description = section.description || '';
  editForm.courseSectionTypeId = section.courseSectionTypeId;
  showEditDialog.value = true;
};

// 关闭编辑对话框
const closeEditDialog = () => {
  showEditDialog.value = false;
  editForm.id = null;
  editForm.name = '';
  editForm.description = '';
  editForm.courseSectionTypeId = null;
};

// 打开删除对话框
const openDeleteDialog = (section) => {
  deleteTarget.value = section;
  deleteProgressText.value = '';
  deleteLogs.value = [];
  showDeleteDialog.value = true;
};

// 关闭删除对话框
const closeDeleteDialog = (force = false) => {
  if (!force && deleting.value) {
    return;
  }
  showDeleteDialog.value = false;
  deleteTarget.value = null;
  deleteProgressText.value = '';
  deleteLogs.value = [];
};

const normalizeList = (res) => {
  if (Array.isArray(res?.data)) {
    return res.data;
  }
  if (Array.isArray(res)) {
    return res;
  }
  return [];
};

const ensureSuccess = (res, fallbackMessage) => {
  if (res?.code && res.code !== 200) {
    throw new Error(res.message || fallbackMessage);
  }
};

const updateDeleteProgress = (message) => {
  deleteProgressText.value = message;
  deleteLogs.value.push(message);
};

const deleteMaterialByType = async (material) => {
  const matId = Number(material?.id);
  if (!Number.isFinite(matId) || matId <= 0) {
    return;
  }

  if (material.type?.startsWith('application')) {
    const fileRes = await getFileContents(null, null, null, null, matId);
    ensureSuccess(fileRes, `获取资料 ${matId} 的文件内容失败`);
    const fileIds = normalizeList(fileRes)
      .map((item) => Number(item?.id))
      .filter((id) => Number.isFinite(id) && id > 0);

    if (fileIds.length > 0) {
      await Promise.all(fileIds.map((id) => deleteFile(id)));
    }
  } else if (material.type?.startsWith('video')) {
    const videoRes = await getVideoContents(null, matId, null);
    ensureSuccess(videoRes, `获取资料 ${matId} 的视频内容失败`);
    const videoIds = normalizeList(videoRes)
      .map((item) => Number(item?.id))
      .filter((id) => Number.isFinite(id) && id > 0);

    if (videoIds.length > 0) {
      await Promise.all(videoIds.map((id) => deleteVideoContent(id)));
    }
  }

  const materialDeleteRes = await deleteMaterial(matId);
  ensureSuccess(materialDeleteRes, `删除资料 ${matId} 失败`);
};

// 处理创建
const handleCreate = async () => {
  try {
    // 表单验证
    if (!createForm.name || !createForm.name.trim()) {
      alert('请输入课程名称！');
      return;
    }
    
    if (!createForm.courseSectionTypeId) {
      alert('请选择课程类型！');
      return;
    }
    
    submitting.value = true;
    
    const newSection = {
      name: createForm.name.trim(),
      description: createForm.description ? createForm.description.trim() : '',
      courseSectionTypeId: createForm.courseSectionTypeId,
      teacherId: userStore.userId
    };
    console.log('新建课程:', newSection)
    
    const response = await addCourseSection(newSection);
    
    // 检查响应状态
    if (response && response.code === 200) {
      // 重置表单
      createForm.name = '';
      createForm.description = '';
      createForm.courseSectionTypeId = null;
      
      // 刷新数据
      await fetchCourseSections();
      await fetchStatistics();
      
      alert('创建成功！');
    } else {
      alert(response?.message || '创建失败，请重试！');
    }
  } catch (error) {
    console.error('创建课程失败:', error);
    const errorMessage = error.response?.data?.message || error.message || '创建失败，请重试！';
    alert(errorMessage);
  } finally {
    submitting.value = false;
  }
};

// 处理编辑
const handleEdit = async () => {
  try {
    // 表单验证
    if (!editForm.name || !editForm.name.trim()) {
      alert('请输入课程名称！');
      return;
    }
    
    if (!editForm.courseSectionTypeId) {
      alert('请选择课程类型！');
      return;
    }
    
    submitting.value = true;
    
    const updatedSection = {
      id: editForm.id,
      name: editForm.name.trim(),
      description: editForm.description ? editForm.description.trim() : '',
      courseSectionTypeId: editForm.courseSectionTypeId,
      teacherId: localStorage.getItem('teacherId') || 'current_teacher_id'
    };
    
    const response = await updateCourseSection(updatedSection);
    
    // 检查响应状态
    if (response && response.code === 200) {
      closeEditDialog();
      await fetchCourseSections();
      await fetchStatistics();
      
      alert('更新成功！');
    } else {
      alert(response?.message || '更新失败，请重试！');
    }
  } catch (error) {
    console.error('更新课程失败:', error);
    const errorMessage = error.response?.data?.message || error.message || '更新失败，请重试！';
    alert(errorMessage);
  } finally {
    submitting.value = false;
  }
};

// 处理删除
const handleDelete = async () => {
  try {
    if (!deleteTarget.value) {
      return;
    }

    const courseSectionId = Number(deleteTarget.value.id);
    if (!Number.isFinite(courseSectionId) || courseSectionId <= 0) {
      throw new Error('课程系列ID无效，无法删除');
    }

    deleting.value = true;
    deleteLogs.value = [];
    updateDeleteProgress(`开始删除课程系列 ${deleteTarget.value.name || courseSectionId}`);

    updateDeleteProgress('步骤1/5：获取课程与练习数据...');
    const [courseRes, practiceIndexRes] = await Promise.all([
      getCourses(null, null, courseSectionId),
      getPracticeIndexes({ courseSectionId })
    ]);
    ensureSuccess(courseRes, '获取课程列表失败');
    ensureSuccess(practiceIndexRes, '获取练习列表失败');

    const coursesUnderSection = normalizeList(courseRes);
    const practiceIds = [...new Set(
      normalizeList(practiceIndexRes)
        .map((item) => Number(item?.practiceId))
        .filter((id) => Number.isFinite(id) && id > 0)
    )];

    updateDeleteProgress(`检测到 ${coursesUnderSection.length} 个课时，${practiceIds.length} 个练习`);

    updateDeleteProgress('步骤2/5：删除课时资料并删除课时...');
    for (const course of coursesUnderSection) {
      const courseId = Number(course?.id);
      if (!Number.isFinite(courseId) || courseId <= 0) {
        continue;
      }

      updateDeleteProgress(`正在处理课时 ${course.name || courseId} 的资料`);
      const materialRes = await getMaterials(null, null, null, null, null, null, courseId);
      ensureSuccess(materialRes, `获取课时 ${courseId} 的资料失败`);
      const materials = normalizeList(materialRes);

      for (const material of materials) {
        await deleteMaterialByType(material);
      }

      const deleteCourseRes = await deleteCourse(courseId);
      ensureSuccess(deleteCourseRes, `删除课时 ${courseId} 失败`);
      updateDeleteProgress(`课时 ${course.name || courseId} 已删除`);
    }

    updateDeleteProgress('步骤3/5：删除练习题目索引并删除练习...');
    for (const practiceId of practiceIds) {
      const questionIndexRes = await getQuestionIndexes({ practiceId });
      ensureSuccess(questionIndexRes, `获取练习 ${practiceId} 的题目索引失败`);
      const questionIndexIds = normalizeList(questionIndexRes)
        .map((item) => Number(item?.id))
        .filter((id) => Number.isFinite(id) && id > 0);

      if (questionIndexIds.length > 0) {
        const deleteIndexRes = await deleteQuestionIndexByIds(questionIndexIds);
        ensureSuccess(deleteIndexRes, `删除练习 ${practiceId} 的题目索引失败`);
      }

      const deletePracticeRes = await deletePracticeById(practiceId);
      ensureSuccess(deletePracticeRes, `删除练习 ${practiceId} 失败`);
      updateDeleteProgress(`练习 ${practiceId} 已删除`);
    }

    updateDeleteProgress('步骤4/5：删除知识图谱节点、边与节点索引...');
    const nodeRes = await getKnowledgeGraphNodes(undefined, undefined, undefined, courseSectionId);
    ensureSuccess(nodeRes, '获取知识图谱节点失败');
    const nodes = normalizeList(nodeRes);

    for (const node of nodes) {
      const nodeId = Number(node?.id);
      if (!Number.isFinite(nodeId) || nodeId <= 0) {
        continue;
      }

      const [outEdgeRes, inEdgeRes, nodeIndexRes] = await Promise.all([
        getKnowledgeGraphEdges(undefined, nodeId, undefined),
        getKnowledgeGraphEdges(undefined, undefined, nodeId),
        getKnowledgeGraphNodeIndexes(undefined, nodeId, undefined)
      ]);
      ensureSuccess(outEdgeRes, `获取节点 ${nodeId} 的出边失败`);
      ensureSuccess(inEdgeRes, `获取节点 ${nodeId} 的入边失败`);
      ensureSuccess(nodeIndexRes, `获取节点 ${nodeId} 的索引失败`);

      const edgeIds = [...new Set([
        ...normalizeList(outEdgeRes),
        ...normalizeList(inEdgeRes)
      ].map((item) => Number(item?.id)).filter((id) => Number.isFinite(id) && id > 0))];

      for (const edgeId of edgeIds) {
        const deleteEdgeRes = await deleteKnowledgeGraphEdge(edgeId);
        ensureSuccess(deleteEdgeRes, `删除知识图谱边 ${edgeId} 失败`);
      }

      const nodeIndexIds = normalizeList(nodeIndexRes)
        .map((item) => Number(item?.id))
        .filter((id) => Number.isFinite(id) && id > 0);

      for (const nodeIndexId of nodeIndexIds) {
        const deleteNodeIndexRes = await deleteKnowledgeGraphNodeIndex(nodeIndexId);
        ensureSuccess(deleteNodeIndexRes, `删除节点索引 ${nodeIndexId} 失败`);
      }

      const deleteNodeRes = await deleteKnowledgeGraphNode(nodeId);
      ensureSuccess(deleteNodeRes, `删除知识图谱节点 ${nodeId} 失败`);
      updateDeleteProgress(`知识图谱节点 ${nodeId} 已删除`);
    }

    updateDeleteProgress('步骤5/5：删除课程系列...');
    const response = await deleteCourseSection(courseSectionId);
    ensureSuccess(response, '删除课程系列失败');

    updateDeleteProgress('删除完成，正在刷新页面数据...');
    await fetchCourseSections();
    await fetchStatistics();

    ElMessage.success('课程系列及关联数据删除成功');
    closeDeleteDialog(true);
  } catch (error) {
    console.error('删除课程失败:', error);
    const errorMessage = error.response?.data?.message || error.message || '删除失败，请重试！';
    ElMessage.error(errorMessage);
  } finally {
    deleting.value = false;
  }
};

// 处理行展开
const handleExpandChange = async (row, expanded) => {
  if (expanded) {
    // 加载课程数据
    courseLoading.value[row.id] = true;
    try {
      const response = await getCourses(null, null, row.id);
      row.courses = response.data || [];
    } catch (error) {
      console.error('获取课程列表失败:', error);
      row.courses = [];
    } finally {
      courseLoading.value[row.id] = false;
    }
  } else {
    // 关闭展开时可以选择是否保留数据
    // 这里选择保留数据，避免重复加载
  }
};

// 处理课程编辑
const handleCourseEdit = (course) => {
  // 课程编辑功能可以后续实现
  alert('课程编辑功能将在后续版本中实现');
};

// 处理课程编辑
const handleSectionEdit = (section) => {
  store.chooseCourseSection(section.id);
  router.push('/teacher/cse');
};

// 组件挂载时加载数据
onMounted(() => {
  fetchCourseSectionTypes().then(() => {
    fetchCourseSections();
    fetchStatistics();
  });
});
</script>

<style scoped>
.course-section-editor {
  padding: 20px;
  background-color: var(--bg-light);
  min-height: 100vh;
}

.page-header {
  margin-bottom: 30px;
  h1 {
    color: var(--text-primary);
    font-size: 24px;
    margin-bottom: 8px;
  }
  p {
    color: var(--text-regular);
    font-size: 14px;
  }
}

/* 统计数据部分 */
.statistics-section {
  margin-bottom: 30px;
  h2 {
    color: var(--text-primary);
    font-size: 18px;
    margin-bottom: 20px;
  }
  
  .stats-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: 20px;
    margin-bottom: 30px;
  }
  
  .stat-card {
    background-color: var(--bg-white);
    border-radius: 8px;
    padding: 20px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
    display: flex;
    align-items: center;
    
    .stat-icon {
      width: 48px;
      height: 48px;
      border-radius: 50%;
      background-color: var(--primary-alpha-10);
      margin-right: 16px;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 20px;
      color: var(--primary);
    }
    
    .total-icon::before {
      content: "📚";
    }
    
    .type-icon::before {
      content: "🏷️";
    }
    
    .recent-icon::before {
      content: "📅";
    }
    
    .my-icon::before {
      content: "👤";
    }
    
    .stat-content {
      .stat-value {
        font-size: 24px;
        font-weight: bold;
        color: var(--text-primary);
        margin-bottom: 4px;
      }
      
      .stat-label {
        font-size: 14px;
        color: var(--text-regular);
      }
    }
  }
  
  .type-stats {
    background-color: var(--bg-white);
    border-radius: 8px;
    padding: 20px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
    
    h3 {
      color: var(--text-primary);
      font-size: 16px;
      margin-bottom: 16px;
    }
    
    .type-stats-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
      gap: 16px;
    }
    
    .type-stat-item {
      background-color: var(--bg-primary-light);
      border-radius: 6px;
      padding: 12px;
      text-align: center;
      
      .type-stat-name {
        font-size: 14px;
        color: var(--text-regular);
        margin-bottom: 8px;
      }
      
      .type-stat-count {
        font-size: 20px;
        font-weight: bold;
        color: var(--primary);
      }
    }
  }
}

/* 表格部分 */
.table-section {
  margin-bottom: 30px;
  h2 {
    color: var(--text-primary);
    font-size: 18px;
    margin-bottom: 20px;
  }
  
  .table-header {
    margin-bottom: 16px;
    
    .create-btn {
      padding: 8px 16px;
      background-color: var(--primary);
      color: white;
      border: none;
      border-radius: 4px;
      font-size: 14px;
      cursor: pointer;
      transition: background-color 0.2s ease;
      
      &:hover {
        background-color: var(--primary-hover);
      }
    }
  }
  
  .table-container {
    background-color: var(--bg-white);
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
    overflow-x: auto;
    margin-bottom: 20px;
    position: relative;
    min-height: 200px;
    
    .loading-overlay {
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background-color: rgba(255, 255, 255, 0.9);
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      z-index: 10;
      
      .loading-spinner {
        width: 40px;
        height: 40px;
        border: 4px solid var(--bg-primary-light);
        border-top: 4px solid var(--primary);
        border-radius: 50%;
        animation: spin 1s linear infinite;
        margin-bottom: 12px;
      }
      
      p {
        color: var(--text-regular);
        font-size: 14px;
      }
    }
    
    @keyframes spin {
      0% { transform: rotate(0deg); }
      100% { transform: rotate(360deg); }
    }
  }
  
  .sub-table-container {
    padding: 16px;
    
    .sub-loading {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      padding: 40px;
      
      .loading-spinner {
        width: 30px;
        height: 30px;
        border: 3px solid var(--bg-primary-light);
        border-top: 3px solid var(--primary);
        border-radius: 50%;
        animation: spin 1s linear infinite;
        margin-bottom: 12px;
      }
      
      p {
        color: var(--text-regular);
        font-size: 14px;
      }
    }
  }
  
  .data-table {
    width: 100%;
    border-collapse: collapse;
    
    thead {
      background-color: var(--bg-primary-light);
      
      th {
        padding: 12px 16px;
        text-align: left;
        font-size: 14px;
        font-weight: 600;
        color: var(--text-primary);
        border-bottom: 2px solid var(--border-light);
      }
    }
    
    tbody {
      tr {
        border-bottom: 1px solid var(--border-light);
        
        &:hover {
          background-color: var(--bg-primary-alpha);
        }
        
        td {
          padding: 12px 16px;
          font-size: 14px;
          color: var(--text-regular);
        }
        
        .action-cell {
          .action-btn {
            padding: 6px 12px;
            border: none;
            border-radius: 4px;
            font-size: 12px;
            cursor: pointer;
            margin-right: 8px;
            transition: all 0.2s ease;
            
            &.edit-btn {
              background-color: var(--primary);
              color: white;
              
              &:hover {
                background-color: var(--primary-hover);
              }
            }
            
            &.delete-btn {
              background-color: var(--danger);
              color: white;
              
              &:hover {
                background-color: #dc2626;
              }
            }
          }
        }
        
        .empty-row {
          text-align: center;
          color: var(--text-placeholder);
          padding: 40px;
        }
      }
    }
  }
  
  .pagination {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 16px;
    
    .pagination-btn {
      padding: 8px 16px;
      border: 1px solid var(--border-light);
      background-color: var(--bg-white);
      color: var(--text-primary);
      border-radius: 4px;
      cursor: pointer;
      transition: all 0.2s ease;
      
      &:hover:not(:disabled) {
        background-color: var(--primary);
        color: white;
        border-color: var(--primary);
      }
      
      &:disabled {
        opacity: 0.5;
        cursor: not-allowed;
      }
    }
    
    .pagination-info {
      font-size: 14px;
      color: var(--text-regular);
    }
  }
}

/* 新建部分 */
.create-section {
  margin-bottom: 30px;
  h2 {
    color: var(--text-primary);
    font-size: 18px;
    margin-bottom: 20px;
  }
  
  .create-form {
    background-color: var(--bg-white);
    border-radius: 8px;
    padding: 24px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
    
    .form-group {
      margin-bottom: 20px;
      
      label {
        display: block;
        font-size: 14px;
        font-weight: 500;
        color: var(--text-primary);
        margin-bottom: 8px;
      }
      
      input[type="text"],
      textarea {
        width: 100%;
        padding: 10px 12px;
        border: 1px solid var(--border-base);
        border-radius: 4px;
        font-size: 14px;
        color: var(--text-primary);
        background-color: var(--bg-white);
        transition: border-color 0.2s ease;
        
        &:focus {
          outline: none;
          border-color: var(--primary);
        }
        
        &::placeholder {
          color: var(--text-placeholder);
        }
      }
      
      textarea {
        resize: vertical;
      }
      
      .checkbox-group {
        display: flex;
        flex-wrap: wrap;
        gap: 16px;
        
        .checkbox-item {
          display: flex;
          align-items: center;
          padding: 8px 16px;
          background-color: var(--bg-primary-light);
          border-radius: 4px;
          cursor: pointer;
          transition: all 0.2s ease;
          
          input[type="radio"] {
            margin-right: 8px;
            cursor: pointer;
          }
          
          &:hover {
            background-color: var(--primary-alpha-10);
          }
          
          &:has(input:checked) {
            background-color: var(--primary);
            color: white;
          }
        }
      }
    }
    
    .submit-btn {
      padding: 12px 32px;
      background-color: var(--primary);
      color: white;
      border: none;
      border-radius: 6px;
      font-size: 14px;
      font-weight: 500;
      cursor: pointer;
      transition: background-color 0.2s ease;
      
      &:hover:not(:disabled) {
        background-color: var(--primary-hover);
      }
      
      &:disabled {
        opacity: 0.6;
        cursor: not-allowed;
      }
    }
  }
}

/* 对话框 */
.dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  
  .dialog-content {
    background-color: var(--bg-white);
    border-radius: 8px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    max-width: 500px;
    width: 90%;
    max-height: 90vh;
    overflow-y: auto;
    
    &.dialog-small {
      max-width: 400px;
    }
    
    .dialog-header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 20px 24px;
      border-bottom: 1px solid var(--border-light);
      
      h3 {
        font-size: 18px;
        font-weight: 600;
        color: var(--text-primary);
        margin: 0;
      }
      
      .close-btn {
        background: none;
        border: none;
        font-size: 24px;
        color: var(--text-placeholder);
        cursor: pointer;
        transition: color 0.2s ease;
        
        &:hover {
          color: var(--text-primary);
        }
      }
    }
    
    .dialog-body {
      padding: 24px;
      
      p {
        font-size: 14px;
        color: var(--text-regular);
        margin-bottom: 12px;
        
        &.warning-text {
          color: var(--danger);
          font-weight: 500;
        }
      }

      .delete-progress {
        margin-top: 12px;
        border: 1px solid var(--border-light);
        border-radius: 6px;
        padding: 10px;
        background: var(--bg-primary-light);

        .progress-title {
          margin: 0 0 8px 0;
          font-weight: 600;
          color: var(--primary);
        }

        .progress-log {
          max-height: 180px;
          overflow-y: auto;
          background: var(--bg-white);
          border-radius: 4px;
          padding: 8px;

          p {
            margin: 0;
            line-height: 1.6;
            color: var(--text-regular);
          }

          p + p {
            margin-top: 4px;
          }
        }
      }
    }
    
    .dialog-form {
      padding: 24px;
      
      .form-group {
        margin-bottom: 20px;
        
        label {
          display: block;
          font-size: 14px;
          font-weight: 500;
          color: var(--text-primary);
          margin-bottom: 8px;
        }
        
        input[type="text"],
        textarea {
          width: 100%;
          padding: 10px 12px;
          border: 1px solid var(--border-base);
          border-radius: 4px;
          font-size: 14px;
          color: var(--text-primary);
          background-color: var(--bg-white);
          transition: border-color 0.2s ease;
          
          &:focus {
            outline: none;
            border-color: var(--primary);
          }
          
          &::placeholder {
            color: var(--text-placeholder);
          }
        }
        
        textarea {
          resize: vertical;
        }
        
        .checkbox-group {
          display: flex;
          flex-wrap: wrap;
          gap: 16px;
          
          .checkbox-item {
            display: flex;
            align-items: center;
            padding: 8px 16px;
            background-color: var(--bg-primary-light);
            border-radius: 4px;
            cursor: pointer;
            transition: all 0.2s ease;
            
            input[type="radio"] {
              margin-right: 8px;
              cursor: pointer;
            }
            
            &:hover {
              background-color: var(--primary-alpha-10);
            }
            
            &:has(input:checked) {
              background-color: var(--primary);
              color: white;
            }
          }
        }
      }
    }
    
    .dialog-actions {
      display: flex;
      justify-content: flex-end;
      gap: 12px;
      padding: 20px 24px;
      border-top: 1px solid var(--border-light);
      
      .cancel-btn {
        padding: 10px 20px;
        background-color: var(--bg-white);
        color: var(--text-primary);
        border: 1px solid var(--border-base);
        border-radius: 4px;
        font-size: 14px;
        cursor: pointer;
        transition: all 0.2s ease;
        
        &:hover {
          background-color: var(--bg-primary-light);
        }
      }
      
      .confirm-btn {
        padding: 10px 20px;
        background-color: var(--primary);
        color: white;
        border: none;
        border-radius: 4px;
        font-size: 14px;
        cursor: pointer;
        transition: background-color 0.2s ease;
        
        &:hover:not(:disabled) {
          background-color: var(--primary-hover);
        }
        
        &:disabled {
          opacity: 0.6;
          cursor: not-allowed;
        }
      }
      
      .delete-confirm-btn {
        padding: 10px 20px;
        background-color: var(--danger);
        color: white;
        border: none;
        border-radius: 4px;
        font-size: 14px;
        cursor: pointer;
        transition: background-color 0.2s ease;
        
        &:hover {
          background-color: #dc2626;
        }
      }
    }
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .course-section-editor {
    padding: 16px;
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
  }
  
  .type-stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .data-table {
    font-size: 12px;
    
    thead th,
    tbody td {
      padding: 8px 12px;
    }
  }
  
  .dialog-content {
    max-width: 95%;
  }
}
</style>