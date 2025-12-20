import request from "@/utils/request";
import axios from "axios";

/**
 * @import { ApiResponse, ApiEmptyResponse, Page } from "@/utils/types"
 */

// model
/**
 * 问题资源实体类
 * @typedef {Object} QuestionResource
 * @property {number|null} id - 资源ID
 * @property {string|null} description - 资源描述
 * @property {string} name - 资源名称
 * @property {number} type - 资源类型（0:测试用例,1:用例答案,2:问题描述资料）
 * @property {number} size - 资源大小
 * @property {number} questionId - 问题ID
 */

// request
/**
 * 新增问题资源请求参数
 * @typedef {Object} CreateQuestionResourceRequest
 * @property {string|null} [description] - 资源描述
 * @property {string} [name] - 资源名称
 * @property {number} type - 资源类型（0:测试用例,1:用例答案,2:问题描述资料）
 * @property {number} size - 资源大小
 * @property {number} questionId - 问题ID
 */

/**
 * 更新问题资源请求参数（仅id必填，其他字段可选）
 * @typedef {Object} UpdateQuestionResourceRequest
 * @property {number} id - 资源ID
 * @property {string|null} [description] - 资源描述
 * @property {string} [name] - 资源名称
 * @property {number} [type] - 资源类型（0:测试用例,1:用例答案,2:问题描述资料）
 * @property {number} [size] - 资源大小
 * @property {number} [questionId] - 问题ID
 */

/**
 * 新增问题资源
 * @param {CreateQuestionResourceRequest} questionResource - 问题资源信息，要求type、size、questionId不能为空
 * @param {File} file - 资源文件
 * @returns {Promise<ApiEmptyResponse>}
 */
export function createQuestionResource(questionResource, file) {
  const formData = new FormData();
  // [FormData - Web API | MDN](https://developer.mozilla.org/zh-CN/docs/Web/API/FormData)
  formData.append("questionResourceJSON", JSON.stringify(questionResource));
  formData.append("resource", file);

  return request({
    url: "/api/question/questionresource",
    method: "POST",
    data: formData,
    headers: {
      "Content-Type": "multipart/form-data",
    },
  });
}

/**
 * 删除问题资源
 * @param {number} id - 问题资源ID
 * @returns {Promise<ApiEmptyResponse>}
 */
export function deleteQuestionResource(id) {
  return request({
    url: `/api/question/questionresource/${id}`,
    method: "DELETE",
  });
}

/**
 * 更新问题资源
 * @param {UpdateQuestionResourceRequest} questionResource - 问题资源信息，要求id不能为空，要更新的字段不能为空
 * @param {File} file - 资源文件
 * @returns {Promise<ApiEmptyResponse>}
 */
export function updateQuestionResource(questionResource, file) {
  const formData = new FormData();
  formData.append("questionResourceJSON", JSON.stringify(questionResource));
  formData.append("resource", file);

  return request({
    url: "/api/question/questionresource",
    method: "PUT",
    data: formData,
    headers: {
      "Content-Type": "multipart/form-data",
    },
  });
}

/**
 * 按参数查询问题资源（不含文件）
 * @param {Object} [params] - 查询参数（均为可选）
 * @param {number} [params.id] - 问题资源ID
 * @param {string} [params.name] - 问题资源名称
 * @param {number} [params.type] - 问题资源类型（0:测试用例,1:用例答案,2:问题描述资料）
 * @param {number} [params.size] - 问题资源大小
 * @param {number} [params.questionId] - 问题ID
 * @returns {Promise<ApiResponse<QuestionResource[]>>} 问题资源列表
 */
export function getQuestionResources(params = {}) {
  return request({
    url: "/api/question/questionresource",
    method: "GET",
    params: params,
  });
}

/**
 * 按参数分页查询问题资源（不含文件）
 * @param {Object} pageParams - 分页参数
 * @param {number} pageParams.pageNo - 当前页码（从1开始）
 * @param {number} pageParams.pageSize - 每页显示条数
 * @param {Object} [queryParams] - 查询参数（均为可选）
 * @param {number} [queryParams.id] - 问题资源ID
 * @param {string} [queryParams.name] - 问题资源名称
 * @param {number} [queryParams.type] - 问题资源类型（0:测试用例,1:用例答案,2:问题描述资料）
 * @param {number} [queryParams.size] - 问题资源大小
 * @param {number} [queryParams.questionId] - 问题ID
 * @returns {Promise<ApiResponse<Page<QuestionResource>>>} 分页问题资源列表
 */
export function getQuestionResourcesPage(pageParams, queryParams = {}) {
  const { pageNo, pageSize } = pageParams;
  return request({
    url: `/api/question/questionresource/${pageNo}/${pageSize}`,
    method: "GET",
    params: queryParams,
  });
}

/**
 * 下载问题资源文件
 * @param {number} id - 问题资源ID
 * @returns {Promise<Blob>} 文件数据
 */
export function downloadQuestionResource(id) {
  // 注意：下载接口返回的是ResponseEntity<Resource>，不是标准的ApiResponse
  // 因此直接使用axios，不经过request拦截器
  return axios.get(`/api/question/questionresource/download/${id}`, {
    responseType: "blob",
  });
}
