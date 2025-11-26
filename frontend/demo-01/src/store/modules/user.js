import { defineStore } from "pinia";
import { computed, ref } from "vue";

export const useUserStore = defineStore(
  "user",
  () => {
    // 登录状态
    const isLoggedIn = ref(false);

    // 登录令牌
    const token = ref("");

    // 用户信息
    const role = ref("");

    // 认证头信息
    const authHeader = computed(() => (token.value ? `Bearer ${token.value}` : ""));

    // 设置令牌
    const setToken = (newToken) => {
      token.value = newToken;
      isLoggedIn.value = newToken ? true : false;
    };

    // 登出，清空用户信息
    const logout = () => {
      role.value = "";
      isLoggedIn.value = false;
      token.value = "";
    };

    return {
      role,
      isLoggedIn,
      token,
      authHeader,
      setToken,
      logout,
    };
  },
  {
    // 配置持久化
    persist: {
      // 持久化的键名，默认为store的id
      key: "user-store",
      // 持久化存储方式，可选：localStorage、sessionStorage、cookie
      storage: localStorage,
      // 需要持久化的状态字段
      pick: ["role", "isLoggedIn", "token"],
    },
  }
);
