// vite.config.js
import vue from "@vitejs/plugin-vue";
import path from "path";
import { defineConfig } from "vite";
import { codeInspectorPlugin } from "code-inspector-plugin";
import { viteMockServe } from "vite-plugin-mock";

export default defineConfig({
  plugins: [
    vue(),
    codeInspectorPlugin({
      bundler: "vite",
      editor: "code", // 指定 IDE 为 vscode https://inspector.fe-dev.cn/api/basic.html#editor
    }),
    viteMockServe({
      // 启用 mock 功能
      enable: true,
      // 监视 mock 文件变化
      watchFiles: true,
      // 开启日志查看拦截情况
      logger: true,
      // mock文件目录
      mockPath: "./src/mock",
    }),
  ],
  resolve: {
    alias: {
      "@": path.resolve(__dirname, "./src"),
    },
  },
});
