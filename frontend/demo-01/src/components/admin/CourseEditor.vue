<template>
  <div class="course-editor">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1>课时管理</h1>
      <p>管理和编辑课时信息</p>
    </div>

    <!-- 统计数据展示部分 -->
    <section class="statistics-section">
      <h2>数据概览</h2>
      <div class="stats-grid">
        <div class="stat-card">
          <div class="stat-icon total-icon"></div>
          <div class="stat-content">
            <div class="stat-value">{{ statistics.total }}</div>
            <div class="stat-label">课时总数</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon recent-icon"></div>
          <div class="stat-content">
            <div class="stat-value">{{ statistics.recentCreated }}</div>
            <div class="stat-label">本周新增</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon section-icon"></div>
          <div class="stat-content">
            <div class="stat-value">{{ statistics.withDescription }}</div>
            <div class="stat-label">有描述的课时</div>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon avg-icon"></div>
          <div class="stat-content">
            <div class="stat-value">{{ statistics.avgLength }}</div>
            <div class="stat-label">平均描述长度</div>
          </div>
        </div>
      </div>
    </section>

    <!-- 表格分页展示部分 -->
    <section class="table-section">
      <h2>课时列表</h2>
      <div class="table-container">
        <div v-if="loading" class="loading-overlay">
          <div class="loading-spinner"></div>
          <p>加载中...</p>
        </div>
        <table v-else class="data-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>课时名称</th>
              <th>描述</th>
              <th>章节ID</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="course in courses" :key="course.id">
              <td>{{ course.id }}</td>
              <td>{{ course.name }}</td>
              <td>{{ course.description || '-' }}</td>
              <td>{{ course.sectionId }}</td>
              <td class="action-cell">
                <button class="action-btn edit-btn" @click="handleEdit(course)">编辑</button>
                <button class="action-btn delete-btn" @click="openDeleteDialog(course)">删除</button>
              </td>
            </tr>
            <tr v-if="courses.length === 0">
              <td colspan="5" class="empty-row">暂无数据</td>
            </tr>
          </tbody>
        </table>
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

    <!-- 新建Course部分 -->
    <section class="create-section">
      <h2>新建课时</h2>
      <form class="create-form" @submit.prevent="handleCreate">
        <div class="form-group">
          <label for="name">课时名称 *</label>
          <input 
            id="name" 
            v-model="createForm.name" 
            type="text" 
            placeholder="请输入课时名称"
            required
          >
        </div>
        <div class="form-group">
          <label for="description">课时描述</label>
          <textarea 
            id="description" 
            v-model="createForm.description" 
            placeholder="请输入课时描述"
            rows="4"
          ></textarea>
        </div>
        <button type="submit" class="submit-btn" :disabled="submitting">
          {{ submitting ? '创建中...' : '创建课时' }}
        </button>
      </form>
    </section>

    <!-- 删除确认对话框 -->
    <div v-if="showDeleteDialog" class="dialog-overlay" @click="closeDeleteDialog">
      <div class="dialog-content dialog-small" @click.stop>
        <div class="dialog-header">
          <h3>确认删除</h3>
          <button class="close-btn" @click="closeDeleteDialog">×</button>
        </div>
        <div class="dialog-body">
          <p>确定要删除课时"{{ deleteTarget?.name }}"吗？</p>
          <p class="warning-text">此操作不可恢复！</p>
        </div>
        <div class="dialog-actions">
          <button class="cancel-btn" @click="closeDeleteDialog">取消</button>
          <button class="delete-confirm-btn" @click="handleDelete">确认删除</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue';
import { 
  getCoursesPage, 
  addCourse, 
  deleteCourse,
  updateCourse 
} from '@/api/modules/teaching/CourseAPI';

// 接收父组件传递的sectionId
const props = defineProps({
  sectionId: {
    type: Number,
    required: true
  }
});

// 统计数据
const statistics = reactive({
  total: 0,
  recentCreated: 0,
  withDescription: 0,
  avgLength: 0
});

// 课时列表
const courses = ref([]);
const currentPage = ref(1);
const pageSize = ref(10);
const totalPages = ref(1);

// 加载状态
const loading = ref(false);
const submitting = ref(false);

// 新建表单
const createForm = reactive({
  name: '',
  description: ''
});

// 编辑表单
const editForm = reactive({
  id: null,
  name: '',
  description: '',
  sectionId: null
});

// 对话框状态
const showDeleteDialog = ref(false);
const deleteTarget = ref(null);

// 获取统计数据
const fetchStatistics = async () => {
  try {
    loading.value = true;
    
    // 获取所有课时用于统计
    const coursesResponse = await getCoursesPage(1, 1000, null, null, props.sectionId);
    const allCourses = coursesResponse.data || [];
    
    statistics.total = allCourses.length;
    
    // 计算本周新增
    const now = new Date();
    const weekAgo = new Date(now.getTime() - 7 * 24 * 60 * 60 * 1000);
    statistics.recentCreated = allCourses.filter(course => {
      if (course.createTime) {
        const createTime = new Date(course.createTime);
        return createTime >= weekAgo;
      }
      return false;
    }).length;
    
    // 计算有描述的课时
    statistics.withDescription = allCourses.filter(course => 
      course.description && course.description.trim()
    ).length;
    
    // 计算平均描述长度
    const coursesWithDesc = allCourses.filter(course => 
      course.description && course.description.trim()
    );
    if (coursesWithDesc.length > 0) {
      const totalLength = coursesWithDesc.reduce((sum, course) => 
        sum + course.description.length, 0
      );
      statistics.avgLength = Math.round(totalLength / coursesWithDesc.length);
    } else {
      statistics.avgLength = 0;
    }
  } catch (error) {
    console.error('获取统计数据失败:', error);
  } finally {
    loading.value = false;
  }
};

// 获取课时列表
const fetchCourses = async () => {
  try {
    loading.value = true;
    
    const response = await getCoursesPage(
      currentPage.value, 
      pageSize.value, 
      null, 
      null, 
      props.sectionId
    );
    courses.value = response.data || [];
    
    // 根据后端返回的数据结构计算总页数
    if (response.total !== undefined) {
      totalPages.value = Math.ceil(response.total / pageSize.value);
    } else {
      totalPages.value = Math.ceil(courses.value.length / pageSize.value);
    }
  } catch (error) {
    console.error('获取课时列表失败:', error);
    courses.value = [];
    totalPages.value = 1;
  } finally {
    loading.value = false;
  }
};

// 分页切换
const changePage = (page) => {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page;
    fetchCourses();
  }
};

// 打开删除对话框
const openDeleteDialog = (course) => {
  deleteTarget.value = course;
  showDeleteDialog.value = true;
};

// 关闭删除对话框
const closeDeleteDialog = () => {
  showDeleteDialog.value = false;
  deleteTarget.value = null;
};

// 处理创建
const handleCreate = async () => {
  try {
    // 表单验证
    if (!createForm.name || !createForm.name.trim()) {
      alert('请输入课时名称！');
      return;
    }
    
    submitting.value = true;
    
    const newCourse = {
      name: createForm.name.trim(),
      description: createForm.description ? createForm.description.trim() : '',
      sectionId: props.sectionId
    };
    
    const response = await addCourse(newCourse);
    
    // 检查响应状态
    if (response && response.code === 200) {
      // 重置表单
      createForm.name = '';
      createForm.description = '';
      
      // 刷新数据
      await fetchCourses();
      await fetchStatistics();
      
      alert('创建成功！');
    } else {
      alert(response?.message || '创建失败，请重试！');
    }
  } catch (error) {
    console.error('创建课时失败:', error);
    const errorMessage = error.response?.data?.message || error.message || '创建失败，请重试！';
    alert(errorMessage);
  } finally {
    submitting.value = false;
  }
};

// 处理编辑
const handleEdit = (course) => {
  // 编辑功能暂不实现，按需求要求
  alert('编辑功能将在后续版本中实现');
};

// 处理删除
const handleDelete = async () => {
  try {
    if (!deleteTarget.value) {
      return;
    }
    
    const response = await deleteCourse(deleteTarget.value.id);
    
    // 检查响应状态
    if (response && response.code === 200) {
      closeDeleteDialog();
      await fetchCourses();
      await fetchStatistics();
      
      alert('删除成功！');
    } else {
      alert(response?.message || '删除失败，请重试！');
    }
  } catch (error) {
    console.error('删除课时失败:', error);
    const errorMessage = error.response?.data?.message || error.message || '删除失败，请重试！';
    alert(errorMessage);
  }
};

// 监听sectionId变化，重新加载数据
watch(() => props.sectionId, () => {
  currentPage.value = 1;
  fetchCourses();
  fetchStatistics();
});

// 组件挂载时加载数据
onMounted(() => {
  fetchCourses();
  fetchStatistics();
});
</script>

<style scoped>
.course-editor {
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
    
    .recent-icon::before {
      content: "📅";
    }
    
    .section-icon::before {
      content: "📝";
    }
    
    .avg-icon::before {
      content: "📊";
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
}

/* 表格部分 */
.table-section {
  margin-bottom: 30px;
  h2 {
    color: var(--text-primary);
    font-size: 18px;
    margin-bottom: 20px;
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
    max-width: 400px;
    width: 90%;
    
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
  .course-editor {
    padding: 16px;
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
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