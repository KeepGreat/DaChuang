<template>
  <div class="question-upload">
    <!-- 错误界面：未获取到 practiceId -->
    <div v-if="!practiceId" class="error-view">
      <el-result icon="error" title="参数错误" sub-title="未获取到练习ID，无法创建题目">
        <template #extra>
          <el-button type="primary" @click="$emit('back')">返回</el-button>
        </template>
      </el-result>
    </div>

    <div v-else class="upload-container">
      <el-card class="upload-card">
        <template #header>
          <div class="card-header">
            <div class="header-left">
              <span class="title">题目添加</span>
              <el-radio-group v-model="activeMode" class="mode-switcher" size="default">
                <el-radio-button label="create">新建题目</el-radio-button>
                <el-radio-button label="select">从题库中选题</el-radio-button>
              </el-radio-group>
            </div>
          </div>
        </template>

        <!-- 模式一：新建题目 (原内容) -->
        <div v-if="activeMode === 'create'" v-loading="saving" class="edit-question-container">
          <!-- 第一部分：题目基本信息 -->
          <div class="edit-section">
            <h4 class="section-title">题目基本信息</h4>
            <el-form :model="questionForm" label-width="100px">
              <el-row :gutter="20">
                <el-col :span="12">
                  <el-form-item label="题目名称">
                    <el-input v-model="questionForm.name" placeholder="请输入题目名称" />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="题目类型">
                    <el-select v-model="questionForm.type" style="width: 100%">
                      <el-option v-for="(label, value) in questionTypeMap" :key="value" :label="label" :value="Number(value)" />
                    </el-select>
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row :gutter="20">
                <el-col :span="12">
                  <el-form-item label="分值">
                    <el-input-number v-model="questionForm.score" :min="1" style="width: 100%" />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="难度">
                    <el-select v-model="questionForm.difficulty" style="width: 100%">
                      <el-option v-for="(label, value) in difficultyMap" :key="value" :label="label" :value="Number(value)" />
                    </el-select>
                  </el-form-item>
                </el-col>
              </el-row>
              <el-form-item label="是否有资源">
                <el-switch v-model="questionForm.hasResource" disabled />
                <span class="text-placeholder" style="margin-left: 10px;">(根据上传资源自动更新)</span>
              </el-form-item>
            </el-form>
          </div>

          <!-- 第二部分：问题资源管理 -->
          <div class="edit-section">
            <h4 class="section-title">问题资源上传</h4>
            <div class="resource-upload-box">
              <el-upload
                class="resource-uploader"
                drag
                action="#"
                :auto-upload="false"
                :on-change="handleResourceFileChange"
                :limit="1"
                :file-list="resourceFileList"
              >
                <el-icon class="el-icon--upload"><upload-filled /></el-icon>
                <div class="el-upload__text">将文件拖到此处，或 <em>点击上传</em></div>
              </el-upload>
              
              <div v-if="selectedResourceFile" class="resource-info">
                <el-form :model="resourceForm" label-width="80px" size="small">
                  <el-row :gutter="10">
                    <el-col :span="12">
                      <el-form-item label="资源类型">
                        <el-select v-model="resourceForm.type" style="width: 100%">
                          <el-option v-for="(label, value) in resourceTypeMap" :key="value" :label="label" :value="Number(value)" />
                        </el-select>
                      </el-form-item>
                    </el-col>
                    <el-col :span="12">
                      <el-form-item label="资源描述">
                        <el-input v-model="resourceForm.description" placeholder="可选" />
                      </el-form-item>
                    </el-col>
                  </el-row>
                </el-form>
              </div>
            </div>
          </div>

          <!-- 第三部分：题目内容与答案 -->
          <div class="edit-section">
            <h4 class="section-title">题目内容与答案</h4>
            <el-form :model="questionForm" label-width="100px">
              <el-form-item label="题目内容">
                <el-input v-model="questionForm.content" type="textarea" :rows="4" placeholder="请输入题目内容" />
              </el-form-item>
            </el-form>
            <el-form :model="answerForm" label-width="100px">
              <el-form-item label="参考答案">
                <el-input v-model="answerForm.content" type="textarea" :rows="3" placeholder="请输入参考答案" />
              </el-form-item>
              <el-form-item label="答案解析">
                <el-input v-model="answerForm.analysis" type="textarea" :rows="3" placeholder="请输入答案解析" />
              </el-form-item>
            </el-form>
          </div>

          <div class="actions">
            <el-button @click="$emit('back')">取消</el-button>
            <el-button type="primary" @click="handleSaveQuestion" :loading="saving">创建题目</el-button>
          </div>
        </div>

        <!-- 模式二：从题库中选题 -->
        <div v-else class="select-questions-container">
          <div class="bank-filter">
            <el-form :inline="true" :model="bankSearchForm" size="small">
              <el-form-item label="题目名称">
                <el-input v-model="bankSearchForm.name" placeholder="搜索名称" clearable @keyup.enter="fetchBankQuestions" />
              </el-form-item>
              <el-form-item label="类型">
                <el-select v-model="bankSearchForm.type" placeholder="全部" clearable style="width: 120px">
                  <el-option v-for="(label, value) in questionTypeMap" :key="value" :label="label" :value="Number(value)" />
                </el-select>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="fetchBankQuestions">查询</el-button>
              </el-form-item>
            </el-form>
          </div>

          <el-table 
            :data="bankQuestions" 
            v-loading="loadingBank" 
            border 
            stripe 
            @selection-change="handleSelectionChange"
            height="450px"
          >
            <el-table-column type="selection" width="55" :selectable="canSelectQuestion" />
            <el-table-column prop="id" label="ID" width="70" align="center" />
            <el-table-column prop="name" label="题目名称" min-width="180" show-overflow-tooltip />
            <el-table-column prop="type" label="类型" width="100">
              <template #default="{ row }">
                {{ questionTypeMap[row.type] || '未知' }}
              </template>
            </el-table-column>
            <el-table-column prop="score" label="分值" width="70" align="center" />
            <el-table-column prop="difficulty" label="难度" width="90" align="center">
              <template #default="{ row }">
                <el-tag :type="difficultyTagMap[row.difficulty]" size="small">
                  {{ difficultyMap[row.difficulty] || '未知' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="状态" width="120" align="center">
              <template #default="{ row }">
                <el-tag v-if="isQuestionAlreadyAdded(row.id)" type="info" size="small">已在该练习</el-tag>
                <el-tag v-else type="success" size="small">可选</el-tag>
              </template>
            </el-table-column>
          </el-table>

          <div class="pagination-footer">
            <el-pagination
              v-model:current-page="bankPageParams.pageNo"
              v-model:page-size="bankPageParams.pageSize"
              :total="bankTotal"
              layout="total, prev, pager, next"
              @current-change="fetchBankQuestions"
              small
            />
            <div class="actions">
              <el-button @click="$emit('back')">取消</el-button>
              <el-button 
                type="primary" 
                @click="handleBatchAdd" 
                :disabled="selectedQuestions.length === 0"
                :loading="addingIndices"
              >
                确认添加 ({{ selectedQuestions.length }})
              </el-button>
            </div>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, watch, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { UploadFilled } from '@element-plus/icons-vue';
import { createQuestionAndIndex, getQuestionsPage } from '@/api/modules/practice/question';
import { addQuestionResource } from '@/api/modules/practice/questionResource';
import { createAnswer } from '@/api/modules/practice/answer';
import { createQuestionIndexes, getQuestionIndexes } from '@/api/modules/practice/questionIndex';

const props = defineProps({
  practiceId: {
    type: [Number, String],
    required: true
  }
});

const emit = defineEmits(['back', 'success']);

// 模式管理
const activeMode = ref('create'); // 'create' or 'select'

const saving = ref(false);

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

// --- 新建题目模式数据 ---
// 题目表单
const questionForm = reactive({
  name: '',
  type: 0,
  content: '',
  score: 5,
  difficulty: 0,
  hasResource: false
});

// 答案表单
const answerForm = reactive({
  content: '',
  analysis: ''
});

// 资源表单
const resourceForm = reactive({
  type: 0,
  description: '',
  name: '',
  size: 0
});

const resourceFileList = ref([]);
const selectedResourceFile = ref(null);

// --- 从题库选题模式数据 ---
const loadingBank = ref(false);
const addingIndices = ref(false);
const bankQuestions = ref([]);
const bankTotal = ref(0);
const bankPageParams = reactive({
  pageNo: 1,
  pageSize: 10
});
const bankSearchForm = reactive({
  name: '',
  type: null
});
const selectedQuestions = ref([]);
const existingQuestionIds = ref([]); // 已经在这个练习里的题目ID

// 初始化
onMounted(() => {
  fetchExistingIndices();
});

// 切换模式时自动加载题库
watch(activeMode, (newMode) => {
  if (newMode === 'select' && bankQuestions.value.length === 0) {
    fetchBankQuestions();
  }
});

// 获取该练习已有的题目ID
const fetchExistingIndices = async () => {
  try {
    const res = await getQuestionIndexes({ practiceId: props.practiceId });
    if (res.code === 200) {
      existingQuestionIds.value = res.data.map(item => item.questionId);
    }
  } catch (error) {
    console.error('获取已有索引失败:', error);
  }
};

// 获取题库题目
const fetchBankQuestions = async () => {
  loadingBank.value = true;
  try {
    const res = await getQuestionsPage(bankPageParams, bankSearchForm);
    if (res.code === 200) {
      bankQuestions.value = res.data.records || [];
      bankTotal.value = res.data.total || 0;
    }
  } catch (error) {
    ElMessage.error('获取题库失败');
  } finally {
    loadingBank.value = false;
  }
};

// 复选框逻辑：已存在的题目不可选
const canSelectQuestion = (row) => {
  return !existingQuestionIds.value.includes(row.id);
};

const isQuestionAlreadyAdded = (id) => {
  return existingQuestionIds.value.includes(id);
};

const handleSelectionChange = (val) => {
  selectedQuestions.value = val;
};

// 批量添加题目索引
const handleBatchAdd = async () => {
  if (selectedQuestions.value.length === 0) return;
  
  addingIndices.value = true;
  try {
    const list = selectedQuestions.value.map(q => ({
      practiceId: Number(props.practiceId),
      questionId: q.id
    }));
    
    const res = await createQuestionIndexes(list);
    if (res.code === 200) {
      ElMessage.success(`成功添加 ${list.length} 道题目`);
      emit('success', { count: list.length }); // 传递增加的数量
    }
  } catch (error) {
    ElMessage.error('批量添加失败');
  } finally {
    addingIndices.value = false;
  }
};

// 监听文件变化
const handleResourceFileChange = (file) => {
  selectedResourceFile.value = file.raw;
  resourceForm.name = file.raw.name;
  resourceForm.size = file.raw.size;
  questionForm.hasResource = true;
};

// 保存题目流程
const handleSaveQuestion = async () => {
  if (!questionForm.content) {
    ElMessage.warning('请输入题目内容');
    return;
  }
  if (!answerForm.content) {
    ElMessage.warning('请输入参考答案');
    return;
  }

  saving.value = true;
  try {
    // 1. 创建题目及其索引
    const createData = {
      question: {
        ...questionForm
      },
      questionIndex: {
        practiceId: Number(props.practiceId),
        questionId: 0 // 后端会自动处理或需要先创建题目
      }
    };
    
    // 注意：根据后端接口设计，createQuestionAndIndex 会同时创建题目和练习索引
    const qRes = await createQuestionAndIndex(createData);
    
    if (qRes.code === 200) {
      // 获取创建成功的题目ID
      const newQuestionId = qRes.data.id;

      // 2. 创建答案
      await createAnswer({
        content: answerForm.content,
        analysis: answerForm.analysis,
        questionId: newQuestionId
      });

      // 3. 上传资源 (如果有)
      if (selectedResourceFile.value) {
        await addQuestionResource({
          ...resourceForm,
          questionId: newQuestionId
        }, selectedResourceFile.value);
      }

      ElMessage.success('题目创建成功');
      emit('success');
    }
  } catch (error) {
    console.error('保存失败:', error);
    ElMessage.error('创建题目失败');
  } finally {
    saving.value = false;
  }
};
</script>

<style lang="scss" scoped>
.question-upload {
  padding: 20px;
  background-color: var(--bg-primary-grey);
  min-height: 100%;

  .upload-container {
    max-width: 1000px;
    margin: 0 auto;
  }

  .upload-card {
    border-radius: 8px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
    border: none;

    .card-header {
      .header-left {
        display: flex;
        align-items: center;
        gap: 20px;

        .title {
          font-size: 18px;
          font-weight: 600;
          color: var(--text-primary);
          border-left: 4px solid var(--primary);
          padding-left: 12px;
        }

        .mode-switcher {
          margin-left: 20px;
        }
      }
    }
  }

  .select-questions-container {
    padding: 10px 0;

    .bank-filter {
      margin-bottom: 15px;
      padding: 12px;
      background-color: var(--bg-primary-light);
      border-radius: 4px;
    }

    .pagination-footer {
      margin-top: 20px;
      display: flex;
      justify-content: space-between;
      align-items: center;

      .actions {
        display: flex;
        gap: 12px;
      }
    }
  }

  .edit-question-container {
    padding: 10px 0;

    .edit-section {
      margin-bottom: 30px;
      
      .section-title {
        font-size: 16px;
        font-weight: 600;
        color: var(--text-primary);
        margin-bottom: 15px;
        border-left: 4px solid var(--primary-light);
        padding-left: 10px;
      }
    }

    .resource-upload-box {
      border: 1px dashed var(--border-base);
      border-radius: 8px;
      padding: 20px;
      background-color: var(--bg-primary-alpha);

      .resource-uploader {
        margin-bottom: 15px;
      }

      .resource-info {
        background-color: var(--bg-white);
        padding: 15px;
        border-radius: 4px;
        border: 1px solid var(--border-light);
      }
    }

    .actions {
      display: flex;
      justify-content: center;
      gap: 20px;
      margin-top: 40px;
    }
  }

  .text-placeholder {
    color: var(--text-placeholder);
    font-size: 13px;
  }

  .error-view {
    padding: 40px;
  }
}

// 确保使用全局变量中的颜色
:deep(.el-button--primary) {
  --el-button-bg-color: var(--primary);
  --el-button-border-color: var(--primary);
}
</style>
