import { defineStore } from "pinia";
import { computed, ref } from "vue";

export const useQuestionsStore = defineStore(
  "questions",
  () => {
    // State
    const questions = ref([
      {
        id: 0,
        name: 'JavaScript是一种编译型语言。',
        type: 0,
        content: '',
        hasResource: false,
        options: [
          { label: 'A', value: 'true', text: '正确' },
          { label: 'B', value: 'false', text: '错误' }
        ],
        answer: ['false'],
        status: null
      },
      {
        id: 1,
        name: '在JavaScript中，null是一个对象类型。',
        type: 0,
        content: '',
        hasResource: false,
        options: [
          { label: 'A', value: 'true', text: '正确' },
          { label: 'B', value: 'false', text: '错误' }
        ],
        answer: ['true'],
        status: null
      },
      {
        id: 2,
        name: '以下哪个不是JavaScript的数据类型？',
        type: 1,
        content: '',
        hasResource: false,
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
        name: 'JavaScript中，以下哪个方法可以将字符串转换为数字？',
        type: 1,
        content: '',
        hasResource: false,
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
        name: '以下哪个关键字用于声明块级作用域的变量？',
        type: 1,
        content: '',
        hasResource: false,
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
        name: '以下哪些是JavaScript的内置对象？',
        type: 1,
        content: '',
        hasResource: false,
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
        name: '以下哪些方法可以用于数组遍历？',
        type: 1,
        content: '',
        hasResource: false,
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
        name: '请简述JavaScript中事件冒泡和事件捕获的区别。',
        type: 2,
        content: '',
        hasResource: false,
        options: [],
        answer: ['事件冒泡是指事件从最具体的元素开始触发，然后逐级向上传播到更不具体的元素；事件捕获则相反，事件从最不具体的元素开始触发，然后逐级向下传播到最具体的元素。'],
        status: null
      },
      {
        id: 8,
        name: '编写一个函数，计算数组中所有元素的和。',
        type: 3,
        content: '请实现一个sum函数，接收一个数组作为参数，返回数组中所有元素的和。例如：sum([1, 2, 3, 4]) 应返回 10。',
        hasResource: false,
        options: [],
        answer: ['function sum(arr) {\n  return arr.reduce((acc, curr) => acc + curr, 0);\n}'],
        status: null
      },
      {
        id: 9,
        name: '实现一个简单的防抖函数。',
        type: 3,
        content: '请实现一个debounce函数，接收一个函数和延迟时间作为参数，返回一个新的函数，该函数在连续调用时，只在最后一次调用后等待指定时间才执行。',
        hasResource: false,
        options: [],
        answer: ['function debounce(func, delay) {\n  let timer = null;\n  return function() {\n    const context = this;\n    const args = arguments;\n    clearTimeout(timer);\n    timer = setTimeout(() => {\n      func.apply(context, args);\n    }, delay);\n  };\n}'],
        status: null
      }
    ]);

    const questionTypes = ref([
      { type: 0, name: '判断题' },
      { type: 1, name: '选择题' },
      { type: 2, name: '简答题' },
      { type: 3, name: '编程题' }
    ]);

    // 已回答的问题数量
    const answeredCount = computed(() => {
      return questions.value.filter(q => q.status !== null).length;
    });

    // 总问题数量
    const totalQuestions = computed(() => {
      return questions.value.length;
    });

    // 侧边栏题型统计数据
    const sidebarQuestionTypes = computed(() => {
      return generateSidebarQuestionTypes();
    });

    // 生成侧边栏题型统计数据的函数，支持传入用户答案数据
    const generateSidebarQuestionTypes = (userAnswersMap = null) => {
      // 初始化空数组，不再包含"全部题型"
      const typesWithStats = [];
      
      // 为每种题型生成统计数据
      questionTypes.value.forEach(type => {
        if (type.type !== -1) { // 排除"全部题型"
          const typeQuestions = questions.value.filter(q => q.type === type.type);
          let typeAnswered = 0;
          
          // 计算已回答的题目数量
          typeQuestions.forEach(question => {
            const isAnswered = isQuestionAnswered(question.id, userAnswersMap);
            if (isAnswered) {
              typeAnswered++;
            }
          });
          
          typesWithStats.push({
            id: type.type,
            name: type.name,
            total: typeQuestions.length,
            answered: typeAnswered
          });
        }
      });

      return typesWithStats;
    };

    // 判断题目是否已回答的辅助函数
    const isQuestionAnswered = (questionId) => {
      // 检查 questionsStore 中的 status 字段（已提交到后端的答案）
      const question = questions.value.find(q => q.id === questionId);
      if (question && question.status !== null) {
        return true;
      }
      
      // 不考虑 userAnswerStore 中的数据（用户点选但未提交的答案）      
      return false;
    };


    // 辅助函数：将API返回的Question转换为store所需的结构
    const transformQuestion = (apiQuestion) => {
      // 根据问题类型设置默认选项
      let defaultOptions = [];
      if (apiQuestion.type === 0) { // 判断题
        defaultOptions = [
          { label: 'A', value: 'true', text: '正确' },
          { label: 'B', value: 'false', text: '错误' }
        ];
      } else if (apiQuestion.type === 1) { // 选择题
        // 设置ABCD四种默认选项
        defaultOptions = [
          { label: 'A', value: 'A', text: '选项A' },
          { label: 'B', value: 'B', text: '选项B' },
          { label: 'C', value: 'C', text: '选项C' },
          { label: 'D', value: 'D', text: '选项D' }
        ];
      }
      
      // 转换为store所需的结构
      return {
        id: apiQuestion.id,
        name: apiQuestion.name,
        type: apiQuestion.type,
        content: apiQuestion.content,
        hasResource: apiQuestion.hasResource,
        options: defaultOptions, // 选择题/判断题的选项
        answer: [], // 正确答案，实际应用中应从API获取
        status: null // 答题状态，初始为null
      };
    };

    // Actions
    const updateQuestionStatus = (questionId, status) => {
      const question = questions.value.find(q => q.id === questionId);
      if (question) {
        question.status = status;
      }
    };

    const setQuestions = (newQuestions) => {
      // 如果传入的问题不是store所需的结构，则进行转换
      const transformedQuestions = newQuestions.map(transformQuestion);
      questions.value = transformedQuestions;
    };

    // 导出transformQuestion供外部使用
    const transformApiQuestions = (apiQuestions) => {
      return apiQuestions.map(transformQuestion);
    };

    return {
      questions,
      questionTypes,
      sidebarQuestionTypes,
      answeredCount,
      totalQuestions,
      updateQuestionStatus,
      setQuestions,
      transformApiQuestions,
      generateSidebarQuestionTypes,
      isQuestionAnswered
    };
  },
  {
    persist: {
      key: 'questions-store',
      storage: localStorage,
      paths: ['questions']
    }
  }
);
