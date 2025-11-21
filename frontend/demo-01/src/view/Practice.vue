<template>
  <div class="practice-container">
    <h1 class="page-title">编程练习</h1>

    <!-- 编程问题文本卡片 -->
    <el-card class="exercise-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <span class="card-title">编程问题</span>
        </div>
      </template>
      <div class="problem-content">
        <p>{{ problem }}</p>
      </div>
    </el-card>

    <!-- 问题相关说明卡片 -->
    <el-card class="exercise-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <span class="card-title">问题说明</span>
        </div>
      </template>
      <div class="note-content">
        <p>输入: {{ input }}</p>
        <br />
        <p>输出: {{ output }}</p>
        <br />
        <p>注意: {{ note }}</p>
      </div>
    </el-card>

    <!-- 代码输入区域卡片 -->
    <el-card class="exercise-card code-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <span class="card-title">编写代码</span>
          <div class="code-actions">
            <el-select
              v-model="language"
              placeholder="选择语言"
              size="medium"
              style="width: 100px"
            >
              <el-option label="Python" value="python"></el-option>
              <el-option label="Java" value="java"></el-option>
              <el-option label="C++" value="cpp"></el-option>
            </el-select>
            <el-button
              type="primary"
              size="medium"
              style="margin-left: 10px"
              @click="submitCode"
              >提交</el-button
            >
            <el-button
              v-if="hasPreviousEvaluation"
              size="medium"
              style="margin-left: 10px"
              @click="showPreviousEvaluation"
              >查看上一次评测</el-button
            >
          </div>
        </div>
      </template>
      <div class="code-content">
        <textarea
          v-model="code"
          class="code-input"
          placeholder="在此输入代码..."
        ></textarea>
      </div>
    </el-card>

    <!-- 评估结果对话框 -->
    <el-dialog v-model="dialogVisible" width="80%" :before-close="handleClose">
      <div class="evaluation-container">
        <h3 class="dialog-inner-title">代码评测结果</h3>

        <div class="evaluation-content" v-loading="evaluationLoading">
          <div
            v-if="evaluationText && !evaluationLoading"
            class="evaluation-item markdown-content"
          >
            <div v-html="parseMarkdown(evaluationText)"></div>
          </div>

          <div v-else-if="!evaluationLoading" class="empty-state">
            <p>暂无评估结果</p>
          </div>
        </div>
      </div>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ElMessage } from "element-plus";
import { marked } from "marked";
import { nextTick, onMounted, ref } from "vue";

// 响应式数据
const language = ref("cpp");
const code = ref(`// 示例代码 - 计算斐波那契数列的第n项
#include <iostream>
using namespace std;

int fibonacci(int n) {
    if (n <= 0) {
        return 0;
    } else if (n == 1 || n == 2) {
        return 1;
    } else {
        return fibonacci(n-1) + fibonacci(n-2);
    }
}

int main() {
    int n;
    cin >> n;
    cout << fibonacci(n) << endl;
    return 0;
}`);
const problem = ref(
  "计算斐波那契数列的第n项, 斐波那契数列的定义为F(1)=1, F(2)=1, F(n)=F(n-1)+F(n-2)（n≥3）"
);
const input = ref("10"); // 严格规范内容，与程序输入输出一致
const output = ref("55"); // 严格规范内容，与程序输入输出一致
const note = ref("斐波那契数列的定义为F(1)=1, F(2)=1, F(n)=F(n-1)+F(n-2)（n≥3）");

const submission = ref({
  question: problem.value,
  codeLanguage: language.value,
  code: code.value,
  input: input.value,
  output: output.value,
});

// dialog相关状态
const dialogVisible = ref(false);
const evaluationText = ref(""); // 使用单个字符串来存储完整的评估文本
const evaluation = ref([]); // 保留用于存储完整响应的数组
const evaluationLoading = ref(false);
const hasPreviousEvaluation = ref(false);

// 初始化检查是否有上一次评估结果
onMounted(() => {
  checkPreviousEvaluation();
});

// 检查是否有上一次评估结果
function checkPreviousEvaluation() {
  const savedEvaluation = sessionStorage.getItem("previousEvaluation");
  hasPreviousEvaluation.value = !!savedEvaluation;
}

// 更新submission数据
function updateSubmission() {
  submission.value = {
    question: problem.value,
    codeLanguage: language.value,
    code: code.value,
    input: input.value,
    output: output.value,
  };
}

// 提交代码并处理flux响应
async function submitCode() {
  if (!code.value.trim()) {
    ElMessage.error("请输入代码");
    return;
  }

  // 更新提交数据
  updateSubmission();

  // 重置评估结果
  evaluationText.value = "";
  evaluation.value = [];
  evaluationLoading.value = true;
  dialogVisible.value = true;

  try {
    //发送请求并处理Flux响应
    const response = await fetch("http://localhost:80/api/analysis/evaluate", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(submission.value),
    });
    //为什么这个不行呢 -- axios不支持流式处理
    // const response = await evaluate(submission.value);

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    // 处理Flux响应流 - 参考Teaching.vue的实现方式
    const reader = response.body.getReader();
    const decoder = new TextDecoder();
    let accumulatedText = "";

    // 读取流数据并实现流式展示
    while (true) {
      const { done, value } = await reader.read();

      if (done) {
        break;
      }

      // 解码接收到的数据
      const chunk = decoder.decode(value, { stream: true });
      accumulatedText += chunk;

      // 使用nextTick确保UI能够正确更新，实现流式显示效果
      await nextTick();
      evaluationText.value = accumulatedText;
    }

    // 保存完整结果到sessionStorage - 不使用JSON.stringify，因为我们需要保存原始文本
    sessionStorage.setItem("previousEvaluation", accumulatedText);
    hasPreviousEvaluation.value = true;
  } catch (error) {
    console.error("Submit code error:", error);
    evaluation.value.push(`提交失败: ${error.message || "未知错误"}`);
  } finally {
    evaluationLoading.value = false;
  }
}

// 显示上一次评测结果 - 修复Markdown解析问题
function showPreviousEvaluation() {
  const savedEvaluation = sessionStorage.getItem("previousEvaluation");
  if (savedEvaluation) {
    try {
      // 首先尝试解析JSON（向后兼容）
      const parsedEvaluation = JSON.parse(savedEvaluation);
      evaluationText.value = parsedEvaluation;
    } catch (error) {
      // 如果解析失败，直接使用原始文本
      evaluationText.value = savedEvaluation;
    }
    evaluationLoading.value = false;
    dialogVisible.value = true;
  }
}

// 对话框关闭前的处理
function handleClose() {
  dialogVisible.value = false;
}

// Markdown解析函数
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
.practice-container {
  background-color: #f0f7ff;
  height: 100%;
  overflow-y: auto;
}

.page-title {
  text-align: center;
  color: #1890ff;
  margin-bottom: 20px;
  font-size: 24px;
}

.exercise-card {
  width: 90%;
  margin: auto;
  margin-bottom: 20px;
  border-radius: 8px;
  background-color: #ffffff;
  border: 1px solid #e6f7ff;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #1890ff;
}

.code-actions {
  display: flex;
  align-items: center;
}

.problem-content,
.note-content {
  padding: 10px 0;
  max-height: 200px;
  overflow-y: auto;
  line-height: 1.6;
  color: #333;
}

.code-card .code-content {
  position: relative;
  height: 400px;
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
  font-family: "Courier New", Courier, monospace;
  font-size: 14px;
  line-height: 1.5;
  color: #333;
  caret-color: #000;
  white-space: pre-wrap;
  word-wrap: break-word;
  letter-spacing: 0;
  tab-size: 4;
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

.evaluation-item pre {
  margin: 0;
  white-space: pre-wrap;
  word-wrap: break-word;
}

.empty-state {
  text-align: center;
  padding: 40px;
  color: #999;
}

.loading-row {
  min-height: 200px;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* Markdown 内容样式 */
.markdown-content {
  font-size: 14px;
  line-height: 1.6;
  color: #333;
}

.markdown-content h1,
.markdown-content h2,
.markdown-content h3,
.markdown-content h4,
.markdown-content h5,
.markdown-content h6 {
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

.markdown-content ul li,
.markdown-content ol li {
  margin-bottom: 4px;
}

.markdown-content strong {
  font-weight: 600;
}

.markdown-content em {
  font-style: italic;
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

.markdown-content blockquote {
  border-left: 4px solid #1890ff;
  padding-left: 16px;
  margin-left: 0;
  margin-right: 0;
  color: #666;
  font-style: italic;
}

/* 自定义滚动条样式 */
.problem-content::-webkit-scrollbar,
.note-content::-webkit-scrollbar,
.code-content pre::-webkit-scrollbar,
.code-input::-webkit-scrollbar,
.evaluation-container::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

.problem-content::-webkit-scrollbar-thumb,
.note-content::-webkit-scrollbar-thumb,
.code-content pre::-webkit-scrollbar-thumb,
.code-input::-webkit-scrollbar-thumb,
.evaluation-container::-webkit-scrollbar-thumb {
  background-color: #d9d9d9;
  border-radius: 3px;
}

.problem-content::-webkit-scrollbar-track,
.note-content::-webkit-scrollbar-track,
.code-content pre::-webkit-scrollbar-track,
.code-input::-webkit-scrollbar-track,
.evaluation-container::-webkit-scrollbar-track {
  background-color: #f5f5f5;
}
</style>
