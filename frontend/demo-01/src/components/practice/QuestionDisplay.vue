<template>
  <div class="question-display">
    <!-- 无题目状态显示 -->
    <div v-if="!question" class="no-question">
      <el-empty description="当前题型下暂无题目"></el-empty>
    </div>

    <template v-else>
      <!-- 编程题展示 -->
      <ProgrammingQuestion
        v-if="isProgrammingQuestion"
        ref="programmingQuestionRef"
        :question="question"
        :show-correctness="showCorrectness"
        :user-answer="currentUserAnswer"
        @answer-submitted="handleProgrammingAnswerSubmitted"
        @answer-changed="handleAnswerChanged"
      />

      <!-- 非编程题展示 -->
      <template v-else>
        <!-- 单题模式 -->
        <div v-if="singleQuestionMode" class="single-question-wrapper">
          <div class="question-content-wrapper">
            <!-- 题目头部信息 -->
            <div class="question-header">
              <!-- 题型和题号信息 -->
              <div class="question-info">
                <span class="question-type">{{
                  getQuestionTypeLabel(question)
                }}</span>
                <!-- 题型标签 -->
                <span class="question-number">第 {{ questionNumber }} 题</span>
                <!-- 当前题号 -->
              </div>
              <!-- 题目状态标签（如已作答、正确、错误） -->
              <div class="question-status" v-if="question.status">
                <el-tag :type="getStatusType(question.status)">{{
                  getStatusText(question.status)
                }}</el-tag>
                <!-- 状态显示 -->
              </div>
            </div>
          </div>

          <div class="question-content-wrapper">
            <!-- 题目内容 -->
            <div class="question-content">
              <h3 class="question-title">{{ question.title }}</h3>

              <!-- 选择题/判断题选项 -->
              <div v-if="hasOptions(question)" class="question-options">
                <div
                  v-for="(option, index) in question.options"
                  :key="index"
                  class="option-item"
                  :class="getOptionClasses(option.value, question)"
                  @click="handleOptionSelection(option.value)"
                >
                  <div class="option-label">{{ option.label }}</div>
                  <div class="option-content">{{ option.text }}</div>
                </div>
              </div>
            </div>

            <!-- 简答题答题区域 -->
            <div v-if="isShortAnswerQuestion(question)" class="answer-section">
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

        <!-- 多题模式 -->
        <div v-else class="all-questions-wrapper">
          <div
            v-for="(q, index) in sameTypeQuestions"
            :key="q.id"
            class="single-question-container"
          >
            <div class="question-header">
              <div class="question-info">
                <span class="question-type">{{
                  getQuestionTypeLabel(q)
                }}</span>
                <span class="question-number">第 {{ index + 1 }} 题</span>
              </div>
              <div class="question-status" v-if="q.status">
                <el-tag :type="getStatusType(q.status)">{{
                  getStatusText(q.status)
                }}</el-tag>
              </div>
            </div>

            <div class="question-content">
              <h3 class="question-title">{{ q.title }}</h3>

              <!-- 选择题/判断题选项 -->
              <div v-if="hasOptions(q)" class="question-options">
                <div
                  v-for="(option, optIndex) in q.options"
                  :key="optIndex"
                  class="option-item"
                  :class="getOptionClasses(option.value, q, q.id)"
                  @click="handleOptionSelection(option.value, q.id)"
                >
                  <div class="option-label">{{ option.label }}</div>
                  <div class="option-content">{{ option.text }}</div>
                </div>
              </div>
            </div>

            <!-- 简答题答题区域 -->
            <div v-if="isShortAnswerQuestion(q)" class="answer-section">
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

      <!-- 操作按钮区域 -->
      <div class="question-actions">
        <el-button v-if="singleQuestionMode" @click="previousQuestion"
          >上一题</el-button
        >

        <div class="right-buttons">
          <el-button type="info" @click="toggleCorrectness">
            {{ showCorrectness ? "隐藏答案" : "查看答案" }}
          </el-button>
          <el-button
            type="primary"
            @click="submitAnswer"
            :disabled="showCorrectness"
          >
            提交答案
          </el-button>
          <el-button
            v-if="singleQuestionMode"
            type="success"
            @click="nextQuestion"
            >下一题</el-button
          >
        </div>
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, computed, watch } from "vue";
import { ElMessage } from "element-plus";
import ProgrammingQuestion from "./ProgrammingQuestion.vue";

// Props 定义
const props = defineProps({
  question: {
    type: Object,
    required: true,
    default: () => ({
      id: "",
      type: 0, // 0: 判断题, 1: 选择题, 2: 简答题, 3: 编程题
      title: "",
      content: "",
      options: [],
      answer: [],
      status: null, // null, answered, correct, incorrect
    }),
  },
  questionNumber: {
    type: Number,
    default: 1,
  },
  showCorrectness: {
    type: Boolean,
    default: false,
  },
  userAnswer: {
    type: [String, Number, Array],
    default: () => [],
  },
  userAnswers: {
    type: Object,
    default: () => ({}),
  },
  singleQuestionMode: {
    type: Boolean,
    default: false,
  },
  sameTypeQuestions: {
    type: Array,
    default: () => [],
  },
});

// Emits 定义：用于与父组件通信的事件列表
const emit = defineEmits([
  "set-show-correctness", // 切换答案显示状态
  "answer-submitted", // 提交答案
  "answer-changed", // 答案内容变化
  "previous", // 上一题导航
  "next", // 下一题导航
]);

// 组件状态管理
const programmingQuestionRef = ref(null); // 编程题内容的引用
const userAnswer = ref([]); // 单题模式下的用户答案
const userAnswers = ref({}); // 多题模式下的用户答案集合（key: questionId, value: answer）

// 计算属性定义
// 编程题当前答案（确保返回字符串格式）
const currentUserAnswer = computed(() => {
  if (!props.question) return "";
  if (props.question.type === 3) {
    return Array.isArray(props.userAnswer)
      ? props.userAnswer.join("")
      : props.userAnswer;
  }
  return props.userAnswer;
});

// 判断当前题目是否为编程题（类型3）
const isProgrammingQuestion = computed(() => {
  return props.question?.type === 3;
});

// 判断当前题目是否为选择题（类型1）
const isChoiceQuestion = computed(() => {
  return props.question?.type === 1;
});
// 计算属性：填空题题干片段
const questionStemSegments = computed(() => {
  return props.question?.stem ? props.question.stem.split("_") : [];
});

// 辅助函数：处理题目相关的工具函数
// 确保所有同类型题目的键都存在于userAnswers对象中（避免访问未定义属性）
const ensureAllQuestionKeysExist = () => {
  props.sameTypeQuestions.forEach((q) => {
    if (userAnswers.value[q.id] === undefined) {
      userAnswers.value[q.id] = []; // 为不存在的题目ID初始化空答案数组
    }
  });
};

// 题目状态转换：将状态字符串转换为中文显示文本
const getStatusText = (status) => {
  const statusMap = {
    answered: "已作答",
    correct: "正确",
    incorrect: "错误",
  };
  return statusMap[status] || ""; // 默认返回空字符串
};

// 题目状态转换：将题目类型数值转换为中文显示文本
const getQuestionTypeLabel = (question) => {
  if (!question) return "";
  const typeMap = {
    0: "判断题",
    1: "选择题",
    2: "简答题",
    3: "编程题",
  };
  return typeMap[question.type] || "未知题型";
};

// 题目状态类型转换：将状态字符串转换为Element Plus标签类型
const getStatusType = (status) => {
  const typeMap = {
    answered: "info", // 已作答 - 信息蓝
    correct: "success", // 正确 - 成功绿
    incorrect: "danger", // 错误 - 危险红
  };
  return typeMap[status] || "info"; // 默认返回信息类型
};

// 题目类型判断工具函数
// 判断是否为选择题/判断题（有选项的题型）
const hasOptions = (q) => {
  return [0, 1].includes(q.type); // 0:判断题, 1:选择题
};

// 判断是否为单选题/判断题（只能选择一个答案的题型）
const isSingleType = (q) => {
  return q.type === 0; // 0:判断题
};

// 判断是否为简答题（需要用户输入文字的题型）
const isShortAnswerQuestion = (q) => {
  return q.type === 2; // 2:简答题
};

// 辅助函数：判断是否应显示选项正确性
const shouldShowOptionCorrectness = (q) => {
  return showCorrectness || q.status === "correct" || q.status === "incorrect";
};

// 选项状态判断函数
// 获取选项的CSS类名（用于控制选项的选中、正确、错误等状态样式）
const getOptionClasses = (value, q, questionId = q.id) => {
  return {
    selected: isOptionSelected(value, questionId), // 是否被用户选中
    correct: isOptionCorrect(value, q) && shouldShowOptionCorrectness(q), // 是否为正确选项且应显示
    incorrect:
      isOptionIncorrect(value, q, questionId) && shouldShowOptionCorrectness(q), // 是否为错误选项且应显示
    missed:
      isOptionMissed(value, q, questionId) && shouldShowOptionCorrectness(q), // 是否为漏选选项且应显示
  };
};

// 判断选项是否被选中
const isOptionSelected = (value, questionId) => {
  if (props.singleQuestionMode) {
    return userAnswer.value.includes(value);
  } else {
    return userAnswers.value[questionId]?.includes(value) || false;
  }
};

// 判断选项是否正确
const isOptionCorrect = (value, q) => {
  const answer = Array.isArray(q.answer) ? q.answer : [q.answer];
  return answer.includes(value);
};

// 判断选项是否错误（选中但不正确）
const isOptionIncorrect = (value, q, questionId) => {
  return isOptionSelected(value, questionId) && !isOptionCorrect(value, q);
};

// 判断选项是否被遗漏（正确但未选中，仅多选题）
const isOptionMissed = (value, q, questionId) => {
  return (
    !isSingleType(q) &&
    !isOptionSelected(value, questionId) &&
    isOptionCorrect(value, q)
  );
};

// 选项选择操作：处理用户对选项的选择/取消选择
const handleOptionSelection = (value, questionId = props.question?.id) => {
  if (props.showCorrectness) return; // 显示答案时禁止修改

  const targetQuestion = props.singleQuestionMode
    ? props.question
    : props.sameTypeQuestions.find((q) => q.id === questionId);
  if (!targetQuestion) return;

  if (props.singleQuestionMode) {
    // 单题模式选项切换
    handleSingleQuestionOptionToggle(value, targetQuestion);
  } else {
    // 多题模式选项切换
    handleMultipleQuestionOptionToggle(value, questionId, targetQuestion);
  }
};

// 单题模式选项切换
const handleSingleQuestionOptionToggle = (value, question) => {
  if (isSingleType(question)) {
    // 单选题/判断题：替换当前选择
    userAnswer.value = [value];
  } else {
    // 多选题：切换选择状态
    const index = userAnswer.value.indexOf(value);
    if (index === -1) {
      userAnswer.value.push(value);
    } else {
      userAnswer.value.splice(index, 1);
    }
  }
};

// 多题模式选项切换
const handleMultipleQuestionOptionToggle = (value, questionId, question) => {
  // 初始化答案数组
  if (!userAnswers.value[questionId]) {
    userAnswers.value[questionId] = [];
  }

  if (isSingleType(question)) {
    // 单选题/判断题：替换当前选择
    userAnswers.value[questionId] = [value];
  } else {
    // 多选题：切换选择状态
    const index = userAnswers.value[questionId].indexOf(value);
    if (index === -1) {
      userAnswers.value[questionId].push(value);
    } else {
      userAnswers.value[questionId].splice(index, 1);
    }
  }

  // 触发答案变化事件
  emit("answer-changed", questionId, userAnswers.value[questionId]);
};

// 答案提交
const submitAnswer = () => {
  if (props.question?.type === 3) {
    // 编程题提交
    handleProgrammingQuestionSubmit();
  } else if (props.singleQuestionMode) {
    // 单题模式提交
    handleSingleQuestionSubmit();
  } else {
    // 多题模式提交
    handleMultipleQuestionSubmit();
  }
};

// 编程题提交
const handleProgrammingQuestionSubmit = () => {
  if (programmingQuestionRef.value) {
    programmingQuestionRef.value.submitCode();
  }
};

// 单题模式提交
const handleSingleQuestionSubmit = () => {
  if (!props.question) return;

  const answer = userAnswer.value;
  const isEmpty = checkAnswerEmpty(answer, props.question);

  if (isEmpty) {
    // 答案为空时的处理
    handleEmptyAnswerSubmit(props.question.id, answer);
    return;
  }

  // 答案不为空时的处理
  emit("answer-submitted", {
    questionId: props.question.id,
    answer: answer,
    isEmpty: false,
  });

  ElMessage({
    message: "答案已提交！",
    type: "success",
  });
};

// 多题模式提交
const handleMultipleQuestionSubmit = () => {
  let hasEmptyAnswers = false;
  const submittedAnswers = [];

  // 遍历所有题目，检查答案
  props.sameTypeQuestions.forEach((q) => {
    const answer = userAnswers.value[q.id];
    const isEmpty = checkAnswerEmpty(answer, q);

    if (isEmpty) {
      hasEmptyAnswers = true;
    }

    submittedAnswers.push({
      questionId: q.id,
      answer: answer,
      isEmpty: isEmpty,
    });
  });

  // 提示信息
  if (hasEmptyAnswers) {
    ElMessage({
      message: "部分题目未填写答案，已提交已完成的题目！",
      type: "warning",
    });
  } else {
    ElMessage({
      message: "所有答案已提交！",
      type: "success",
    });
  }

  // 提交所有答案
  submittedAnswers.forEach((submittedAnswer) => {
    emit("answer-submitted", submittedAnswer);
  });
};

// 检查答案是否为空
const checkAnswerEmpty = (answer, question) => {
  if (hasOptions(question)) {
    // 选择题/判断题：检查数组是否为空
    return Array.isArray(answer) ? answer.length === 0 : !answer;
  } else if (isShortAnswerQuestion(question)) {
    // 简答题：检查字符串是否为空
    return !answer || answer.trim() === "";
  }
  return true;
};

// 空答案提交处理
const handleEmptyAnswerSubmit = (questionId, answer) => {
  emit("answer-submitted", {
    questionId: questionId,
    answer: answer,
    isEmpty: true,
  });

  ElMessage({
    message: "请先填写答案！",
    type: "warning",
  });
};

// 监听逻辑：响应外部数据变化并更新内部状态
// 监听题目变化，更新本地答案状态
watch(
  () => props.question,
  (newQuestion) => {
    if (newQuestion && props.singleQuestionMode) {
      // 单题模式下，当题目变化时初始化答案数组
      userAnswer.value = Array.isArray(props.userAnswer)
        ? [...props.userAnswer]
        : [props.userAnswer];
    }
  },
  { immediate: true, deep: true },
); // immediate：立即执行，deep：深度监听

// 监听父组件传递的完整用户答案变化（多题模式）
watch(
  () => props.userAnswers,
  (newUserAnswers) => {
    if (newUserAnswers && !props.singleQuestionMode) {
      userAnswers.value = { ...newUserAnswers }; // 深拷贝用户答案

      // 确保所有同类型题目的键都存在于userAnswers对象中
      ensureAllQuestionKeysExist();
    }
  },
  { immediate: true, deep: true },
);

// 监听同类型题目列表变化，确保答案结构完整
watch(
  () => props.sameTypeQuestions,
  (newQuestions) => {
    if (newQuestions && !props.singleQuestionMode) {
      ensureAllQuestionKeysExist(); // 确保每个题目都有对应的答案字段
    }
  },
  { immediate: true, deep: true },
);

// 监听单题答案变化，实时通知父组件
watch(
  userAnswer,
  (newAnswer) => {
    if (props.question && props.singleQuestionMode) {
      emit("answer-changed", props.question.id, newAnswer); // 触发答案变化事件
    }
  },
  { deep: true },
);

// 事件处理
// 处理编程题答案提交
const handleProgrammingAnswerSubmitted = (result) => {
  emit("answer-submitted", result);
};

// 处理答案变化
const handleAnswerChanged = (questionId, answer) => {
  emit("answer-changed", questionId, answer);
};

// 切换显示正确性
const toggleCorrectness = () => {
  emit("set-show-correctness");
};

// 导航控制
const previousQuestion = () => {
  emit("previous");
};

const nextQuestion = () => {
  emit("next");
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
