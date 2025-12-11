import request from "@/utils/request";

/**
 * @import { ApiResponse, ApiEmptyResponse, Page } from "@/utils/types"
 */

// model
/**
 * 答案实体类
 * @typedef {Object} Answer
 * @property {number|null} id - 答案ID
 * @property {string} content - 问题答案
 * @property {string|null} analysis - 答案解析
 * @property {number} questionId - 问题ID
 */

// request
/**
 * 新增答案请求参数
 * @typedef {Object} CreateAnswerRequest
 * @property {string} content - 问题答案（必填）
 * @property {string|null} [analysis] - 答案解析（可选）
 * @property {number} questionId - 问题ID（必填）
 */

/**
 * 更新答案请求参数（仅id必填，其他字段可选）
 * @typedef {Object} UpdateAnswerRequest
 * @property {number} id - 答案ID
 * @property {string} [content] - 问题答案
 * @property {string} [analysis] - 答案解析
 * @property {number} [questionId] - 问题ID
 */

/**
 * 新增答案
 * @param {CreateAnswerRequest} data - 答案信息，要求content、questionId不能为空
 * @returns {Promise<ApiEmptyResponse>}
 */
export function createAnswer(data) {
  return request({
    url: "/api/practice/answer",
    method: "POST",
    data: data,
  });
}

/**
 * 根据答案ID删除答案
 * @param {number} answerId - 答案ID
 * @returns {Promise<ApiEmptyResponse>}
 */
export function deleteAnswerById(answerId) {
  return request({
    url: `/api/practice/answer/${answerId}`,
    method: "DELETE",
  });
}

/**
 * 更新答案
 * @param {UpdateAnswerRequest} data - 更新请求参数，id必填，其他字段可选（传递则不能为空）
 * @returns {Promise<ApiEmptyResponse>}
 */
export function updateAnswer(data) {
  return request({
    url: "/api/practice/answer",
    method: "PUT",
    data: data,
  });
}

/**
 * 按问题id数组查询答案
 * @param {number[]} questionIds - 问题ID数组
 * @returns {Promise<ApiResponse<Answer[]>>} 答案列表
 */
export function getAnswersByQuestionIds(questionIds) {
  return request({
    url: "/api/practice/answer",
    method: "GET",
    params: { questionIds },
  });
}

/**
 * 按参数查询答案
 * @param {Object} [params] - 查询参数（均为可选）
 * @param {number} [params.id] - 答案ID
 * @param {number} [params.questionId] - 问题ID
 * @returns {Promise<ApiResponse<Answer[]>>} 答案列表
 */
export function getAnswers(params = {}) {
  return request({
    url: "/api/practice/answer",
    method: "GET",
    params: params,
  });
}

/**
 * 按参数分页查询答案
 * @param {Object} pageParams - 分页参数
 * @param {number} pageParams.pageNo - 当前页码（从1开始）
 * @param {number} pageParams.pageSize - 每页显示条数
 * @param {Object} [queryParams] - 查询参数（均为可选）
 * @param {number} [queryParams.id] - 答案ID
 * @param {number} [queryParams.questionId] - 问题ID
 * @returns {Promise<ApiResponse<Page<Answer>>>} 分页答案列表
 */
export function getAnswersPage(pageParams, queryParams = {}) {
  const { pageNo, pageSize } = pageParams;
  return request({
    url: `/api/practice/answer/${pageNo}/${pageSize}`,
    method: "GET",
    params: queryParams,
  });
}
