import { defineStore } from "pinia";
import { ref } from "vue";

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
    const userId = ref(""); // 添加用户ID字段

    /**
     * 设置token
     */
    const setToken = (newToken) => {
      token.value = newToken;
      isLoggedIn.value = newToken ? true : false;
    };

    // 设置用户ID
    const setUserId = (id) => {
      userId.value = id;
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
      userId.value = "";
    };

    return {
      role,
      isLoggedIn,
      token,
      userId,
      setToken,
      setUserRole,
      getDefaultRoute,
      hasPermission,
      setUserId,
      logout,
    };
  },
  {
    // 配置持久化
    persist: {
      key: "user-store",
      storage: localStorage,
      // 需要持久化的状态字段
      pick: ["role", "isLoggedIn", "token", "userId"],
    },
  }
);
