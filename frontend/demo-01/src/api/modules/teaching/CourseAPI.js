import request from "@/api/request";

// model
/**
 * 课程实体类
 * @typedef {Object} Course
 * @property {number|null} id - 课程ID
 * @property {string} name - 课程名称
 * @property {string} description - 课程描述
 * @property {number} sectionId - 章节ID
 */

// API
/**
 * 添加课程
 * @param {Course} course - 课程信息
 * @returns {Promise<any>} 添加结果
 */
export function addCourse(course) {
  return request({
    url: "/api/teaching/course",
    method: "POST",
    data: course
  });
}

/**
 * 根据ID删除课程
 * @param {number} id - 课程ID
 * @returns {Promise<any>} 删除结果
 */
export function deleteCourse(id) {
  return request({
    url: `/api/teaching/course/${id}`,
    method: "DELETE"
  });
}

/**
 * 更新课程信息
 * @param {Course} course - 课程信息
 * @returns {Promise<any>} 更新结果
 */
export function updateCourse(course) {
  return request({
    url: "/api/teaching/course",
    method: "PUT",
    data: course
  });
}

/**
 * 获取课程列表（根据条件查询）
 * @param {number} [id] - 课程ID（可选）
 * @param {string} [name] - 课程名称（可选）
 * @param {number} [sectionId] - 章节ID（可选）
 * @returns {Promise<any>} 课程列表
 */
export function getCourses(id, name, sectionId) {
  return request({
    url: "/api/teaching/course",
    method: "GET",
    params: {
      id,
      name,
      sectionId
    }
  });
}

/**
 * 分页获取课程列表
 * @param {number} pageNo - 页码
 * @param {number} pageSize - 每页大小
 * @param {number} [id] - 课程ID（可选）
 * @param {string} [name] - 课程名称（可选）
 * @param {number} [sectionId] - 章节ID（可选）
 * @returns {Promise<any>} 分页课程列表
 */
export function getCoursesPage(pageNo, pageSize, id, name, sectionId) {
  return request({
    url: `/api/teaching/course/${pageNo}/${pageSize}`,
    method: "GET",
    params: {
      id,
      name,
      sectionId
    }
  });
}
