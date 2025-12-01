<template>
  <div class="question-display">
    <!-- 当没有匹配的题目时显示 -->
    <div v-if="!question" class="no-question">
      <el-empty description="当前题型下暂无题目"></el-empty>
    </div>
    
    <!-- 显示问题内容 -->
    <div v-else class="question-content-wrapper">
      <div class="question-header">
        <div class="question-info">
          <span class="question-type">{{ getQuestionTypeText(question.type) }}</span>
          <span class="question-number">第 {{ questionNumber }} 题</span>
        </div>
        <div class="question-status" v-if="question.status">
          <el-tag :type="getStatusType(question.status)">{{ getStatusText(question.status) }}</el-tag>
        </div>
      </div>
      
      <div class="question-content">
        <h3 class="question-title">{{ question.title }}</h3>
        
        <!-- 选择题选项区域 - 判断题、单选题和多选题共用 -->
        <div v-if="hasOptions()" class="question-options">
          <div 
            v-for="(option, index) in question.options" 
            :key="index"
            class="option-item"
            :class="{ 
              'selected': isSelected(option.value),
              'correct': isCorrect(option.value) && (showCorrectness || question.status === 'correct' || question.status === 'incorrect'),
              'incorrect': isIncorrect(option.value) && (showCorrectness || question.status === 'incorrect'),
              'missed': isMissed(option.value) && (showCorrectness || question.status === 'incorrect')
            }"
            @click="toggleOption(option.value)"
          >
            <div class="option-label">{{ option.label }}</div>
            <div class="option-content">{{ option.text }}</div>
          </div>
        </div>
      </div>
      
      <!-- 简答题答题区域 -->
      <div v-if="question.type === 2" class="answer-section">
        <el-input
          v-model="userAnswer"
          type="textarea"
          :rows="6"
          placeholder="请输入您的答案..."
          :disabled="showCorrectness"
          class="short-answer-input"
        ></el-input>
        
        <!-- 正确答案展示 -->
        <div v-if="showCorrectness" class="correct-answer">
          <h4>参考答案：</h4>
          <div class="answer-content" v-html="question.answer"></div>
        </div>
      </div>
      
      <!-- 编程题答题区域 -->
      <div v-if="question.type === 3" class="answer-section">
        <div class="programming-header">
          <div class="language-selector">
            <el-select v-model="selectedLanguage" placeholder="选择编程语言">
              <el-option 
                v-for="lang in supportedLanguages" 
                :key="lang.value" 
                :label="lang.label" 
                :value="lang.value"
              ></el-option>
            </el-select>
          </div>
          <div class="code-actions">
            <el-button type="primary" size="small" @click="runCode">运行代码</el-button>
            <el-button size="small" @click="resetCode">重置</el-button>
          </div>
        </div>
        
        <!-- 代码编辑区域 -->
        <div class="code-editor">
          <textarea
            v-model="userAnswer"
            placeholder="请在此编写代码..."
            class="code-input"
            :disabled="showCorrectness"
          ></textarea>
        </div>
        
        <!-- 运行结果 -->
        <div v-if="codeResult" class="code-result">
          <h4>运行结果：</h4>
          <pre>{{ codeResult }}</pre>
        </div>
        
        <!-- 正确答案展示 -->
        <div v-if="showCorrectness" class="correct-answer">
          <h4>参考答案：</h4>
          <div class="code-editor">
            <pre>{{ question.answer }}</pre>
          </div>
        </div>
      </div>
      
      <!-- 答题操作按钮 -->
      <div class="question-actions">
        <el-button @click="previousQuestion">上一题</el-button>
        <div class="right-buttons">
          <el-button type="info" @click="toggleCorrectness" :disabled="!question.status">
            {{ showCorrectness ? '隐藏答案' : '查看答案' }}
          </el-button>
          <el-button type="primary" @click="submitAnswer" :disabled="showCorrectness">提交答案</el-button>
          <el-button type="success" @click="nextQuestion">下一题</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue';
import { ElMessage, ElEmpty } from 'element-plus';

// Props 定义
const props = defineProps({
  question: {
    type: Object,
    required: true,
    default: () => ({
      id: '',
      type: 'choice', // judgment, single_choice, multiple_choice, short_answer, programming
      title: '',
      content: '',
      options: [],
      answer: [],
      status: null // null, answered, correct, incorrect
    })
  },
  questionNumber: {
    type: Number,
    default: 1
  },
  showCorrectness: {
    type: Boolean,
    default: false
  },
  userAnswer: {
    type: [Number, Array],
    default: () => []
  }
});
// Emits 定义
const emit = defineEmits(['set-show-correctness', 'answer-submitted', 'answer-changed', 'previous', 'next']);

// 用户答案 - 统一使用数组存储
const userAnswer = ref([]);

// 编程语言选择
const selectedLanguage = ref('javascript');
const supportedLanguages = ref([
  { label: 'JavaScript', value: 'javascript' },
  { label: 'Python', value: 'python' },
  { label: 'Java', value: 'java' },
  { label: 'C++', value: 'cpp' }
]);

// 代码运行结果
const codeResult = ref('');

// 当props.question变化时，更新本地答案
watch(() => props.question, (newQuestion, oldQuestion) => {
  if (newQuestion) {
    // 只有当题目变化时，才使用传入的userAnswer初始化本地答案
    const initialAnswer = Array.isArray(props.userAnswer) ? [...props.userAnswer] : [props.userAnswer];
    userAnswer.value = initialAnswer;
  }
  codeResult.value = '';
}, { immediate: true, deep: true });

// 监听本地答案变化，触发事件
watch(userAnswer, (newAnswer) => {
  if (props.question) {
    emit('answer-changed', props.question.id, newAnswer);
  }
}, { deep: true });

// 获取问题类型文本
const getQuestionTypeText = (type) => {
  const typeMap = {
    0: '判断题',
    1: '选择题',
    2: '简答题',
    3: '编程题'
  };
  return typeMap[type] || '未知题型';
};

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    answered: '已作答',
    correct: '正确',
    incorrect: '错误'
  };
  return statusMap[status] || '';
};

// 获取状态类型
const getStatusType = (status) => {
  const typeMap = {
    answered: 'info',
    correct: 'success',
    incorrect: 'danger'
  };
  return typeMap[status] || 'info';
};

// 判断是否有选项
const hasOptions = () => {
  return [0, 1].includes(props.question.type);
};

// 判断是否为单选类型
const isSingleType = () => {
  return  props.question.type === 1 && props.question.answer.length === 1;
};

// 判断选项是否被选中
const isSelected = (value) => {
  return userAnswer.value.includes(value);
};

// 判断选项是否正确（根据题目答案）
const isCorrect = (value) => {
  const answer = Array.isArray(props.question.answer) ? props.question.answer : [props.question.answer];
  return answer.includes(value);
};

// 判断选项是否错误（用户选择了但不是正确答案）
const isIncorrect = (value) => {
  return userAnswer.value.includes(value) && !isCorrect(value);
};

// 判断选项是否被遗漏（只用于多选题，正确答案但用户没选）
const isMissed = (value) => {
  return !isSingleType() && !userAnswer.value.includes(value) && isCorrect(value);
};

// 切换选项
const toggleOption = (value) => {
  if (props.showCorrectness) return;
  
  if (isSingleType()) {
    // 单选题和判断题：只能选择一个选项，替换当前选择
    userAnswer.value = [value];
  } else {
    // 多选题：可选择多个选项，切换当前选项的选中状态
    const index = userAnswer.value.indexOf(value);
    if (index === -1) {
      userAnswer.value.push(value);
    } else {
      userAnswer.value.splice(index, 1);
    }
  }
};

// 提交答案
const submitAnswer = () => {
  // 检查答案是否为空
  let isEmpty = false;
  if (Array.isArray(userAnswer.value)) {
    // 选择题、判断题：检查数组是否为空
    isEmpty = userAnswer.value.length === 0;
  } else {
    // 简答题、编程题：检查字符串是否为空
    isEmpty = !userAnswer.value || userAnswer.value.trim() === '';
  }
  
  if (isEmpty) {
    // 答案为空时，发出提交事件但标记为空答案
    emit('answer-submitted', {
      questionId: props.question.id,
      answer: userAnswer.value,
      isEmpty: true
    });
    
    ElMessage({
      message: '请先填写答案！',
      type: 'warning'
    });
    return;
  }
  
  // 标准化答案格式，确保都是数组
  const userAnswerArray = Array.isArray(userAnswer.value) ? userAnswer.value : [userAnswer.value];
  
  // 发出答案提交事件，不包含正确性判断
  emit('answer-submitted', {
    questionId: props.question.id,
    answer: userAnswer.value,
    isEmpty: false
  });
  
  ElMessage({
    message: '答案已提交！',
    type: 'success'
  });
};

// 运行代码（模拟）
const runCode = () => {
  if (!userAnswer.value.trim()) {
    ElMessage.warning('请先编写代码！');
    return;
  }
  
  // 模拟代码运行结果
  codeResult.value = `[${new Date().toLocaleTimeString()}] 运行成功！\n输出：Hello, World!\n执行时间：12ms`;
  ElMessage.success('代码运行成功！');
};

// 重置代码
const resetCode = () => {
  userAnswer.value = '';
  codeResult.value = '';
};

// 上一题
const previousQuestion = () => {
  emit('previous');
};

// 切换显示正确性
const toggleCorrectness = () => {
  emit('set-show-correctness');
};

// 下一题
const nextQuestion = () => {
  emit('next');
};
</script>

<style scoped>
.question-display {
  display: flex;
  flex-direction: column;
  background: #fff;
  border-radius: 8px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  overflow: hidden;
}

.question-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 1px solid #ebeef5;
}

.question-info {
  display: flex;
  gap: 16px;
  align-items: center;
}

.question-type {
  background: #ecf5ff;
  color: #409eff;
  padding: 4px 12px;
  border-radius: 16px;
  font-size: 12px;
  font-weight: 500;
}

.question-number {
  font-size: 14px;
  color: #606266;
}

.question-content {
  display: flex;
  flex-direction: column;
  margin-bottom: 24px;
}

.question-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 12px;
}

.question-body {
  font-size: 16px;
  color: #606266;
  line-height: 1.6;
  margin-top: 24px;
}

/* 选项样式 */
.question-options {
  display: flex;
  flex-direction: column;
  gap: 12px;
  width: 100%;
}

.option-item {
  display: flex;
  align-items: flex-start;
  padding: 16px;
  border: 1px solid #dcdfe6;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s ease;
  background: #fff;
}

.option-item:hover {
  border-color: #409eff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.15);
}

.option-item.selected {
  border-color: #409eff;
  background: #ecf5ff;
}

.option-item.correct {
  border-color: #67c23a;
  background: #f0f9eb;
}

.option-item.incorrect {
  border-color: #f56c6c;
  background: #fef0f0;
}

.option-item.missed {
  border-color: #e6a23c;
  background: #fdf6ec;
  opacity: 0.8;
}

.option-label {
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  border-radius: 50%;
  font-weight: 600;
  color: #606266;
  margin-right: 12px;
  flex-shrink: 0;
}

.option-item.selected .option-label {
  background: #409eff;
  color: #fff;
}

.option-item.correct .option-label {
  background: #67c23a;
  color: #fff;
}

.option-item.incorrect .option-label {
  background: #f56c6c;
  color: #fff;
}

.option-content {
  flex: 1;
  font-size: 15px;
  color: #606266;
  line-height: 1.5;
}

/* 简答题样式 */
.answer-section {
  margin-bottom: 24px;
}

.short-answer-input {
  width: 100%;
  font-size: 14px;
  line-height: 1.5;
}

/* 编程题样式 */
.programming-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.language-selector {
  width: 160px;
}

.code-actions {
  display: flex;
  gap: 8px;
}

.code-editor {
  margin-bottom: 16px;
  border: 1px solid #dcdfe6;
  border-radius: 6px;
  overflow: hidden;
  background: #fafafa;
}

.code-input {
  width: 100%;
  min-height: 200px;
  padding: 16px;
  border: none;
  outline: none;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 14px;
  line-height: 1.6;
  background: #fafafa;
  resize: vertical;
}

.code-editor pre {
  margin: 0;
  padding: 16px;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 14px;
  line-height: 1.6;
  background: #fafafa;
  overflow-x: auto;
}

.code-result {
  margin-bottom: 16px;
  padding: 12px;
  background: #f0f9eb;
  border: 1px solid #e1f3d8;
  border-radius: 6px;
}

.code-result pre {
  margin: 0;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 14px;
  line-height: 1.5;
  color: #67c23a;
}

/* 正确答案展示 */
.correct-answer {
  margin-top: 20px;
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

.answer-content {
  font-size: 15px;
  color: #606266;
  line-height: 1.6;
}

/* 内容包装器 */
.question-content-wrapper {
  display: flex;
  flex-direction: column;
}

/* 无题目提示 */
.no-question {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 300px;
}

/* 操作按钮 */
.question-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid #ebeef5;
}

/* 右侧按钮容器 */
.right-buttons {
  display: flex;
  gap: 12px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .question-display {
    padding: 16px;
  }
  
  .question-title {
    font-size: 16px;
  }
  
  .option-item {
    padding: 12px;
  }
  
  .programming-header {
    flex-direction: column;
    align-items: stretch;
    gap: 12px;
  }
  
  .language-selector {
    width: 100%;
  }
  
  .code-actions {
    justify-content: flex-end;
  }
}
</style>
