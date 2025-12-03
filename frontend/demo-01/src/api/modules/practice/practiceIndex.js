import request from "@/utils/request";

/**
 * @import { ApiResponse, Page } from "@/utils/types"
 */

// 实体类
/**
 * 练习索引实体类
 * @typedef {Object} PracticeIndex
 * @property {number|null} id - 索引ID
 * @property {number} practiceId - 练习ID
 * @property {number} courseSectionId - 课程系列ID
 * @property {number} courseId - 课程ID
 */

// request
/**
 * 新增练习索引请求参数
 * @typedef {Object} CreatePracticeIndexRequest
 * @property {number} practiceId - 练习ID（必填）
 * @property {number} courseSectionId - 课程系列ID（必填）
 * @property {number} [courseId] - 课程ID（可选，默认为0）
 */

/**
 * 更新练习索引请求参数（仅id必填，其他字段可选）
 * @typedef {Object} UpdatePracticeIndexRequest
 * @property {number} id - 索引ID
 * @property {number} [practiceId] - 练习ID
 * @property {number} [courseSectionId] - 课程系列ID
 * @property {number} [courseId] - 课程ID
 */

/**
 * 新增练习与课程关系
 * @param {CreatePracticeIndexRequest[]} indexList - PracticeIndex数组，要求每项元素的practiceId、courseSectionId不能为空
 * @returns {Promise<ApiResponse<null>>}
 */
export function createPracticeIndex(indexList) {
  return request({
    url: "/api/practice/practiceindex/index",
    method: "POST",
    data: indexList,
  });
}

/**
 * 批量删除练习与课程关系
 * @param {number[]} indexIds - PracticeIndex的id数组
 * @returns {Promise<ApiResponse<null>>}
 */
export function deletePracticeIndexByIds(indexIds) {
  return request({
    url: "/api/practice/practiceindex/index",
    method: "DELETE",
    params: { indexIds },
  });
}

/**
 * 更新练习与课程关系
 * @param {UpdatePracticeIndexRequest} practiceIndex - 更新请求参数，id必填，其他字段可选（传递则不能为空）
 * @returns {Promise<ApiResponse<null>>}
 */
export function updatePracticeIndex(practiceIndex) {
  return request({
    url: "/api/practice/practiceindex/index",
    method: "PUT",
    data: practiceIndex,
  });
}

/**
 * 按参数查询练习索引
 * @param {Object} [params] - 查询参数（均为可选）
 * @param {number} [params.practiceId] - 练习ID
 * @param {number} [params.courseSectionId] - 课程系列ID
 * @param {number} [params.courseId] - 课程ID
 * @returns {Promise<ApiResponse<PracticeIndex[]>>} 练习索引列表
 */
export function getPracticeIndexes(params = {}) {
  return request({
    url: "/api/practice/practiceindex",
    method: "GET",
    params: params,
  });
}

/**
 * 按参数分页查询练习索引
 * @param {Object} pageParams - 分页参数
 * @param {number} pageParams.pageNo - 当前页码（从1开始）
 * @param {number} pageParams.pageSize - 每页显示条数
 * @param {Object} [queryParams] - 查询参数（均为可选）
 * @param {number} [queryParams.practiceId] - 练习ID
 * @param {number} [queryParams.courseSectionId] - 课程系列ID
 * @param {number} [queryParams.courseId] - 课程ID
 * @returns {Promise<ApiResponse<Page<PracticeIndex>>>} 分页练习索引列表
 */
export function getPracticeIndexesPage(pageParams, queryParams = {}) {
  const { pageNo, pageSize } = pageParams;
  return request({
    url: `/api/practice/practiceindex/${pageNo}/${pageSize}`,
    method: "GET",
    params: queryParams,
  });
}
