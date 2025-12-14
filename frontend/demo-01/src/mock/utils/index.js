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
 * @param {string} [defaultType='number'] - 当列表为空时的默认ID类型，可选值：'number' | 'string'
 * @returns {number|string} - 下一个可用的ID，类型与第一个元素的id类型保持一致，返回相同类型
 */
export function getNextId(list, defaultType = "number") {
  // 若长度为0，则返回1作为第一个id，类型由defaultType决定
  if (list.length === 0) {
    return defaultType === "string" ? "1" : 1;
  }

  // 记录第一个元素的id类型
  let idType = typeof list[0].id;
  let maxId = 0;

  for (const item of list) {
    if (item.id == null) {
      throw new Error(`Invalid item: item 缺少 id 属性`);
    }

    // 转换为number进行比较
    const numId = typeof item.id === "string" ? parseInt(item.id) : item.id;
    if (isNaN(numId) || !Number.isInteger(numId) || numId < 0) {
      throw new Error(
        `Invalid id: id 必须是非负整数字符串或数字, 但得到 ${item.id} (${typeof item.id})`
      );
    }
    if (numId > maxId) {
      maxId = numId;
    }
  }

  const nextId = maxId + 1;
  // 根据第一个元素的id类型返回相应类型
  return idType === "string" ? String(nextId) : nextId;
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

/**
 * 模拟生成JWT token
 * @param {Object} user - 用户对象，包含username和role属性
 * @returns {string} - 模拟的JWT token
 */
export function generateToken(user) {
  return `${user.username}-${user.role}-${getDateTimeString()}`;
}

/**
 * 验证token并获取用户信息
 * @param {string} token - JWT token
 * @param {Array} users - 用户数据数组
 * @returns {Object|null} 用户信息或null
 */
export function validateToken(token, users) {
  if (!token) return null;

  // 从token中解析用户信息
  // 格式: username-role-YYYY-MM-DD-HH-mm-ss
  const match = token.match(/^(\w+)-(\w+)-\d{4}-\d{2}-\d{2}-\d{2}-\d{2}-\d{2}$/);
  if (!match) return null;

  const username = match[1];
  const role = match[2];

  return users.find((user) => user.username === username && user.role === role) || null;
}

/**
 * 创建分页结果对象
 * @param {Array} data - 要分页的数据数组
 * @param {number} pageNo - 当前页码
 * @param {number} pageSize - 每页大小
 * @returns {Object} - 分页结果对象，包含records、total、current、size、pages
 */
export function createPageResult(data, pageNo, pageSize) {
  const total = data.length;
  const pages = total > 0 ? Math.ceil(total / pageSize) : 0;
  const records = total > 0 ? data.slice((pageNo - 1) * pageSize, pageNo * pageSize) : [];

  const pageResult = {
    records,
    total,
    current: total > 0 ? pageNo : null,
    size: total > 0 ? pageSize : null,
    pages,
  };
  return pageResult;
}
