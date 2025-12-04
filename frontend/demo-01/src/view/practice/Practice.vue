<template>
  <div class="practice-view">
    <!-- 导航栏组件 -->
    <PracticeNavbar 
      :practice-title="practiceTitle" 
      :remaining-time="remainingTime" 
      :user-info="userInfo"
      v-model:single-question-mode="singleQuestionMode"
    />
    
    <div class="practice-content">
      <!-- 侧边栏组件 -->
       <div style="background: #f5f7fa;">
        <PracticeSiderbar 
          :question-types="questionsStore.sidebarQuestionTypes" 
          :active-type-id="activeType"
          @type-change="handleTypeChange"
        />
        
        <!-- 进度信息区域 -->
        <div class="progress-info-sidebar">
          <div class="progress-bar">
            <el-progress 
              :percentage="progressPercentage" 
              :stroke-width="8"
              :color="progressColor"
              :show-text="false"
            />
          </div>
          <div class="progress-stats">
            <span>已完成：{{ answeredCount }}/{{ totalQuestions }}</span>
            <span>完成率：{{ completionRate }}%</span>
          </div>
        </div>
      </div>
      
      <div class="main-content">
        <!-- 题目展示容器 -->
        <div class="question-container">
          <QuestionDisplay 
            :question="currentQuestion" 
            :question-number="currentQuestionIndex + 1"
            :show-correctness="showCorrectness"
            :user-answer="currentQuestion ? userAnswers[currentQuestion.id] || [] : []"
            :user-answers="userAnswers"
            :single-question-mode="singleQuestionMode"
            :same-type-questions="filteredQuestions"
            @set-show-correctness="toggleShowCorrectness"
            @answer-submitted="handleAnswerSubmitted"
            @answer-changed="handleAnswerChanged"
            @previous="handlePreviousQuestion"
            @next="handleNextQuestion"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
// 导入依赖
import { ref, computed, onMounted, onUnmounted } from 'vue';
import PracticeNavbar from '@/components/practice/PracticeNavbar.vue';
import PracticeSiderbar from '@/components/practice/PracticeSiderbar.vue';
import QuestionDisplay from '@/components/practice/QuestionDisplay.vue';
import { useQuestionsStore } from '@/store/modules/questionsStore';

// 使用questions store
const questionsStore = useQuestionsStore();

// -------------------
// 基础数据定义
// -------------------

// 练习基本信息
const practiceTitle = ref('JavaScript基础练习');
const userInfo = ref({ name: '张三', avatar: '' });

// 单题作答模式，默认为false
const singleQuestionMode = ref(false);

// 倒计时相关
const deadline = ref(new Date(Date.now() + 30 * 60 * 1000)); // 示例：当前时间后30分钟
const remainingTime = ref('30:00');
let timerInterval = null;

// 当前激活的题型，初始值设为第一个具体题型（判断题）
const activeType = ref(0);

// 用户答案存储，key为questionId，value为用户答案
const userAnswers = ref({});

// 当前问题索引
const currentQuestionIndex = ref(0);

// 是否显示正确答案
const showCorrectness = ref(false);

// -------------------
// 计算属性
// -------------------

// 根据题型过滤问题
const filteredQuestions = computed(() => {
  return questionsStore.questions.filter(q => q.type === activeType.value);
});

// 当前显示的问题
const currentQuestion = computed(() => {
  if (filteredQuestions.value.length === 0) {
    return null;
  }
  return filteredQuestions.value[currentQuestionIndex.value];
});

// 已回答的问题数量
const answeredCount = computed(() => {
  return questionsStore.answeredCount;
});

// 正确的问题数量
const correctCount = computed(() => {
  return questionsStore.questions.filter(q => q.status === 'correct').length;
});

// 总问题数量
const totalQuestions = computed(() => {
  return questionsStore.totalQuestions;
});

// 当前过滤后的问题数量
const filteredTotalQuestions = computed(() => {
  return filteredQuestions.value.length;
});

// 进度百分比
const progressPercentage = computed(() => {
  if (totalQuestions.value === 0) return 0;
  return Math.round((answeredCount.value / totalQuestions.value) * 100);
});

// 完成率（与进度百分比相同）
const completionRate = computed(() => {
  return progressPercentage.value;
});

// 进度条颜色 - 使用侧边栏相同的渐变色
const progressColor = computed(() => {
  return 'linear-gradient(45deg, #2563eb, #1d4ed8)'; // 与PracticeSiderbar.vue第147行相同的渐变色
});

// -------------------
// 方法定义
// -------------------

// 初始化用户答案
const initUserAnswers = () => {
  const initialAnswers = {};
  questionsStore.questions.forEach(question => {
    initialAnswers[question.id] = []; // 为每道题初始化空数组作为答案
  });
  userAnswers.value = initialAnswers;
};

// 更新用户答案
const updateUserAnswer = (questionId, answer) => {
  userAnswers.value[questionId] = answer;
};

// 计算剩余秒数
const calculateRemainingSeconds = () => {
  const now = Date.now();
  const diff = deadline.value - now;
  return Math.max(0, Math.floor(diff / 1000));
};

// 格式化时间显示
const formatTime = (seconds) => {
  if (seconds <= 0) {
    return '00:00';
  }
  const mins = Math.floor(seconds / 60).toString().padStart(2, '0');
  const secs = (seconds % 60).toString().padStart(2, '0');
  return `${mins}:${secs}`;
};

// 更新剩余时间
const updateRemainingTime = () => {
  const seconds = calculateRemainingSeconds();
  remainingTime.value = formatTime(seconds);
  return seconds;
};

// 启动倒计时
const startTimer = () => {
  // 初始化剩余时间
  const initialSeconds = updateRemainingTime();
  
  if (initialSeconds <= 0) {
    console.log('练习时间已结束');
    return;
  }
  
  timerInterval = setInterval(() => {
    const seconds = updateRemainingTime();
    if (seconds <= 0) {
      // 时间结束，停止计时器
      clearInterval(timerInterval);
      timerInterval = null;
      console.log('练习时间结束');
      // 可以添加时间结束的处理逻辑
    }
  }, 1000);
};

// 切换显示正确答案
const toggleShowCorrectness = () => {
  showCorrectness.value = !showCorrectness.value;
};

// 处理题型切换
const handleTypeChange = (typeId) => {
  activeType.value = typeId;
  currentQuestionIndex.value = 0; // 切换题型时，重置到第一个问题
};

// 处理答案变化
const handleAnswerChanged = (questionId, answer) => {
  updateUserAnswer(questionId, answer);
};

// 处理答案提交
const handleAnswerSubmitted = (result) => {
  const question = questionsStore.questions.find(q => q.id === result.questionId);
  if (question) {
    // 保存用户答案
    updateUserAnswer(result.questionId, result.answer);
    
    // 根据是否为空答案设置题目状态
    question.status = result.isEmpty ? null : 'answered';
  }
};

// 处理上一题
const handlePreviousQuestion = () => {
  if (currentQuestionIndex.value > 0) {
    // 不是当前题型的第一题，直接切换到上一题
    currentQuestionIndex.value--;
  } else {
    // 当前是当前题型的第一题，切换到上一类题型的最后一题
    const currentTypeIndex = sidebarQuestionTypes.value.findIndex(type => type.id === activeType.value);
    if (currentTypeIndex > 0) {
      const prevType = sidebarQuestionTypes.value[currentTypeIndex - 1];
      activeType.value = prevType.id;
      // 设置为上一类题型的最后一题
      const prevTypeQuestions = prevType.id === 'all' ? questions.value : questions.value.filter(q => q.type === prevType.id);
      currentQuestionIndex.value = prevTypeQuestions.length - 1;
    }
  }
};

// 处理下一题
const handleNextQuestion = () => {
  if (currentQuestionIndex.value < filteredQuestions.value.length - 1) {
    // 不是当前题型的最后一题，直接切换到下一题
    currentQuestionIndex.value++;
  } else {
    // 当前是当前题型的最后一题，切换到下一类题型的第一题
    const currentTypeIndex = sidebarQuestionTypes.value.findIndex(type => type.id === activeType.value);
    if (currentTypeIndex < sidebarQuestionTypes.value.length - 1) {
      const nextType = sidebarQuestionTypes.value[currentTypeIndex + 1];
      activeType.value = nextType.id;
      currentQuestionIndex.value = 0; // 设置为下一类题型的第一题
    }
  }
};

// -------------------
// 生命周期钩子
// -------------------

// 组件挂载时初始化
onMounted(() => {
  initUserAnswers(); // 初始化用户答案
  startTimer(); // 启动倒计时
});

// 组件卸载时清理
onUnmounted(() => {
  if (timerInterval) {
    clearInterval(timerInterval);
    timerInterval = null;
  }
});
</script>

<style scoped>
/* 练习页面主容器 */
.practice-view {
  display: flex;
  flex-direction: column;
  height: 100vh;
  overflow: hidden;
}

/* 练习内容区域 */
.practice-content {
  display: flex;
  flex: 1;
  overflow: hidden;
}

/* 主内容区域 */
.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  padding: 20px;
  background: #f5f7fa;
}

/* 题目容器 */
.question-container {
  flex: 1;
  overflow-y: auto;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  margin-bottom: 20px;
}

/* 进度信息容器 */
.progress-info {
  background: #fff;
  padding: 16px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

/* 侧边栏下方的进度信息容器 */
.progress-info-sidebar {
  background: #fff;
  padding: 16px;
  border-radius: 0 0 8px 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  margin: 0 0 20px 0;
  width: 240px;
  box-sizing: border-box;
}

/* 进度条容器 */
.progress-bar {
  margin-bottom: 12px;
}

/* 进度统计信息 */
.progress-stats {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
  color: #606266;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .practice-content {
    flex-direction: column;
  }
}

@media (max-width: 768px) {
  .main-content {
    padding: 12px;
  }
  
  .progress-stats {
    flex-direction: column;
    gap: 8px;
  }
}
</style>