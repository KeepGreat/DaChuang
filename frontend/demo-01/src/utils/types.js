// 通用的类型定义

/**
 * 通用API响应结构，用于有数据返回的接口
 * @template T - 响应数据的类型泛型
 * @typedef {Object} ApiResponse
 * @property {number} code - 响应状态码
 * @property {string} message - 响应消息
 * @property {T} data - 响应数据，确保不为null
 */

/**
 * 空响应API响应结构，用于无数据返回的接口，确保data为null
 * @typedef {Object} ApiEmptyResponse
 * @property {number} code - 响应状态码
 * @property {string} message - 响应消息
 * @property {null} data - 响应数据，固定为null
 */

/**
 * 分页响应结构，MyBatis-Plus 的 Page<T> 的部分属性
 * https://baomidou.com/plugins/pagination/
 * @template T - 列表项的数据类型
 * @typedef {Object} Page
 * @property {T[]} records - 查询数据列表，List<T>
 * @property {number} total - 查询列表总记录数
 * @property {number} current - 当前页码，从 1 开始
 * @property {number} size - 每页显示条数，默认 10
 * @property {number} pages - 总页数，根据 total 和 size 自动计算得出
 */

// 使用ES模块的空导出，确保文件被识别为ES模块
export { };

