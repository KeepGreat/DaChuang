import { users } from "../user/mock-data";

/**
 * 生成可读的日期时间格式:
 * @returns {string} - YYYY-MM-DD-HH-mm-ss格式的日期时间字符串
 */
function getDateTimeString() {
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
 * 模拟生成JWT token
 * @param user - 用户对象，包含id属性
 * @returns {string} - 模拟的JWT token
 */
function generateToken(user) {
  return `mock-token-${user.username}-${user.role}-${getDateTimeString()}`;
}

export default [
  // register
  {
    url: "/api/user/register",
    method: "post",
    response: (req) => {
      try {
        console.log("req :", req);

        const { username, password, role } = req.body;

        // 检查用户名是否已存在
        const existingUser = users.find((u) => u.username === username);
        if (existingUser) {
          return {
            code: 400,
            message: "用户名已存在",
            data: null,
          };
        }

        // 创建新用户
        const newUser = {
          id: users.length > 0 ? Math.max(...users.map((u) => u.id)) + 1 : 1,
          username,
          password,
          role,
        };

        console.log("newUser :", newUser);
        users.push(newUser);

        return {
          code: 200,
          message: "注册成功",
          data: null,
        };
      } catch (error) {
        console.error("register error:", error);
        return {
          code: 500,
          message: "Internal Server Error",
          data: null,
        };
      }
    },
  },
  // login
  {
    url: "/api/authenticate",
    method: "post",
    response: (req) => {
      try {
        console.log("req :", req);
        const { username, password } = req.body;

        // 首先检查用户是否存在
        const user = users.find((u) => u.username === username);

        // 用户不存在返回404错误
        if (!user) {
          return {
            code: 404,
            message: "用户不存在",
            data: null,
          };
        }

        // 用户存在但密码错误返回401错误
        if (user.password !== password) {
          return {
            code: 401,
            message: "密码错误",
            data: null,
          };
        }

        const token = generateToken(user);
        console.log(`token for ${username} is ${token}`);

        return {
          code: 200,
          message: "登录成功",
          data: token,
        };
      } catch (error) {
        console.error("login error:", error);
        return {
          code: 500,
          message: "Internal Server Error",
          data: null,
        };
      }
    },
  },

  // refreshToken
  {
    url: "/api/refreshtoken",
    method: "post",
    response: (req) => {
      try {
        console.log("req:", req);
        const { token, userId } = req.body;

        if (!token || !userId) {
          return {
            code: 400,
            message: "token 和 userId 不能为空",
            data: null,
          };
        }

        // 简单验证：userId 是否存在
        const user = users.find((u) => u.id === Number(userId));
        if (!user) {
          return {
            code: 404,
            message: "用户不存在",
            data: null,
          };
        }

        // 模拟token刷新
        const newToken = generateToken(user);

        return {
          code: 200,
          message: "Token 刷新成功",
          data: newToken,
        };
      } catch (error) {
        console.error("refreshToken error:", error);
        return {
          code: 500,
          message: "Internal Server Error",
          data: null,
        };
      }
    },
  },
];
