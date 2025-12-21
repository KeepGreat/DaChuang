import request from "@/utils/request";

/**
 * @import { ApiResponse, ApiEmptyResponse, Page } from "@/utils/types"
 */

// model
/**
 * 用户答案实体类
 * @typedef {Object} UserAnswer
 * @property {number} id - 用户答案id
 * @property {string} content - 用户答案
 * @property {string} userId - 用户id
 * @property {number} questionId - 问题id
 * @property {number} questionType - 问题类型，0:判断，1:选择，2:简答，3:编程
 * @property {number} score - 得分应小于等于问题分值，-1代表未批改
 * @property {string} comment - 批注，教师或AI对用户答案的评价
 */

/**
 * 代码沙箱输入
 * @typedef {Object} CodeSandboxInput
 * @property {string} codeLanguage
 * @property {string} code
 * @property {string} input
 */

/**
 * 分析结果输出
 * @typedef {Object} AnalysisOutput
 * @property {string} analysis
 * @property {Object<string, number>} score - Map<String, Integer> score
 */

/**
 * 代码沙箱输出
 * @typedef {Object} CodeSandboxOutput
 * @property {string} stout
 * @property {string} stderr
 * @property {string} status
 * @property {number} exitstatus
 * @property {number} memory
 * @property {number} runTime
 * @property {number} procPeak
 * @property {number} time
 */

// request
/**
 * 新增用户答案请求参数
 * @typedef {Object} CreateUserAnswerRequest
 * @property {string} content
 * @property {string} userId
 * @property {number} questionId
 * @property {number} questionType - 问题类型，0:判断，1:选择，2:简答，3:编程（必填）
 */

/**
 * 更新用户答案请求参数
 * @typedef {Object} UpdateUserAnswerRequest
 * @property {number} id
 * @property {string} [content]
 * @property {string} [userId]
 * @property {number} [questionId]
 * @property {number} [questionType]
 * @property {string} [comment]
 */

/**
 * 代码判题请求参数 UserAnswerInputDTO
 * @typedef {Object} JudgeCodeRequest
 * @property {CodeSandboxInput} codeSandboxInput
 * @property {UserAnswer} userAnswer
 */

// response
/**
 * 代码判题响应 UserAnswerOutputDTO
 * @typedef {Object} JudgeCodeResponse
 * @property {UserAnswer} userAnswer
 * @property {CodeSandboxOutput} codeSandboxOutput
 * @property {AnalysisOutput} analysisOutput
 */

/**
 * 增加用户答案记录
 * @param {CreateUserAnswerRequest} data - 用户答案信息，要求content、userId、questionId、questionType不能为空
 * @returns {Promise<ApiEmptyResponse>}
 */
export function createUserAnswer(data) {
  return request({
    url: "/api/practice/useranswer",
    method: "POST",
    data: data,
  });
}

/**
 * 根据id删除用户答案记录
 * @param {number} id - 用户答案ID
 * @returns {Promise<ApiEmptyResponse>}
 */
export function deleteUserAnswerById(id) {
  return request({
    url: "/api/practice/useranswer",
    method: "DELETE",
    params: { id },
  });
}

/**
 * 根据用户id删除所有相关的用户答案记录
 * @param {string} id - 用户ID
 * @returns {Promise<ApiEmptyResponse>}
 */
export function deleteUserAnswerByUserId(id) {
  return request({
    url: "/api/practice/useranswer",
    method: "DELETE",
    params: { id },
  });
}

/**
 * 根据id更新用户答案记录
 * @param {UpdateUserAnswerRequest} data - 更新请求参数，id不能为空，要更新的字段不能为空
 * @returns {Promise<ApiEmptyResponse>}
 */
export function updateUserAnswerById(data) {
  return request({
    url: "/api/practice/useranswer",
    method: "PUT",
    data: data,
  });
}

/**
 * 查询用户答案记录
 * @param {Object} [params] - 查询参数（均为可选）
 * @param {number} [params.id] - 用户答案ID
 * @param {string} [params.userId] - 用户ID
 * @param {number} [params.questionId] - 问题ID
 * @param {number} [params.questionType] - 问题类型
 * @param {number} [params.score] - 得分
 * @returns {Promise<ApiResponse<UserAnswer[]>>} 用户答案列表
 */
export function getUserAnswers(params = {}) {
  return request({
    url: "/api/practice/useranswer",
    method: "GET",
    params: params,
  });
}

/**
 * 分页查询用户答案记录
 * @param {Object} pageParams - 分页参数
 * @param {number} pageParams.pageNo - 当前页码（从1开始）
 * @param {number} pageParams.pageSize - 每页显示条数
 * @param {Object} [queryParams] - 查询参数（均为可选）
 * @param {number} [queryParams.id] - 用户答案ID
 * @param {string} [queryParams.userId] - 用户ID
 * @param {number} [queryParams.questionId] - 问题ID
 * @param {number} [queryParams.questionType] - 问题类型
 * @param {number} [queryParams.score] - 得分
 * @returns {Promise<ApiResponse<Page<UserAnswer>>>} 分页用户答案列表
 */
export function getUserAnswersPage(pageParams, queryParams = {}) {
  const { pageNo, pageSize } = pageParams;
  return request({
    url: `/api/practice/useranswer/${pageNo}/${pageSize}`,
    method: "GET",
    params: queryParams,
  });
}

/**
 * 自动判题，判断常规题中的非简答题，直接进行字符串比较来判题，并将分数写在score字段中
 * @param {UserAnswer[]} data - 自动判题请求列表，每个元素的questionID不能为空
 * @returns {Promise<ApiResponse<UserAnswer[]>>} 判题后的用户答案列表
 */
export function judgeAnswersAuto(data) {
  return request({
    url: "/api/practice/useranswer/judge/auto",
    method: "POST",
    data: data,
  });
}

/**
 * 手动判题，判断常规题中的简答题，教师在前端输入好分数后传给后端存入数据库，若comment字段不为空则存入数据库
 * @param {UserAnswer} data - 手动判题请求参数，id和score不能为空
 * @returns {Promise<ApiEmptyResponse>}
 */
export function judgeAnswerManual(data) {
  return request({
    url: "/api/practice/useranswer/judge/manual",
    method: "POST",
    data: data,
  });
}

/**
 * 代码判题，判断编程题，并将AI分析结果写入UserAnswer中的comment
 * @param {JudgeCodeRequest} data - 判题请求参数，要求codeSandboxInput和userAnswer不能为空
 * @returns {Promise<ApiResponse<JudgeCodeResponse>>} 代码判题响应
 */
export function judgeCodeAnswer(data) {
  return request({
    url: "/api/practice/useranswer/judge/code",
    method: "POST",
    data: data,
  });
}
