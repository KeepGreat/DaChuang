import request from "@/api/request";

// model
/**
 * 材料实体类
 * @typedef {Object} Material
 * @property {number|null} id - 材料ID（自增主键）
 * @property {string} type - 材料类型
 * @property {string} description - 材料描述
 * @property {string} createdAt - 创建时间（ISO格式字符串）
 * @property {string} updatedAt - 更新时间（ISO格式字符串）
 * @property {number} courseId - 所属课程ID
 */

// API
/**
 * 添加材料
 * @param {Material} material - 材料信息
 * @returns {Promise<any>} 添加结果
 */
export function addMaterial(material) {
    return request.post('/api/teaching/material', material);
}

/**
 * 根据ID删除材料
 * @param {number} id - 材料ID
 * @returns {Promise<any>} 删除结果
 */
export function deleteMaterial(id) {
    return request.delete(`/api/teaching/material/${id}`);
}

/**
 * 更新材料信息
 * @param {Material} material - 材料信息
 * @returns {Promise<any>} 更新结果
 */
export function updateMaterial(material) {
    return request.put('/api/teaching/material', material);
}

/**
 * 获取材料列表（根据条件查询）
 * @param {number} [id] - 材料ID（可选）
 * @param {string} [type] - 材料类型（可选）
 * @param {string} [createdAtStart] - 创建时间起始（ISO格式字符串，可选）
 * @param {string} [createdAtEnd] - 创建时间结束（ISO格式字符串，可选）
 * @param {string} [updatedAtStart] - 更新时间起始（ISO格式字符串，可选）
 * @param {string} [updatedAtEnd] - 更新时间结束（ISO格式字符串，可选）
 * @param {number} [courseId] - 所属课程ID（可选）
 * @returns {Promise<any>} 材料列表
 */
export function getMaterials(id, type, createdAtStart, createdAtEnd, updatedAtStart, updatedAtEnd, courseId) {
    return request.get('/api/teaching/material', {
        params: {
            id,
            type,
            createdAtStart,
            createdAtEnd,
            updatedAtStart,
            updatedAtEnd,
            courseId
        }
    });
}

/**
 * 分页获取材料列表
 * @param {number} pageNo - 页码
 * @param {number} pageSize - 每页大小
 * @param {number} [id] - 材料ID（可选）
 * @param {string} [type] - 材料类型（可选）
 * @param {string} [createdAtStart] - 创建时间起始（ISO格式字符串，可选）
 * @param {string} [createdAtEnd] - 创建时间结束（ISO格式字符串，可选）
 * @param {string} [updatedAtStart] - 更新时间起始（ISO格式字符串，可选）
 * @param {string} [updatedAtEnd] - 更新时间结束（ISO格式字符串，可选）
 * @param {number} [courseId] - 所属课程ID（可选）
 * @returns {Promise<any>} 分页材料列表
 */
export function getMaterialsPage(pageNo, pageSize, id, type, createdAtStart, createdAtEnd, updatedAtStart, updatedAtEnd, courseId) {
    return request.get(`/api/teaching/material/${pageNo}/${pageSize}`, {
        params: {
            id,
            type,
            createdAtStart,
            createdAtEnd,
            updatedAtStart,
            updatedAtEnd,
            courseId
        }
    });
}