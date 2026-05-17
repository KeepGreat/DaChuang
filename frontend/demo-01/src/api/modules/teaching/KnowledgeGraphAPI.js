import request from "@/api/request";

// model（对应后端实体类）
/**
 * 对应后端实体类：com.hbwl.pojo.KnowledgeGraphNode
 * @typedef {Object} KnowledgeGraphNode
 * @property {number|null} [id] - 节点ID（自增主键）
 * @property {string} [name] - 知识点名称（新增时必填）
 * @property {number} [level] - 知识点等级，越低等级越高（新增时必填）
 * @property {string} [label] - 知识点标签（最长10字符）
 * @property {string} [description] - 知识点描述（最长100字符）
 * @property {string} [cognition] - 知识点认知维度（最长10字符）
 * @property {number} [courseSectionId] - 课程章节ID（新增时必填）
 */

/**
 * 对应后端实体类：com.hbwl.pojo.KnowledgeGraphEdge
 * @typedef {Object} KnowledgeGraphEdge
 * @property {number|null} [id] - 边ID（自增主键）
 * @property {number} [fromNodeId] - 起始节点ID（新增时必填）
 * @property {number} [toNodeId] - 目标节点ID（新增时必填）
 * @property {number} [relationType] - 关系类型：0父子，1前置，2后置，3关联（新增时必填）
 * @property {number} [courseSectionId] - 课程系列ID（新增时必填）
 */

/**
 * 对应后端实体类：com.hbwl.pojo.KnowledgeGraphNodeIndex
 * 针对这个实体类的特殊说明：该实体类表示知识点与资料的映射关系，我希望知识图谱中的知识节点可以关联到教学资料（如视频、文档等）。
 * 因此，KnowledgeGraphNodeIndex 实际上是一个关联表，用于存储知识点ID和资料ID的对应关系。
 * 具体的Material获取参考对应的接口文件
 * @typedef {Object} KnowledgeGraphNodeIndex
 * @property {number|null} [id] - 索引ID（自增主键）
 * @property {number} [nodeId] - 知识点ID（新增时必填）
 * @property {number} [materialId] - 资料ID（新增时必填）
 * @property {number} [courseSectionId] - 课程系列ID（新增时必填）
 */

// API
/**
 * 方法用途：新增知识图谱节点
 * @param {KnowledgeGraphNode} node - 节点信息（对应 KnowledgeGraphNode）
 * @returns {Promise<any>} 后端统一响应结果（Result）
 */
export function addKnowledgeGraphNode(node) {
	return request({
		url: "/api/teaching/knowledgegraph/node",
		method: "POST",
		data: node,
	});
}

/**
 * 方法用途：按ID删除知识图谱节点
 * @param {number} id - 节点ID
 * @returns {Promise<any>} 后端统一响应结果（Result）
 */
export function deleteKnowledgeGraphNode(id) {
	return request({
		url: `/api/teaching/knowledgegraph/node/${id}`,
		method: "DELETE",
	});
}

/**
 * 方法用途：更新知识图谱节点
 * @param {KnowledgeGraphNode} node - 节点信息（需包含 id）
 * @returns {Promise<any>} 后端统一响应结果（Result）
 */
export function updateKnowledgeGraphNode(node) {
	return request({
		url: "/api/teaching/knowledgegraph/node",
		method: "PUT",
		data: node,
	});
}

/**
 * 方法用途：按条件查询知识图谱节点
 * @param {number} [id] - 节点ID（可选）
 * @param {string} [name] - 知识点名称（可选）
 * @param {number} [level] - 知识点等级（可选）
 * @param {number} [courseSectionId] - 课程章节ID（可选）
 * @returns {Promise<any>} 后端统一响应结果（Result，data为节点列表）
 */
export function getKnowledgeGraphNodes(id, name, level, courseSectionId) {
	return request({
		url: "/api/teaching/knowledgegraph/node",
		method: "GET",
		params: {
			id,
			name,
			level,
			courseSectionId,
		},
	});
}

/**
 * 方法用途：新增知识图谱边
 * @param {KnowledgeGraphEdge} edge - 边信息（对应 KnowledgeGraphEdge）
 * @returns {Promise<any>} 后端统一响应结果（Result）
 */
export function addKnowledgeGraphEdge(edge) {
	return request({
		url: "/api/teaching/knowledgegraph/edge",
		method: "POST",
		data: edge,
	});
}

/**
 * 方法用途：按ID删除知识图谱边
 * @param {number} id - 边ID
 * @returns {Promise<any>} 后端统一响应结果（Result）
 */
export function deleteKnowledgeGraphEdge(id) {
	return request({
		url: `/api/teaching/knowledgegraph/edge/${id}`,
		method: "DELETE",
	});
}

/**
 * 方法用途：按课程章节ID删除该章节下整张知识图谱
 * @param {number} courseSectionId - 课程章节ID
 * @returns {Promise<any>} 后端统一响应结果（Result）
 */
export function deleteKnowledgeGraphByCourseSectionId(courseSectionId) {
	return request({
		url: `/api/teaching/knowledgegraph/courseSection/${courseSectionId}`,
		method: "DELETE",
	});
}

/**
 * 方法用途：更新知识图谱边
 * @param {KnowledgeGraphEdge} edge - 边信息（需包含 id）
 * @returns {Promise<any>} 后端统一响应结果（Result）
 */
export function updateKnowledgeGraphEdge(edge) {
	return request({
		url: "/api/teaching/knowledgegraph/edge",
		method: "PUT",
		data: edge,
	});
}

/**
 * 方法用途：按条件查询知识图谱边
 * @param {number} [id] - 边ID（可选）
 * @param {number} [fromNodeId] - 起始节点ID（可选）
 * @param {number} [toNodeId] - 目标节点ID（可选）
 * @param {number} [courseSectionId] - 课程系列ID（可选）
 * @returns {Promise<any>} 后端统一响应结果（Result，data为边列表）
 */
export function getKnowledgeGraphEdges(id, fromNodeId, toNodeId, courseSectionId) {
	return request({
		url: "/api/teaching/knowledgegraph/edge",
		method: "GET",
		params: {
			id,
			fromNodeId,
			toNodeId,
			courseSectionId,
		},
	});
}

/**
 * 方法用途：通过 Excel 导入知识图谱
 * @param {File|Blob} file - Excel 文件（表单字段名：file）
 * @param {number} courseSectionId - 课程章节ID（请求参数）
 * @returns {Promise<any>} 后端统一响应结果（Result）
 */
export function importKnowledgeGraphFromExcel(file, courseSectionId) {
	const formData = new FormData();
	formData.append("file", file);

	return request({
		url: "/api/teaching/knowledgegraph/import",
		method: "POST",
		params: { courseSectionId },
		data: formData,
		headers: {
			"Content-Type": "multipart/form-data",
		},
	});
}

/**
 * 方法用途：新增知识图谱节点索引（知识点-资料映射）
 * @param {KnowledgeGraphNodeIndex} index - 节点索引信息（对应 KnowledgeGraphNodeIndex）
 * @returns {Promise<any>} 后端统一响应结果（Result）
 */
export function addKnowledgeGraphNodeIndex(index) {
	return request({
		url: "/api/teaching/knowledgegraph/index",
		method: "POST",
		data: index,
	});
}

/**
 * 方法用途：按ID删除知识图谱节点索引
 * @param {number} id - 节点索引ID
 * @returns {Promise<any>} 后端统一响应结果（Result）
 */
export function deleteKnowledgeGraphNodeIndex(id) {
	return request({
		url: `/api/teaching/knowledgegraph/index/${id}`,
		method: "DELETE",
	});
}

/**
 * 方法用途：按条件查询知识图谱节点索引
 * @param {number} [id] - 节点索引ID（可选）
 * @param {number} [nodeId] - 知识点ID（可选）
 * @param {number} [materialId] - 资料ID（可选）
 * @param {number} [courseSectionId] - 课程系列ID（可选）
 * @returns {Promise<any>} 后端统一响应结果（Result，data为节点索引列表）
 */
export function getKnowledgeGraphNodeIndexes(id, nodeId, materialId, courseSectionId) {
	return request({
		url: "/api/teaching/knowledgegraph/index",
		method: "GET",
		params: {
			id,
			nodeId,
			materialId,
			courseSectionId,
		},
	});
}
