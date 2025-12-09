/**
 * 生成可读的日期时间格式: YYYY-MM-DD-HH-mm-ss
 * @returns {string} - 格式化的日期时间字符串
 */
export function getDateTimeString() {
  const now = new Date();
  const year = String(now.getFullYear());
  const month = String(now.getMonth() + 1).padStart(2, "0");
  const day = String(now.getDate()).padStart(2, "0");
  const hours = String(now.getHours()).padStart(2, "0");
  const minutes = String(now.getMinutes()).padStart(2, "0");
  const seconds = String(now.getSeconds()).padStart(2, "0");

  return `${year}-${month}-${day}-${hours}-${minutes}-${seconds}`;
}

/**
 * 生成符合格式的当前日期时间（yyyy-mm-ddThh:mm:ss）
 * @returns {string} - ISO格式的日期时间字符串
 */
export function generateDateTime() {
  const now = new Date();
  const year = now.getFullYear();
  const month = String(now.getMonth() + 1).padStart(2, "0");
  const day = String(now.getDate()).padStart(2, "0");
  const hours = String(now.getHours()).padStart(2, "0");
  const minutes = String(now.getMinutes()).padStart(2, "0");
  const seconds = String(now.getSeconds()).padStart(2, "0");
  return `${year}-${month}-${day}T${hours}:${minutes}:${seconds}`;
}

/**
 * 生成下一个自增ID
 * @param {Array} list - 包含id属性的对象数组
 * @returns {number} - 下一个可用的ID
 */
export function getNextId(list) {
  return list.length > 0 ? Math.max(...list.map((item) => item.id)) + 1 : 1;
}

/**
 * 模拟生成JWT token
 * @param {Object} user - 用户对象，包含username和role属性
 * @returns {string} - 模拟的JWT token
 */
export function generateToken(user) {
  return `mock-token-${user.username}-${user.role}-${getDateTimeString()}`;
}

/**
 * 从URL中解析路径参数，解决路径参数和查询参数混合时的问题
 * @param {Object} req - 请求对象
 * @param {number} paramCount - 路径参数数量
 * @returns {Object} - 解析后的路径参数对象
 * @example parsePathParams(req, 2) // 返回 { param1: '1', param2: '15' }
 */
export function parsePathParams(req, paramCount) {
  const urlWithoutQuery = req.url.split("?")[0];
  const urlParts = urlWithoutQuery.split("/");

  const params = {};
  for (let i = 0; i < paramCount; i++) {
    params[`param${i + 1}`] = urlParts[urlParts.length - paramCount + i];
  }

  console.log("parsePathParams, params :", params);
  return params;
}
