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
];

/**
 * 练习索引数据（用于关联课程）
 * @type {PracticeIndex[]}
 */
export const practiceIndexes = [
  { id: 1, practiceId: 1, courseSectionId: 101, courseId: 1001 },
  { id: 2, practiceId: 2, courseSectionId: 101, courseId: 1002 },
  { id: 3, practiceId: 3, courseSectionId: 102, courseId: 1003 },
  { id: 4, practiceId: 4, courseSectionId: 103, courseId: 0 },
  { id: 4, practiceId: 2, courseSectionId: 3, courseId: 0 },
  { id: 5, practiceId: 5, courseSectionId: 104, courseId: 1004 },
  { id: 6, practiceId: 6, courseSectionId: 104, courseId: 1005 },
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
];

/**
 * 问题索引数据（用于关联练习）
 * @type {QuestionIndex[]}
 */
export const questionIndexes = [
  { id: 1, questionId: 1, practiceId: 1 },
  { id: 2, questionId: 2, practiceId: 1 },
  { id: 3, questionId: 3, practiceId: 2 },
  { id: 4, questionId: 4, practiceId: 3 },
  { id: 5, questionId: 1, practiceId: 4 },
  { id: 6, questionId: 2, practiceId: 5 },
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
export const userAnswers = [
  {
    id: 1,
    content: "True",
    userId: "2",
    questionId: 1,
    questionType: 0, // 0:判断，1:选择，2:简答，3:编程
    score: 5, // 得分应小于等于问题分值，-1代表未批改
  },
  {
    id: 2,
    content: "A",
    userId: "2",
    questionId: 2,
    questionType: 1,
    score: 10,
  },
  {
    id: 3,
    content:
      "Vue 3的Composition API具有更好的逻辑复用性、代码组织更灵活、类型推导更完善等优势。",
    userId: "2",
    questionId: 3,
    questionType: 2,
    score: -1, // 未批改
  },
  {
    id: 4,
    content: `function uniqueArray(arr) {
      return [...new Set(arr)];
    }`,
    userId: "2",
    questionId: 4,
    questionType: 3,
    score: 15,
  },
  {
    id: 5,
    content: "False",
    userId: "2",
    questionId: 1,
    questionType: 0,
    score: 0, // 答错得0分
  },
  {
    id: 6,
    content: "C",
    userId: "2",
    questionId: 2,
    questionType: 1,
    score: 0,
  },
  {
    id: 7,
    content:
      "Composition API通过setup函数和响应式API，实现了更好的代码组织方式，特别是对于复杂组件。",
    userId: "2",
    questionId: 3,
    questionType: 2,
    score: 8,
  },
  {
    id: 8,
    content: `function removeDuplicates(arr) {
      const result = [];
      const seen = new Set();
      for (const item of arr) {
        if (!seen.has(item)) {
          seen.add(item);
          result.push(item);
        }
      }
      return result;
    }`,
    userId: "2",
    questionId: 4,
    questionType: 3,
    score: 12,
  },
];
