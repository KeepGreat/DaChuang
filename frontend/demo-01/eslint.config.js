import js from "@eslint/js";
import pluginVue from "eslint-plugin-vue";
import globals from "globals";

export default [
  js.configs.recommended,
  ...pluginVue.configs["flat/essential"],
  {
    languageOptions: {
      ecmaVersion: 2022,
      sourceType: "module",
      // 使用预定义的全局环境
      globals: {
        ...globals.browser,
        ...globals.node,
      },
    },
    rules: {
      // JavaScript https://eslint.org/docs/latest/rules
      // 未定义变量
      "no-undef": "error",
      // 未使用变量
      "no-unused-vars": "warn",

      // Vue https://eslint.vuejs.org/rules/
      // 允许单个单词的组件名
      "vue/multi-word-component-names": "off",
    },
  },
];
