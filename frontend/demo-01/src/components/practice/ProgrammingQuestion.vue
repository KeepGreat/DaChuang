<template>
  <!-- 编程题组件主容器 -->
  <div class="programming-question-container">
    <!-- 左侧：问题描述区域 -->
    <div class="left-section">
      <!-- 问题题目卡片 -->
      <div class="section-card">
        <h3 class="section-title">问题题目</h3>
        <div class="problem-title">{{ question.title }}</div>
      </div>

      <!-- 问题要求卡片 -->
      <div class="section-card">
        <h3 class="section-title">问题要求</h3>
        <div class="problem-content">{{ question.content }}</div>
      </div>

      <!-- 问题说明卡片 -->
      <div class="section-card">
        <h3 class="section-title">问题说明</h3>
        <div class="note-content">
          <div v-if="question.input" class="note-item">
            <span class="note-label">输入:</span> {{ question.input }}
          </div>
          <div v-if="question.output" class="note-item">
            <span class="note-label">输出:</span> {{ question.output }}
          </div>
          <div v-if="question.note" class="note-item">
            <span class="note-label">注意:</span> {{ question.note }}
          </div>
        </div>
      </div>

      <!-- 问题资源 -->
      <QuestionResources v-if="question.hasResource" :question-id="question.id" />
    </div>

    <!-- 右侧：代码编写区域 -->
    <div class="right-section">
      <div class="code-card">
        <!-- 代码卡头部 -->
        <div class="code-header">
          <span class="code-title">编写代码</span>
          <div class="code-actions">
            <!-- 编程语言选择器 -->
            <el-select
              v-model="selectedLanguage"
              placeholder="选择语言"
              size="default"
              style="width: 100px"
            >
              <el-option label="Python" value="python"></el-option>
              <el-option label="Java" value="java"></el-option>
              <el-option label="C++" value="cpp"></el-option>
              <el-option label="JavaScript" value="javascript"></el-option>
            </el-select>
            <!-- 查看上一次评测按钮 -->
            <el-button
              v-if="hasPreviousEvaluation"
              size="default"
              style="margin-left: 10px"
              @click="showPreviousEvaluation"
              >查看上一次评测</el-button
            >
          </div>
        </div>

        <!-- 代码输入区域 -->
        <div class="code-content">
          <textarea
            v-model="codeInput"
            class="code-input"
            placeholder="在此输入代码..."
            :disabled="showCorrectness"
          ></textarea>
        </div>

        <!-- 代码运行结果展示 -->
        <div v-if="codeResult" class="code-result">
          <h4>运行结果：</h4>
          <pre>{{ codeResult }}</pre>
        </div>
      </div>
    </div>

    <!-- 代码评测结果对话框 -->
    <el-dialog
      v-model="evaluationDialogVisible"
      width="80%"
      :before-close="handleDialogClose"
    >
      <div class="evaluation-container">
        <h3 class="dialog-inner-title">代码评测结果</h3>

        <div class="evaluation-content" v-loading="isEvaluationLoading">
          <!-- 评测结果内容 -->
          <div
            v-if="evaluationContent && !isEvaluationLoading"
            class="evaluation-item markdown-content"
          >
            <div v-html="parseMarkdown(evaluationContent)"></div>
          </div>

          <!-- 无评估结果状态 -->
          <div v-else-if="!isEvaluationLoading" class="empty-state">
            <p>暂无评估结果</p>
          </div>
        </div>
      </div>

      <!-- 对话框底部按钮 -->
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="evaluationDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
// 导入必要的库和组件
import { getUserAnswers, getUserId, judgeCodeAnswer } from "@/api";
import { useUserAnswerStore, useUserStore } from "@/store";
import { ElMessage } from "element-plus";
import { marked } from "marked";
import { onMounted, ref, watch } from "vue";
import QuestionResources from "./QuestionResources.vue";

// ==========================================================================
// Store 初始化
// ==========================================================================
const userAnswerStore = useUserAnswerStore(); // 管理用户答案
const userStore = useUserStore(); // 管理用户状态

// ==========================================================================
// Props 定义：父组件传递的数据
// ==========================================================================
const props = defineProps({
  // 问题信息对象
  question: {
    type: Object,
    required: true,
    default: () => ({
      id: "",
      title: "",
      content: "",
      input: "",
      output: "",
      note: "",
      answer: "",
      status: null,
    }),
  },
  // 是否显示答案
  showCorrectness: {
    type: Boolean,
    default: false,
  },
  // 用户已有的答案
  userAnswer: {
    type: String,
    default: "",
  },
});

// ==========================================================================
// Emits 定义：向父组件传递的事件
// ==========================================================================
const emit = defineEmits([
  "answer-submitted", // 答案提交事件
]);

// ==========================================================================
// 组件暴露方法：供父组件调用
// ==========================================================================
defineExpose({
  submitCode, // 暴露代码提交方法
});

// ==========================================================================
// 响应式状态管理
// ==========================================================================
// 核心状态
const selectedLanguage = ref("cpp"); // 选中的编程语言
const codeInput = ref(""); // 用户输入的代码
const codeResult = ref(""); // 代码运行结果

// 评估对话框状态
const evaluationDialogVisible = ref(false); // 评估对话框可见性
const evaluationContent = ref(""); // 评估内容（使用单个字符串存储完整评估文本）
const isEvaluationLoading = ref(false); // 评估加载状态
const hasPreviousEvaluation = ref(false); // 是否有上一次评估结果

// ==========================================================================
// 生命周期钩子
// ==========================================================================
// 组件挂载时初始化
onMounted(() => {
  checkForPreviousEvaluation(); // 检查是否存在上一次评估结果
});

// ==========================================================================
// 监听逻辑：响应数据变化
// ==========================================================================
// 监听问题变化，更新本地代码
watch(
  () => props.question,
  (newQuestion) => {
    if (newQuestion) {
      // 初始化代码，从store中获取用户答案
      const userAnswer = userAnswerStore.getUserAnswerByQuestionId(newQuestion.id);
      // 确保 codeInput 始终是字符串类型
      codeInput.value = typeof userAnswer === "string" ? userAnswer : "";
    }
    codeResult.value = ""; // 清空之前的运行结果
  },
  { immediate: true, deep: true }
); // immediate：立即执行，deep：深度监听

// 监听代码变化，更新store中的用户答案
watch(
  codeInput,
  (newCode) => {
    userAnswerStore.updateUserAnswerByQuestionId(props.question.id, newCode);
  },
  { deep: true }
);

// ==========================================================================
// 核心功能函数
// ==========================================================================
/**
 * 检查是否存在上一次评估结果
 */
function checkForPreviousEvaluation() {
  const savedEvaluation = sessionStorage.getItem("previousEvaluation");
  hasPreviousEvaluation.value = !!savedEvaluation;
}

/**
 * 提交代码并处理评估响应
 */
async function submitCode() {
  // 验证代码是否为空
  if (!codeInput.value.trim()) {
    ElMessage.error("请输入代码");
    emit("answer-submitted", {
      questionId: props.question.id,
      answer: codeInput.value,
      isEmpty: true,
    });
    return;
  }

  // 获取用户ID
  let userId = null;
  try {
    if (!userStore.token) {
      ElMessage.error("用户未登录，无法提交代码");
      return;
    }

    const res = await getUserId({ token: userStore.token });
    userId = res.data;
  } catch (error) {
    console.error("获取用户ID异常:", error);
    ElMessage.error("获取用户信息失败，请重新登录");
    return;
  }

  // 向父组件发出答案提交事件
  emit("answer-submitted", {
    questionId: props.question.id,
    answer: codeInput.value,
    isEmpty: false,
  });

  // 查询现有的用户答案记录，获取id
  let existingAnswerId = null;
  try {
    const existingAnswers = await getUserAnswers({
      questionId: props.question.id,
      userId: userId,
    });

    if (existingAnswers?.data?.length > 0) {
      const answers = existingAnswers.data;
      if (answers.length === 1) {
        // 只有一条记录，直接使用
        existingAnswerId = answers[0].id;
        console.log(`找到现有答案记录，id: ${existingAnswerId}`);
      } else {
        // 有多条记录，取最新的一条作为兜底处理
        const sortedAnswers = answers.sort((a, b) => b.id - a.id);
        existingAnswerId = sortedAnswers[0].id;
        console.log(`找到多条现有答案记录，取最新的一条，id: ${existingAnswerId}`);
      }
    }
  } catch (error) {
    console.error("查询现有答案失败:", error);
    // 查询失败不影响后续流程，继续执行
  }

  // 准备提交给后端的代码判题数据，按照JudgeCodeRequest结构
  const judgeCodeRequest = {
    codeSandboxInput: {
      codeLanguage: selectedLanguage.value,
      code: codeInput.value,
      input: props.question.codeInput,
    },
    userAnswer: {
      content: codeInput.value,
      userId: userId,
      questionId: props.question.id,
      questionType: 3, // 3代表编程题
      // 如果找到现有记录，传递id，否则不传id
      ...(existingAnswerId && { id: existingAnswerId }),
    },
  };

  // 重置评估相关状态并打开评估对话框
  resetEvaluationState();
  evaluationDialogVisible.value = true;

  try {
    // 调用代码判题API
    const res = await judgeCodeAnswer(judgeCodeRequest);

    // 从响应中提取评估结果
    evaluationContent.value = res.data.analysisOutput.analysis;

    // 保存完整结果到sessionStorage，以便用户查看历史记录
    sessionStorage.setItem("previousEvaluation", res.data.analysisOutput.analysis);
    hasPreviousEvaluation.value = true;
  } catch (error) {
    console.error("代码提交失败:", error);
    ElMessage.error("代码提交失败，请稍后重试");
  } finally {
    isEvaluationLoading.value = false;
  }
}

/**
 * 重置评估相关状态
 */
function resetEvaluationState() {
  evaluationContent.value = "";
  isEvaluationLoading.value = true;
}

/**
 * 显示上一次的代码评测结果
 */
function showPreviousEvaluation() {
  const savedEvaluation = sessionStorage.getItem("previousEvaluation");
  if (savedEvaluation) {
    evaluationContent.value = savedEvaluation;
    isEvaluationLoading.value = false;
    evaluationDialogVisible.value = true;
  }
}

// ==========================================================================
// 对话框相关函数
// ==========================================================================
/**
 * 处理对话框关闭前的逻辑
 */
function handleDialogClose() {
  evaluationDialogVisible.value = false;
}

// ==========================================================================
// 辅助函数
// ==========================================================================
/**
 * 将Markdown文本解析为HTML
 * @param {string} text - 需要解析的Markdown文本
 * @returns {string} - 解析后的HTML内容
 */
function parseMarkdown(text) {
  try {
    return marked.parse(text);
  } catch (error) {
    console.error("Markdown解析错误:", error);
    return text; // 解析失败时返回原始文本
  }
}
</script>

<style scoped>
.programming-question-container {
  display: flex;
  gap: 20px;
  width: 100%;
  height: auto;
  overflow: hidden;
  margin-bottom: 20px;
}

/* 左侧区域样式 */
.left-section {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 20px;
  overflow-y: auto;
  padding-right: 10px;
}

.section-card {
  background-color: #ffffff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  border: 1px solid #e6f7ff;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #1890ff;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid #f0f0f0;
}

.problem-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  line-height: 1.6;
}

.problem-content {
  font-size: 14px;
  line-height: 1.6;
  color: #666;
  white-space: pre-wrap;
  word-wrap: break-word;
}

.note-content {
  font-size: 14px;
  line-height: 1.6;
  color: #666;
}

.note-item {
  margin-bottom: 10px;
}

.note-label {
  font-weight: 600;
  color: #333;
  margin-right: 5px;
}

/* 右侧区域样式 */
.right-section {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 20px;
  overflow: hidden;
}

.code-card {
  background-color: #ffffff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  border: 1px solid #e6f7ff;
  display: flex;
  flex-direction: column;
  height: 100%;
  overflow: hidden;
}

.code-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  border-bottom: 1px solid #f0f0f0;
  background-color: #fafafa;
}

.code-title {
  font-size: 16px;
  font-weight: 600;
  color: #1890ff;
}

.code-actions {
  display: flex;
  align-items: center;
}

.code-content {
  flex: 1;
  position: relative;
  padding: 20px;
  overflow: hidden;
}

.code-input {
  width: 100%;
  height: 100%;
  margin: 0;
  background-color: #fafafa;
  border: 1px solid #e8e8e8;
  border-radius: 4px;
  outline: none;
  resize: none;
  font-family: "Consolas", "Monaco", "Courier New", monospace;
  font-size: 14px;
  line-height: 1.5;
  color: #333;
  caret-color: #000;
  white-space: pre-wrap;
  word-wrap: break-word;
  letter-spacing: 0;
  tab-size: 4;
  padding: 15px;
}

/* 代码运行结果 */
.code-result {
  margin: 0 20px 20px 20px;
  padding: 12px;
  background: #f0f9eb;
  border: 1px solid #e1f3d8;
  border-radius: 6px;
}

.code-result h4 {
  margin: 0 0 10px 0;
  color: #67c23a;
  font-size: 14px;
  font-weight: 600;
}

.code-result pre {
  margin: 0;
  font-family: "Consolas", "Monaco", "Courier New", monospace;
  font-size: 14px;
  line-height: 1.5;
  color: #67c23a;
  white-space: pre-wrap;
  word-wrap: break-word;
}

/* 正确答案展示 */
.correct-answer {
  margin: 0 20px 20px 20px;
  padding: 16px;
  background: #f0f9eb;
  border: 1px solid #e1f3d8;
  border-radius: 6px;
}

.correct-answer h4 {
  margin: 0 0 12px 0;
  color: #67c23a;
  font-size: 16px;
  font-weight: 600;
}

.correct-answer .code-editor {
  background-color: #f5f5f5;
  padding: 12px;
  border-radius: 4px;
  overflow-x: auto;
  border: 1px solid #ddd;
}

.correct-answer pre {
  margin: 0;
  font-family: "Consolas", "Monaco", "Courier New", monospace;
  font-size: 14px;
  line-height: 1.5;
  color: #333;
  white-space: pre-wrap;
  word-wrap: break-word;
}

/* 评估对话框样式 */
.evaluation-container {
  max-height: 500px;
  overflow-y: auto;
  padding: 10px;
}

.dialog-inner-title {
  font-size: 16px;
  font-weight: 600;
  color: #1890ff;
  margin-bottom: 15px;
  text-align: left;
}

.evaluation-content {
  font-family: "Courier New", Courier, monospace;
  font-size: 14px;
  line-height: 1.5;
}

.evaluation-item {
  padding: 15px;
  background-color: #f5f5f5;
  border-radius: 4px;
  margin-bottom: 10px;
}

.evaluation-item pre {
  margin: 8px 0;
  white-space: pre-wrap;
  word-wrap: break-word;
  line-height: 1.6;
}

.empty-state {
  text-align: center;
  padding: 40px;
  color: #999;
}

/* Markdown 内容样式 */
.markdown-content {
  font-size: 14px;
  line-height: 1.6;
  color: #333;
}

.markdown-content h1,
.markdown-content h2,
.markdown-content h3 {
  margin-top: 16px;
  margin-bottom: 8px;
  color: #1890ff;
  font-weight: 600;
}

.markdown-content h1 {
  font-size: 24px;
}
.markdown-content h2 {
  font-size: 20px;
}
.markdown-content h3 {
  font-size: 18px;
}

.markdown-content p {
  margin-bottom: 12px;
  margin-top: 0;
}
.markdown-content a {
  color: #1890ff;
  text-decoration: none;
}
.markdown-content a:hover {
  text-decoration: underline;
}

.markdown-content ul,
.markdown-content ol {
  margin-bottom: 12px;
  padding-left: 24px;
}

.markdown-content code {
  background-color: #f5f5f5;
  padding: 2px 4px;
  border-radius: 3px;
  font-family: "Consolas", "Monaco", "Courier New", monospace;
  font-size: 13px;
}

.markdown-content pre {
  background-color: #f5f5f5;
  padding: 12px;
  border-radius: 4px;
  overflow-x: auto;
  margin-bottom: 12px;
  border: 1px solid #ddd;
}

.markdown-content pre code {
  background-color: transparent;
  padding: 0;
}

/* 自定义滚动条样式 */
.left-section::-webkit-scrollbar,
.code-input::-webkit-scrollbar,
.evaluation-container::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

.left-section::-webkit-scrollbar-thumb,
.code-input::-webkit-scrollbar-thumb,
.evaluation-container::-webkit-scrollbar-thumb {
  background-color: #d9d9d9;
  border-radius: 3px;
}

.left-section::-webkit-scrollbar-track,
.code-input::-webkit-scrollbar-track,
.evaluation-container::-webkit-scrollbar-track {
  background-color: #f5f5f5;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .programming-question-container {
    flex-direction: column;
  }

  .left-section,
  .right-section {
    overflow: visible;
  }

  .code-content {
    height: 400px;
  }
}
</style>
