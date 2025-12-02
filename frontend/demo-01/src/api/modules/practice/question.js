import request from "@/utils/request";

/**
 * @import { ApiResponse, Page } from "@/utils/types"
 */

// 实体类
/**
 * 问题实体类
 * @typedef {Object} Question
 * @property {number|null} id - 问题ID
 * @property {string} name - 问题名称
 * @property {number} type - 问题类型（0:判断,1:选择,2:简答,3:编程）
 * @property {string} content - 问题内容
 * @property {boolean} hasResource - 是否有资源
 */

/**
 * 问题索引实体类
 * @typedef {Object} QuestionIndex
 * @property {number|null} id - 索引ID
 * @property {number} questionId - 问题ID
 * @property {number} practiceId - 练习ID
 */

// request
/**
 * 新增问题请求参数
 * @typedef {Object} CreateQuestionRequest
 * @property {Question} question - 问题信息（type、content、hasResource必填）
 * @property {QuestionIndex} questionIndex - 关联的练习信息（practiceId、questionId必填）
 */

/**
 * 更新问题请求参数（仅id必填，其他字段可选）
 * @typedef {Object} UpdateQuestionRequest
 * @property {number} id - 问题ID
 * @property {string} [name] - 问题名称
 * @property {number} [type] - 问题类型
 * @property {string} [content] - 问题内容
 * @property {boolean} [hasResource] - 是否有资源
 */

/**
 * 新增问题及关联的QuestionIndex
 * @param {CreateQuestionRequest} data - 请求参数，要求question和questionIndex不能为空
 * @returns {Promise<ApiResponse<null>>}
 */
export function createQuestionAndIndex(data) {
  return request({
    url: "/api/practice/question",
    method: "POST",
    data: data,
  });
}

/**
 * 根据问题ID删除问题及关联的QuestionIndex
 * @param {number} questionId - 问题ID
 * @returns {Promise<ApiResponse<null>>}
 */
export function deleteQuestionById(questionId) {
  return request({
    url: `/api/practice/question/${questionId}`,
    method: "DELETE",
  });
}

/**
 * 按练习ID批量删除问题及关联的QuestionIndex
 * @param {number} practiceId - 练习ID
 * @returns {Promise<ApiResponse<null>>}
 */
export function deleteQuestionByIndex(practiceId) {
  return request({
    url: "/api/practice/question",
    method: "DELETE",
    params: { practiceId },
  });
}

/**
 * 更新问题信息
 * @param {UpdateQuestionRequest} data - 更新请求参数，id必填，其他字段可选（传递则不能为空）
 * @returns {Promise<ApiResponse<null>>}
 */
export function updateQuestion(data) {
  return request({
    url: "/api/practice/question",
    method: "PUT",
    data: data,
  });
}

/**
 * 按参数查询问题
 * @param {Object} [params] - 查询参数（均为可选）
 * @param {number} [params.id] - 问题ID
 * @param {string} [params.name] - 问题名称
 * @param {number} [params.type] - 问题类型
 * @param {boolean} [params.hasResource] - 是否有资源
 * @returns {Promise<ApiResponse<Question[]>>} 问题列表
 */
export function getQuestions(params = {}) {
  return request({
    url: "/api/practice/question",
    method: "GET",
    params: params,
  });
}

/**
 * 按参数分页查询问题
 * @param {Object} pageParams - 分页参数
 * @param {number} pageParams.pageNo - 当前页码（从1开始）
 * @param {number} pageParams.pageSize - 每页显示条数
 * @param {Object} [queryParams] - 查询参数（均为可选）
 * @param {number} [queryParams.id] - 问题ID
 * @param {string} [queryParams.name] - 问题名称
 * @param {number} [queryParams.type] - 问题类型
 * @param {boolean} [queryParams.hasResource] - 是否有资源
 * @returns {Promise<ApiResponse<Page<Question>>>} 分页问题列表
 */
export function getQuestionsPage(pageParams, queryParams = {}) {
  const { pageNo, pageSize } = pageParams;
  return request({
    url: `/api/practice/question/${pageNo}/${pageSize}`,
    method: "GET",
    params: queryParams,
  });
}

/**
 * 根据练习ID查询问题
 * @param {number} practiceId - 练习ID
 * @returns {Promise<ApiResponse<Question[]>>} 问题列表
 */
export function getQuestionByIndex(practiceId) {
  return request({
    url: "/api/practice/question/index",
    method: "GET",
    params: { practiceId },
  });
}

/**
 * 根据练习ID分页查询问题
 * @param {Object} pageParams - 分页参数
 * @param {number} pageParams.pageNo - 当前页码（从1开始）
 * @param {number} pageParams.pageSize - 每页显示条数
 * @param {number} practiceId - 练习ID
 * @returns {Promise<ApiResponse<Page<Question>>>} 分页问题列表
 */
export function getQuestionPageByIndex(pageParams, practiceId) {
  const { pageNo, pageSize } = pageParams;
  return request({
    url: `/api/practice/question/index/${pageNo}/${pageSize}`,
    method: "GET",
    params: { practiceId },
  });
}
