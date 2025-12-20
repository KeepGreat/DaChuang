import { defineStore } from "pinia";
import { computed, ref } from "vue";

export const useUserStore = defineStore(
  "user",
  () => {
    /**
     * 登录状态
     */
    const isLoggedIn = ref(false);

    /**
     * 登录令牌
     */
    const token = ref("");

    /**
     * 用户角色信息
     */
    const role = ref("");

    /**
     * 认证头信息
     */
    const authHeader = computed(() => token.value || "");

    /**
     * 设置token
     */
    const setToken = (newToken) => {
      token.value = newToken;
      isLoggedIn.value = newToken ? true : false;
    };

    /**
     * 设置用户角色
     */
    const setUserRole = (userRole) => {
      role.value = userRole;
    };

    /**
     * 根据role获取默认路由
     */
    const getDefaultRoute = () => {
      const routeMap = {
        student: "/",
        teacher: "/teach",
        admin: "/admin",
      };
      return routeMap[role.value] || "/";
    };

    /**
     * 检查角色权限
     */
    const hasPermission = (requiredRole) => {
      return role.value === requiredRole;
    };

    /**
     * 登出，清空用户信息
     */
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
      setUserRole,
      getDefaultRoute,
      hasPermission,
      logout,
    };
  },
  {
    // 配置持久化
    persist: {
      key: "user-store",
      storage: localStorage,
      // 需要持久化的状态字段
      pick: ["role", "isLoggedIn", "token"],
    },
  }
);
