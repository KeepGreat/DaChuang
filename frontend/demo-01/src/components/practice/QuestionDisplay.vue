<template>
  <div class="question-display">
    <!-- 当没有匹配的题目时显示 -->
    <div v-if="!question" class="no-question">
      <el-empty description="当前题型下暂无题目"></el-empty>
    </div>
    
    <template v-else>
      <!-- 编程题使用专门的左右并排组件 -->
      <ProgrammingQuestion
        v-if="question.type === 3"
        ref="programmingQuestionRef"
        :question="question"
        :show-correctness="showCorrectness"
        :user-answer="currentUserAnswer"
        @answer-submitted="handleProgrammingAnswerSubmitted"
        @answer-changed="handleAnswerChanged"
      />
      
      <!-- 非编程题 -->
      <template v-else>
        <!-- 单题模式：只显示当前题目 -->
        <div v-if="singleQuestionMode" class="single-question-wrapper">
          <!-- 所有题型共用的题目信息 -->
          <div class="question-content-wrapper">
            <div class="question-header">
              <div class="question-info">
                <span class="question-type">{{ getQuestionTypeText(question.type) }}</span>
                <span class="question-number">第 {{ questionNumber }} 题</span>
              </div>
              <div class="question-status" v-if="question.status">
                <el-tag :type="getStatusType(question.status)">{{ getStatusText(question.status) }}</el-tag>
              </div>
            </div>
          </div>
          
          <div class="question-content-wrapper">
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
          </div>
        </div>
        
        <!-- 多题模式：显示所有同类型题目 -->
        <div v-else class="all-questions-wrapper">
          <div 
            v-for="(q, index) in sameTypeQuestions" 
            :key="q.id"
            class="single-question-container"
          >
            <div class="question-header">
              <div class="question-info">
                <span class="question-type">{{ getQuestionTypeText(q.type) }}</span>
                <span class="question-number">第 {{ index + 1 }} 题</span>
              </div>
              <div class="question-status" v-if="q.status">
                <el-tag :type="getStatusType(q.status)">{{ getStatusText(q.status) }}</el-tag>
              </div>
            </div>
            
            <div class="question-content">
              <h3 class="question-title">{{ q.title }}</h3>
              
              <!-- 选择题选项区域 - 判断题、单选题和多选题共用 -->
              <div v-if="hasOptions(q)" class="question-options">
                <div 
                  v-for="(option, optIndex) in q.options" 
                  :key="optIndex"
                  class="option-item"
                  :class="{ 
                    'selected': isSelected(option.value, q.id),
                    'correct': isCorrect(option.value, q) && (showCorrectness || q.status === 'correct' || q.status === 'incorrect'),
                    'incorrect': isIncorrect(option.value, q) && (showCorrectness || q.status === 'incorrect'),
                    'missed': isMissed(option.value, q) && (showCorrectness || q.status === 'incorrect')
                  }"
                  @click="toggleOption(option.value, q.id)"
                >
                  <div class="option-label">{{ option.label }}</div>
                  <div class="option-content">{{ option.text }}</div>
                </div>
              </div>
            </div>
            
            <!-- 简答题答题区域 -->
            <div v-if="q.type === 2" class="answer-section">
              <el-input
                v-model="userAnswers[q.id]"
                type="textarea"
                :rows="4"
                placeholder="请输入您的答案..."
                :disabled="showCorrectness"
                class="short-answer-input"
                @input="handleAnswerChanged(q.id, userAnswers[q.id])"
              ></el-input>
              
              <!-- 正确答案展示 -->
              <div v-if="showCorrectness" class="correct-answer">
                <h4>参考答案：</h4>
                <div class="answer-content" v-html="q.answer"></div>
              </div>
            </div>
          </div>
        </div>
      </template>
      
      <!-- 答题操作按钮 - 所有题型共用 -->
      <div class="question-actions">
        <!-- 上一题/下一题按钮仅在单题模式下显示 -->
        <el-button v-if="singleQuestionMode" @click="previousQuestion">上一题</el-button>
        <div class="right-buttons">
          <el-button type="info" @click="toggleCorrectness" :disabled="false">
            {{ showCorrectness ? '隐藏答案' : '查看答案' }}
          </el-button>
          <el-button 
            type="primary" 
            @click="submitAnswer" 
            :disabled="showCorrectness"
          >
            提交答案
          </el-button>
          <!-- 下一题按钮仅在单题模式下显示 -->
          <el-button v-if="singleQuestionMode" type="success" @click="nextQuestion">下一题</el-button>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue';
import { ElMessage, ElEmpty } from 'element-plus';
import ProgrammingQuestion from './ProgrammingQuestion.vue';

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
  },
  userAnswers: {
    type: Object,
    default: () => ({})
  },
  singleQuestionMode: {
    type: Boolean,
    default: false
  },
  sameTypeQuestions: {
    type: Array,
    default: () => []
  }
});
// Emits 定义
const emit = defineEmits(['set-show-correctness', 'answer-submitted', 'answer-changed', 'previous', 'next']);

// 用户答案 - 单题模式使用单个数组，多题模式使用对象存储
const userAnswer = ref([]);
const userAnswers = ref({});

// 编程题组件引用
const programmingQuestionRef = ref(null);

// 计算属性：根据题目类型返回合适的用户答案格式
const currentUserAnswer = computed(() => {
  if (props.question && props.question.type === 3) {
    // 编程题需要字符串格式
    return Array.isArray(props.userAnswer) ? props.userAnswer.join('') : props.userAnswer;
  }
  // 其他题型保持原格式
  return props.userAnswer;
});

// 当props.question变化时，更新本地答案
watch(() => props.question, (newQuestion, oldQuestion) => {
  if (newQuestion) {
    // 只有当题目变化时，才使用传入的userAnswer初始化本地答案
    const initialAnswer = Array.isArray(props.userAnswer) ? [...props.userAnswer] : [props.userAnswer];
    userAnswer.value = initialAnswer;
  }
}, { immediate: true, deep: true });

// 监听父组件传递的完整用户答案变化，同步到本地userAnswers对象
watch(() => props.userAnswers, (newUserAnswers) => {
  if (newUserAnswers && !props.singleQuestionMode) {
    // 将父组件传递的完整用户答案同步到本地userAnswers对象
    // 这样在切换题型时，本地就能获取到父组件中已保存的所有题目的答案
    userAnswers.value = { ...newUserAnswers };
    
    // 确保所有同类型题目的键都存在于userAnswers对象中
    if (props.sameTypeQuestions) {
      props.sameTypeQuestions.forEach(q => {
        if (userAnswers.value[q.id] === undefined) {
          userAnswers.value[q.id] = [];
        }
      });
    }
  }
}, { immediate: true, deep: true });

// 当props.sameTypeQuestions变化时，确保本地userAnswers对象包含所有同类型题目的键
watch(() => props.sameTypeQuestions, (newQuestions) => {
  if (newQuestions && !props.singleQuestionMode) {
    // 确保所有同类型题目的键都存在于userAnswers对象中
    newQuestions.forEach(q => {
      if (userAnswers.value[q.id] === undefined) {
        userAnswers.value[q.id] = [];
      }
    });
  }
}, { immediate: true, deep: true });

// 监听本地答案变化，触发事件
watch(userAnswer, (newAnswer) => {
  if (props.question) {
    emit('answer-changed', props.question.id, newAnswer);
  }
}, { deep: true });

// 监听userAnswers变化，触发事件
watch(userAnswers, (newAnswers) => {
  // 多题模式下，当任何题目答案变化时，触发事件
  // 这里可以根据需要调整触发逻辑
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
const hasOptions = (q = props.question) => {
  return [0, 1].includes(q.type);
};

// 判断是否为单选类型
const isSingleType = (q = props.question) => {
  // 判断题（type === 0）和单选题（type === 1且答案长度为1）都视为单选类型
  return q.type === 0;
};

// 判断选项是否被选中
const isSelected = (value, questionId = props.question?.id) => {
  if (props.singleQuestionMode) {
    return userAnswer.value.includes(value);
  } else {
    return userAnswers.value[questionId]?.includes(value) || false;
  }
};

// 判断选项是否正确（根据题目答案）
const isCorrect = (value, q = props.question) => {
  const answer = Array.isArray(q.answer) ? q.answer : [q.answer];
  return answer.includes(value);
};

// 判断选项是否错误（用户选择了但不是正确答案）
const isIncorrect = (value, q = props.question, questionId) => {
  const actualQuestionId = questionId ?? q.id;
  return isSelected(value, actualQuestionId) && !isCorrect(value, q);
};

// 判断选项是否被遗漏（只用于多选题，正确答案但用户没选）
const isMissed = (value, q = props.question, questionId) => {
  const actualQuestionId = questionId ?? q.id;
  return !isSingleType(q) && !isSelected(value, actualQuestionId) && isCorrect(value, q);
};

// 切换选项
const toggleOption = (value, questionId = props.question?.id) => {
  if (props.showCorrectness) return;
  
  const q = props.singleQuestionMode ? props.question : props.sameTypeQuestions.find(item => item.id === questionId);
  if (!q) return;
  
  if (props.singleQuestionMode) {
    if (isSingleType(q)) {
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
  } else {
    // 多题模式
    if (!userAnswers.value[questionId]) {
      userAnswers.value[questionId] = [];
    }
    
    if (isSingleType(q)) {
      // 单选题和判断题：只能选择一个选项，替换当前选择
      userAnswers.value[questionId] = [value];
    } else {
      // 多选题：可选择多个选项，切换当前选项的选中状态
      const index = userAnswers.value[questionId].indexOf(value);
      if (index === -1) {
        userAnswers.value[questionId].push(value);
      } else {
        userAnswers.value[questionId].splice(index, 1);
      }
    }
    
    // 触发答案变化事件
    emit('answer-changed', questionId, userAnswers.value[questionId]);
  }
};

// 提交答案
const submitAnswer = () => {
  if (props.question.type === 3) {
    // 编程题：调用子组件的提交方法
    if (programmingQuestionRef.value) {
      programmingQuestionRef.value.submitCode();
    }
  } else if (props.singleQuestionMode) {
    // 单题模式：提交当前题目
    // 检查答案是否为空
    let isEmpty = false;
    if (Array.isArray(userAnswer.value)) {
      // 选择题、判断题：检查数组是否为空
      isEmpty = userAnswer.value.length === 0;
    } else {
      // 简答题：检查字符串是否为空
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
    
    // 答案不为空时，发出提交事件
    emit('answer-submitted', {
      questionId: props.question.id,
      answer: userAnswer.value,
      isEmpty: false
    });
    
    ElMessage({
      message: '答案已提交！',
      type: 'success'
    });
  } else {
    // 多题模式：提交所有同类型题目
    let hasEmptyAnswers = false;
    const submittedAnswers = [];
    
    // 遍历所有题目，检查答案是否为空
    props.sameTypeQuestions.forEach(q => {
      const answer = userAnswers.value[q.id];
      let isEmpty = false;
      
      if (Array.isArray(answer)) {
        // 选择题、判断题：检查数组是否为空
        isEmpty = answer.length === 0;
      } else {
        // 简答题：检查字符串是否为空
        isEmpty = !answer || answer.trim() === '';
      }
      
      if (isEmpty) {
        hasEmptyAnswers = true;
      }
      
      submittedAnswers.push({
        questionId: q.id,
        answer: answer,
        isEmpty: isEmpty
      });
    });
    
    if (hasEmptyAnswers) {
      // 有未填写的答案时，发出警告但仍提交所有答案
      ElMessage({
        message: '部分题目未填写答案，已提交已完成的题目！',
        type: 'warning'
      });
    } else {
      ElMessage({
        message: '所有答案已提交！',
        type: 'success'
      });
    }
    
    // 提交所有答案
    submittedAnswers.forEach(submittedAnswer => {
      emit('answer-submitted', submittedAnswer);
    });
  }
};

// 处理编程题答案提交
const handleProgrammingAnswerSubmitted = (result) => {
  emit('answer-submitted', result);
};

// 处理编程题答案变化
const handleAnswerChanged = (questionId, answer) => {
  emit('answer-changed', questionId, answer);
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

/* 单题模式样式 */
.single-question-wrapper {
  display: flex;
  flex-direction: column;
}

/* 所有题目展示样式 */
.all-questions-wrapper {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.single-question-container {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  margin-bottom: 20px;
  border: 1px solid #ebeef5;
}

.single-question-container .question-header {
  margin-bottom: 16px;
}

.single-question-container .question-content {
  margin-bottom: 16px;
}

.single-question-container .answer-section {
  margin-bottom: 0;
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
  
  .single-question-container {
    padding: 12px;
    margin-bottom: 12px;
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
