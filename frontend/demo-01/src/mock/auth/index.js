import { generateToken, getNextId, validateToken } from "../utils";
import { users } from "./mockData";

export const register = {
  url: "/user/register",
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
        id: getNextId(users),
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
};

export const login = {
  url: "/authenticate",
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
};

export const getUserRole = {
  url: "/identify",
  method: "post",
  response: (req) => {
    try {
      console.log("req:", req);
      const { token } = req.body;

      if (!token) {
        return {
          code: 400,
          message: "token 不能为空",
          data: null,
        };
      }

      const user = validateToken(token, users);

      if (!user) {
        return {
          code: 401,
          message: "无效的token",
          data: null,
        };
      }

      return {
        code: 200,
        message: "获取用户权限成功",
        data: user.role,
      };
    } catch (error) {
      console.error("identify error:", error);
      return {
        code: 500,
        message: "Internal Server Error",
        data: null,
      };
    }
  },
};

export const refreshToken = {
  url: "/refreshtoken",
  method: "post",
  response: (req) => {
    try {
      console.log("req:", req);
      const { oldToken } = req.body;

      if (!oldToken) {
        return {
          code: 400,
          message: "oldToken 不能为空",
          data: null,
        };
      }

      // 验证旧token是否有效
      const user = validateToken(oldToken, users);
      if (!user) {
        return {
          code: 401,
          message: "无效的token",
          data: null,
        };
      }

      // 生成新token
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
};

export default [register, login, getUserRole, refreshToken];
