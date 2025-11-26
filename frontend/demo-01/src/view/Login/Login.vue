<template>
  <div class="login-page">
    <div class="login-card">
      <div class="login-header">
        <div class="platform-name">慧编未来 · AI 教学平台</div>
        <h2>账号登录</h2>
      </div>
      
      <el-form 
        :model="loginForm" 
        :rules="loginRules" 
        ref="loginFormRef" 
        class="login-form"
        @submit.prevent="handleLogin"
      >
        <el-form-item prop="username" label="用户名">
          <el-input 
            v-model="loginForm.username" 
            placeholder="请输入用户名"
            prefix-icon="User"
          ></el-input>
        </el-form-item>
        
        <el-form-item prop="password" label="密码">
          <el-input 
            v-model="loginForm.password" 
            type="password" 
            placeholder="请输入密码"
            prefix-icon="Lock"
            show-password
          ></el-input>
        </el-form-item>
        
        <el-form-item class="form-actions">
          <el-link @click.prevent="goRegister" class="forgot-link">忘记密码？</el-link>
        </el-form-item>
        
        <el-form-item>
          <el-button 
            type="primary" 
            class="login-btn" 
            native-type="submit"
            :loading="loading"
          >
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
import { ref, reactive } from 'vue';
import { useRouter } from 'vue-router';
import { ElForm, ElFormItem, ElInput, ElButton, ElLink, ElMessage } from 'element-plus';

const router = useRouter();
const loading = ref(false);
const loginFormRef = ref(null);

const loginForm = reactive({
  username: '',
  password: ''
});

const loginRules = reactive({
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于 6 个字符', trigger: 'blur' }
  ]
});

const handleLogin = async () => {
  try {
    loading.value = true;
    // 表单验证
    await loginFormRef.value.validate();
    
    // 模拟登录请求
    setTimeout(() => {
      // 实际项目中这里会调用登录API
      ElMessage.success('登录成功');
      router.push('/'); // 登录成功跳转到首页
    }, 1000);
  } catch (error) {
    console.error('表单验证失败:', error);
  } finally {
    loading.value = false;
  }
};

const goRegister = () => {
  router.push('/register'); // 跳转到注册页面
};
</script>

<style scoped>
.login-page {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: #f0f4ff;
  font-family: "Segoe UI", Helvetica, Arial;
  padding: 20px;
}

.login-card {
  width: 100%;
  max-width: 400px;
  background: #ffffffcc;
  backdrop-filter: blur(10px);
  border-radius: 12px;
  padding: 36px;
  box-shadow: 0 6px 24px rgba(106, 90, 205, 0.2);
  border: 1px solid #e6e6fa;
}

.login-header {
  text-align: center;
  margin-bottom: 32px;
}

.platform-name {
  font-size: 20px;
  font-weight: bold;
  color: #6a5acd; /* 蓝紫色主色 */
  margin-bottom: 12px;
}

.login-header h2 {
  color: #483d8b; /* 深一点的蓝紫色 */
  margin: 0;
  font-size: 24px;
}

.form-actions {
  text-align: right;
  margin-bottom: 8px;
}

.forgot-link {
  color: #6a5acd; /* 蓝紫色链接 */
  font-size: 14px;
}

.forgot-link:hover {
  color: #483d8b; /* 深一点的蓝紫色 */
}

.login-btn {
  width: 100%;
  background-color: #6a5acd; /* 蓝紫色按钮 */
  border-color: #6a5acd;
  padding: 12px 0;
  font-size: 16px;
}

.login-btn:hover, .login-btn:focus {
  background-color: #483d8b; /* 深一点的蓝紫色 */
  border-color: #483d8b;
}

.register-section {
  text-align: center;
  margin-top: 24px;
  color: #666;
  font-size: 14px;
}

.register-link {
  color: #6a5acd; /* 蓝紫色链接 */
  font-weight: 500;
  margin-left: 4px;
}

.register-link:hover {
  color: #483d8b;
}

/* 适配Element Plus组件样式 - 修复标签对齐和输入框间距问题 */
.login-form {
  /* 确保表单容器的样式正确 */
  width: 100%;
}

.login-form :deep(.el-form-item) {
  margin-bottom: 24px;
  display: flex;
  align-items: center;
  width: 100%;
  /* 确保整个表单项对齐 */
}

/* 修复标签对齐问题 */
.login-form :deep(.el-form-item__label) {
  width: 80px; /* 固定标签宽度 */
  min-width: 80px; /* 确保标签宽度固定 */
  text-align: center; 
  padding-right: 16px; /* 标签与输入框之间的间距 */
  padding-left: 0; /* 移除左侧内边距 */
  margin-right: 0; /* 移除右侧外边距 */
  margin-left: 0; /* 确保左侧无额外间距 */
  margin-bottom: 0; /* 移除底部外边距 */
  color: #555;
  font-weight: 500;
}
/* 修复输入框内容区对齐 */
.login-form :deep(.el-form-item__content) {
  flex: 1;
  margin-left: 0; /* 移除内容区左侧间距 */
  width: auto;
}

/* 修复输入框左侧距离太宽问题 */
.login-form :deep(.el-input__wrapper) {
  border-radius: 8px;
  padding-left: 4px; /* 减少左侧内边距 */
  box-sizing: border-box;
}

/* 调整图标位置 */
.login-form :deep(.el-input__prefix) {
  margin-right: 2px; /* 调整图标与文字的距离 */
  margin-left: 0; /* 移除左侧额外间距 */
  padding-right: 0;
}

.login-form :deep(.el-input__prefix-inner) {
  display: none;
}

/* 修复输入框内部输入区域 */
.login-form :deep(.el-input__inner) {
  padding-left: 2px; /* 调整输入框内部左侧内边距 */
  padding-right: 2px;
  box-sizing: border-box;
}

/* 确保密码框的显示密码按钮不影响布局 */
.login-form :deep(.el-input__suffix) {
  margin-left: 0;
}

.login-form :deep(.el-input__wrapper:focus-within) {
  box-shadow: 0 0 0 3px rgba(106, 90, 205, 0.2);
  border-color: #b19cd9;
}
</style>