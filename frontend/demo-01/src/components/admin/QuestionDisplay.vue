<template>
  <div class="question-display">
    <div v-if="!isValidRole" class="error-view">
      <el-result icon="error" title="权限错误" sub-title="您没有权限查看此页面">
        <template #extra>
          <el-button type="primary" @click="router.back()">返回</el-button>
        </template>
      </el-result>
    </div>

    <div v-else class="content-container">
      <!-- 筛选器部分 - 仅在非练习模式下显示，因为 practiceId 模式下的 API 不支持后端过滤 -->
      <el-card v-if="!props.practiceId" class="filter-card">
        <el-form :inline="true" :model="searchForm" class="search-form">
          <el-form-item label="题目名称">
            <el-input v-model="searchForm.name" placeholder="请输入题目名称" clearable @keyup.enter="handleSearch" />
          </el-form-item>
          <el-form-item label="题目类型">
            <el-select v-model="searchForm.type" placeholder="请选择" clearable style="width: 150px">
              <el-option v-for="(label, value) in questionTypeMap" :key="value" :label="label" :value="Number(value)" />
            </el-select>
          </el-form-item>
          <el-form-item label="难度">
            <el-select v-model="searchForm.difficulty" placeholder="请选择" clearable style="width: 150px">
              <el-option v-for="(label, value) in difficultyMap" :key="value" :label="label" :value="Number(value)" />
            </el-select>
          </el-form-item>
          <el-form-item label="分值">
            <el-input-number v-model="searchForm.score" :min="0" placeholder="分值" style="width: 120px" />
          </el-form-item>
          <el-form-item label="资源">
            <el-select v-model="searchForm.hasResource" placeholder="是否含资源" clearable style="width: 120px">
              <el-option label="是" :value="true" />
              <el-option label="否" :value="false" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">查询</el-button>
            <el-button @click="resetSearch">重置</el-button>
          </el-form-item>
        </el-form>
      </el-card>

      <!-- 表格展示部分 -->
      <el-card class="table-card">
        <template #header>
          <div class="card-header">
            <span class="title">{{ props.practiceId ? '练习关联题目' : '所有问题库' }}</span>
            <el-button 
              type="primary" 
              link 
              :icon="Refresh" 
              @click="fetchQuestionList"
              :loading="loading"
            >
              刷新列表
            </el-button>
          </div>
        </template>

        <el-table 
          :data="questionList" 
          v-loading="loading" 
          border 
          stripe 
          style="width: 100%"
          @sort-change="handleSortChange"
        >
          <el-table-column prop="id" label="ID" width="80" align="center" />
          <el-table-column prop="name" label="题目名称" min-width="200" show-overflow-tooltip />
          <el-table-column 
            prop="type" 
            label="题目类型" 
            width="120" 
            sortable="custom"
          >
            <template #default="{ row }">
              {{ questionTypeMap[row.type] || '未知' }}
            </template>
          </el-table-column>
          <el-table-column 
            prop="score" 
            label="分值" 
            width="100" 
            align="center" 
            sortable="custom"
          />
          <el-table-column 
            prop="difficulty" 
            label="难度" 
            width="120" 
            align="center" 
            sortable="custom"
          >
            <template #default="{ row }">
              <el-tag :type="difficultyTagMap[row.difficulty]">
                {{ difficultyMap[row.difficulty] || '未知' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="问题资源" width="120" align="center">
            <template #default="{ row }">
              <el-button 
                v-if="row.hasResource" 
                type="primary" 
                link 
                @click="showResources(row)"
              >
                查看资源
              </el-button>
              <span v-else class="text-placeholder">无资源</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200" fixed="right" align="center">
            <template #default="{ row }">
              <el-button type="primary" link @click="viewQuestionDetail(row)">查看</el-button>
              <el-button 
                type="warning" 
                link 
                @click="handleEditQuestion(row)"
                v-if="isAdmin || isTeacher"
              >
                修改
              </el-button>
              <el-button 
                type="danger" 
                link 
                @click="handleDeleteQuestion(row)"
                v-if="isAdmin || isTeacher"
              >
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 分页 -->
        <div class="pagination-container">
          <el-pagination
            v-model:current-page="pageParams.pageNo"
            v-model:page-size="pageParams.pageSize"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, sizes, prev, pager, next, jumper"
            :total="total"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </el-card>
    </div>

    <!-- 资源列表对话框 -->
    <el-dialog
      v-model="resourceDialogVisible"
      title="问题资源"
      width="600px"
      destroy-on-close
    >
      <el-table :data="currentResources" v-loading="loadingResources" border>
        <el-table-column prop="name" label="资源名称" />
        <el-table-column prop="type" label="资源类型" width="120">
          <template #default="{ row }">
            {{ resourceTypeMap[row.type] || '未知' }}
          </template>
        </el-table-column>
        <el-table-column prop="size" label="大小" width="100">
          <template #default="{ row }">
            {{ (row.size / 1024).toFixed(2) }} KB
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" align="center">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleDownloadResource(row)">下载</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- 题目详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="题目详情"
      width="800px"
      destroy-on-close
    >
      <div v-loading="loadingDetail" class="question-detail-content">
        <div class="detail-section">
          <h4 class="section-title">题目内容</h4>
          <div class="content-box">{{ currentQuestionDetail?.content }}</div>
        </div>
        
        <div v-if="currentAnswer" class="detail-section">
          <h4 class="section-title">参考答案</h4>
          <div class="content-box answer-content">{{ currentAnswer.content }}</div>
          
          <h4 class="section-title">答案解析</h4>
          <div class="content-box analysis-content">{{ currentAnswer.analysis || '暂无解析' }}</div>
        </div>
        <div v-else-if="!loadingDetail" class="detail-section">
          <el-empty description="暂无答案信息" />
        </div>
      </div>
    </el-dialog>

    <!-- 修改题目子组件 -->
    <QuestionEditor
      v-model:visible="editDialogVisible"
      :question-id="currentEditingQuestionId"
      @success="fetchQuestionList"
    />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed, watch } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Refresh } from '@element-plus/icons-vue';
import QuestionEditor from './QuestionEditor.vue';
import { getQuestionsPage, deleteQuestionById, getQuestionPageByIndex } from '@/api/modules/practice/question';
import { getQuestionResources, downloadQuestionResource, deleteQuestionResource } from '@/api/modules/practice/questionResource';
import { getAnswers, deleteAnswerById } from '@/api/modules/practice/answer';
import { getQuestionIndexes, deleteQuestionIndexByIds } from '@/api/modules/practice/questionIndex';
import { useUserStore } from '@/store/modules/auth/user'

const props = defineProps({
  practiceId: {
    type: Number,
    default: null
  }
});

const router = useRouter();
const userStore = useUserStore();

// 角色验证
const role = userStore.role;
const isValidRole = computed(() => role === 'teacher' || role === 'admin');
const isAdmin = computed(() => role === 'admin');
const isTeacher = computed(() => role === 'teacher');

// 映射关系
const questionTypeMap = {
  0: '判断题',
  1: '选择题',
  2: '简答题',
  3: '编程题'
};
const difficultyMap = {
  0: '简单',
  1: '中等',
  2: '困难'
};
const difficultyTagMap = {
  0: 'success',
  1: 'warning',
  2: 'danger'
};
const resourceTypeMap = {
  0: '测试用例',
  1: '用例答案',
  2: '问题描述资料'
};

const emit = defineEmits(['success']);

// 状态变量
const loading = ref(false);
const questionList = ref([]);
const total = ref(0);

const searchForm = reactive({
  name: '',
  type: null,
  difficulty: null,
  score: null,
  hasResource: null
});

const pageParams = reactive({
  pageNo: 1,
  pageSize: 10
});

// 资源对话框
const resourceDialogVisible = ref(false);
const currentResources = ref([]);
const loadingResources = ref(false);

// 详情对话框
const detailDialogVisible = ref(false);
const currentQuestionDetail = ref(null);
const currentAnswer = ref(null);
const loadingDetail = ref(false);

// 编辑对话框
const editDialogVisible = ref(false);
const currentEditingQuestionId = ref(0);

// 初始化
onMounted(() => {
  if (isValidRole.value) {
    fetchQuestionList();
  }
});

// 监听 practiceId 变化
watch(() => props.practiceId, () => {
  pageParams.pageNo = 1;
  fetchQuestionList();
});

// 获取列表数据
const fetchQuestionList = async () => {
  loading.value = true;
  try {
    let res;
    if (props.practiceId) {
      // 这里的 API 暂时不支持后端过滤 queryParams，需在前端自行处理或根据业务调整
      res = await getQuestionPageByIndex(pageParams, props.practiceId);
    } else {
      const queryParams = { ...searchForm };
      res = await getQuestionsPage(pageParams, queryParams);
    }

    if (res.code === 200) {
      questionList.value = res.data.records || [];
      total.value = res.data.total || 0;
    }
  } catch (error) {
    ElMessage.error('加载题目列表失败');
  } finally {
    loading.value = false;
  }
};

// 暴露方法给父组件
defineExpose({
  fetchQuestionList
});

// 搜索与重置
const handleSearch = () => {
  pageParams.pageNo = 1;
  fetchQuestionList();
};

const resetSearch = () => {
  Object.keys(searchForm).forEach(key => searchForm[key] = null);
  searchForm.name = '';
  handleSearch();
};

// 分页处理
const handleSizeChange = (val) => {
  pageParams.pageSize = val;
  fetchQuestionList();
};

const handleCurrentChange = (val) => {
  pageParams.pageNo = val;
  fetchQuestionList();
};

// 排序处理
const handleSortChange = ({ prop, order }) => {
  if (!order) {
    // 如果取消排序，则按默认顺序
    fetchQuestionList();
    return;
  }
  
  // 本地排序示例（如果后端不支持排序参数，则采用本地排序）
  // 注意：分页场景下本地排序仅针对当前页数据
  questionList.value.sort((a, b) => {
    let valA = a[prop];
    let valB = b[prop];
    
    if (order === 'ascending') {
      return valA > valB ? 1 : -1;
    } else {
      return valA < valB ? 1 : -1;
    }
  });
};

// 查看资源
const showResources = async (question) => {
  resourceDialogVisible.value = true;
  loadingResources.value = true;
  currentResources.value = [];
  try {
    const res = await getQuestionResources(null, null, null, null, question.id);
    if (res.code === 200) {
      currentResources.value = res.data;
    }
  } catch (error) {
    ElMessage.error('获取资源失败');
  } finally {
    loadingResources.value = false;
  }
};

// 下载资源
const handleDownloadResource = async (resource) => {
  try {
    const res = await downloadQuestionResource(resource.id);
    const blob = res.data; 
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', resource.name);
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    window.URL.revokeObjectURL(url);
    ElMessage.success('下载开始');
  } catch (error) {
    ElMessage.error('下载资源失败');
  }
};

// 查看详情
const viewQuestionDetail = async (question) => {
  detailDialogVisible.value = true;
  loadingDetail.value = true;
  currentQuestionDetail.value = question;
  currentAnswer.value = null;
  try {
    const res = await getAnswers({ questionId: question.id });
    if (res.code === 200 && res.data.length > 0) {
      currentAnswer.value = res.data[0];
    }
  } catch (error) {
    ElMessage.error('获取答案失败');
  } finally {
    loadingDetail.value = false;
  }
};

// 修改题目
const handleEditQuestion = (question) => {
  currentEditingQuestionId.value = question.id;
  editDialogVisible.value = true;
};

// 删除题目
const handleDeleteQuestion = async (question) => {
  const isSpecificPractice = !!props.practiceId;
  const confirmMsg = isSpecificPractice 
    ? `确定要将题目 "${question.name}" 从当前练习中移除吗？(不会删除题库中的原题)`
    : `确定要从题库中永久删除题目 "${question.name}" 吗？该操作将同时清理所有关联资源及答案。`;
  
  try {
    await ElMessageBox.confirm(
      confirmMsg,
      isSpecificPractice ? '提示' : '严重警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: isSpecificPractice ? 'warning' : 'error',
      }
    );

    loading.value = true;
    
    if (isSpecificPractice) {
      // 仅移除练习关联索引
      const idxRes = await getQuestionIndexes({ practiceId: props.practiceId, questionId: question.id });
      if (idxRes.code === 200 && idxRes.data.length > 0) {
        const indexIds = idxRes.data.map(idx => idx.id);
        await deleteQuestionIndexByIds(indexIds);
        ElMessage.success('题目已从当前练习中移除');
      }
    } else {
      // 1. 清理资源
      const resRes = await getQuestionResources(null, null, null, null, question.id);
      if (resRes.code === 200 && resRes.data.length > 0) {
        await Promise.all(resRes.data.map(r => deleteQuestionResource(r.id)));
      }

      // 2. 清理答案
      const ansRes = await getAnswers({ questionId: question.id });
      if (ansRes.code === 200 && ansRes.data.length > 0) {
        await Promise.all(ansRes.data.map(a => deleteAnswerById(a.id)));
      }

      // 4. 删除题目本体
      await deleteQuestionById(question.id);
      ElMessage.success('题目已从题库中移除');
    }

    emit('success', { action: isSpecificPractice ? 'remove' : 'delete', question });
    fetchQuestionList();
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除操作失败');
    }
  } finally {
    loading.value = false;
  }
};
</script>

<style lang="scss" scoped>
.question-display {
  padding: 20px;
  background-color: var(--bg-primary-grey);
  min-height: calc(100vh - 40px);

  .filter-card {
    margin-bottom: 20px;
    border-radius: 8px;
    border: none;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  }

  .table-card {
    border-radius: 8px;
    border: none;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);

    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .title {
        font-size: 18px;
        font-weight: 600;
        color: var(--text-primary);
        border-left: 4px solid var(--primary);
        padding-left: 12px;
      }
    }
  }

  .pagination-container {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }

  .text-placeholder {
    color: var(--text-placeholder);
    font-size: 13px;
  }

  .question-detail-content {
    .detail-section {
      margin-bottom: 24px;

      .section-title {
        font-size: 16px;
        font-weight: 600;
        color: var(--text-primary);
        margin-bottom: 12px;
        border-left: 4px solid var(--primary-light);
        padding-left: 10px;
      }

      .content-box {
        padding: 16px;
        background-color: var(--bg-light);
        border: 1px solid var(--border-light);
        border-radius: 6px;
        line-height: 1.6;
        color: var(--text-regular);
        white-space: pre-wrap;
        word-break: break-all;
      }

      .answer-content {
        background-color: var(--success-alpha-10);
        border-color: var(--success-alpha-20);
        color: var(--success);
        font-weight: 500;
        margin-bottom: 16px;
      }

      .analysis-content {
        background-color: var(--warning-alpha-10);
        border-color: var(--warning-alpha-10);
      }
    }
  }

  .error-view {
    padding: 40px;
  }
}

// 样式覆盖
:deep(.el-table) {
  --el-table-header-bg-color: var(--bg-primary-light);
  --el-table-header-text-color: var(--text-primary);
}

:deep(.el-button--primary) {
  --el-button-bg-color: var(--primary);
  --el-button-border-color: var(--primary);
}
</style>
