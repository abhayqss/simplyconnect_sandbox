const { createProxyMiddleware } = require("http-proxy-middleware");

module.exports = function (app) {
  app.use(
    "/sdk-api",
    createProxyMiddleware({
      target: "https://chat-dev.micmd.com", // 目标服务器
      changeOrigin: true,
      pathRewrite: {
        "^/sdk-api": "/api",
      },
      onProxyReq: (proxyReq, req, res) => {
        // 打印每次代理请求的信息
        console.log(
          `[代理] ${req.method} ${req.originalUrl} => ${proxyReq.protocol}//${proxyReq.host}${proxyReq.path}`,
        );
      },
      onProxyRes: (proxyRes, req, res) => {
        // 可选：打印响应状态
        console.log(`[代理响应] ${req.method} ${req.originalUrl} <== ${proxyRes.statusCode}`);
      },
      onError: (err, req, res) => {
        console.error(`[代理错误] ${req.method} ${req.originalUrl}:`, err.message);
      },
    }),
  );
};
