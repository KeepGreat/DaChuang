// vite.config.js
import vue from "@vitejs/plugin-vue";
import { codeInspectorPlugin } from "code-inspector-plugin";
import path from "path";
import { defineConfig } from "vite";
import { viteMockServe } from "vite-plugin-mock";

export default defineConfig({
  // 设置scss的api类型为modern-compiler，否则会出现： Deprecation Warning [legacy-js-api]: The legacy JS API is deprecated and will be removed in Dart Sass 2.0.0. More info: https://sass-lang.com/d/legacy-js-api
  css: {
    preprocessorOptions: {
      scss: {
        api: "modern-compiler",
      },
    },
  },
  plugins: [
    vue(),
    codeInspectorPlugin({
      bundler: "vite",
      editor: "code", // 指定 IDE 为 vscode https://inspector.fe-dev.cn/api/basic.html#editor
    }),
    // 添加 vite-plugin-mock 配置，https://www.npmjs.com/package/vite-plugin-mock/v/2.9.6
    viteMockServe({
      // 启用 mock 功能
      enable: true,
      // mock文件目录
      mockPath: "./src/mock",
      // 监视 mock 文件变化
      watchFiles: true,
      // 开启日志查看拦截情况
      logger: true,
    }),
  ],
  resolve: {
    alias: {
      "@": path.resolve(__dirname, "./src"),
    },
  },
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:80',
        changeOrigin: true,
        configure: (proxy, options) => {
          proxy.on('error', (err, req, res) => {
            console.log('代理错误，后端服务未启动');
            res.writeHead(404, { 'Content-Type': 'application/json' });
            res.end(JSON.stringify({ error: '后端服务未启动' }));
          });
        }
      }
    }
  }
});
