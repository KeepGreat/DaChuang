import request from "@/api/request";

// model
/**
 * 视频内容实体类
 * @typedef {Object} VideoContent
 * @property {number|null} id - 视频信息ID
 * @property {string} type - 视频类型
 * @property {string} name - 视频名称
 * @property {number} size - 视频大小
 * @property {number} duration - 视频时长
 * @property {number} matId - 相关的资料ID
 */

// API
/**
 * 流式播放视频（支持断点续传）
 * @param {string} fileName - 文件名
 * @param {string} [rangeHeader] - Range请求头（可选）
 * @returns {Promise<any>} 视频流
 */
export function streamVideo(fileName, rangeHeader) {
  const headers = {};
  if (rangeHeader) {
    headers.Range = rangeHeader;
  }
  return request({
    url: "/api/teaching/video/stream",
    method: "GET",
    params: {
      fileName
    },
    headers: headers,
    responseType: "blob" // 流式播放需要设置响应类型为blob
  });
}

/**
 * 添加视频内容
 * @param {VideoContent} videoContent - 视频内容信息
 * @returns {Promise<any>} 添加结果
 */
export function addVideoContent(videoContent) {
  return request({
    url: "/api/teaching/video",
    method: "POST",
    data: videoContent
  });
}

/**
 * 删除视频内容
 * @param {number} id - 视频内容ID
 * @returns {Promise<any>} 删除结果
 */
export function deleteVideoContent(id) {
  return request({
    url: `/api/teaching/video/${id}`,
    method: "DELETE"
  });
}

/**
 * 更新视频内容
 * @param {VideoContent} videoContent - 视频内容信息
 * @returns {Promise<any>} 更新结果
 */
export function updateVideoContent(videoContent) {
  return request({
    url: "/api/teaching/video",
    method: "PUT",
    data: videoContent
  });
}

/**
 * 获取视频内容列表
 * @param {number} id - 视频ID
 * @param {number} matId - 资料ID
 * @param {number} size - 视频大小
 * @returns {Promise<any>} 视频内容列表
 */
export function getVideoContents(id, matId, size) {
  return request({
    url: "/api/teaching/video",
    method: "GET",
    params: {
      id,
      matId,
      size
    }
  });
}

/**
 * 分页获取视频内容
 * @param {number} pageNo - 页码
 * @param {number} pageSize - 每页大小
 * @param {number} id - 视频ID
 * @param {number} matId - 资料ID
 * @param {number} size - 视频大小
 * @returns {Promise<any>} 分页视频内容列表
 */
export function getVideoContentsPage(pageNo, pageSize, id, matId, size) {
  return request({
    url: `/api/teaching/video/${pageNo}/${pageSize}`,
    method: "GET",
    params: {
      id,
      matId,
      size
    }
  });
}

