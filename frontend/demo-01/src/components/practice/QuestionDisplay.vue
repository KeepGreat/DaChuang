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
                <span class="question-type">{{ getQuestionTypeLabel(question) }}</span>
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
              <h3 class="question-title">{{ question.content }}</h3>

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

              <!-- 判断题/选择题答案解析 -->
              <div
                v-if="showCorrectness && currentStandardAnswer?.analysis"
                class="answer-analysis"
              >
                <h4>答案解析：</h4>
                <div
                  class="analysis-content"
                  v-html="currentStandardAnswer.analysis"
                ></div>
              </div>
            </div>

            <!-- 简答题答题区域 -->
            <div v-if="isShortAnswerQuestion(question)" class="answer-section">
              <el-input
                :model-value="getUserAnswer(question.id)"
                type="textarea"
                :rows="6"
                placeholder="请输入您的答案..."
                :disabled="showCorrectness"
                class="short-answer-input"
                @update:model-value="(val) => updateUserAnswer(question.id, val)"
                @blur="handleShortAnswerBlur(question.id, getUserAnswer(question.id))"
              ></el-input>

              <!-- 正确答案展示 -->
              <div v-if="showCorrectness" class="correct-answer">
                <h4>参考答案：</h4>
                <div
                  class="answer-content"
                  v-html="currentStandardAnswer?.content || question.answer"
                ></div>

                <!-- 答案解析 -->
                <div v-if="currentStandardAnswer?.analysis" class="answer-analysis">
                  <h4>答案解析：</h4>
                  <div
                    class="analysis-content"
                    v-html="currentStandardAnswer.analysis"
                  ></div>
                </div>
              </div>
            </div>

            <!-- 问题资源展示 -->
            <QuestionResources v-if="question.hasResource" :question-id="question.id" />
          </div>
        </div>

        <!-- 多题模式 -->
        <div v-else class="all-questions-wrapper">
          <div
            v-for="(q, index) in sameTypeQuestions"
            :key="q.id"
            class="single-question-container"
          >
            <div class="question-content-wrapper">
              <div class="question-header">
                <div class="question-info">
                  <span class="question-type">{{ getQuestionTypeLabel(q) }}</span>
                  <span class="question-number">第 {{ index + 1 }} 题</span>
                </div>
                <div class="question-status" v-if="q.status">
                  <el-tag :type="getStatusType(q.status)">{{
                    getStatusText(q.status)
                  }}</el-tag>
                </div>
              </div>

              <div class="question-content-wrapper">
                <div class="question-content">
                  <h3 class="question-title">{{ q.content }}</h3>

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

                  <!-- 判断题/选择题答案解析 -->
                  <div
                    v-if="showCorrectness && getQuestionStandardAnswer(q.id)?.analysis"
                    class="answer-analysis"
                  >
                    <h4>答案解析：</h4>
                    <div
                      class="analysis-content"
                      v-html="getQuestionStandardAnswer(q.id).analysis"
                    ></div>
                  </div>
                </div>

                <!-- 简答题答题区域 -->
                <div v-if="isShortAnswerQuestion(q)" class="answer-section">
                  <el-input
                    :model-value="getUserAnswer(q.id)"
                    type="textarea"
                    :rows="4"
                    placeholder="请输入您的答案..."
                    :disabled="showCorrectness"
                    class="short-answer-input"
                    @update:model-value="(val) => updateUserAnswer(q.id, val)"
                    @blur="handleShortAnswerBlur(q.id, getUserAnswer(q.id))"
                  ></el-input>

                  <!-- 正确答案展示 -->
                  <div v-if="showCorrectness" class="correct-answer">
                    <h4>参考答案：</h4>
                    <div
                      class="answer-content"
                      v-html="getQuestionStandardAnswer(q.id)?.content || q.answer"
                    ></div>

                    <!-- 答案解析 -->
                    <div
                      v-if="getQuestionStandardAnswer(q.id)?.analysis"
                      class="answer-analysis"
                    >
                      <h4>答案解析：</h4>
                      <div
                        class="analysis-content"
                        v-html="getQuestionStandardAnswer(q.id).analysis"
                      ></div>
                    </div>
                  </div>
                </div>

                <!-- 问题资源展示 -->
                <QuestionResources v-if="q.hasResource" :question-id="q.id" />
              </div>
            </div>
          </div>
        </div>
      </template>

      <!-- 操作按钮区域 -->
      <div class="question-actions">
        <el-button v-if="singleQuestionMode || isProgrammingQuestion" @click="previousQuestion">上一题</el-button>

        <div class="right-buttons">
          <el-button type="info" @click="toggleCorrectness">
            {{ showCorrectness ? "隐藏答案" : "查看答案" }}
          </el-button>
          <el-button type="primary" @click="submitAnswer" :disabled="showCorrectness">
            提交答案
          </el-button>
          <el-button v-if="singleQuestionMode || isProgrammingQuestion" type="success" @click="nextQuestion"
            >下一题</el-button
          >
        </div>
      </div>
    </template>
  </div>
</template>

<script setup>
import { useAnswerStore, useQuestionResourceStore, useUserAnswerStore } from "@/store";
import { ElMessage } from "element-plus";
import { computed, ref } from "vue";
import ProgrammingQuestion from "./ProgrammingQuestion.vue";
import QuestionResources from "./QuestionResources.vue";

// 初始化答案store
const answerStore = useAnswerStore(); // 管理标准答案
const userAnswerStore = useUserAnswerStore(); // 管理用户答案
const questionResourceStore = useQuestionResourceStore(); // 管理问题资源

// Props 定义
const props = defineProps({
  question: {
    type: [Object, null],
    required: false,
    default: () => ({
      id: "",
      type: 0, // 0: 判断题, 1: 选择题, 2: 简答题, 3: 编程题
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
  "previous", // 上一题导航
  "next", // 下一题导航
]);

// 组件状态管理
const programmingQuestionRef = ref(null); // 编程题内容的引用

// 计算属性定义
// 编程题当前答案（确保返回字符串格式）
const currentUserAnswer = computed(() => {
  if (!props.question) return "";
  if (props.question.type === 3) {
    const answer = userAnswerStore.getUserAnswerByQuestionId(props.question.id);
    return Array.isArray(answer) ? answer.join("") : answer;
  }
  return userAnswerStore.getUserAnswerByQuestionId(props.question.id);
});

// 获取当前题目的标准答案
const currentStandardAnswer = computed(() => {
  if (!props.question) return null;
  const answers = answerStore.getAnswersByQuestionId(props.question.id);
  return answers && answers.length > 0 ? answers[0] : null;
});

// 判断当前题目是否为编程题（类型3）
const isProgrammingQuestion = computed(() => {
  return props.question?.type === 3;
});

// 辅助函数：处理题目相关的工具函数

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
  return props.showCorrectness || q.status === "correct" || q.status === "incorrect";
};

// 选项状态判断函数
// 获取选项的CSS类名（用于控制选项的选中、正确、错误等状态样式）
const getOptionClasses = (value, q, questionId = q.id) => {
  return {
    selected: isOptionSelected(value, questionId), // 是否被用户选中
    correct: isOptionCorrect(value, q) && shouldShowOptionCorrectness(q), // 是否为正确选项且应显示
    incorrect: isOptionIncorrect(value, q, questionId) && shouldShowOptionCorrectness(q), // 是否为错误选项且应显示
    missed: isOptionMissed(value, q, questionId) && shouldShowOptionCorrectness(q), // 是否为漏选选项且应显示
  };
};

// 判断选项是否被选中
const isOptionSelected = (value, questionId) => {
  return getUserAnswer(questionId).includes(value);
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
    !isSingleType(q) && !isOptionSelected(value, questionId) && isOptionCorrect(value, q)
  );
};

// 统一的答案管理
const useAnswerManagement = () => {
  // 获取指定题目的用户答案
  const getUserAnswer = (questionId) => {
    return userAnswerStore.getUserAnswerByQuestionId(questionId) || [];
  };

  // 更新指定题目的用户答案
  const updateUserAnswer = (questionId, answer) => {
    // 更新userAnswerStore
    userAnswerStore.updateUserAnswerByQuestionId(questionId, answer);
  };

  return { getUserAnswer, updateUserAnswer };
};

const { getUserAnswer, updateUserAnswer } = useAnswerManagement();

// 选项选择操作：处理用户对选项的选择/取消选择
const handleOptionSelection = (value, questionId = props.question?.id) => {
  if (props.showCorrectness) return; // 显示答案时禁止修改

  const targetQuestion = props.singleQuestionMode
    ? props.question
    : props.sameTypeQuestions.find((q) => q.id === questionId);
  if (!targetQuestion) return;

  // 获取当前答案
  let currentAnswer = getUserAnswer(questionId) || [];

  if (isSingleType(targetQuestion)) {
    // 单选题/判断题：替换当前选择
    currentAnswer = [value];
  } else {
    // 多选题：切换选择状态
    const index = currentAnswer.indexOf(value);
    if (index === -1) {
      currentAnswer.push(value);
    } else {
      currentAnswer = [...currentAnswer];
      currentAnswer.splice(index, 1);
    }
  }

  // 更新答案
  updateUserAnswer(questionId, currentAnswer);
};

// 统一的答案提交处理
const handleAnswerSubmit = (questionId, answer) => {
  const isEmpty = checkAnswerEmpty(
    answer,
    props.singleQuestionMode
      ? props.question
      : props.sameTypeQuestions.find((q) => q.id === questionId)
  );

  if (isEmpty) {
    // 答案为空时的处理
    emit("answer-submitted", {
      questionId: questionId,
      answer: answer,
      isEmpty: true,
    });

    return false; // 返回false表示答案为空
  }

  // 答案不为空时的处理
  emit("answer-submitted", {
    questionId: questionId,
    answer: answer,
    isEmpty: false,
  });
  return true; // 返回true表示答案提交成功
};

// 答案提交
const submitAnswer = () => {
  if (props.question?.type === 3) {
    // 编程题提交
    handleProgrammingQuestionSubmit();
  } else if (props.singleQuestionMode) {
    // 单题模式提交
    const answer = getUserAnswer(props.question.id);
    const success = handleAnswerSubmit(props.question.id, answer);
    if (success) {
      ElMessage({
        message: "答案已提交！",
        type: "success",
      });
    }
  } else {
    // 多题模式提交
    let hasEmptyAnswers = false;
    let successCount = 0;

    // 遍历所有题目，检查并提交答案
    props.sameTypeQuestions.forEach((q) => {
      const answer = getUserAnswer(q.id);
      const success = handleAnswerSubmit(q.id, answer);
      if (success) {
        successCount++;
      } else {
        hasEmptyAnswers = true;
      }
    });

    // 提示信息
    if (hasEmptyAnswers) {
      ElMessage({
        message: `部分题目未填写答案，已提交${successCount}道题目！`,
        type: "warning",
      });
    } else {
      ElMessage({
        message: "所有答案已提交！",
        type: "success",
      });
    }
  }
};

// 检查答案是否为空
const checkAnswerEmpty = (answer, question) => {
  if (hasOptions(question)) {
    // 选择题/判断题：检查数组是否为空
    return Array.isArray(answer) ? answer.length === 0 : !answer;
  } else if (isShortAnswerQuestion(question)) {
    // 简答题：检查字符串是否为空，先确保answer是字符串类型
    const answerStr = Array.isArray(answer) ? answer.join("") : String(answer);
    return !answer || answerStr.trim() === "";
  }
  return true;
};

// 编程题提交
const handleProgrammingQuestionSubmit = () => {
  if (programmingQuestionRef.value) {
    programmingQuestionRef.value.submitCode();
  }
};

// 获取指定题目的标准答案
const getQuestionStandardAnswer = (questionId) => {
  const answers = answerStore.getAnswersByQuestionId(questionId);
  return answers && answers.length > 0 ? answers[0] : null;
};

// 事件处理
// 处理编程题答案提交
const handleProgrammingAnswerSubmitted = (result) => {
  emit("answer-submitted", result);
};

// 处理简答题失去焦点事件
const handleShortAnswerBlur = (questionId, answer) => {
  // 使用统一的答案更新方法
  updateUserAnswer(questionId, answer);
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
  max-height: calc(100vh - 120px);
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

/* 答案解析样式 */
.answer-analysis {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #e1f3d8;
}

.answer-analysis h4 {
  margin: 0 0 12px 0;
  color: #409eff;
  font-size: 16px;
  font-weight: 600;
}

.analysis-content {
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
  margin-top: auto;
  padding-top: 24px;
  border-top: 1px solid #ebeef5;
  flex-shrink: 0;
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
  flex: 1;
  overflow-y: auto;
  padding-right: 8px;
  margin-bottom: 16px;
}

/* 所有题目展示样式 */
.all-questions-wrapper {
  display: flex;
  flex-direction: column;
  gap: 24px;
  flex: 1;
  overflow-y: auto;
  padding-right: 8px;
  margin-bottom: 16px;
}

/* 单题模式滚动条样式 */
.single-question-wrapper::-webkit-scrollbar {
  width: 6px;
}

.single-question-wrapper::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.single-question-wrapper::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.single-question-wrapper::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

/* 单题模式滚动条样式 */
.single-question-wrapper::-webkit-scrollbar {
  width: 6px;
}

.single-question-wrapper::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.single-question-wrapper::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.single-question-wrapper::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

/* 滚动条样式 */
.all-questions-wrapper::-webkit-scrollbar {
  width: 6px;
}

.all-questions-wrapper::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.all-questions-wrapper::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.all-questions-wrapper::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

.single-question-container {
  display: flex;
  flex-direction: column;
  margin-bottom: 20px;
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
