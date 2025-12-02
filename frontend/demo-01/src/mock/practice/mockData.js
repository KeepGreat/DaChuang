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
