/**
 * @import { QuestionResource, Practice, PracticeIndex, Answer, Question, QuestionIndex, UserAnswer } from "@/api"
 */

/**
 * 模拟练习数据
 * @type {Practice[]}
 */
export const practices = [
  {
    id: 1,
    name: "高数第一章练习",
    questionNum: 10,
    createdAt: "2025-11-01T08:00:00",
    expiredAt: "2026-01-01T00:00:00",
  },
  {
    id: 2,
    name: "线性代数单元测试",
    questionNum: 15,
    createdAt: "2025-11-10T10:30:00",
    expiredAt: "2025-12-31T23:59:59",
  },
  {
    id: 3,
    name: "概率论随堂测验",
    questionNum: 8,
    createdAt: "2025-11-20T14:20:00",
    expiredAt: "2025-11-27T14:20:00",
  },
  {
    id: 4,
    name: "JavaScript基础语法练习",
    questionNum: 12,
    createdAt: "2025-11-15T09:00:00",
    expiredAt: "2025-12-15T23:59:59",
  },
  {
    id: 5,
    name: "Vue.js组件开发实战",
    questionNum: 10,
    createdAt: "2025-11-18T14:30:00",
    expiredAt: "2025-12-18T23:59:59",
  },
  {
    id: 6,
    name: "数据结构与算法",
    questionNum: 18,
    createdAt: "2025-11-22T16:00:00",
    expiredAt: "2025-12-22T23:59:59",
  },
  {
    id: 7,
    name: "前端开发综合测试",
    questionNum: 20,
    createdAt: "2025-11-25T10:00:00",
    expiredAt: "2025-12-25T23:59:59",
  },
  {
    id: 8,
    name: "数据库设计基础",
    questionNum: 15,
    createdAt: "2025-11-28T13:20:00",
    expiredAt: "2025-12-28T23:59:59",
  },
];

/**
 * 练习索引数据（用于关联课程）
 * @type {PracticeIndex[]}
 */
export const practiceIndexes = [
  { id: 1, practiceId: 1, courseSectionId: 1, courseId: 1001 },
  { id: 2, practiceId: 2, courseSectionId: 1, courseId: 1002 },
  { id: 3, practiceId: 3, courseSectionId: 2, courseId: 1003 },
  { id: 5, practiceId: 5, courseSectionId: 4, courseId: 1004 },
  { id: 6, practiceId: 6, courseSectionId: 4, courseId: 1005 },
  // courseId=3的
  { id: 4, practiceId: 4, courseSectionId: 3, courseId: 0 },
  { id: 10, practiceId: 5, courseSectionId: 3, courseId: 0 },
  { id: 7, practiceId: 2, courseSectionId: 3, courseId: 3 },
  { id: 8, practiceId: 7, courseSectionId: 3, courseId: 3 },
  { id: 9, practiceId: 8, courseSectionId: 3, courseId: 3 },
];

/**
 * 答案数据
 * @type {Answer[]}
 */
export const answers = [
  {
    id: 1,
    content: "True",
    analysis: "这是一个判断题的正确答案",
    questionId: 1,
  },
  {
    id: 2,
    content: "A",
    analysis: "这是一个选择题的正确答案，选项A符合题目要求",
    questionId: 2,
  },
  {
    id: 3,
    content: "这是一个简答题的标准答案，需要包含关键要点",
    analysis: "简答题评分要点：1.概念清晰 2.逻辑完整 3.表达准确",
    questionId: 3,
  },
  {
    id: 4,
    content: "def hello():\n    print('Hello, World!')",
    analysis: "编程题答案：定义一个hello函数，输出Hello, World!",
    questionId: 4,
  },
  {
    id: 5,
    content: "False",
    analysis: "判断题错误答案，该陈述不成立",
    questionId: 5,
  },
  {
    id: 6,
    content: "C",
    analysis: "选择题答案，选项C是正确答案",
    questionId: 6,
  },
  {
    id: 7,
    content: "B",
    analysis: "JavaScript中变量声明使用let是正确的",
    questionId: 5,
  },
  {
    id: 8,
    content: "D",
    analysis: "Vue 3中 Composition API 是新的API设计模式",
    questionId: 6,
  },
  {
    id: 9,
    content:
      "Vue 3的Composition API相比Options API有以下优势：1.更好的逻辑复用 2.更灵活的代码组织 3.更好的TypeScript支持 4.更小的打包体积",
    analysis: "简答题评分要点：1.概念清晰 2.逻辑完整 3.表达准确",
    questionId: 7,
  },
  {
    id: 10,
    content: "function uniqueArray(arr) {\n  return [...new Set(arr)];\n}",
    analysis: "使用Set数据结构实现数组去重，这是ES6的标准方法",
    questionId: 8,
  },
  {
    id: 11,
    content:
      "function fibonacci(n) {\n  if (n <= 1) return n;\n  return fibonacci(n-1) + fibonacci(n-2);\n}",
    analysis: "使用递归实现斐波那契数列，时间复杂度较高但代码简洁",
    questionId: 9,
  },
  {
    id: 12,
    content: "True",
    analysis: "React使用JSX语法，这是正确的",
    questionId: 10,
  },
  {
    id: 13,
    content: "function debounce(func, delay) {\n  let timer = null;\n  return function(...args) {\n    const context = this;\n    clearTimeout(timer);\n    timer = setTimeout(() => {\n      func.apply(context, args);\n    }, delay);\n  };\n}",
    analysis: "防抖函数实现：通过定时器控制函数执行，只在最后一次调用后延迟执行",
    questionId: 13,
  },
  {
    id: 14,
    content: "function throttle(func, delay) {\n  let timer = null;\n  let lastCall = 0;\n  return function(...args) {\n    const context = this;\n    const now = Date.now();\n    if (now - lastCall >= delay) {\n      lastCall = now;\n      func.apply(context, args);\n    } else {\n      clearTimeout(timer);\n      timer = setTimeout(() => {\n        lastCall = Date.now();\n        func.apply(context, args);\n      }, delay - (now - lastCall));\n    }\n  };\n}",
    analysis: "节流函数实现：通过时间戳控制函数执行频率，确保指定时间间隔内最多执行一次",
    questionId: 14,
  },
  {
    id: 15,
    content: "function flattenArray(arr) {\n  return arr.reduce((acc, val) => {\n    return Array.isArray(val) ? acc.concat(flattenArray(val)) : acc.concat(val);\n  }, []);\n}",
    analysis: "数组扁平化实现：使用递归和reduce方法，将多维数组转换为一维数组",
    questionId: 15,
  },
  {
    id: 16,
    content: "True",
    analysis: "CSS3中的flexbox布局确实可以轻松实现水平和垂直居中，这是flexbox的优势之一",
    questionId: 16,
  },
  {
    id: 17,
    content: "True",
    analysis: "ES6中的箭头函数确实不能作为构造函数使用，如果使用new关键字会报错",
    questionId: 17,
  },
];

/**
 * 问题数据
 * @type {Question[]}
 */
export const questions = [
  {
    id: 1,
    name: "判断题1",
    type: 0, // 0:判断,1:选择,2:简答,3:编程
    content: "1+1=2是对的吗？",
    hasResource: false,
  },
  {
    id: 2,
    name: "选择题1",
    type: 1,
    content:
      "以下哪个是JavaScript的数据类型？\nA. String\nB. Integer\nC. Float\nD. Character",
    hasResource: true,
  },
  {
    id: 3,
    name: "简答题1",
    type: 2,
    content: "请简述Vue 3的Composition API的优势。",
    hasResource: false,
  },
  {
    id: 4,
    name: "编程题1",
    type: 3,
    content: "请编写一个函数，实现数组去重的功能。",
    hasResource: true,
  },
  {
    id: 5,
    name: "选择题2",
    type: 1,
    content:
      "以下哪个是ES6中用于声明变量的关键字？\nA. var\nB. let\nC. const\nD. 以上都是",
    hasResource: false,
  },
  {
    id: 6,
    name: "选择题3",
    type: 1,
    content:
      "Vue 3中新引入的API设计模式叫什么？\nA. Options API\nB. Mixins API\nC. Composition API\nD. Components API",
    hasResource: true,
  },
  {
    id: 7,
    name: "简答题2",
    type: 2,
    content: "请详细说明Vue 3的Composition API相比Options API的优势。",
    hasResource: false,
  },
  {
    id: 8,
    name: "编程题2",
    type: 3,
    content: "请编写一个函数，使用Set实现数组去重。",
    hasResource: true,
  },
  {
    id: 9,
    name: "编程题3",
    type: 3,
    content: "请编写一个函数，实现斐波那契数列的第n项。",
    hasResource: false,
  },
  {
    id: 10,
    name: "判断题2",
    type: 0,
    content: "React框架使用JSX语法进行开发。",
    hasResource: false,
  },
  {
    id: 11,
    name: "选择题4",
    type: 1,
    content: "以下哪个不是前端框架？\nA. Vue.js\nB. React\nC. Angular\nD. Node.js",
    hasResource: true,
  },
  {
    id: 12,
    name: "简答题3",
    type: 2,
    content: "请简述前端工程化的概念及其主要优势。",
    hasResource: false,
  },
  {
    id: 13,
    name: "编程题4",
    type: 3,
    content: "请实现一个防抖函数debounce，接收一个函数和延迟时间作为参数，返回一个新的函数，该函数在连续调用时，只在最后一次调用后等待指定时间才执行。",
    hasResource: false,
  },
  {
    id: 14,
    name: "编程题5",
    type: 3,
    content: "请实现一个节流函数throttle，接收一个函数和时间间隔作为参数，返回一个新的函数，该函数在指定时间间隔内最多只执行一次。",
    hasResource: false,
  },
  {
    id: 15,
    name: "编程题6",
    type: 3,
    content: "请实现一个函数，将多维数组扁平化为一维数组。例如：[1, [2, 3], [[4, 5], 6]] -> [1, 2, 3, 4, 5, 6]。",
    hasResource: false,
  },
  {
    id: 16,
    name: "判断题3",
    type: 0, // 0:判断题
    content: "CSS3中的flexbox布局可以轻松实现水平和垂直居中。",
    hasResource: false,
  },
  {
    id: 17,
    name: "判断题4",
    type: 0, // 0:判断题
    content: "ES6中的箭头函数不能作为构造函数使用。",
    hasResource: false,
  },
];

/**
 * 问题索引数据（用于关联练习）
 * @type {QuestionIndex[]}
 */
export const questionIndexes = [
  // 高数第一章练习
  { id: 1, questionId: 1, practiceId: 1 }, // 基础数学判断题

  // 线性代数单元测试
  { id: 2, questionId: 3, practiceId: 2 }, // 数学相关简答题
  { id: 13, questionId: 2, practiceId: 2 }, // JavaScript题目（复用）
  { id: 14, questionId: 4, practiceId: 2 }, // 编程题目（逻辑思维）
  { id: 15, questionId: 1, practiceId: 2 }, // 基础判断题

  // 概率论随堂测验
  { id: 3, questionId: 4, practiceId: 3 }, // 编程题目（算法思维）

  // JavaScript基础语法练习
  { id: 4, questionId: 2, practiceId: 4 }, // JS数据类型
  { id: 5, questionId: 5, practiceId: 4 }, // ES6变量声明

  // Vue.js组件开发实战
  { id: 6, questionId: 6, practiceId: 5 }, // Vue 3 API
  { id: 7, questionId: 7, practiceId: 5 }, // Vue 3优势

  // 数据结构与算法
  { id: 8, questionId: 8, practiceId: 6 }, // Set数组去重
  { id: 9, questionId: 9, practiceId: 6 }, // 斐波那契数列

  // 前端开发综合测试
  { id: 10, questionId: 10, practiceId: 7 }, // React JSX语法
  { id: 11, questionId: 11, practiceId: 7 }, // 前端框架选择题
  { id: 12, questionId: 12, practiceId: 7 }, // 前端工程化
  { id: 16, questionId: 2, practiceId: 7 }, // JS数据类型
  { id: 17, questionId: 5, practiceId: 7 }, // ES6变量声明
  { id: 18, questionId: 6, practiceId: 7 }, // Vue 3 API
  { id: 19, questionId: 7, practiceId: 7 }, // Vue 3优势
  { id: 24, questionId: 13, practiceId: 7 }, // 防抖函数编程题
  { id: 25, questionId: 14, practiceId: 7 }, // 节流函数编程题
  { id: 26, questionId: 15, practiceId: 7 }, // 数组扁平化编程题
  { id: 27, questionId: 16, practiceId: 7 }, // CSS3 flexbox判断题
  { id: 28, questionId: 17, practiceId: 7 }, // ES6箭头函数判断题

  // 数据库设计基础
  { id: 20, questionId: 9, practiceId: 8 }, // 斐波那契数列（逻辑思维）
  { id: 21, questionId: 12, practiceId: 8 }, // 前端工程化（相关概念）
  { id: 22, questionId: 1, practiceId: 8 }, // 基础判断题
  { id: 23, questionId: 4, practiceId: 8 }, // 编程题目（逻辑思维）
];

/**
 * 问题资源数据
 * @type {QuestionResource[]}
 */
export const questionResources = [
  {
    id: 1,
    description: "测试用例文件",
    name: "test_cases.txt",
    type: 0, // 0:测试用例
    size: 1024,
    questionId: 1,
  },
  {
    id: 2,
    description: "答案文件",
    name: "answer.json",
    type: 1, // 1:用例答案
    size: 512,
    questionId: 1,
  },
  {
    id: 3,
    description: "编程题目说明",
    name: "programming_guide.pdf",
    type: 2, // 2:问题描述资料
    size: 2048,
    questionId: 2,
  },
  {
    id: 4,
    description: "算法执行流程图",
    name: "algorithm_flow.png",
    type: 2, // 2:问题描述资料
    size: 15360,
    questionId: 3,
  },
  {
    id: 5,
    description: "代码结构示意图",
    name: "code_structure.jpg",
    type: 2, // 2:问题描述资料
    size: 25600,
    questionId: 4,
  },
  {
    id: 6,
    description: "数据结构示意图",
    name: "data_structure.png",
    type: 2, // 2:问题描述资料
    size: 8192,
    questionId: 2,
  },
  {
    id: 7,
    description: "编程界面示例",
    name: "coding_interface.png",
    type: 2, // 2:问题描述资料
    size: 32768,
    questionId: 4,
  },
  {
    id: 8,
    description: "系统架构图",
    name: "system_architecture.jpg",
    type: 2, // 2:问题描述资料
    size: 45056,
    questionId: 3,
  },
  {
    id: 9,
    description: "知识点关系图",
    name: "knowledge_map.jpg",
    type: 2, // 2:问题描述资料
    size: 12288,
    questionId: 2,
  },
  {
    id: 10,
    description: "练习界面原型",
    name: "exercise_ui.png",
    type: 2, // 2:问题描述资料
    size: 28672,
    questionId: 1,
  },
  {
    id: 11,
    description: "算法演示动画",
    name: "algorithm_demo.gif",
    type: 2, // 2:问题描述资料
    size: 18432,
    questionId: 4,
  },
  {
    id: 12,
    description: "函数调用关系图",
    name: "function_calls.png",
    type: 2, // 2:问题描述资料
    size: 9728,
    questionId: 3,
  },
  {
    id: 13,
    description: "程序执行时序图",
    name: "execution_sequence.jpg",
    type: 2, // 2:问题描述资料
    size: 21504,
    questionId: 2,
  },
  {
    id: 14,
    description: "状态转换图",
    name: "state_transition.png",
    type: 2, // 2:问题描述资料
    size: 16384,
    questionId: 1,
  },
  {
    id: 15,
    description: "数据库设计图",
    name: "database_design.jpg",
    type: 2, // 2:问题描述资料
    size: 14336,
    questionId: 4,
  },
];

// 模拟文件存储
export const storedFiles = new Map([
  [
    1,
    {
      filename: "test_cases.txt",
      size: 1024,
      uploadedAt: "2025-11-01T08:00:00Z",
    },
  ],
  [
    2,
    {
      filename: "answer.json",
      size: 512,
      uploadedAt: "2025-11-01T08:05:00Z",
    },
  ],
  [
    3,
    {
      filename: "programming_guide.pdf",
      size: 2048,
      uploadedAt: "2025-11-01T08:10:00Z",
    },
  ],
  [
    4,
    {
      filename: "algorithm_flow.png",
      size: 15360,
      uploadedAt: "2025-11-01T08:15:00Z",
    },
  ],
  [
    5,
    {
      filename: "code_structure.jpg",
      size: 25600,
      uploadedAt: "2025-11-01T08:20:00Z",
    },
  ],
  [
    6,
    {
      filename: "data_structure.png",
      size: 8192,
      uploadedAt: "2025-11-01T08:25:00Z",
    },
  ],
  [
    7,
    {
      filename: "coding_interface.png",
      size: 32768,
      uploadedAt: "2025-11-01T08:30:00Z",
    },
  ],
  [
    8,
    {
      filename: "system_architecture.jpg",
      size: 45056,
      uploadedAt: "2025-11-01T08:35:00Z",
    },
  ],
  [
    9,
    {
      filename: "knowledge_map.jpg",
      size: 12288,
      uploadedAt: "2025-11-01T08:40:00Z",
    },
  ],
  [
    10,
    {
      filename: "exercise_ui.png",
      size: 28672,
      uploadedAt: "2025-11-01T08:45:00Z",
    },
  ],
  [
    11,
    {
      filename: "algorithm_demo.gif",
      size: 18432,
      uploadedAt: "2025-11-01T08:50:00Z",
    },
  ],
  [
    12,
    {
      filename: "function_calls.png",
      size: 9728,
      uploadedAt: "2025-11-01T08:55:00Z",
    },
  ],
  [
    13,
    {
      filename: "execution_sequence.jpg",
      size: 21504,
      uploadedAt: "2025-11-01T09:00:00Z",
    },
  ],
  [
    14,
    {
      filename: "state_transition.png",
      size: 16384,
      uploadedAt: "2025-11-01T09:05:00Z",
    },
  ],
  [
    15,
    {
      filename: "database_design.jpg",
      size: 14336,
      uploadedAt: "2025-11-01T09:10:00Z",
    },
  ],
]);

/**
 * 用户答案数据
 * @type {UserAnswer[]}
 */
export const userAnswers = [];
