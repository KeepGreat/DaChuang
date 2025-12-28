import { useUserStore } from "@/store";
import axios from "axios";
import { BusinessError } from "./error.js";

const request = axios.create({
  // 本地后端接口地址
  baseURL: "http://localhost:80",
  // Mock 接口地址
  // baseURL: "http://localhost:5173",
  // 后端接口地址
  // baseURL: "http://192.168.42.88:80",
  timeout: 60000,
});

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    // 如果接口方法定义的 JwtToken 设置为 no-auth，则不携带 Token，例如register, login等
    if (config.headers.JwtToken === "no-auth") {
      delete config.headers.JwtToken;
      return config;
    }

    // 检查token是否存在
    const userStore = useUserStore();
    if (userStore.token) {
      config.headers.JwtToken = userStore.token;
    } else {
      console.warn("用户未登录");
      return Promise.reject(new Error("用户未登录，请先登录"));
    }
    return config;
  },
  (error) => {
    console.error(`${new Date().toLocaleTimeString()} request error:`, error);
    return Promise.reject(error);
  }
);
// 响应拦截器
request.interceptors.response.use(
  (response) => {
    // 检查响应数据是否为HTML，说明相应的Mock接口未实现
    if (
      typeof response.data === "string" &&
      response.data.trim().startsWith("<!DOCTYPE html>")
    ) {
      console.error(
        `${new Date().toLocaleTimeString()} Response Error: 请求的接口可能没有对应的Mock实现`,
        `URL: ${response.config.url}`,
        `Method: ${response.config.method?.toUpperCase()}`
      );

      // 返回一个标准的错误响应结构
      const mockNotFoundError = new BusinessError(
        404,
        `未找到相应的Mock接口: ${response.config.url}`,
        null
      );
      return Promise.reject(mockNotFoundError);
    }

    // 记录响应信息和调用堆栈
    console.log(`${new Date().toLocaleTimeString()} response interceptors:`, response);
    console.log("response.data :", response.data);

    // 取消注释可以查看接口的调用堆栈
    /* console.trace(
      `调用堆栈 - 接口: ${
        response.config.url
      }, 方法: ${response.config.method?.toUpperCase()}`
    ); */

    // 获取data结构
    const res = response.data;

    // 如果响应是二进制流，则直接返回，用于下载文件、Excel 导出等
    if (response.config.responseType === "blob") {
      return response;
    }

    // code在[200, 300)区间内时，视为业务成功响应
    if (res.code >= 200 && res.code < 300) {
      return res;
    }

    if(response.status >= 200 && response.status < 300){
      return response;
    }

    // 业务错误处理
    const businessError = new BusinessError(
      res.code,
      res?.message || "BusinessError default message",
      res?.data
    );
    console.error(
      `${new Date().toLocaleTimeString()} BusinessError [${res.code}]:`,
      businessError.message
    );
    return Promise.reject(businessError);
  },
  (error) => {
    console.error(`${new Date().toLocaleTimeString()} response error:`, error);
    return Promise.reject(error);
  }
);

export default request;
