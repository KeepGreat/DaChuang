import request from "@/api/request";

// model
/**
 * 文件内容实体类
 * @typedef {Object} FileContent
 * @property {number|null} id - 文件内容ID（自增主键）
 * @property {string} type - 文件类型
 * @property {string} name - 文件名称
 * @property {number} size - 文件大小（字节）
 * @property {number} matId - 关联材料ID
 */

// API
/**
 * 下载文件
 * @param {number} id - 文件内容ID
 * @returns {Promise<any>} 文件下载响应（blob类型）
 */
export function downloadFile(id) {
    return request.get('/api/teaching/file/download', {
        params: {
            id
        },
        responseType: 'blob'
    });
}

/**
 * 上传文件
 * @param {FileContent} fileContent - 文件内容信息
 * @param {File} file - 上传的文件对象
 * @returns {Promise<any>} 上传结果
 */
export function uploadFile(fileContent, file) {
    const formData = new FormData();
    formData.append('fileContent', JSON.stringify(fileContent));
    formData.append('file', file);
    return request.post('/api/teaching/file', formData, {
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    });
}

/**
 * 删除文件
 * @param {number} id - 文件内容ID
 * @returns {Promise<any>} 删除结果
 */
export function deleteFile(id) {
    return request.delete(`/api/teaching/file/${id}`);
}

/**
 * 更新文件
 * @param {FileContent} fileContent - 文件内容信息
 * @param {File} file - 上传的文件对象
 * @returns {Promise<any>} 更新结果
 */
export function updateFile(fileContent, file) {
    const formData = new FormData();
    formData.append('fileContent', JSON.stringify(fileContent));
    formData.append('file', file);
    return request.put('/api/teaching/file', formData, {
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    });
}

/**
 * 获取文件内容列表（根据条件查询）
 * @param {number} [id] - 文件内容ID（可选）
 * @param {string} [type] - 文件类型（可选）
 * @param {string} [name] - 文件名称（可选）
 * @param {number} [size] - 文件大小（可选）
 * @param {number} [matId] - 关联材料ID（可选）
 * @returns {Promise<any>} 文件内容列表
 */
export function getFileContents(id, type, name, size, matId) {
    return request.get('/api/teaching/file', {
        params: {
            id,
            type,
            name,
            size,
            matId
        }
    });
}

/**
 * 分页获取文件内容列表
 * @param {number} pageNo - 页码
 * @param {number} pageSize - 每页大小
 * @param {number} [id] - 文件内容ID（可选）
 * @param {string} [type] - 文件类型（可选）
 * @param {string} [name] - 文件名称（可选）
 * @param {number} [size] - 文件大小（可选）
 * @param {number} [matId] - 关联材料ID（可选）
 * @returns {Promise<any>} 分页文件内容列表
 */
export function getFileContentsPage(pageNo, pageSize, id, type, name, size, matId) {
    return request.get(`/api/teaching/file/${pageNo}/${pageSize}`, {
        params: {
            id,
            type,
            name,
            size,
            matId
        }
    });
}