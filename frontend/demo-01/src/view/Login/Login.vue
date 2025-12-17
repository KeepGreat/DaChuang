<template>
  <div class="login-page">
    <div class="login-card">
      <div class="login-header">
        <div class="platform-name">慧编未来 · AI 教学平台</div>
        <h2>账号登录</h2>
      </div>

      <!-- 用 label-position="left" 来设置label左对齐 -->
      <el-form
        :model="loginForm"
        :rules="loginRules"
        ref="loginFormRef"
        class="login-form"
        label-position="left"
        @submit.prevent="handleLogin"
      >
        <el-form-item prop="username" label="用户名">
          <el-input v-model="loginForm.username" placeholder="请输入用户名">
            <template #prefix>
              <el-icon><User /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item prop="password" label="密码">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            show-password
          >
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item>
          <el-button class="login-btn" native-type="submit" :loading="loading">
            登录
          </el-button>
        </el-form-item>

        <div class="register-section">
          还没有账号？
          <el-link @click.prevent="goRegister" class="register-link">立即注册</el-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { getUserRole, login } from "@/api";
import { useFormValidation } from "@/hooks";
import { useUserStore } from "@/store";
import { BusinessError } from "@/utils/error";
import { Lock, User } from "@element-plus/icons-vue";
import {
	ElButton,
	ElForm,
	ElFormItem,
	ElIcon,
	ElInput,
	ElLink,
	ElMessage,
} from "element-plus";
import { reactive, ref } from "vue";
import { useRouter } from "vue-router";

const router = useRouter();
const userStore = useUserStore();
const loading = ref(false);
const loginFormRef = ref(null);

const loginForm = reactive({
  username: "",
  password: "",
});

const loginRules = reactive({
  username: [
    { required: true, message: "请输入用户名", trigger: "blur" },
    { min: 3, max: 20, message: "用户名长度在 3 到 20 个字符", trigger: "blur" },
  ],
  password: [
    { required: true, message: "请输入密码", trigger: "blur" },
    { min: 6, message: "密码长度不能少于 6 个字符", trigger: "blur" },
  ],
});

async function handleLogin() {
  try {
    loading.value = true;
    // 表单验证
    const { validateForm } = useFormValidation();
    const isValid = await validateForm(loginFormRef.value);
    if (!isValid) return;

    const tokenRes = await login(loginForm);

    // 登录成功, 保存token
    userStore.setToken(tokenRes.data);

    // 调用getUserRole接口获取用户角色
    const roleRes = await getUserRole({ token: tokenRes.data });

    // 保存角色到store
    userStore.setUserRole(roleRes.data);
    ElMessage.success("登录成功");

    // 根据角色跳转到不同页面
    const roleDefaultRoute = userStore.getDefaultRoute();
    router.push(roleDefaultRoute);
  } catch (error) {
    console.error("登录失败:", error);
    if (error instanceof BusinessError) {
      if (error.code === 401) {
        ElMessage.error("密码错误");
      } else if (error.code === 404) {
        ElMessage.error("用户不存在");
      } else {
        ElMessage.error(error.message);
      }
    } else {
      ElMessage.error("网络错误，请稍后重试");
    }
  } finally {
    loading.value = false;
  }
}

// 跳转到注册页面
function goRegister() {
  router.push("/register");
}
</script>

<style scoped>
@import "@/styles/main.css";

.login-page {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: var(--bg-gradient-primary);
  font-family: "Segoe UI", Helvetica, Arial;
  padding: 20px;
  position: relative;
  overflow: hidden;
}

.login-page::before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image: radial-gradient(
      circle at 20% 80%,
      rgba(255, 255, 255, 0.1) 0%,
      transparent 50%
    ),
    radial-gradient(circle at 80% 20%, rgba(255, 255, 255, 0.1) 0%, transparent 50%);
  pointer-events: none;
}

.login-card {
  width: 100%;
  max-width: 420px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  padding: 40px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1), 0 0 0 1px rgba(255, 255, 255, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.3);
  position: relative;
  z-index: 1;
  transform: translateY(0);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.login-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 30px 60px rgba(0, 0, 0, 0.15), 0 0 0 1px rgba(255, 255, 255, 0.3);
}

.login-header {
  text-align: center;
  margin-bottom: 40px;
}

.platform-name {
  font-size: 22px;
  font-weight: 700;
  color: #2563eb;
  margin-bottom: 16px;
  letter-spacing: 0.5px;
  background: var(--brand-gradient);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.login-header h2 {
  color: #1e293b;
  margin: 0;
  font-size: 28px;
  font-weight: 600;
  letter-spacing: -0.5px;
}

.form-actions {
  text-align: right;
  margin-bottom: 12px;
}

.forgot-link {
  color: #2563eb;
  font-size: 14px;
  font-weight: 500;
  transition: color 0.2s ease;
}

.forgot-link:hover {
  color: #1d4ed8;
}

.login-btn {
  width: 100%;
  background: var(--brand-gradient);
  border: none;
  border-radius: 12px;
  padding: 14px 0;
  font-size: 16px;
  font-weight: 600;
  color: white;
  box-shadow: 0 4px 15px rgba(37, 99, 235, 0.3);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.login-btn::before {
  content: "";
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.5s ease;
}

.login-btn:hover::before {
  left: 100%;
}

.login-btn:hover,
.login-btn:focus {
  background: linear-gradient(45deg, #1d4ed8, #1e40af);
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(37, 99, 235, 0.4);
}

.login-btn:active {
  transform: translateY(0);
}

.register-section {
  text-align: center;
  margin-top: 32px;
  color: #64748b;
  font-size: 14px;
  font-weight: 500;
}

.register-link {
  color: #2563eb;
  font-weight: 600;
  margin-left: 4px;
  transition: color 0.2s ease;
  position: relative;
}

.register-link::after {
  content: "";
  position: absolute;
  bottom: -2px;
  left: 0;
  width: 0;
  height: 2px;
  background: #2563eb;
  transition: width 0.3s ease;
}

.register-link:hover::after {
  width: 100%;
}

.register-link:hover {
  color: #1d4ed8;
}

/* 适配Element Plus组件样式 - 修复标签对齐和输入框间距问题 */
.login-form {
  width: 100%;
}

.login-form :deep(.el-form-item) {
  margin-bottom: 28px;
  display: flex;
  align-items: center;
  width: 100%;
}

/* 修复标签对齐问题 */
.login-form :deep(.el-form-item__label) {
  width: 80px;
  min-width: 80px;
  padding-right: 10px;
  padding-left: 0;
  margin-right: 0;
  margin-left: 0;
  margin-bottom: 0;
  color: #475569;
  font-weight: 600;
  font-size: 14px;
}

/* 修复输入框内容区对齐 */
.login-form :deep(.el-form-item__content) {
  flex: 1;
  margin-left: 0;
  width: auto;
}

/* 修复输入框样式 */
.login-form :deep(.el-input__wrapper) {
  border-radius: 10px;
  padding-left: 12px;
  box-sizing: border-box;
  border: 2px solid #e2e8f0;
  background: #f8fafc;
  transition: all 0.3s ease;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.login-form :deep(.el-input__wrapper:hover) {
  border-color: #cbd5e1;
  background: #ffffff;
}

/* 调整图标位置 */
.login-form :deep(.el-input__prefix) {
  margin-right: 8px;
  margin-left: 0;
  padding-right: 0;
  color: #94a3b8;
}

.login-form :deep(.el-input__prefix-inner) {
  color: #94a3b8;
}

/* 修复输入框内部输入区域 */
.login-form :deep(.el-input__inner) {
  padding-left: 8px;
  padding-right: 8px;
  box-sizing: border-box;
  color: #1e293b;
  font-weight: 500;
}

.login-form :deep(.el-input__inner::placeholder) {
  color: #94a3b8;
  font-weight: 400;
}

/* 确保密码框的显示密码按钮不影响布局 */
.login-form :deep(.el-input__suffix) {
  margin-left: 0;
  color: #94a3b8;
}

.login-form :deep(.el-input__wrapper:focus-within) {
  border-color: #2563eb;
  background: #ffffff;
  box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.1), 0 1px 3px rgba(0, 0, 0, 0.1);
}

/* 动画效果 */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.login-card {
  animation: fadeInUp 0.6s ease-out;
}

/* 响应式设计 */
@media (max-width: 480px) {
  .login-card {
    padding: 30px 20px;
    margin: 20px;
  }

  .platform-name {
    font-size: 20px;
  }

  .login-header h2 {
    font-size: 24px;
  }
}
</style>
