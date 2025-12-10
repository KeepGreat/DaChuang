import request from "@/utils/request";

/**
 * @import { ApiResponse, ApiEmptyResponse } from "@/utils/types"
 */

/**
 * @param {Object} data
 * @param {string} data.username - 用户名
 * @param {string} data.password - 密码
 * @param {string} data.role - 用户角色，枚举值："admin"、"student"、"teacher"
 * @returns {Promise<ApiEmptyResponse>} null
 */
export function register(data) {
  return request({
    url: "/user/register",
    method: "POST",
    data: data,
    headers: {
      JwtToken: "no-auth",
    },
  });
}

/**
 * @param {Object} data
 * @param {string} data.username - 用户名
 * @param {string} data.password - 密码
 * @returns {Promise<ApiResponse<string>>} JWT token
 */
export function login(data) {
  return request({
    url: "/authenticate",
    method: "POST",
    data: data,
    headers: {
      JwtToken: "no-auth",
    },
  });
}

/**
 * @param {Object} data
 * @param {string} data.token - JWT token
 * @returns {Promise<ApiResponse<string>>} 用户权限角色，"admin"、"student"、"teacher"
 */
export function getUserRole(data) {
  return request({
    url: "/identify",
    method: "POST",
    data: data,
    headers: {
      JwtToken: "no-auth",
    },
  });
}

/**
 * @param {Object} data
 * @param {string} data.oldToken - 当前有效的token
 * @returns {Promise<ApiResponse<string>>} 新的JWT token
 */
export function refreshToken(data) {
  return request({
    url: "/refreshtoken",
    method: "POST",
    data: data,
    headers: {
      JwtToken: "no-auth",
    },
  });
}