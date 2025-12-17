import request from "@/utils/request";

/**
 * @import { ApiResponse, ApiEmptyResponse, Page } from "@/utils/types"
 */

// model
/**
 * 练习实体类
 * @typedef {Object} Practice
 * @property {number|null} id - 练习ID
 * @property {string} name - 练习名称
 * @property {number} questionNum - 问题数量
 * @property {string|null} createdAt - 创建时间，格式：yyyy-mm-ddThh:mm:ss
 * @property {string|null} expiredAt - 过期时间，格式：yyyy-mm-ddThh:mm:ss
 */

/**
 * 练习索引实体类
 * @typedef {Object} PracticeIndex
 * @property {number|null} id - 索引ID
 * @property {number|null} practiceId - 练习ID
 * @property {number} courseSectionId - 课程系列ID
 * @property {number} courseId - 课程ID
 */

// request
/**
 * 新增练习请求参数
 * @typedef {Object} CreatePracticeRequest
 * @property {Practice} practice - 练习信息（name、questionNum必填）
 * @property {PracticeIndex} practiceIndex - 关联的课程信息（courseId必填）
 */

/**
 * 更新练习请求参数（仅id必填，其他字段可选）
 * @typedef {Object} UpdatePracticeRequest
 * @property {number} id - 练习ID
 * @property {string} [name] - 练习名称
 * @property {number} [questionNum] - 问题数量
 * @property {string|null} [expiredAt] - 过期时间，格式：yyyy-mm-ddThh:mm:ss
 */

/**
 * 新增练习及关联的 PracticeIndex，
 * practice和practiceIndex字段不能为空，
 * practice.name、practice.questionNum字段不能为空，
 * practiceIndex.courseId字段不能为空
 * @param {CreatePracticeRequest} data
 * @returns {Promise<ApiEmptyResponse>}
 */
export function createPracticeAndIndex(data) {
  return request({
    url: "/api/practice/practice",
    method: "POST",
    data: data,
  });
}

/**
 * 根据练习ID删除练习及其关联的 PracticeIndex
 * @param {number} practiceId - 练习ID
 * @returns {Promise<ApiEmptyResponse>}
 */
export function deletePracticeById(practiceId) {
  return request({
    url: `/api/practice/practice/${practiceId}`,
    method: "DELETE",
  });
}

/**
 * 根据课程系列ID和课程ID批量删除练习及关联的 PracticeIndex
 * @param {Object} params
 * @param {number} params.courseSectionId - 课程系列ID
 * @param {number} [params.courseId] - 课程ID
 * @returns {Promise<ApiEmptyResponse>}
 */
export function deletePracticeByCourse(params) {
  return request({
    url: "/api/practice/practice",
    method: "DELETE",
    params: params,
  });
}

/**
 * 更新练习信息
 * @param {UpdatePracticeRequest} data - 更新请求参数，id必填，其他字段可选（传递则不能为空）
 * @returns {Promise<ApiEmptyResponse>}
 */
export function updatePractice(data) {
  return request({
    url: "/api/practice/practice",
    method: "PUT",
    data: data,
  });
}

/**
 * 按参数查询Practice，
 * Practice的id（可选），创建时间起始点（可选），创建时间结束点（可选），过期时间起始点（可选），过期时间结束点（可选）
 * @param {Object} [params] - 查询参数（均为可选）
 * @param {number} [params.id] - 练习ID
 * @param {string} [params.createdAtStart] - 创建时间起始点
 * @param {string} [params.createdAtEnd] - 创建时间结束点
 * @param {string} [params.expiredAtStart] - 过期时间起始点
 * @param {string} [params.expiredAtEnd] - 过期时间结束点
 * @returns {Promise<ApiResponse<Practice[]>>} 练习列表
 */
export function getPractices(params = {}) {
  return request({
    url: "/api/practice/practice",
    method: "GET",
    params: params,
  });
}

/**
 * 按参数分页查询Practice，
 * Practice的id（可选），创建时间起始点（可选），创建时间结束点（可选），过期时间起始点（可选），过期时间结束点（可选）
 * @param {Object} pageParams - 分页参数
 * @param {number} pageParams.pageNo - 当前页码（从1开始）
 * @param {number} pageParams.pageSize - 每页显示条数
 * @param {Object} [queryParams] - 查询参数（均为可选）
 * @param {number} [queryParams.id] - 练习ID
 * @param {string} [queryParams.createdAtStart] - 创建时间起始点
 * @param {string} [queryParams.createdAtEnd] - 创建时间结束点
 * @param {string} [queryParams.expiredAtStart] - 过期时间起始点
 * @param {string} [queryParams.expiredAtEnd] - 过期时间结束点
 * @returns {Promise<ApiResponse<Page<Practice>>>} 分页练习列表
 */
export function getPracticesPage(pageParams, queryParams = {}) {
  const { pageNo, pageSize } = pageParams;
  return request({
    url: `/api/practice/practice/${pageNo}/${pageSize}`,
    method: "GET",
    params: queryParams,
  });
}
/**
 * 根据课程索引查询Practice
 * @param {Object} params - 查询参数
 * @param {number} params.courseSectionId - 课程系列ID
 * @param {number|null} [params.courseId] - 课程ID
 * @returns {Promise<ApiResponse<Practice[]>>} 练习列表
 */
export function getPracticesByIndex(params) {
  return request({
    url: "/api/practice/practice/index",
    method: "GET",
    params: params,
  });
}

/**
 * 根据课程索引分页查询Practice
 * @param {Object} pageParams - 分页参数
 * @param {number} pageParams.pageNo - 当前页码（从1开始）
 * @param {number} pageParams.pageSize - 每页显示条数
 * @param {Object} queryParams - 查询参数
 * @param {number} queryParams.courseSectionId - 课程系列ID
 * @param {number|null} [queryParams.courseId] - 课程ID
 * @returns {Promise<ApiResponse<Page<Practice>>>} 分页练习列表
 */
export function getPracticesPageByIndex(pageParams, queryParams) {
  const { pageNo, pageSize } = pageParams;
  return request({
    url: `/api/practice/practice/index/${pageNo}/${pageSize}`,
    method: "GET",
    params: queryParams,
  });
}
