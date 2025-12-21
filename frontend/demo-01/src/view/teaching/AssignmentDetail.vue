<template>
  <div class="assignment-detail">
    <!-- 返回按钮 -->
    <el-button class="back-button" @click="handleBack">
      <el-icon>
        <ArrowLeft />
      </el-icon>
      返回作业列表
    </el-button>

    <div class="assignment-content">
      <!-- 左侧练习信息 -->
      <div class="assignment-info-panel">
        <el-card class="info-card">
          <template #header>
            <div class="card-header">
              <h3>{{ assignment.title }}</h3>
              <el-tag :type="getStatusType(assignment.status)">
                {{ assignment.status }}
              </el-tag>
            </div>
          </template>

          <div class="assignment-details">
            <div class="detail-item">
              <span class="label">截止时间：</span>
              <span class="value">{{ assignment.deadline }}</span>
            </div>
            <div class="detail-item">
              <span class="label">分值：</span>
              <span class="value">{{ assignment.score }}分</span>
            </div>
            <div class="detail-item">
              <span class="label">难度：</span>
              <el-rate v-model="assignment.difficulty" :colors="['#99A9BF', '#F7BA2A', '#FF9900']" disabled show-score
                text-color="#ff9900" score-template="{value}">
              </el-rate>
            </div>
          </div>

          <div class="assignment-description">
            <h4>练习要求</h4>
            <div class="description-content" v-html="assignment.requirement"></div>
          </div>

          <div class="test-cases">
            <h4>测试用例</h4>
            <el-collapse v-model="activeTestCase">
              <el-collapse-item v-for="(testCase, index) in assignment.testCases" :key="index"
                :title="`测试用例 ${index + 1}`" :name="index">
                <div class="test-case-content">
                  <div class="test-input">
                    <span class="test-label">输入：</span>
                    <pre class="test-value">{{ testCase.input }}</pre>
                  </div>
                  <div class="test-output">
                    <span class="test-label">预期输出：</span>
                    <pre class="test-value">{{ testCase.output }}</pre>
                  </div>
                </div>
              </el-collapse-item>
            </el-collapse>
          </div>
        </el-card>

        <!-- 提交记录 -->
        <el-card class="submission-card">
          <template #header>
            <div class="card-header">
              <h3>提交记录</h3>
              <el-button type="primary" size="small" @click="showHistory = !showHistory">
                {{ showHistory ? '隐藏' : '查看' }}历史
              </el-button>
            </div>
          </template>

          <div v-if="showHistory" class="submission-history">
            <div v-for="(record, index) in submissionHistory" :key="index" class="submission-record">
              <div class="record-header">
                <span class="record-time">{{ record.submitTime }}</span>
                <el-tag :type="getSubmissionType(record.status)">
                  {{ record.status }}
                </el-tag>
                <span class="record-score" v-if="record.score">
                  得分：{{ record.score }}/{{ assignment.totalScore }}
                </span>
              </div>
              <div class="record-details" v-if="record.details">
                <p>通过测试：{{ record.passedTests }}/{{ record.totalTests }}</p>
                <el-progress :percentage="Math.round((record.passedTests / record.totalTests) * 100)"
                  :color="getProgressColor((record.passedTests / record.totalTests) * 100)"></el-progress>
              </div>
            </div>
          </div>
        </el-card>
      </div>

      <!-- 右侧代码编辑器 -->
      <div class="code-panel">
        <div class="code-header">
          <h4>代码编辑器</h4>
          <div class="code-actions">
            <el-select v-model="selectedLanguage" class="language-select">
              <el-option label="C++" value="cpp"></el-option>
              <el-option label="Python" value="python"></el-option>
              <el-option label="Java" value="java"></el-option>
            </el-select>
            <el-button type="success" @click="submitAssignment">
              <el-icon>
                <Upload />
              </el-icon>
              提交作业
            </el-button>
          </div>
        </div>

        <div class="code-editor-wrapper">
          <div ref="codeEditor" class="code-editor">
            <code ref="codeBlock" :class="`language-${selectedLanguage}`">{{ codeContent }}</code>
          </div>
          <textarea ref="codeInput" v-model="codeContent" class="code-input"
            :placeholder="`请输入${languageMap[selectedLanguage]}代码...`" @input="highlightCode" @keydown="handleKeyDown"
            @scroll="syncScroll"></textarea>
        </div>

        <div class="test-section">
          <el-card class="test-input-card">
            <template #header>
              <div class="card-header">
                <span class="header-title">测试输入</span>
                <el-button size="small" @click="runTest">
                  <el-icon>
                    <Flag />
                  </el-icon>
                  运行测试
                </el-button>
              </div>
            </template>
            <textarea v-model="testInput" class="test-input-area" placeholder="输入测试数据..."></textarea>
          </el-card>

          <el-card class="test-output-card">
            <template #header>
              <div class="card-header">
                <span class="header-title">运行结果</span>
              </div>
            </template>
            <pre class="test-output-display">{{ testOutput || '点击运行测试查看结果...' }}</pre>
          </el-card>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, watch, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import {
  ArrowLeft, Upload, Flag
} from '@element-plus/icons-vue';
import {
  ElCard, ElButton, ElIcon, ElSelect, ElOption, ElTag,
  ElRate, ElCollapse, ElCollapseItem, ElProgress, ElMessage
} from 'element-plus';
import hljs from 'highlight.js/lib/core';
import cpp from 'highlight.js/lib/languages/cpp';
import python from 'highlight.js/lib/languages/python';
import java from 'highlight.js/lib/languages/java';
import 'highlight.js/styles/atom-one-light.css';
import { usePracticeStore } from '@/store';

hljs.registerLanguage('cpp', cpp);
hljs.registerLanguage('python', python);
hljs.registerLanguage('java', java);

const route = useRoute();
const router = useRouter();

// 使用Pinia store
const practiceStore = usePracticeStore();

// 练习信息
const assignment = computed(() => practiceStore.currentAssignment || {});

// 代码相关
const selectedLanguage = ref('cpp');
const codeContent = ref('');
const testInput = ref('');
const testOutput = ref('');
const codeBlock = ref(null);
const codeEditor = ref(null);
const codeInput = ref(null);

// 其他状态
const showHistory = ref(false);
const activeTestCase = ref([]);
const submissionHistory = ref([]);

const languageMap = {
  cpp: 'C++',
  python: 'Python',
  java: 'Java'
};

// 获取练习详情
const fetchAssignmentDetail = () => {
  const assignmentId = route.params.assignmentId;
  console.log('Assignment ID:', assignmentId); // 调试日志

  // 从store获取练习数据
  const assignmentData = practiceStore.getAssignmentById(assignmentId);

  if (assignmentData) {
    practiceStore.setCurrentAssignment(assignmentData);
  }

  // 获取提交历史
  submissionHistory.value = practiceStore.getSubmissionHistory(assignmentId);

  // 初始化代码
  codeContent.value = practiceStore.getCodeTemplate(assignmentId, selectedLanguage.value);
  nextTick(() => highlightCode());
};


// 代码高亮
const highlightCode = () => {
  if (codeBlock.value && codeContent.value) {
    codeBlock.value.className = `language-${selectedLanguage.value}`;
    // 保持换行符和空白字符
    codeBlock.value.textContent = codeContent.value;
    delete codeBlock.value.dataset.highlighted;
    hljs.highlightElement(codeBlock.value);

    // 同步滚动位置
    if (codeInput.value && codeEditor.value) {
      codeEditor.value.scrollTop = codeInput.value.scrollTop;
      codeEditor.value.scrollLeft = codeInput.value.scrollLeft;
    }
  }
};

// 处理键盘事件
const handleKeyDown = (event) => {
  if (event.key === 'Tab') {
    event.preventDefault();
    const textarea = event.target;
    const start = textarea.selectionStart;
    const end = textarea.selectionEnd;
    const spaces = '    ';
    codeContent.value = codeContent.value.substring(0, start) + spaces + codeContent.value.substring(end);
    nextTick(() => textarea.selectionStart = textarea.selectionEnd = start + spaces.length);
  }

  const brackets = { '(': ')', '[': ']', '{': '}' };
  if (brackets[event.key]) {
    event.preventDefault();
    const textarea = event.target;
    const start = textarea.selectionStart;
    const end = textarea.selectionEnd;
    const open = event.key;
    const close = brackets[open];
    codeContent.value = codeContent.value.substring(0, start) + open + close + codeContent.value.substring(end);
    nextTick(() => textarea.selectionStart = textarea.selectionEnd = start + 1);
  }
};

// 运行测试
const runTest = () => {
  // 模拟运行测试
  setTimeout(() => {
    testOutput.value = `正在编译...\n编译成功！\n\n运行结果：\n输入：${testInput.value || '无输入'}\n输出：1 1 3 4 5\n\n测试结果：通过`;
    ElMessage.success('测试运行成功');
  }, 1000);
};

// 提交练习
const submitAssignment = async () => {
  const assignmentId = route.params.assignmentId;
  try {
    ElMessage.loading('正在提交...');

    // 使用store的提交方法
    await practiceStore.submitAssignment(
      assignmentId,
      codeContent.value,
      selectedLanguage.value
    );

    ElMessage.success('练习提交成功！');

    // 刷新提交历史
    submissionHistory.value = practiceStore.getSubmissionHistory(assignmentId);
  } catch (error) {
    ElMessage.error('提交失败，请重试');
  }
};

// 返回练习列表
const handleBack = () => {
  // 确保使用正确的courseId参数
  const courseId = route.params.courseId || route.params.id;
  router.push(`/teaching/course/${courseId}/assignment`);
};

// 获取状态类型
const getStatusType = (status) => {
  const types = {
    '未开始': 'info',
    '未提交': 'warning',
    '已提交': 'success',
    '部分正确': 'warning',
    '全部正确': 'success'
  };
  return types[status] || 'info';
};

// 获取提交状态类型
const getSubmissionType = (status) => {
  const types = {
    '评判中': 'warning',
    '部分正确': 'warning',
    '全部正确': 'success',
    '编译错误': 'danger',
    '运行错误': 'danger'
  };
  return types[status] || 'info';
};

// 获取进度条颜色
const getProgressColor = (percentage) => {
  if (percentage === 100) return '#67c23a';
  if (percentage >= 60) return '#e6a23c';
  return '#f56c6c';
};

// 监听语言变化
watch(selectedLanguage, (newLang) => {
  const assignmentId = route.params.assignmentId;
  codeContent.value = practiceStore.getCodeTemplate(assignmentId, newLang);
  nextTick(() => highlightCode());
});

// 监听代码变化
watch(codeContent, () => {
  nextTick(() => highlightCode());
});

// 同步滚动
const syncScroll = (event) => {
  const textarea = event.target;
  if (codeEditor.value) {
    codeEditor.value.scrollTop = textarea.scrollTop;
    codeEditor.value.scrollLeft = textarea.scrollLeft;
  }
};

onMounted(() => {
  console.log('AssignmentDetail mounted, route params:', route.params);
  fetchAssignmentDetail();
});
</script>

<style scoped>
.assignment-detail {
  padding: 20px;
  background: #fff6fb;
  min-height: 100vh;
  font-family: "Inter", Arial, sans-serif;
}

.back-button {
  margin-bottom: 20px;
  background: linear-gradient(90deg, #ff7ab1, #d63384);
  border-color: #d63384;
  color: white;
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  border-radius: 12px;
  font-weight: 600;
  transition: all 0.2s ease;
}

.back-button:hover {
  background: linear-gradient(90deg, #ff8fc0, #e04577);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(214, 51, 132, 0.2);
}

.assignment-content {
  display: flex;
  gap: 20px;
  height: calc(100vh - 100px);
}

/* 左侧信息面板 */
.assignment-info-panel {
  flex: 0 0 450px;
  display: flex;
  flex-direction: column;
  gap: 20px;
  overflow-y: auto;
}

.info-card,
.submission-card {
  background: rgba(255, 255, 255, 0.9);
  border-radius: 12px;
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.04);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h3 {
  margin: 0;
  color: #d63384;
  font-size: 18px;
  font-weight: 700;
}

.assignment-details {
  margin-bottom: 20px;
}

.detail-item {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
}

.detail-item .label {
  color: #9b7a88;
  font-size: 14px;
  min-width: 80px;
}

.detail-item .value {
  color: #7b2a53;
  font-size: 14px;
  font-weight: 500;
}

.assignment-description h4,
.test-cases h4 {
  color: #7b2a53;
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 12px;
}

.description-content {
  color: #6b3b52;
  font-size: 14px;
  line-height: 1.6;
}

.test-case-content {
  padding: 12px;
  background: #f8f9fa;
  border-radius: 6px;
}

.test-input,
.test-output {
  margin-bottom: 10px;
}

.test-label {
  color: #9b7a88;
  font-size: 13px;
  font-weight: 500;
}

.test-value {
  margin: 4px 0 0 0;
  padding: 8px;
  background: white;
  border-radius: 4px;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 13px;
  color: #7b2a53;
}

/* 提交记录 */
.submission-record {
  padding: 12px;
  background: white;
  border-radius: 8px;
  margin-bottom: 12px;
  border: 1px solid #ffd6e7;
}

.record-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.record-time {
  color: #9b7a88;
  font-size: 13px;
}

.record-score {
  margin-left: auto;
  color: #7b2a53;
  font-weight: 600;
}

/* 右侧代码面板 */
.code-panel {
  flex: 1;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 12px;
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.04);
  padding: 20px;
  display: flex;
  flex-direction: column;
}

.code-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.code-header h4 {
  margin: 0;
  color: #d63384;
  font-size: 18px;
  font-weight: 700;
}

.code-actions {
  display: flex;
  gap: 12px;
  align-items: center;
}

.language-select {
  width: 140px;
}

.code-editor-wrapper {
  position: relative;
  height: 350px;
  margin-bottom: 20px;
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid #ffd6e7;
  background: #fff;
}

.code-editor {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  margin: 0;
  padding: 16px;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 14px;
  line-height: 1.5;
  background: #f8f9fa;
  color: #333;
  overflow: hidden;
  pointer-events: none;
  z-index: 1;
  white-space: pre;
  word-wrap: normal;
  box-sizing: border-box;
}

.code-editor code {
  background: transparent !important;
  padding: 0 !important;
  font-family: inherit !important;
  display: block;
  white-space: pre;
  word-wrap: normal;
}

.code-input {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  margin: 0;
  padding: 16px;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 14px;
  line-height: 1.5;
  background: transparent;
  color: transparent;
  caret-color: #333;
  border: none;
  resize: none;
  outline: none;
  z-index: 2;
  overflow: scroll;
  white-space: pre;
  word-wrap: normal;
  box-sizing: border-box;
  scrollbar-width: thin;
  scrollbar-color: #ccc transparent;
}

.code-input::-webkit-scrollbar {
  width: 12px;
  height: 12px;
}

.code-input::-webkit-scrollbar-track {
  background: transparent;
}

.code-input::-webkit-scrollbar-thumb {
  background-color: #ccc;
  border-radius: 6px;
  border: 3px solid transparent;
  background-clip: content-box;
}

.code-input::-webkit-scrollbar-thumb:hover {
  background-color: #999;
  background-clip: content-box;
}

/* 测试部分 */
.test-section {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  flex: 1;
}

.test-input-area {
  width: 100%;
  min-height: 120px;
  padding: 12px;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 14px;
  line-height: 1.5;
  border-radius: 6px;
  border: 1px solid #ffd6e7;
  resize: vertical;
  background: white;
}

.test-output-display {
  width: 100%;
  min-height: 120px;
  padding: 12px;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 14px;
  line-height: 1.5;
  border-radius: 6px;
  background: #f8f9fa;
  margin: 0;
  white-space: pre-wrap;
  word-wrap: break-word;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .assignment-info-panel {
    flex: 0 0 400px;
  }
}

@media (max-width: 768px) {
  .assignment-content {
    flex-direction: column;
    height: auto;
  }

  .assignment-info-panel {
    flex: none;
    width: 100%;
  }

  .test-section {
    grid-template-columns: 1fr;
  }
}
</style>