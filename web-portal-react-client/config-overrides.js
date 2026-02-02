const { override, addWebpackAlias } = require('customize-cra');
const path = require('path');
const webpack = require('webpack');

module.exports = override(
  // 添加别名
  addWebpackAlias({
    '@': path.resolve(__dirname, 'src'),
    // 更多别名...
  }),

  // 添加插件
  (config) => {
    config.resolve.fallback = {
      "crypto": require.resolve("crypto-browserify"),
      "stream": require.resolve("stream-browserify")
    };

    return config;
  }
);
