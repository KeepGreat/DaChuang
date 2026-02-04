<template>
  <div class="video-content-manager">
    <h3>视频内容管理</h3>
    
    <!-- 筛选和搜索部分 -->
    <div class="filter-section">
      <h4>筛选条件</h4>
      <div class="filter-form">
        <div class="form-item">
          <label>视频ID：</label>
          <input v-model.number="filterParams.id" type="number" placeholder="请输入视频ID" />
        </div>
        <div class="form-item">
          <label>资料ID：</label>
          <input v-model.number="filterParams.matId" type="number" placeholder="请输入资料ID" />
        </div>
        <div class="form-item">
          <label>视频大小：</label>
          <input v-model.number="filterParams.size" type="number" placeholder="请输入视频大小" />
        </div>
        <div class="form-actions">
          <button @click="getVideoContentsList" class="btn btn-primary">搜索</button>
          <button @click="resetFilter" class="btn btn-secondary">重置</button>
          <button @click="showAddForm = true" class="btn btn-success">新增视频</button>
        </div>
      </div>
    </div>
    
    <!-- 视频内容列表 -->
    <div class="list-section">
      <h4>视频内容列表</h4>
      <div v-if="loading" class="loading-overlay">
        <div class="loading-spinner"></div>
        <p>加载中...</p>
      </div>
      <table v-else class="content-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>类型</th>
            <th>名称</th>
            <th>大小</th>
            <th>时长</th>
            <th>资料ID</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="content in videoContents" :key="content.id">
            <td>{{ content.id }}</td>
            <td>{{ content.type }}</td>
            <td>{{ content.name }}</td>
            <td>{{ formatFileSize(content.size) }}</td>
            <td>{{ formatDuration(content.duration) }}</td>
            <td>{{ content.matId }}</td>
            <td class="action-buttons">
              <button @click="editContent(content)" class="btn btn-edit">编辑</button>
              <button @click="deleteContent(content.id)" class="btn btn-delete">删除</button>
            </td>
          </tr>
          <tr v-if="videoContents.length === 0">
            <td colspan="7" class="empty-message">暂无视频内容</td>
          </tr>
        </tbody>
      </table>
      
      <!-- 分页控件 -->
      <div class="pagination" v-if="total > 0">
        <button 
          @click="changePage(1)" 
          :disabled="currentPage === 1"
          class="btn btn-page"
        >
          首页
        </button>
        <button 
          @click="changePage(currentPage - 1)" 
          :disabled="currentPage === 1"
          class="btn btn-page"
        >
          上一页
        </button>
        <span class="page-info">
          第 {{ currentPage }} 页，共 {{ totalPages }} 页，总计 {{ total }} 条
        </span>
        <button 
          @click="changePage(currentPage + 1)" 
          :disabled="currentPage === totalPages"
          class="btn btn-page"
        >
          下一页
        </button>
        <button 
          @click="changePage(totalPages)" 
          :disabled="currentPage === totalPages"
          class="btn btn-page"
        >
          末页
        </button>
        <div class="page-size-selector">
          <label>每页显示：</label>
          <select v-model="pageSize" @change="changePageSize">
            <option value="10">10</option>
            <option value="20">20</option>
            <option value="50">50</option>
            <option value="100">100</option>
          </select>
        </div>
      </div>
    </div>
    
    <!-- 新增/编辑表单 -->
    <div v-if="showAddForm || showEditForm" class="form-overlay">
      <div class="form-container">
        <h4>{{ showAddForm ? '新增视频内容' : '编辑视频内容' }}</h4>
        <form @submit.prevent="submitForm">
          <div class="form-item">
            <label>类型：</label>
            <input v-model="formData.type" type="text" placeholder="请输入视频类型" required />
          </div>
          <div class="form-item">
            <label>名称：</label>
            <input v-model="formData.name" type="text" placeholder="请输入视频名称" required />
          </div>
          <div class="form-item">
            <label>大小：</label>
            <input v-model.number="formData.size" type="number" placeholder="请输入视频大小" required />
          </div>
          <div class="form-item">
            <label>时长：</label>
            <input v-model.number="formData.duration" type="number" placeholder="请输入视频时长(秒)" required />
          </div>
          <div class="form-item">
            <label>资料ID：</label>
            <input v-model.number="formData.matId" type="number" placeholder="请输入资料ID" required />
          </div>
          <div class="form-actions">
            <button type="submit" class="btn btn-primary">保存</button>
            <button type="button" @click="cancelForm" class="btn btn-secondary">取消</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import * as VideoContentAPI from '@/api/modules/teaching/VideoContentAPI';

// 响应式数据
const videoContents = ref([]);
const total = ref(0);
const currentPage = ref(1);
const pageSize = ref(10);
const loading = ref(false);
const showAddForm = ref(false);
const showEditForm = ref(false);
const formData = ref({
  id: null,
  type: '',
  name: '',
  size: 0,
  duration: 0,
  matId: 0
});
const filterParams = ref({
  id: null,
  matId: null,
  size: null
});

// 计算属性
const totalPages = computed(() => {
  return Math.ceil(total.value / pageSize.value);
});

// 格式化文件大小
const formatFileSize = (bytes) => {
  if (bytes === 0) return '0 Bytes';
  const k = 1024;
  const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
};

// 格式化视频时长
const formatDuration = (seconds) => {
  if (!seconds) return '00:00';
  const mins = Math.floor(seconds / 60);
  const secs = seconds % 60;
  return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
};

// 方法
// 获取视频内容列表（分页）
const getVideoContentsList = async () => {
  loading.value = true;
  try {
    const response = await VideoContentAPI.getVideoContentsPage(
      currentPage.value,
      pageSize.value,
      filterParams.value.id,
      filterParams.value.matId,
      filterParams.value.size
    );
    videoContents.value = response.data.records || [];
    total.value = response.data.total || 0;
    console.log('获取视频内容列表成功');
  } catch (error) {
    console.error('获取视频内容列表失败:', error);
    alert('获取视频内容列表失败，请重试');
  } finally {
    loading.value = false;
  }
};

// 重置筛选条件
const resetFilter = () => {
  filterParams.value = {
    id: null,
    matId: null,
    size: null
  };
  currentPage.value = 1;
  getVideoContentsList();
};

// 切换页码
const changePage = (page) => {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page;
    getVideoContentsList();
  }
};

// 切换每页显示条数
const changePageSize = () => {
  currentPage.value = 1;
  getVideoContentsList();
};

// 打开新增表单
const openAddForm = () => {
  formData.value = {
    id: null,
    type: '',
    name: '',
    size: 0,
    duration: 0,
    matId: 0
  };
  showAddForm.value = true;
  showEditForm.value = false;
};

// 打开编辑表单
const editContent = (content) => {
  formData.value = { ...content };
  showEditForm.value = true;
  showAddForm.value = false;
};

// 提交表单
const submitForm = async () => {
  try {
    if (showAddForm.value) {
      // 新增视频内容
      await VideoContentAPI.addVideoContent(formData.value);
      console.log('新增视频内容成功');
      alert('新增视频内容成功');
    } else if (showEditForm.value) {
      // 更新视频内容
      await VideoContentAPI.updateVideoContent(formData.value);
      console.log('更新视频内容成功');
      alert('更新视频内容成功');
    }
    cancelForm();
    getVideoContentsList();
  } catch (error) {
    console.error('保存视频内容失败:', error);
    alert('保存视频内容失败，请重试');
  }
};

// 取消表单
const cancelForm = () => {
  showAddForm.value = false;
  showEditForm.value = false;
  formData.value = {
    id: null,
    type: '',
    name: '',
    size: 0,
    duration: 0,
    matId: 0
  };
};

// 删除视频内容
const deleteContent = async (id) => {
  if (!confirm('确定要删除这条视频内容吗？')) {
    return;
  }
  
  try {
    await VideoContentAPI.deleteVideoContent(id);
    console.log('删除视频内容成功');
    alert('删除视频内容成功');
    getVideoContentsList();
  } catch (error) {
    console.error('删除视频内容失败:', error);
    alert('删除视频内容失败，请重试');
  }
};

// 组件挂载时获取数据
onMounted(() => {
  getVideoContentsList();
});
</script>

<style scoped>
.video-content-manager {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  border: 1px solid #ddd;
  border-radius: 8px;
  background-color: #f9f9f9;
}

h3, h4 {
  margin-top: 0;
  color: #333;
}

.filter-section {
  margin-bottom: 20px;
  padding: 15px;
  background-color: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.filter-form {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  align-items: flex-end;
}

.form-item {
  display: flex;
  flex-direction: column;
  min-width: 150px;
}

.form-item label {
  margin-bottom: 5px;
  font-weight: 500;
  color: #555;
  font-size: 14px;
}

.form-item input {
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.form-actions {
  display: flex;
  gap: 10px;
}

.btn {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.3s ease;
}

.btn-primary {
  background-color: #007bff;
  color: white;
}

.btn-primary:hover {
  background-color: #0069d9;
}

.btn-secondary {
  background-color: #6c757d;
  color: white;
}

.btn-secondary:hover {
  background-color: #5a6268;
}

.btn-success {
  background-color: #28a745;
  color: white;
}

.btn-success:hover {
  background-color: #218838;
}

.btn-edit {
  background-color: #17a2b8;
  color: white;
  margin-right: 5px;
}

.btn-edit:hover {
  background-color: #138496;
}

.btn-delete {
  background-color: #dc3545;
  color: white;
}

.btn-delete:hover {
  background-color: #c82333;
}

.btn-page {
  background-color: #f8f9fa;
  color: #333;
  border: 1px solid #dee2e6;
  margin: 0 5px;
}

.btn-page:hover:not(:disabled) {
  background-color: #e2e6ea;
}

.btn-page:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.list-section {
  margin-bottom: 20px;
  padding: 15px;
  background-color: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  position: relative;
}

.content-table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 10px;
}

.content-table th,
.content-table td {
  padding: 12px;
  text-align: left;
  border-bottom: 1px solid #ddd;
}

.content-table th {
  background-color: #f2f2f2;
  font-weight: 600;
  color: #333;
}

.content-table tr:hover {
  background-color: #f5f5f5;
}

.empty-message {
  text-align: center;
  padding: 40px;
  color: #666;
  font-style: italic;
}

.action-buttons {
  display: flex;
  gap: 5px;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 20px;
  gap: 10px;
  flex-wrap: wrap;
}

.page-info {
  margin: 0 10px;
  font-size: 14px;
  color: #666;
}

.page-size-selector {
  display: flex;
  align-items: center;
  gap: 5px;
}

.page-size-selector select {
  padding: 4px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.loading-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(255, 255, 255, 0.8);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  z-index: 1000;
  border-radius: 4px;
}

.loading-spinner {
  width: 50px;
  height: 50px;
  border: 5px solid rgba(0, 123, 255, 0.3);
  border-top: 5px solid #007bff;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 15px;
}

.loading-overlay p {
  color: #333;
  font-size: 16px;
  margin: 0;
}

.form-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 2000;
}

.form-container {
  background-color: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  width: 90%;
  max-width: 500px;
}

.form-container h4 {
  margin-bottom: 20px;
  color: #333;
}

.form-container .form-item {
  margin-bottom: 15px;
}

.form-container .form-actions {
  margin-top: 20px;
  justify-content: flex-end;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .filter-form {
    flex-direction: column;
    align-items: stretch;
  }
  
  .form-item {
    min-width: auto;
  }
  
  .pagination {
    flex-direction: column;
    gap: 10px;
  }
  
  .page-info {
    margin: 5px 0;
  }
  
  .content-table {
    font-size: 12px;
  }
  
  .content-table th,
  .content-table td {
    padding: 8px;
  }
  
  .action-buttons {
    flex-direction: column;
    gap: 3px;
  }
  
  .btn-edit,
  .btn-delete {
    margin-right: 0;
    font-size: 12px;
    padding: 4px 8px;
  }
}
</style>