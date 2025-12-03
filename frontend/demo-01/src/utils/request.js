import { useUserStore } from "@/store";
import axios from "axios";
import { BusinessError } from "./error.js";

const request = axios.create({
  // 后端接口地址
  // baseURL: "http://localhost:80",
  // Mock 接口地址
  baseURL: "http://localhost:5173",
  timeout: 50000,
});

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    // 如果接口方法定义的 JwtToken 设置为 no-auth，则不携带 Token，例如register, login等
    if (config.headers.JwtToken === "no-auth") {
      delete config.headers.JwtToken;
      return config;
    }

    // 创建userStore实例
    const userStore = useUserStore();

    // TODO 暂时注释登录和token检查，后续功能完善后再处理，这里需要检查token是否存在
    /* 
    if (!userStore.token) {
			console.warn("用户未登录");
      return Promise.reject(new Error("用户未登录"));
    }
		*/

    // 在Headers携带token，key是JwtToken，value是实际的token
    // TODO 暂时这样处理，避免传undefined
    if (userStore.authHeader) {
      config.headers.JwtToken = userStore.authHeader;
    } else {
      console.warn("authHeader 为空，未携带 JwtToken 请求头");
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
    // 记录响应信息
    console.log(`${new Date().toLocaleTimeString()} response interceptors:`, response);
    console.log("response.data :", response.data);

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
