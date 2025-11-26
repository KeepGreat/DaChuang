import { users } from "./mock-data.js";

export default [
  // register
  {
    url: "/api/auth/register",
    method: "post",
    response: (req) => {
      try {
        console.log("req :", req);

        const { username, password, email } = req.body;

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
          id: users.length + 1,
          username,
          password,
          email: email || "",
          role: "user",
        };

        users.push(newUser);

        return {
          code: 200,
          message: "注册成功",
          data: {
            id: newUser.id,
            username: newUser.username,
            email: newUser.email,
            role: newUser.role,
          },
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
    url: "/api/auth/login",
    method: "post",
    response: (req) => {
      try {
        console.log("req :", req);
        const { username, password } = req.body;

        // 首先检查用户是否存在
        const user = users.find((u) => u.username === username);

        if (!user) {
          // 用户不存在返回404错误
          return {
            code: 404,
            message: "用户不存在",
            data: null,
          };
        }

        if (user.password !== password) {
          // 用户存在但密码错误返回401错误
          return {
            code: 401,
            message: "密码错误",
            data: null,
          };
        }

        return {
          code: 200,
          message: "登录成功",
          data: {
            id: user.id,
            username: user.username,
            email: user.email,
            role: user.role,
          },
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
];
