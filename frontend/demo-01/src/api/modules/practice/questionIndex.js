import request from "@/utils/request";

/**
 * @import { ApiResponse, ApiEmptyResponse, Page } from "@/utils/types"
 */

// model
/**
 * 问题索引实体类
 * @typedef {Object} QuestionIndex
 * @property {number|null} id - 索引ID
 * @property {number} questionId - 问题ID
 * @property {number} practiceId - 练习ID
 */

// request
/**
 * 新增问题索引请求参数
 * @typedef {Object} CreateQuestionIndexRequest
 * @property {number} questionId - 问题ID（必填）
 * @property {number} practiceId - 练习ID（必填）
 */

/**
 * 更新问题索引请求参数（仅id必填，其他字段可选）
 * @typedef {Object} UpdateQuestionIndexRequest
 * @property {number} id - 索引ID
 * @property {number} [questionId] - 问题ID
 * @property {number} [practiceId] - 练习ID
 */

/**
 * 新增问题与练习关系
 * @param {CreateQuestionIndexRequest[]} questionIndexList - QuestionIndex数组，要求每项元素的practiceId、questionId不能为空
 * @returns {Promise<ApiEmptyResponse>}
 */
export function createQuestionIndexes(questionIndexList) {
  return request({
    url: "/api/practice/questionindex",
    method: "POST",
    data: questionIndexList,
  });
}

/**
 * 批量删除问题与练习关系
 * @param {number[]} indexIds - QuestionIndex的id数组
 * @returns {Promise<ApiEmptyResponse>}
 */
export function deleteQuestionIndexByIds(indexIds) {
  return request({
    url: "/api/practice/questionindex",
    method: "DELETE",
    params: { indexIds },
  });
}

/**
 * 更新问题与练习关系
 * @param {UpdateQuestionIndexRequest} questionIndex - 更新请求参数，id必填，其他字段可选（传递则不能为空）
 * @returns {Promise<ApiEmptyResponse>}
 */
export function updateQuestionIndex(questionIndex) {
  return request({
    url: "/api/practice/questionindex",
    method: "PUT",
    data: questionIndex,
  });
}

/**
 * 按参数查询问题索引
 * @param {Object} [params] - 查询参数（均为可选）
 * @param {number} [params.practiceId] - 练习ID
 * @param {number} [params.questionId] - 问题ID
 * @returns {Promise<ApiResponse<QuestionIndex[]>>} 问题索引列表
 */
export function getQuestionIndexes(params = {}) {
  return request({
    url: "/api/practice/questionindex",
    method: "GET",
    params: params,
  });
}

/**
 * 按参数分页查询问题索引
 * @param {Object} pageParams - 分页参数
 * @param {number} pageParams.pageNo - 当前页码（从1开始）
 * @param {number} pageParams.pageSize - 每页显示条数
 * @param {Object} [queryParams] - 查询参数（均为可选）
 * @param {number} [queryParams.practiceId] - 练习ID
 * @param {number} [queryParams.questionId] - 问题ID
 * @returns {Promise<ApiResponse<Page<QuestionIndex>>>} 分页问题索引列表
 */
export function getQuestionIndexesPage(pageParams, queryParams = {}) {
  const { pageNo, pageSize } = pageParams;
  return request({
    url: `/api/practice/questionindex/${pageNo}/${pageSize}`,
    method: "GET",
    params: queryParams,
  });
}
