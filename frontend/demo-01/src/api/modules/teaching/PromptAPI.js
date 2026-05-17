import request from "@/api/request";

// model
/**
 * 后端统一返回结构（com.hbwl.common.Result）。
 * @template T
 * @typedef {Object} BackendResult
 * @property {number} code - 业务状态码
 * @property {string} message - 响应消息
 * @property {T} data - 业务数据
 */

/**
 * 后端实体类设计：com.hbwl.pojo.ai.PromptInfo
 * @typedef {Object} PromptInfo
 * @property {number|null} id - 提示词ID（自增主键，可为空）
 * @property {string} name - txt文件名称
 * @property {string} description - 提示词描述
 * @property {string} creatorId - 创建者ID
 */

// API
/**
 * 新增提示词（对应后端 POST /api/teaching/ai/prompt）。
 *
 * @param {PromptInfo} promptInfo - 提示词元数据（对应 @RequestPart("promptInfo")）
 * @param {string} promptString - 提示词正文内容（对应 @RequestPart("promptString")）
 * @returns {Promise<BackendResult<string>>} 新增结果
 */
export function addPrompt(promptInfo, promptString) {
	const formData = new FormData();
	formData.append("promptInfo", JSON.stringify(promptInfo));
	formData.append("promptString", promptString);

	return request({
		url: "/api/teaching/ai/prompt",
		method: "POST",
		data: formData,
		headers: {
			"Content-Type": "multipart/form-data",
		},
	});
}

/**
 * 删除提示词（对应后端 DELETE /api/teaching/ai/prompt/{id}）。
 *
 * @param {number} id - 提示词ID（路径参数）
 * @returns {Promise<BackendResult<string>>} 删除结果
 */
export function deletePrompt(id) {
	return request({
		url: `/api/teaching/ai/prompt/${id}`,
		method: "DELETE",
	});
}

/**
 * 更新提示词（对应后端 PUT /api/teaching/ai/prompt）。
 *
 * @param {PromptInfo} promptInfo - 提示词元数据（需包含待更新记录ID）
 * @param {string} promptString - 更新后的提示词正文内容
 * @returns {Promise<BackendResult<string>>} 更新结果
 */
export function updatePrompt(promptInfo, promptString) {
	const formData = new FormData();
	formData.append("promptInfo", JSON.stringify(promptInfo));
	formData.append("promptString", promptString);

	return request({
		url: "/api/teaching/ai/prompt",
		method: "PUT",
		data: formData,
		headers: {
			"Content-Type": "multipart/form-data",
		},
	});
}

/**
 * 查询提示词列表（对应后端 GET /api/teaching/ai/prompt）。
 *
 * @param {number} [id] - 提示词ID（可选查询条件）
 * @param {string} [creatorId] - 创建者ID（可选查询条件）
 * @returns {Promise<BackendResult<PromptInfo[]>>} 查询结果
 */
export function getPromptInfos(id, creatorId) {
	return request({
		url: "/api/teaching/ai/prompt",
		method: "GET",
		params: {
			id,
			creatorId,
		},
	});
}

/**
 * 设置运行时提示词（对应后端 PUT /api/teaching/ai/prompt/setRuntime）。
 *
 * @param {number} id - 需要设为运行时提示词的ID（请求体为纯数字）
 * @returns {Promise<BackendResult<string>>} 设置结果
 */
export function setRuntimePrompt(id) {
	return request({
		url: "/api/teaching/ai/prompt/setRuntime",
		method: "PUT",
		data: JSON.stringify(id),
		headers: {
			"Content-Type": "application/json",
		},
	});
}

/**
 * 加载指定提示词正文（对应后端 GET /api/teaching/ai/prompt/load）。
 * 后端会校验 role，但该字段不需要前端在此方法中显式携带。
 *
 * @param {number} id - 提示词ID（查询参数）
 * @returns {Promise<BackendResult<string>>} 提示词正文加载结果
 */
export function loadPrompt(id) {
	return request({
		url: "/api/teaching/ai/prompt/load",
		method: "GET",
		params: {
			id,
		},
	});
}

/**
 * 获取当前运行时提示词正文（对应后端 GET /api/teaching/ai/prompt/getRuntime）。
 * 后端会校验 role，但该字段不需要前端在此方法中显式携带。
 *
 * @returns {Promise<BackendResult<string>>} 当前运行时提示词获取结果
 */
export function getRuntimePrompt() {
	return request({
		url: "/api/teaching/ai/prompt/getRuntime",
		method: "GET",
	});
}
