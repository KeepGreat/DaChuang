<template>
  <div class="register-page">
    <div class="register-card">
      <div class="register-header">
        <div class="platform-name">慧编未来 · AI 教学平台</div>
        <h2>账号注册</h2>
      </div>
      
      <el-form 
        :model="registerForm" 
        :rules="registerRules" 
        ref="registerFormRef" 
        class="register-form"
        @submit.prevent="handleRegister"
      >
        <el-form-item prop="username" label="用户名">
          <el-input 
            v-model="registerForm.username" 
            placeholder="请输入用户名"
            prefix-icon="User"
          ></el-input>
        </el-form-item>
        
        <el-form-item prop="email" label="邮箱">
          <el-input 
            v-model="registerForm.email" 
            type="email" 
            placeholder="请输入邮箱"
            prefix-icon="Message"
          ></el-input>
        </el-form-item>
        
        <el-form-item prop="password" label="密码">
          <el-input 
            v-model="registerForm.password" 
            type="password" 
            placeholder="请输入密码"
            prefix-icon="Lock"
            show-password
          ></el-input>
        </el-form-item>
        
        <el-form-item prop="confirmPassword" label="确认密码">
          <el-input 
            v-model="registerForm.confirmPassword" 
            type="password" 
            placeholder="请再次输入密码"
            prefix-icon="Lock"
            show-password
          ></el-input>
        </el-form-item>
        
        <el-form-item>
          <el-button 
            type="primary" 
            class="register-btn" 
            native-type="submit"
            :loading="loading"
          >
            注册
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
import { ref, reactive } from 'vue';
import { useRouter } from 'vue-router';
import { ElForm, ElFormItem, ElInput, ElButton, ElLink, ElMessage } from 'element-plus';

const router = useRouter();
const loading = ref(false);
const registerFormRef = ref(null);

const registerForm = reactive({
  username: '',
  email: '',
  password: '',
  confirmPassword: ''
});

const registerRules = reactive({
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: ['blur', 'change'] }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于 6 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { 
      validator: (rule, value, callback) => {
        if (value !== registerForm.password) {
          callback(new Error('两次输入的密码不一致'));
        } else {
          callback();
        }
      },
      trigger: 'blur'
    }
  ]
});

const handleRegister = async () => {
  try {
    loading.value = true;
    // 表单验证
    await registerFormRef.value.validate();
    
    // 模拟注册请求
    setTimeout(() => {
      // 实际项目中这里会调用注册API
      ElMessage.success('注册成功，请登录');
      router.push('/login'); // 注册成功跳转到登录页
    }, 1000);
  } catch (error) {
    console.error('表单验证失败:', error);
  } finally {
    loading.value = false;
  }
};

const goLogin = () => {
  router.push('/login'); // 跳转到登录页面
};
</script>

<style scoped>
.register-page {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: #f0f4ff;
  font-family: "Segoe UI", Helvetica, Arial;
  padding: 20px;
}

.register-card {
  width: 100%;
  max-width: 400px;
  background: #ffffffcc;
  backdrop-filter: blur(10px);
  border-radius: 12px;
  padding: 36px;
  box-shadow: 0 6px 24px rgba(106, 90, 205, 0.2);
  border: 1px solid #e6e6fa;
}

.register-header {
  text-align: center;
  margin-bottom: 32px;
}

.platform-name {
  font-size: 20px;
  font-weight: bold;
  color: #6a5acd; /* 蓝紫色主色 */
  margin-bottom: 12px;
}

.register-header h2 {
  color: #483d8b; /* 深一点的蓝紫色 */
  margin: 0;
  font-size: 24px;
}

.register-btn {
  width: 100%;
  background-color: #6a5acd; /* 蓝紫色按钮 */
  border-color: #6a5acd;
  padding: 12px 0;
  font-size: 16px;
}

.register-btn:hover, .register-btn:focus {
  background-color: #483d8b; /* 深一点的蓝紫色 */
  border-color: #483d8b;
}

.login-section {
  text-align: center;
  margin-top: 24px;
  color: #666;
  font-size: 14px;
}

.login-link {
  color: #6a5acd; /* 蓝紫色链接 */
  font-weight: 500;
  margin-left: 4px;
}

.login-link:hover {
  color: #483d8b;
}

/* 适配Element Plus组件样式 - 修复标签对齐和输入框间距问题 */
.register-form {
  /* 确保表单容器的样式正确 */
  width: 100%;
}

.register-form :deep(.el-form-item) {
  margin-bottom: 24px;
  display: flex;
  align-items: center;
  width: 100%;
  /* 确保整个表单项对齐 */
}

/* 修复标签对齐问题 */
.register-form :deep(.el-form-item__label) {
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
.register-form :deep(.el-form-item__content) {
  flex: 1;
  margin-left: 0; /* 移除内容区左侧间距 */
  width: auto;
}

/* 修复输入框左侧距离太宽问题 */
.register-form :deep(.el-input__wrapper) {
  border-radius: 8px;
  padding-left: 4px; /* 减少左侧内边距 */
  box-sizing: border-box;
}

/* 调整图标位置 */
.register-form :deep(.el-input__prefix) {
  margin-right: 2px; /* 调整图标与文字的距离 */
  margin-left: 0; /* 移除左侧额外间距 */
  padding-right: 0;
}

.register-form :deep(.el-input__prefix-inner) {
  display: none;
}

/* 修复输入框内部输入区域 */
.register-form :deep(.el-input__inner) {
  padding-left: 2px; /* 调整输入框内部左侧内边距 */
  padding-right: 2px;
  box-sizing: border-box;
}

/* 确保密码框的显示密码按钮不影响布局 */
.register-form :deep(.el-input__suffix) {
  margin-left: 0;
}

.register-form :deep(.el-input__wrapper:focus-within) {
  box-shadow: 0 0 0 3px rgba(106, 90, 205, 0.2);
  border-color: #b19cd9;
}
</style>