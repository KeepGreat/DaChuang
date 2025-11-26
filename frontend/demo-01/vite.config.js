// vite.config.js
import vue from "@vitejs/plugin-vue";
import path from "path";
import { defineConfig } from "vite";
import { codeInspectorPlugin } from "code-inspector-plugin";

export default defineConfig({
  plugins: [
    vue(),
    codeInspectorPlugin({
      bundler: "vite",
      editor: "code", // 指定 IDE 为 vscode https://inspector.fe-dev.cn/api/basic.html#editor
    }),
  ],
  resolve: {
    alias: {
      "@": path.resolve(__dirname, "./src"),
    },
  },
});
