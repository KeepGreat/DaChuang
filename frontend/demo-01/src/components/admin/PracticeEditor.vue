<template>
  <div class="practice-editor">
    <!-- Practice 基础信息编辑部分 -->
    <el-card class="practice-info-card">
      <template #header>
        <div class="card-header">
          <span class="title">练习基础信息</span>
          <el-button type="primary" @click="handleUpdatePractice">保存修改</el-button>
        </div>
      </template>
      
      <el-form :model="practiceForm" label-width="120px" class="practice-form">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="练习名称">
              <el-input v-model="practiceForm.name" placeholder="请输入练习名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
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
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="截止日期">
              <el-date-picker
                v-model="practiceForm.expiredAt"
                type="datetime"
                placeholder="选择截止日期"
                format="YYYY-MM-DD HH:mm:ss"
                value-format="YYYY-MM-DDTHH:mm:ss"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="问题数量">
              <el-input v-model="practiceForm.questionNum" disabled />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="创建时间">
              <el-input v-model="practiceForm.createdAt" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="课时关联">
              <el-select v-model="practiceForm.courseId" placeholder="请选择关联课时" style="width: 100%">
                <el-option label="不关联课时" :value="0" />
                <el-option
                  v-for="course in courses"
                  :key="course.id"
                  :label="course.name"
                  :value="course.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </el-card>

    <!-- Question 展示部分 -->
    <el-card class="questions-card">
      <template #header>
        <div class="card-header">
          <span class="title">题目管理</span>
          <el-button type="primary" size="small" @click="addDialogVisible = true">新增题目</el-button>
        </div>
      </template>

      <!-- 使用 QuestionDisplay 子组件，传入 practiceId 进入练习模式 -->
      <QuestionDisplay 
        v-if="practiceForm.id"
        :practice-id="practiceForm.id" 
        ref="questionDisplayRef"
        @success="handleQuestionSuccess"
      />
    </el-card>

    <!-- 新增题目对话框 -->
    <el-dialog
      v-model="addDialogVisible"
      title="新增题目"
      width="1100px"
      destroy-on-close
      append-to-body
    >
      <QuestionUpload
        :practice-id="practiceId"
        @back="addDialogVisible = false"
        @success="handleUploadSuccess"
      />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, reactive } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import QuestionDisplay from './QuestionDisplay.vue';
import QuestionUpload from './QuestionUpload.vue';
import { teacherStore } from '@/store/modules/teacher/teacherStore';
import { 
  getPractices, 
  getPracticeTypes, 
  updatePractice 
} from '@/api/modules/practice/practice';
import { getCourses } from '@/api/modules/teaching/CourseAPI';
import { getPracticeIndexes, updatePracticeIndex } from '@/api/modules/practice/practiceIndex';

const router = useRouter();
const store = teacherStore();
const practiceId = computed(() => store.chosenPracticeId);

// Practice 数据
const practiceForm = reactive({
  id: null,
  name: '',
  practiceTypeId: null,
  expiredAt: '',
  questionNum: 0,
  createdAt: '',
  courseId: 0 // 新增：关联的课时ID
});
const practiceTypes = ref([]);
const courses = ref([]); // 新增：课时列表
const currentPracticeIndex = ref(null); // 新增：保存当前的索引对象

// QuestionDisplay 组件引用
const questionDisplayRef = ref(null);

// 新增题目对话框
const addDialogVisible = ref(false);

// 初始化加载
onMounted(async () => {
  if (!practiceId.value || practiceId.value === 0) {
    ElMessage.warning('请先选择一个练习');
    router.back();
    return;
  }
  
  await Promise.all([
    fetchPracticeInfo(),
    fetchPracticeTypes(),
    fetchPracticeIndex(),
    fetchCourses()
  ]);
});

// 获取练习索引信息
const fetchPracticeIndex = async () => {
  try {
    const res = await getPracticeIndexes({ practiceId: practiceId.value });
    if (res.code === 200 && res.data.length > 0) {
      currentPracticeIndex.value = res.data[0];
      practiceForm.courseId = res.data[0].courseId || 0;
    }
  } catch (error) {
    console.error('获取练习索引失败:', error);
  }
};

// 获取课时列表
const fetchCourses = async () => {
  try {
    // 根据当前选中的课程系列获取课时
    if (store.chosenCourseSectionId) {
      const res = await getCourses(null, null, store.chosenCourseSectionId);
      if (res.code === 200) {
        courses.value = res.data || [];
      }
    }
  } catch (error) {
    console.error('获取课时列表失败:', error);
  }
};

// 获取练习信息
const fetchPracticeInfo = async () => {
  try {
    const res = await getPractices(practiceId.value);
    if (res.code === 200 && res.data.length > 0) {
      const p = res.data[0];
      Object.assign(practiceForm, p);
    }
  } catch (error) {
    ElMessage.error('获取练习信息失败');
  }
};

// 获取练习类型
const fetchPracticeTypes = async () => {
  try {
    const res = await getPracticeTypes();
    if (res.code === 200) {
      practiceTypes.value = res.data;
    }
  } catch (error) {
    ElMessage.error('获取练习类型失败');
  }
};

// 处理 QuestionDisplay 的成功回调
const handleQuestionSuccess = async ({ action }) => {
  // 同步更新练习题目数量
  if (action === 'delete' || action === 'remove') {
    practiceForm.questionNum = Math.max(0, practiceForm.questionNum - 1);
    // 这里建议同步更新后端数据
    try {
      await updatePractice({
        id: practiceForm.id,
        questionNum: practiceForm.questionNum
      });
    } catch (error) {
      console.error('同步题目数量失败:', error);
    }
  }
};

// 保存练习修改
const handleUpdatePractice = async () => {
  try {
    // 1. 更新练习基础信息
    const data = {
      id: practiceForm.id,
      name: practiceForm.name,
      practiceTypeId: practiceForm.practiceTypeId,
      expiredAt: practiceForm.expiredAt
    };
    const res = await updatePractice(data);
    
    // 2. 更新练习与课时的关联关系 (PracticeIndex)
    if (res.code === 200 && currentPracticeIndex.value) {
      // 保持 practiceId 和 courseSectionId 不变，只修改 courseId
      const indexData = {
        id: currentPracticeIndex.value.id,
        courseId: practiceForm.courseId
      };
      await updatePracticeIndex(indexData);
      
      ElMessage.success('练习信息及课时关联更新成功');
    }
  } catch (error) {
    ElMessage.error('更新练习信息失败');
  }
};

// 新增题目成功回调
const handleUploadSuccess = async (data) => {
  addDialogVisible.value = false;
  
  // 1. 更新练习题目数量
  try {
    const increment = (data && data.count) ? data.count : 1;
    const updatedPractice = {
      id: practiceForm.id,
      questionNum: practiceForm.questionNum + increment
    };
    const res = await updatePractice(updatedPractice);
    if (res.code === 200) {
      practiceForm.questionNum += increment;
      ElMessage.success('练习信息已同步');
    }
  } catch (error) {
    ElMessage.error('更新练习题目数量失败');
  }

  // 2. 刷新题目列表
  if (questionDisplayRef.value) {
    questionDisplayRef.value.fetchQuestionList();
  }
};
</script>

<style lang="scss" scoped>
.practice-editor {
  padding: 20px;
  background-color: var(--bg-primary-grey);
  min-height: calc(100vh - 40px);

  .el-card {
    margin-bottom: 20px;
    border-radius: 8px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
    border: none;

    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .title {
        font-size: 18px;
        font-weight: 600;
        color: var(--text-primary);
        position: relative;
        padding-left: 12px;

        &::before {
          content: '';
          position: absolute;
          left: 0;
          top: 50%;
          transform: translateY(-50%);
          width: 4px;
          height: 18px;
          background-color: var(--primary);
          border-radius: 2px;
        }
      }
    }
  }

  .practice-form {
    padding: 10px 0;
  }

  .text-placeholder {
    color: var(--text-placeholder);
    font-size: 13px;
  }

  :deep(.el-table) {
    --el-table-header-bg-color: var(--bg-primary-light);
    --el-table-header-text-color: var(--text-primary);
    
    .el-table__header {
      th {
        font-weight: 600;
      }
    }
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
}

// Element Plus 样式覆盖，确保使用 variables.scss 中的蓝色
:deep(.el-button--primary) {
  --el-button-bg-color: var(--primary);
  --el-button-border-color: var(--primary);
  --el-button-hover-bg-color: var(--primary-hover);
  --el-button-hover-border-color: var(--primary-hover);
  --el-button-active-bg-color: var(--primary-dark);
  --el-button-active-border-color: var(--primary-dark);
}

:deep(.el-tag--primary) {
  --el-tag-bg-color: var(--primary-lightest);
  --el-tag-border-color: var(--primary-alpha-20);
  --el-tag-text-color: var(--primary);
}

:deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px var(--primary) inset !important;
}
</style>
