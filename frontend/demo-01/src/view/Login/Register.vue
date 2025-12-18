<template>
  <div class="register-page">
    <div class="register-card">
      <div class="register-header">
        <div class="platform-name">慧编未来 · AI 教学平台</div>
        <h2>创建新账号</h2>
        <p class="register-subtitle">加入我们，开启编程学习之旅</p>
      </div>

      <el-form
        :model="registerForm"
        :rules="registerRules"
        ref="registerFormRef"
        class="register-form"
        label-position="left"
        @submit.prevent="handleRegister"
      >
        <el-form-item prop="username" label="用户名">
          <el-input v-model="registerForm.username" placeholder="请输入用户名">
            <template #prefix>
              <el-icon class="el-input__icon"><User /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item prop="email" label="邮箱">
          <el-input v-model="registerForm.email" type="email" placeholder="请输入邮箱">
            <template #prefix>
              <el-icon class="el-input__icon"><Message /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item prop="password" label="密码">
          <el-input
            v-model="registerForm.password"
            type="password"
            placeholder="请输入密码"
            show-password
          >
            <template #prefix>
              <el-icon class="el-input__icon"><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item prop="confirmPassword" label="确认密码">
          <el-input
            v-model="registerForm.confirmPassword"
            type="password"
            placeholder="请再次输入密码"
            show-password
          >
            <template #prefix>
              <el-icon class="el-input__icon"><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item prop="role" label="角色">
          <el-select v-model="registerForm.role" placeholder="请选择角色">
            <el-option label="学生" value="student" />
            <el-option label="教师" value="teacher" />
            <el-option label="管理员" value="admin" />
          </el-select>
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            class="register-btn"
            native-type="submit"
            :loading="loading"
          >
            <span v-if="!loading">注册</span>
            <span v-else>注册中...</span>
          </el-button>
        </el-form-item>

        <div class="login-section">
          已有账号？
          <el-link @click.prevent="goLogin" class="login-link">立即登录</el-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { register } from "@/api";
import { useFormValidation } from "@/hooks";
import { BusinessError } from "@/utils/error";
import { Lock, Message, User } from "@element-plus/icons-vue";
import {
	ElButton,
	ElForm,
	ElFormItem,
	ElIcon,
	ElInput,
	ElLink,
	ElMessage,
	ElOption,
	ElSelect,
} from "element-plus";
import { reactive, ref } from "vue";
import { useRouter } from "vue-router";

const router = useRouter();
const loading = ref(false);
const registerFormRef = ref(null);

const registerForm = reactive({
  username: "",
  email: "",
  password: "",
  confirmPassword: "",
  role: "",
});

const registerRules = reactive({
  username: [
    { required: true, message: "请输入用户名", trigger: "blur" },
    { min: 3, max: 20, message: "用户名长度在 3 到 20 个字符", trigger: "blur" },
  ],
  email: [
    { required: true, message: "请输入邮箱", trigger: "blur" },
    { type: "email", message: "请输入正确的邮箱格式", trigger: ["blur", "change"] },
  ],
  password: [
    { required: true, message: "请输入密码", trigger: "blur" },
    { min: 6, message: "密码长度不能少于 6 个字符", trigger: "blur" },
  ],
  confirmPassword: [
    { required: true, message: "请确认密码", trigger: "blur" },
    {
      validator: (rule, value, callback) => {
        if (value !== registerForm.password) {
          callback(new Error("两次输入的密码不一致"));
        } else {
          callback();
        }
      },
      trigger: "blur",
    },
  ],
  role: [{ required: true, message: "请选择角色", trigger: "change" }],
});

async function handleRegister() {
  try {
    loading.value = true;
    // 表单验证
    const { validateForm } = useFormValidation();
    const isValid = await validateForm(registerFormRef.value);
    if (!isValid) return;

    const res = await register({
      username: registerForm.username,
      password: registerForm.password,
      role: registerForm.role,
    });

    // 注册成功
    if (res) {
      ElMessage.success("注册成功，请登录");
      // 注册成功跳转到登录页
      router.push("/login");
    }
  } catch (error) {
    console.error("注册失败:", error);
    if (error instanceof BusinessError) {
      if (error.code === 400) {
        ElMessage.error("用户名已存在");
      } else {
        ElMessage.error("注册失败，请稍后重试");
      }
    } else {
      ElMessage.error("网络错误，请稍后重试");
    }
  } finally {
    loading.value = false;
  }
}

const goLogin = () => {
  router.push("/login"); // 跳转到登录页面
};
</script>

<style lang="scss" scoped>
.register-page {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: var(--gradient-primary);
  font-family: "Segoe UI", Helvetica, Arial;
  padding: 20px;
  position: relative;
  overflow: hidden;
}

.register-page::before {
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

.register-card {
  width: 100%;
  max-width: 380px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  padding: 36px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1), 0 0 0 1px rgba(255, 255, 255, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.3);
  position: relative;
  z-index: 1;
  transform: translateY(0);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.register-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 30px 60px rgba(0, 0, 0, 0.15), 0 0 0 1px rgba(255, 255, 255, 0.3);
}

.register-header {
  text-align: center;
  margin-bottom: 40px;
}

.platform-name {
  font-size: 22px;
  font-weight: 700;
  color: var(--primary);
  margin-bottom: 16px;
  letter-spacing: 0.5px;
  background: var(--gradient-brand);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.register-header h2 {
  color: var(--text-primary);
  margin: 0;
  font-size: 28px;
  font-weight: 600;
  letter-spacing: -0.5px;
}

.register-subtitle {
  color: #64748b;
  font-size: 14px;
  margin-top: 8px;
  font-weight: 500;
}

.register-btn {
  width: 100%;
  background: var(--gradient-brand);
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

.register-btn::before {
  content: "";
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.5s ease;
}

.register-btn:hover::before {
  left: 100%;
}

.register-btn:hover,
.register-btn:focus {
  background: linear-gradient(45deg, #1d4ed8, #1e40af);
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(37, 99, 235, 0.4);
}

.register-btn:active {
  transform: translateY(0);
}

.login-section {
  text-align: center;
  margin-top: 32px;
  color: #64748b;
  font-size: 14px;
  font-weight: 500;
}

.login-link {
  color: var(--primary);
  font-weight: 600;
  margin-left: 4px;
  transition: color 0.2s ease;
  position: relative;
}

.login-link::after {
  content: "";
  position: absolute;
  bottom: -2px;
  left: 0;
  width: 0;
  height: 2px;
  background: var(--primary);
  transition: width 0.3s ease;
}

.login-link:hover::after {
  width: 100%;
}

.login-link:hover {
  color: var(--primary-hover);
}

/* 适配Element Plus组件样式 - 修复标签对齐和输入框间距问题 */
.register-form {
  width: 100%;
}

.register-form :deep(.el-form-item) {
  margin-bottom: 28px;
  display: flex;
  align-items: center;
  width: 100%;
}

/* 修复标签对齐问题 */
.register-form :deep(.el-form-item__label) {
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
.register-form :deep(.el-form-item__content) {
  flex: 1;
  margin-left: 0;
  width: auto;
}

/* 修复输入框样式 */
.register-form :deep(.el-input__wrapper) {
  border-radius: 10px;
  padding-left: 12px;
  box-sizing: border-box;
  border: 2px solid var(--border-light);
  background: var(--bg-light);
  transition: all 0.3s ease;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.register-form :deep(.el-input__wrapper:hover) {
  border-color: #cbd5e1;
  background: var(--bg-white);
}

/* 调整图标位置 */
.register-form :deep(.el-input__prefix) {
  margin-right: 8px;
  margin-left: 0;
  padding-right: 0;
  color: #94a3b8;
}

.register-form :deep(.el-input__prefix-inner) {
  color: #94a3b8;
}

/* 修复输入框内部输入区域 */
.register-form :deep(.el-input__inner) {
  padding-left: 8px;
  padding-right: 8px;
  box-sizing: border-box;
  color: var(--text-primary);
  font-weight: 500;
}

.register-form :deep(.el-input__inner::placeholder) {
  color: #94a3b8;
  font-weight: 400;
}

/* 确保密码框的显示密码按钮不影响布局 */
.register-form :deep(.el-input__suffix) {
  margin-left: 0;
  color: #94a3b8;
}

.register-form :deep(.el-input__wrapper:focus-within) {
  border-color: var(--primary);
  background: var(--bg-white);
  box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.1), 0 1px 3px rgba(0, 0, 0, 0.1);
}

/* 用deep修改Element Plus组件的样式，使得和上方的输入框UI更一致 */
.register-form :deep(.el-select__wrapper) {
  border-radius: 10px;
  border: 2px solid var(--border-light);
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}
.register-form :deep(.el-select__wrapper):hover {
  border-color: #cbd5e1;
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

.register-card {
  animation: fadeInUp 0.6s ease-out;
}

/* 响应式设计 */
@media (max-width: 480px) {
  .register-card {
    padding: 30px 20px;
    margin: 20px;
  }

  .platform-name {
    font-size: 20px;
  }

  .register-header h2 {
    font-size: 24px;
  }
}
</style>
