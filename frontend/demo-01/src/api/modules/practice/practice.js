import request from "@/api/request";

// model
/**
 * 练习实体类
 * @typedef {Object} Practice
 * @property {number|null} id - 练习ID（自增主键）
 * @property {string} name - 练习名称
 * @property {number} questionNum - 问题数量
 * @property {number} practiceTypeId - 练习类型ID
 * @property {string} createdAt - 创建时间（ISO格式字符串，格式：yyyy-mm-ddThh:mm:ss）
 * @property {string|null} expiredAt - 过期时间（ISO格式字符串，格式：yyyy-mm-ddThh:mm:ss）
 */

/**
 * 练习索引实体类
 * @typedef {Object} PracticeIndex
 * @property {number|null} id - 索引ID（自增主键）
 * @property {number} practiceId - 练习ID
 * @property {number} courseSectionId - 课程章节ID
 * @property {number} courseId - 课程对应ID，若无则为0，即为整个课程的练习
 */

/**
 * 练习类型实体类
 * @typedef {Object} PracticeType
 * @property {number|null} id - 练习类型ID（自增主键）
 * @property {string} name - 练习类型名称
 */

/**
 * 练习DTO实体类
 * @typedef {Object} PracticeDTO
 * @property {Practice} practice - 练习信息
 * @property {PracticeIndex} practiceIndex - 练习索引信息
 */

// API
/**
 * 添加练习
 * @param {PracticeDTO} practiceDTO - 练习DTO信息（包含practice和practiceIndex）
 * @returns {Promise<any>} 添加结果
 */
export function addPractice(practiceDTO) {
  return request({
    url: "/api/practice/practice",
    method: "POST",
    data: practiceDTO,
  });
}

/**
 * 根据ID删除练习
 * @param {number} id - 练习ID
 * @returns {Promise<any>} 删除结果
 */
export function deletePracticeById(id) {
  return request({
    url: `/api/practice/practice/${id}`,
    method: "DELETE",
  });
}

/**
 * 根据课程索引删除练习
 * @param {number} courseSectionId - 课程章节ID
 * @param {number} [courseId] - 课程ID（可选）
 * @returns {Promise<any>} 删除结果
 */
export function deletePracticesByIndex(courseSectionId, courseId) {
  return request({
    url: "/api/practice/practice",
    method: "DELETE",
    params: {
      courseSectionId,
      courseId
    },
  });
}

/**
 * 更新练习信息
 * @param {Practice} practice - 练习信息（id必填，其他字段可选）
 * @returns {Promise<any>} 更新结果
 */
export function updatePractice(practice) {
  return request({
    url: "/api/practice/practice",
    method: "PUT",
    data: practice,
  });
}

/**
 * 获取练习列表（根据条件查询）
 * @param {number} [id] - 练习ID（可选）
 * @param {string} [name] - 练习名称（可选）
 * @param {number} [practiceTypeId] - 练习类型ID（可选）
 * @param {string} [createdAtStart] - 创建时间起始（ISO格式字符串，可选）
 * @param {string} [createdAtEnd] - 创建时间结束（ISO格式字符串，可选）
 * @param {string} [expiredAtStart] - 过期时间起始（ISO格式字符串，可选）
 * @param {string} [expiredAtEnd] - 过期时间结束（ISO格式字符串，可选）
 * @returns {Promise<any>} 练习列表
 */
export function getPractices(id, name, practiceTypeId, createdAtStart, createdAtEnd, expiredAtStart, expiredAtEnd) {
  return request({
    url: "/api/practice/practice",
    method: "GET",
    params: {
      id,
      name,
      practiceTypeId,
      createdAtStart,
      createdAtEnd,
      expiredAtStart,
      expiredAtEnd
    },
  });
}

/**
 * 分页获取练习列表
 * @param {number} pageNo - 页码
 * @param {number} pageSize - 每页大小
 * @param {number} [id] - 练习ID（可选）
 * @param {string} [name] - 练习名称（可选）
 * @param {number} [practiceTypeId] - 练习类型ID（可选）
 * @param {string} [createdAtStart] - 创建时间起始（ISO格式字符串，可选）
 * @param {string} [createdAtEnd] - 创建时间结束（ISO格式字符串，可选）
 * @param {string} [expiredAtStart] - 过期时间起始（ISO格式字符串，可选）
 * @param {string} [expiredAtEnd] - 过期时间结束（ISO格式字符串，可选）
 * @returns {Promise<any>} 分页练习列表
 */
export function getPracticesPage(pageNo, pageSize, id, name, practiceTypeId, createdAtStart, createdAtEnd, expiredAtStart, expiredAtEnd) {
  return request({
    url: `/api/practice/practice/${pageNo}/${pageSize}`,
    method: "GET",
    params: {
      id,
      name,
      practiceTypeId,
      createdAtStart,
      createdAtEnd,
      expiredAtStart,
      expiredAtEnd
    },
  });
}

/**
 * 根据课程索引获取练习列表
 * @param {number} courseSectionId - 课程章节ID
 * @param {number} [courseId] - 课程ID（可选）
 * @returns {Promise<any>} 练习列表
 */
export function getPracticesByIndex(courseSectionId, courseId) {
  return request({
    url: "/api/practice/practice/index",
    method: "GET",
    params: {
      courseSectionId,
      courseId
    },
  });
}

/**
 * 分页根据课程索引获取练习列表
 * @param {number} pageNo - 页码
 * @param {number} pageSize - 每页大小
 * @param {number} courseSectionId - 课程章节ID
 * @param {number} [courseId] - 课程ID（可选）
 * @returns {Promise<any>} 分页练习列表
 */
export function getPracticesPageByIndex(pageNo, pageSize, courseSectionId, courseId) {
  return request({
    url: `/api/practice/practice/index/${pageNo}/${pageSize}`,
    method: "GET",
    params: {
      courseSectionId,
      courseId
    },
  });
}

/**
 * 添加练习类型
 * @param {PracticeType} practiceType - 练习类型信息
 * @returns {Promise<any>} 添加结果
 */
export function addPracticeType(practiceType) {
  return request({
    url: "/api/practice/practice/type",
    method: "POST",
    data: practiceType,
  });
}

/**
 * 根据ID删除练习类型
 * @param {number} id - 练习类型ID
 * @returns {Promise<any>} 删除结果
 */
export function deletePracticeType(id) {
  return request({
    url: `/api/practice/practice/type/${id}`,
    method: "DELETE",
  });
}

/**
 * 更新练习类型信息
 * @param {PracticeType} practiceType - 练习类型信息
 * @returns {Promise<any>} 更新结果
 */
export function updatePracticeType(practiceType) {
  return request({
    url: "/api/practice/practice/type",
    method: "PUT",
    data: practiceType,
  });
}

/**
 * 获取练习类型列表（根据条件查询）
 * @param {number} [id] - 练习类型ID（可选）
 * @param {string} [name] - 练习类型名称（可选）
 * @returns {Promise<any>} 练习类型列表
 */
export function getPracticeTypes(id, name) {
  return request({
    url: "/api/practice/practice/type",
    method: "GET",
    params: {
      id,
      name
    },
  });
}
