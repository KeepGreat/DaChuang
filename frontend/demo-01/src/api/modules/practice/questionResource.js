import request from "@/api/request";

// model
/**
 * 问题资源实体类 (QuestionResource)
 * @typedef {Object} QuestionResource
 * @property {number|null} id - 资源ID (自增主键)
 * @property {string|null} description - 资源描述
 * @property {string} name - 资源名称
 * @property {number} type - 资源类型 (0:测试用例, 1:用例答案, 2:问题描述资料)
 * @property {number} size - 资源大小
 * @property {number} questionId - 问题ID
 */

// API
/**
 * 添加问题资源
 * @param {QuestionResource} questionResource - 问题资源信息 (questionResourceJSON)
 * @param {File} file - 资源文件 (resource)
 * @returns {Promise<any>} 添加结果
 * @example
 * addQuestionResource({ name: 'test', type: 0, questionId: 1 }, fileFile)
 */
export function addQuestionResource(questionResource, file) {
  const formData = new FormData();
  formData.append("questionResourceJSON", JSON.stringify(questionResource));
  formData.append("resource", file);
  return request({
    url: "/api/practice/questionresource",
    method: "POST",
    data: formData,
    headers: {
      "Content-Type": "multipart/form-data",
    },
  });
}

/**
 * 根据ID删除问题资源
 * @param {number} id - 问题资源ID
 * @returns {Promise<any>} 删除结果
 * @example
 * deleteQuestionResource(1)
 */
export function deleteQuestionResource(id) {
  return request({
    url: `/api/practice/questionresource/${id}`,
    method: "DELETE",
  });
}

/**
 * 更新问题资源
 * @param {QuestionResource} questionResource - 问题资源信息 (questionResourceJSON, id必填)
 * @param {File} file - 资源文件 (resource)
 * @returns {Promise<any>} 更新结果
 * @example
 * updateQuestionResource({ id: 1, name: 'new name' }, fileFile)
 */
export function updateQuestionResource(questionResource, file) {
  const formData = new FormData();
  formData.append("questionResourceJSON", JSON.stringify(questionResource));
  formData.append("resource", file);
  return request({
    url: "/api/practice/questionresource",
    method: "PUT",
    data: formData,
    headers: {
      "Content-Type": "multipart/form-data",
    },
  });
}

/**
 * 获取问题资源列表 (根据条件查询)
 * @param {number} [id] - 问题资源ID (可选)
 * @param {string} [name] - 问题资源名称 (可选)
 * @param {number} [type] - 问题资源类型 (可选, 0:测试用例, 1:用例答案, 2:问题描述资料)
 * @param {number} [size] - 问题资源大小 (可选)
 * @param {number} [questionId] - 问题ID (可选)
 * @returns {Promise<any>} 问题资源列表
 * @example
 * getQuestionResources(null, 'test', 0)
 */
export function getQuestionResources(id, name, type, size, questionId) {
  return request({
    url: "/api/practice/questionresource",
    method: "GET",
    params: {
      id,
      name,
      type,
      size,
      questionId,
    },
  });
}

/**
 * 分页获取问题资源列表
 * @param {number} pageNo - 页码
 * @param {number} pageSize - 每页大小
 * @param {number} [id] - 问题资源ID (可选)
 * @param {string} [name] - 问题资源名称 (可选)
 * @param {number} [type] - 问题资源类型 (可选)
 * @param {number} [size] - 问题资源大小 (可选)
 * @param {number} [questionId] - 问题ID (可选)
 * @returns {Promise<any>} 分页问题资源列表
 * @example
 * getQuestionResourcesPage(1, 10, null, 'test')
 */
export function getQuestionResourcesPage(pageNo, pageSize, id, name, type, size, questionId) {
  return request({
    url: `/api/practice/questionresource/${pageNo}/${pageSize}`,
    method: "GET",
    params: {
      id,
      name,
      type,
      size,
      questionId,
    },
  });
}

/**
 * 下载问题资源文件
 * @param {number} id - 问题资源ID
 * @returns {Promise<any>} 文件数据 (Blob)
 * @example
 * downloadResource(1).then(blob => { ... })
 */
export function downloadQuestionResource(id) {
  return request({
    url: "/api/practice/questionresource/download",
    method: "GET",
    params: {
      id,
    },
    responseType: "blob",
  });
}
