// api.js
import axios from "axios";

// 创建 axios 实例
const service = axios.create({
  baseURL: "/sdk-api", // 基础URL，可根据环境变量配置
  timeout: 10000, // 超时时间，毫秒
});

// 全局存储chatToken的变量
let globalChatToken = null;

// 设置全局chatToken的函数
export function setGlobalChatToken(token) {
  globalChatToken = token;
}

// 请求拦截器（可选：比如添加 token）
service.interceptors.request.use(
  (config) => {
    // 如果没有token，拒绝请求
    if (!globalChatToken) {
      return Promise.reject(new Error("No authentication token available"));
    }

    config.headers["Authorization"] = `Bearer ${globalChatToken}`;
    return config;
  },
  (error) => {
    return Promise.reject(error);
  },
);

// 响应拦截器
service.interceptors.response.use(
  (response) => {
    // 一般 API 都把数据放 response.data 里
    return response.data;
  },
  (error) => {
    // 可在此统一处理错误
    // 比如弹出提示、登录过期等
    return Promise.reject(error);
  },
);

// 封装 GET 方法
export function get(url, params = {}, config = {}) {
  return service.get(url, { params, ...config });
}

// 封装 POST 方法
export function post(url, data = {}, config = {}) {
  return service.post(url, data, config);
}

// 其它方法可按需添加
export default service;
