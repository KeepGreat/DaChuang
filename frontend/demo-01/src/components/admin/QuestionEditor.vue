<template>
  <el-dialog
    :model-value="visible"
    @update:model-value="$emit('update:visible', $event)"
    title="修改题目"
    width="900px"
    destroy-on-close
    append-to-body
  >
    <div v-loading="loading" class="edit-question-container">
      <!-- 第一部分：题目基本信息 -->
      <div class="edit-section">
        <h4 class="section-title">题目基本信息</h4>
        <el-form :model="editQuestionForm" label-width="100px">
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="题目名称">
                <el-input v-model="editQuestionForm.name" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="题目类型">
                <el-select v-model="editQuestionForm.type" style="width: 100%">
                  <el-option v-for="(label, value) in questionTypeMap" :key="value" :label="label" :value="Number(value)" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="分值">
                <el-input-number v-model="editQuestionForm.score" :min="1" style="width: 100%" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="难度">
                <el-select v-model="editQuestionForm.difficulty" style="width: 100%">
                  <el-option v-for="(label, value) in difficultyMap" :key="value" :label="label" :value="Number(value)" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          <el-form-item label="是否有资源">
            <el-switch v-model="editQuestionForm.hasResource" disabled />
            <span class="text-placeholder" style="margin-left: 10px;">(根据上传/删除行为自动更新)</span>
          </el-form-item>
        </el-form>
      </div>

      <!-- 第二部分：问题资源管理 -->
      <div class="edit-section">
        <h4 class="section-title">问题资源管理</h4>
        
        <!-- 现有资源列表 -->
        <div v-if="editResources.length > 0" class="existing-resources">
          <el-table :data="editResources" border stripe size="small">
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
            <el-table-column label="操作" width="150" align="center">
              <template #default="{ row }">
                <el-button type="primary" link @click="handlePrepareReplace(row)">替换</el-button>
                <el-button type="danger" link @click="handleDeleteEditResource(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
        <div v-else class="text-placeholder" style="margin-bottom: 15px;">暂无资源数据</div>

        <!-- 上传/替换资源 -->
        <div class="resource-upload-box">
          <h5 class="sub-section-title">
            {{ replacingResourceId ? '替换资源文件' : '添加新资源' }}
            <el-button v-if="replacingResourceId" link type="primary" size="small" @click="cancelReplace">取消替换</el-button>
          </h5>
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
              <div class="upload-btn-container">
                <el-button v-if="replacingResourceId" type="success" size="small" @click="handleUploadResource" :loading="uploadingResource">
                  “替换资源”
                </el-button>
              </div>
            </el-form>
          </div>
        </div>
      </div>

      <!-- 第三部分：题目内容与答案 -->
      <div class="edit-section">
        <h4 class="section-title">题目内容与答案</h4>
        <el-form :model="editQuestionForm" label-width="100px">
          <el-form-item label="题目内容">
            <el-input v-model="editQuestionForm.content" type="textarea" :rows="4" />
          </el-form-item>
        </el-form>
        <el-form :model="editAnswerForm" label-width="100px">
          <el-form-item label="参考答案">
            <el-input v-model="editAnswerForm.content" type="textarea" :rows="3" />
          </el-form-item>
          <el-form-item label="答案解析">
            <el-input v-model="editAnswerForm.analysis" type="textarea" :rows="3" />
          </el-form-item>
        </el-form>
      </div>
    </div>
    <template #footer>
      <el-button @click="$emit('update:visible', false)">取消</el-button>
      <el-button type="primary" @click="handleSaveQuestionEdit" :loading="saving">保存修改</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, watch } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { UploadFilled } from '@element-plus/icons-vue';
import { getQuestions, updateQuestion } from '@/api/modules/practice/question';
import { getQuestionResources, addQuestionResource, deleteQuestionResource, updateQuestionResource } from '@/api/modules/practice/questionResource';
import { getAnswers, updateAnswer, createAnswer } from '@/api/modules/practice/answer';

const props = defineProps({
  visible: Boolean,
  questionId: {
    type: Number,
    required: true
  }
});

const emit = defineEmits(['update:visible', 'success']);

const loading = ref(false);
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
const resourceTypeMap = {
  0: '测试用例',
  1: '用例答案',
  2: '问题描述资料'
};

// 表单数据
const editQuestionForm = reactive({
  id: null,
  name: '',
  type: 0,
  score: 0,
  difficulty: 0,
  hasResource: false,
  content: ''
});

const editAnswerForm = reactive({
  id: null,
  content: '',
  analysis: '',
  questionId: null
});

const resourceForm = reactive({
  type: 0,
  description: '',
  name: '',
  size: 0,
  questionId: null
});

// 资源管理
const editResources = ref([]);
const resourceFileList = ref([]);
const selectedResourceFile = ref(null);
const uploadingResource = ref(false);
const replacingResourceId = ref(null);

// 监听 questionId 变化加载数据
watch(() => props.questionId, (newVal) => {
  if (newVal && props.visible) {
    initData();
  }
}, { immediate: true });

// 监听可见性
watch(() => props.visible, (newVal) => {
  if (newVal && props.questionId) {
    initData();
  }
});

const initData = async () => {
  loading.value = true;
  selectedResourceFile.value = null;
  resourceFileList.value = [];
  replacingResourceId.value = null;
  editResources.value = [];

  try {
    // 1. 获取题目基本信息
    const qRes = await getQuestions({ id: props.questionId });
    if (qRes.code === 200 && qRes.data.length > 0) {
      Object.assign(editQuestionForm, qRes.data[0]);
    }

    // 2. 获取答案和资源
    const [ansRes, resRes] = await Promise.all([
      getAnswers({ questionId: props.questionId }),
      getQuestionResources(null, null, null, null, props.questionId)
    ]);

    // 初始化答案表单
    Object.assign(editAnswerForm, {
      id: null,
      content: '',
      analysis: '',
      questionId: props.questionId
    });

    if (ansRes.code === 200 && ansRes.data.length > 0) {
      Object.assign(editAnswerForm, ansRes.data[0]);
    }

    if (resRes.code === 200) {
      editResources.value = resRes.data;
    } else if (resRes.code === 500) {
      editResources.value = [];
    }
    
    // 同步 hasResource 状态
    editQuestionForm.hasResource = editResources.value.length > 0;

  } catch (error) {
    ElMessage.error('获取题目详细信息失败');
  } finally {
    loading.value = false;
  }
};

const fetchEditResources = async () => {
  try {
    const res = await getQuestionResources(null, null, null, null, props.questionId);
    if (res.code === 200) {
      editResources.value = res.data;
      editQuestionForm.hasResource = editResources.value.length > 0;
    } else if (res.code === 500) {
      editResources.value = [];
      editQuestionForm.hasResource = false;
    }
  } catch (error) {
    ElMessage.error('刷新资源列表失败');
  }
};

const handleResourceFileChange = (file) => {
  selectedResourceFile.value = file.raw;
  resourceForm.name = file.raw.name;
  resourceForm.size = file.raw.size;
  resourceForm.questionId = props.questionId;
};

const handlePrepareReplace = (resource) => {
  replacingResourceId.value = resource.id;
  selectedResourceFile.value = null;
  resourceFileList.value = [];
  Object.assign(resourceForm, {
    id: resource.id,
    type: resource.type,
    description: resource.description,
    name: resource.name,
    size: resource.size,
    questionId: resource.questionId
  });
};

const cancelReplace = () => {
  replacingResourceId.value = null;
  selectedResourceFile.value = null;
  resourceFileList.value = [];
};

const handleDeleteEditResource = async (resource) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除资源 "${resource.name}" 吗？该操作不可撤销。`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    );
    
    const res = await deleteQuestionResource(resource.id);
    if (res.code === 200) {
      ElMessage.success('资源删除成功');
      await fetchEditResources();
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('资源删除失败');
    }
  }
};

const handleUploadResource = async () => {
  if (!selectedResourceFile.value) return;
  
  uploadingResource.value = true;
  try {
    let res;
    if (replacingResourceId.value) {
      res = await updateQuestionResource({
        id: replacingResourceId.value,
        type: resourceForm.type,
        description: resourceForm.description,
        questionId: props.questionId
      }, selectedResourceFile.value);
    } else {
      res = await addQuestionResource(resourceForm, selectedResourceFile.value);
    }

    if (res.code === 200) {
      ElMessage.success(replacingResourceId.value ? '资源替换成功' : '资源上传成功');
      replacingResourceId.value = null;
      selectedResourceFile.value = null;
      resourceFileList.value = [];
      await fetchEditResources();
    }
  } catch (error) {
    ElMessage.error(replacingResourceId.value ? '资源替换失败' : '资源上传失败');
  } finally {
    uploadingResource.value = false;
  }
};

const handleSaveQuestionEdit = async () => {
  saving.value = true;
  try {
    // 1. 如果有选中的新资源但没点“替换资源”按钮，则先上传
    if (selectedResourceFile.value) {
      await handleUploadResource();
    }

    // 2. 更新题目
    const qRes = await updateQuestion(editQuestionForm);
    
    // 3. 更新或创建答案
    let aRes;
    if (editAnswerForm.id) {
      aRes = await updateAnswer(editAnswerForm);
    } else {
      aRes = await createAnswer(editAnswerForm);
    }

    if (qRes.code === 200 && aRes.code === 200) {
      ElMessage.success('题目及答案修改成功');
      emit('success');
      emit('update:visible', false);
    }
  } catch (error) {
    ElMessage.error('保存修改失败');
  } finally {
    saving.value = false;
  }
};
</script>

<style lang="scss" scoped>
.edit-question-container {
  .edit-section {
    margin-bottom: 30px;
    
    .section-title {
      font-size: 16px;
      font-weight: 600;
      color: var(--text-primary);
      margin-bottom: 15px;
      border-left: 4px solid var(--primary);
      padding-left: 10px;
    }
    
    .sub-section-title {
      font-size: 14px;
      font-weight: 600;
      color: var(--text-regular);
      margin-bottom: 12px;
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
  }

  .existing-resources {
    margin-bottom: 20px;
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

      .upload-btn-container {
        display: flex;
        justify-content: flex-end;
        margin-top: 10px;
      }
    }
  }
}

.text-placeholder {
  color: var(--text-placeholder);
  font-size: 13px;
}
</style>
