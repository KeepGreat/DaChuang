<template>
  <div class="practice-view">
    <PracticeNavbar 
      :practice-title="practiceTitle" 
      :remaining-time="remainingTime" 
      :user-info="userInfo"
    />
    
    <div class="practice-content">
      <PracticeSiderbar 
        :question-types="sidebarQuestionTypes" 
        :active-type-id="activeType"
        @type-change="handleTypeChange"
      />
      
      <div class="main-content">
        <div class="question-container">
          <QuestionDisplay 
            :question="currentQuestion" 
            :question-number="currentQuestionIndex + 1"
            :show-correctness="showCorrectness"
            :user-answer="currentQuestion ? userAnswers[currentQuestion.id] || [] : []"
            @set-show-correctness="setShowCorrectness"
            @answer-submitted="handleAnswerSubmitted"
            @answer-changed="handleAnswerChanged"
            @previous="handlePreviousQuestion"
            @next="handleNextQuestion"
          />
        </div>
        
        <div class="progress-info">
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
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onUnmounted, onMounted } from 'vue';
import PracticeNavbar from '@/components/practice/PracticeNavbar.vue';
import PracticeSiderbar from '@/components/practice/PracticeSiderbar.vue';
import QuestionDisplay from '@/components/practice/QuestionDisplay.vue';

// 练习数据
const practiceTitle = ref('JavaScript基础练习');
const userInfo = ref({ name: '张三', avatar: '' });

// 倒计时相关
const deadline = ref(new Date(Date.now() + 30 * 60 * 1000)); // 示例：当前时间后30分钟
const remainingTime = ref('30:00');
let timerInterval = null;

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

// 倒计时函数
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
      // 可以添加时间结束的处理逻辑
      console.log('练习时间结束');
    }
  }, 1000);
};

// 题型列表
const questionTypes = ref([
  { type: -1, name: '全部题型' },
  { type: 0, name: '判断题' },
  { type: 1, name: '选择题' },
  { type: 2, name: '简答题' },
  { type: 3, name: '编程题' }
]);

const activeType = ref('all');

// 存储用户答案，key为questionId，value为用户答案
const userAnswers = ref({});

// 初始化用户答案函数
const initUserAnswers = () => {
  const initialAnswers = {};
  questions.value.forEach(question => {
    initialAnswers[question.id] = []; // 为每道题初始化空数组作为答案
  });
  userAnswers.value = initialAnswers;
};

// 用户答案更新函数
const updateUserAnswer = (questionId, answer) => {
  userAnswers.value[questionId] = answer;
};

// 计算属性：按type值分组并统计题型数据
const sidebarQuestionTypes = computed(() => {
  // 初始化题型统计数据，包含"全部题型"
  const typesWithStats = [
    {
      id: 'all',
      name: '全部题型',
      total: totalQuestions.value,
      answered: questions.value.filter(q => q.status !== null).length
    }
  ];
  
  // 为每种题型生成统计数据
  questionTypes.value.forEach(type => {
    if (type.type !== -1) { // 排除"全部题型"（已单独处理）
      const typeQuestions = questions.value.filter(q => q.type === type.type);
      const typeAnswered = typeQuestions.filter(q => q.status !== null).length;
      
      typesWithStats.push({
        id: type.type,
        name: type.name,
        total: typeQuestions.length,
        answered: typeAnswered
      });
    }
  });
  
  return typesWithStats;
});

// 问题数据示例
const questions = ref([
  {
    id: 0,
    type: 0,
    title: 'JavaScript是一种编译型语言。',
    content: '',
    options: [
      { label: 'A', value: 'true', text: '正确' },
      { label: 'B', value: 'false', text: '错误' }
    ],
    answer: ['false'],
    status: null
  },
  {
    id: 1,
    type: 0,
    title: '在JavaScript中，null是一个对象类型。',
    content: '',
    options: [
      { label: 'A', value: 'true', text: '正确' },
      { label: 'B', value: 'false', text: '错误' }
    ],
    answer: ['true'],
    status: null
  },
  {
    id: 2,
    type: 1,
    title: '以下哪个不是JavaScript的数据类型？',
    content: '',
    options: [
      { label: 'A', value: 'string', text: '字符串' },
      { label: 'B', value: 'number', text: '数字' },
      { label: 'C', value: 'boolean', text: '布尔值' },
      { label: 'D', value: 'class', text: '类' }
    ],
    answer: ['class'],
    status: null
  },
  {
    id: 3,
    type: 1,
    title: 'JavaScript中，以下哪个方法可以将字符串转换为数字？',
    content: '',
    options: [
      { label: 'A', value: 'parseInt()', text: 'parseInt()' },
      { label: 'B', value: 'toString()', text: 'toString()' },
      { label: 'C', value: 'split()', text: 'split()' },
      { label: 'D', value: 'join()', text: 'join()' }
    ],
    answer: ['parseInt()'],
    status: null
  },
  {
    id: 4,
    type: 1,
    title: '以下哪个关键字用于声明块级作用域的变量？',
    content: '',
    options: [
      { label: 'A', value: 'var', text: 'var' },
      { label: 'B', value: 'let', text: 'let' },
      { label: 'C', value: 'const', text: 'const' },
      { label: 'D', value: 'function', text: 'function' }
    ],
    answer: ['let'],
    status: null
  },
  {
    id: 5,
    type: 1,
    title: '以下哪些是JavaScript的内置对象？',
    content: '',
    options: [
      { label: 'A', value: 'Object', text: 'Object' },
      { label: 'B', value: 'Array', text: 'Array' },
      { label: 'C', value: 'String', text: 'String' },
      { label: 'D', value: 'jQuery', text: 'jQuery' }
    ],
    answer: ['Object', 'Array', 'String'],
    status: null
  },
  {
    id: 6,
    type: 1,
    title: '以下哪些方法可以用于数组遍历？',
    content: '',
    options: [
      { label: 'A', value: 'forEach()', text: 'forEach()' },
      { label: 'B', value: 'map()', text: 'map()' },
      { label: 'C', value: 'filter()', text: 'filter()' },
      { label: 'D', value: 'push()', text: 'push()' }
    ],
    answer: ['forEach()', 'map()', 'filter()'],
    status: null
  },
  {
    id: 7,
    type: 2,
    title: '请简述JavaScript中事件冒泡和事件捕获的区别。',
    content: '',
    options: [],
    answer: '事件冒泡是指事件从最具体的元素开始触发，然后逐级向上传播到更不具体的元素；事件捕获则相反，事件从最不具体的元素开始触发，然后逐级向下传播到最具体的元素。',
    status: null
  },
  {
    id: 8,
    type: 3,
    title: '编写一个函数，计算数组中所有元素的和。',
    content: '请实现一个sum函数，接收一个数组作为参数，返回数组中所有元素的和。例如：sum([1, 2, 3, 4]) 应返回 10。',
    options: [],
    answer: 'function sum(arr) {\n  return arr.reduce((acc, curr) => acc + curr, 0);\n}',
    status: null
  },
  {
    id: 9,
    type: 3,
    title: '实现一个简单的防抖函数。',
    content: '请实现一个debounce函数，接收一个函数和延迟时间作为参数，返回一个新的函数，该函数在连续调用时，只在最后一次调用后等待指定时间才执行。',
    options: [],
    answer: 'function debounce(func, delay) {\n  let timer = null;\n  return function() {\n    const context = this;\n    const args = arguments;\n    clearTimeout(timer);\n    timer = setTimeout(() => {\n      func.apply(context, args);\n    }, delay);\n  };\n}',
    status: null
  }
]);

// 当前问题索引
const currentQuestionIndex = ref(0);
// 是否显示正确答案
const showCorrectness = ref(false);

// 根据题型过滤问题
const filteredQuestions = computed(() => {
  if (activeType.value === 'all') {
    return questions.value;
  }
  return questions.value.filter(q => q.type === activeType.value);
});

// 计算当前显示的问题
const currentQuestion = computed(() => {
  if (filteredQuestions.value.length === 0) {
    return null;
  }
  return filteredQuestions.value[currentQuestionIndex.value];
});

// 已回答的问题数量
const answeredCount = computed(() => {
  return questions.value.filter(q => q.status !== null).length;
});

// 正确的问题数量
const correctCount = computed(() => {
  return questions.value.filter(q => q.status === 'correct').length;
});

// 总问题数量
const totalQuestions = computed(() => {
  return questions.value.length;
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

// 完成率
const completionRate = computed(() => {
  return progressPercentage.value;
});

// 进度条颜色 - 使用侧边栏相同的渐变色
const progressColor = computed(() => {
  return 'linear-gradient(45deg, #2563eb, #1d4ed8)'; // 与PracticeSiderbar.vue第147行相同的渐变色
});

// 设置showCorrectness的函数 - 切换真假值
const setShowCorrectness = () => {
  showCorrectness.value = !showCorrectness.value;
};

// 处理题型切换
const handleTypeChange = (typeId) => {
  activeType.value = typeId;
  // 切换题型时，重置到第一个问题
  currentQuestionIndex.value = 0;
};

// 处理答案变化
const handleAnswerChanged = (questionId, answer) => {
  updateUserAnswer(questionId, answer);
};

// 处理答案提交
const handleAnswerSubmitted = (result) => {
  const question = questions.value.find(q => q.id === result.questionId);
  if (question) {
    // 保存用户答案
    updateUserAnswer(result.questionId, result.answer);
    
    // 根据是否为空答案设置题目状态
    if (result.isEmpty) {
      question.status = null; // 空答案时状态设为null
    } else {
      question.status = 'answered'; // 非空答案时标记为已作答
    }
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
      // 有上一类题型
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
      // 有下一类题型
      const nextType = sidebarQuestionTypes.value[currentTypeIndex + 1];
      activeType.value = nextType.id;
      // 设置为下一类题型的第一题
      currentQuestionIndex.value = 0;
    }
  }
};

// 初始化和倒计时功能
onMounted(() => {
  // 初始化用户答案
  initUserAnswers();
  
  // 启动倒计时
  startTimer();
});

// 组件卸载时清除计时器
onUnmounted(() => {
  // 确保清除计时器并重置引用
  if (timerInterval) {
    clearInterval(timerInterval);
    timerInterval = null;
  }
});
</script>

<style scoped>
.practice-view {
  display: flex;
  flex-direction: column;
  height: 100vh;
  overflow: hidden;
}

.practice-content {
  display: flex;
  flex: 1;
  overflow: hidden;
}

.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  padding: 20px;
  background: #f5f7fa;
}

.question-container {
  flex: 1;
  overflow-y: auto;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  margin-bottom: 20px;
}

.progress-info {
  background: #fff;
  padding: 16px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.progress-bar {
  margin-bottom: 12px;
}

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