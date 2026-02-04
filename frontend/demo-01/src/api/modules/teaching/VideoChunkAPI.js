import request from "@/api/request";

// model
/**
 * 视频分片实体类
 * @typedef {Object} VideoChunk
 * @property {number|null} id - 分片ID
 * @property {string|null} name - 分片名称
 * @property {number} number - 分片序号
 * @property {number} total - 总分片数
 * @property {number} size - 分片大小
 * @property {number} videoId - 视频ID
 * @property {boolean} canDeleted - 是否可删除，默认false
 * @property {string|null} createdAt - 创建时间
 */

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
 * 检查分片是否存在（秒传/断点续传）
 * @param {number} videoId - 视频ID
 * @param {number} chunkNumber - 分片序号
 * @returns {Promise<any>} 检查结果
 */
export function checkChunk(videoId, chunkNumber) {
  return request({
    url: "/api/teaching/video/chunk/check",
    method: "GET",
    params: {
      videoId,
      chunkNumber
    }
  });
}

/**
 * 上传分片
 * @param {string} videoChunkJSON - 视频分片信息JSON字符串
 * @param {File} chunk - 分片文件
 * @returns {Promise<any>} 上传结果
 */
export function uploadChunk(videoChunkJSON, chunk) {
  const formData = new FormData();
  formData.append("videoChunk", videoChunkJSON);
  formData.append("chunk", chunk);
  
  return request({
    url: "/api/teaching/video/chunk",
    method: "POST",
    data: formData,
    headers: {
      "Content-Type": "multipart/form-data"
    }
  });
}

/**
 * 合并分片
 * @param {VideoContent} videoContent - 视频内容信息
 * @returns {Promise<any>} 合并结果
 */
export function mergeChunk(videoContent) {
  return request({
    url: "/api/teaching/video/chunk/merge",
    method: "POST",
    data: videoContent
  });
}

/**
 * 删除分片
 * @param {number} videoId - 视频ID
 * @returns {Promise<any>} 删除结果
 */
export function deleteChunk(videoId) {
  return request({
    url: `/api/teaching/video/chunk/${videoId}`,
    method: "DELETE"
  });
}

/**
 * 获取已上传分片数量
 * @param {number} videoId - 视频ID
 * @returns {Promise<any>} 已上传分片数量
 */
export function getUploadedChunkCount(videoId) {
  return request({
    url: "/api/teaching/video/chunk/count",
    method: "GET",
    params: {
      videoId
    }
  });
}

