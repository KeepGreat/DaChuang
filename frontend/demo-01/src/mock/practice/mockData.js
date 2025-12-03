// 生成符合格式的当前日期时间（yyyy-mm-ddThh:mm:ss）
export function generateDateTime() {
  const now = new Date();
  const year = now.getFullYear();
  const month = String(now.getMonth() + 1).padStart(2, "0");
  const day = String(now.getDate()).padStart(2, "0");
  const hours = String(now.getHours()).padStart(2, "0");
  const minutes = String(now.getMinutes()).padStart(2, "0");
  const seconds = String(now.getSeconds()).padStart(2, "0");
  return `${year}-${month}-${day}T${hours}:${minutes}:${seconds}`;
}

// 生成下一个自增ID
export function getNextId(list) {
  return list.length > 0 ? Math.max(...list.map((item) => item.id)) + 1 : 1;
}

/**
 * 从URL中解析路径参数，解决路径参数和查询参数混合时的问题
 * @param {Object} req - 请求对象
 * @param {number} paramCount - 路径参数数量
 * @returns {Object} - 解析后的路径参数对象
 * @example parsePathParams, params : { param1: '1', param2: '15' }
 */
export function parsePathParams(req, paramCount) {
  const urlWithoutQuery = req.url.split("?")[0];
  const urlParts = urlWithoutQuery.split("/");

  const params = {};
  for (let i = 0; i < paramCount; i++) {
    params[`param${i + 1}`] = urlParts[urlParts.length - paramCount + i];
  }

  console.log("parsePathParams, params :", params);
  return params;
}

// 模拟练习数据
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

// 练习索引数据（用于关联课程）
export const practiceIndexes = [
  { id: 1, practiceId: 1, courseSectionId: 101, courseId: 1001 },
  { id: 2, practiceId: 2, courseSectionId: 101, courseId: 1002 },
  { id: 3, practiceId: 3, courseSectionId: 102, courseId: 1003 },
  { id: 4, practiceId: 4, courseSectionId: 103, courseId: 0 },
  { id: 5, practiceId: 5, courseSectionId: 104, courseId: 1004 },
  { id: 6, practiceId: 6, courseSectionId: 104, courseId: 1005 },
];

// 答案数据
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

// 问题数据
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
    content: "以下哪个是JavaScript的数据类型？\nA. String\nB. Integer\nC. Float\nD. Character",
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

// 问题索引数据（用于关联练习）
export const questionIndexes = [
  { id: 1, questionId: 1, practiceId: 1 },
  { id: 2, questionId: 2, practiceId: 1 },
  { id: 3, questionId: 3, practiceId: 2 },
  { id: 4, questionId: 4, practiceId: 3 },
  { id: 5, questionId: 1, practiceId: 4 },
  { id: 6, questionId: 2, practiceId: 5 },
];

// 问题资源数据
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
    description: "问题描述资料",
    name: "question_desc.pdf",
    type: 2, // 2:问题描述资料
    size: 2048,
    questionId: 2,
  },
];

// 模拟文件存储（在实际应用中，文件会存储在文件系统）
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
      filename: "question_desc.pdf",
      size: 2048,
      uploadedAt: "2025-11-01T08:10:00Z",
    },
  ],
]);
