const { createProxyMiddleware } = require('http-proxy-middleware');

module.exports = function(app) {
  app.use(
        '/api',
        createProxyMiddleware({
            target: 'http://1192.168.0.149:8080/',
            changeOrigin: true,
        })
    );
};
