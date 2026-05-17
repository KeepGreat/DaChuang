import request from "@/api/request";

// model
/**
 * 课程章节实体类
 * @typedef {Object} CourseSection
 * @property {number|null} id - 课程章节ID
 * @property {string} name - 课程章节名称
 * @property {string} description - 课程章节描述
 * @property {number} courseSectionTypeId - 课程章节类型ID
 * @property {string} teacherId - 教师ID
 */

/**
 * 课程章节类型实体类
 * @typedef {Object} CourseSectionType
 * @property {number|null} id - 课程章节类型ID
 * @property {string} name - 课程章节类型名称
 */

// API
/**
 * 添加课程章节
 * @param {CourseSection} courseSection - 课程章节信息
 * @returns {Promise<any>} 添加结果
 */
export function addCourseSection(courseSection) {
  return request({
    url: "/api/teaching/coursesection",
    method: "POST",
    data: courseSection
  });
}

/**
 * 根据ID删除课程章节
 * @param {number} id - 课程章节ID
 * @returns {Promise<any>} 删除结果
 */
export function deleteCourseSection(id) {
  return request({
    url: `/api/teaching/coursesection/${id}`,
    method: "DELETE"
  });
}

/**
 * 更新课程章节信息
 * @param {CourseSection} courseSection - 课程章节信息
 * @returns {Promise<any>} 更新结果
 */
export function updateCourseSection(courseSection) {
  return request({
    url: "/api/teaching/coursesection",
    method: "PUT",
    data: courseSection
  });
}

/**
 * 获取课程章节列表（根据条件查询）
 * @param {number} [id] - 课程章节ID（可选）
 * @param {string} [name] - 课程章节名称（可选）
 * @param {number} [courseSectionTypeId] - 课程章节类型ID（可选）
 * @param {string} [teacherId] - 教师ID（可选）
 * @returns {Promise<any>} 课程章节列表
 */
export function getCourseSections(id, name, courseSectionTypeId, teacherId) {
  return request({
    url: "/api/teaching/coursesection",
    method: "GET",
    params: {
      id,
      name,
      courseSectionTypeId,
      teacherId
    }
  });
}

/**
 * 获取所有课程章节（不分页）
 * @param {string} [studentId] - 学生ID（可选）
 * @returns {Promise<any>} 所有课程章节列表
 */
export function getAllCourseSections(studentId) {
  return request({
    url: "/api/teaching/coursesection",
    method: "GET",
    params: {
      studentId
    }
  });
}

/**
 * 分页获取课程章节列表
 * @param {number} pageNo - 页码
 * @param {number} pageSize - 每页大小
 * @param {string} [studentId] - 学生ID（可选）
 * @param {number} [id] - 课程章节ID（可选）
 * @param {string} [name] - 课程章节名称（可选）
 * @returns {Promise<any>} 分页课程章节列表
 */
export function getCourseSectionsPage(pageNo, pageSize, studentId, id, name) {
  return request({
    url: `/api/teaching/coursesection/${pageNo}/${pageSize}`,
    method: "GET",
    params: {
      studentId,
      id,
      name
    }
  });
}

/**
 * 添加课程章节类型
 * @param {CourseSectionType} courseSectionType - 课程章节类型信息
 * @returns {Promise<any>} 添加结果
 */
export function addCourseSectionType(courseSectionType) {
  return request({
    url: "/api/teaching/coursesection/type",
    method: "POST",
    data: courseSectionType
  });
}

/**
 * 根据ID删除课程章节类型
 * @param {number} id - 课程章节类型ID
 * @returns {Promise<any>} 删除结果
 */
export function deleteCourseSectionType(id) {
  return request({
    url: `/api/teaching/coursesection/type/${id}`,
    method: "DELETE"
  });
}

/**
 * 更新课程章节类型信息
 * @param {CourseSectionType} courseSectionType - 课程章节类型信息
 * @returns {Promise<any>} 更新结果
 */
export function updateCourseSectionType(courseSectionType) {
  return request({
    url: "/api/teaching/coursesection/type",
    method: "PUT",
    data: courseSectionType
  });
}

/**
 * 获取课程章节类型列表（根据条件查询）
 * @param {number} [id] - 课程章节类型ID（可选）
 * @param {string} [name] - 课程章节类型名称（可选）
 * @returns {Promise<any>} 课程章节类型列表
 */
export function getCourseSectionTypes(id, name) {
  return request({
    url: "/api/teaching/coursesection/type",
    method: "GET",
    params: {
      id,
      name
    }
  });
}
