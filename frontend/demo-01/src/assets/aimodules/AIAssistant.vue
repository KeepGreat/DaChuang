<template>
  <div class="ai-assistant-container">
    <!-- 对话记录区域 -->
    <div class="chat-history">
      <div v-if="messages.length === 0" class="empty-state">
        <p>智能助教随时为您解答问题</p>
        <p>您可以提问或分享代码</p>
      </div>
      <div 
        v-for="(message, index) in messages" 
        :key="index" 
        :class="['message', message.role]"
      >
        <div class="message-header">
          <span class="role-label">{{ message.role === 'user' ? '您' : '助教' }}</span>
          <span class="timestamp">{{ formatTime(message.timestamp) }}</span>
        </div>
        <div class="message-content">
          <div v-if="message.question" class="question-content">{{ message.question }}</div>
          <div v-if="message.code" class="code-content">
            <div class="code-header">
              <span>{{ message.codeLanguage || '代码' }}</span>
            </div>
            <pre>{{ message.code }}</pre>
          </div>
          <div v-if="message.response" class="response-content">
            <!-- 使用v-html指令渲染Markdown内容 -->
            <div v-html="renderMarkdown(message.response)"></div>
            <!-- 流式加载指示器 -->
            <div v-if="message.isStreaming" class="typing-indicator">
              <span class="typing-dot"></span>
              <span class="typing-dot"></span>
              <span class="typing-dot"></span>
            </div>
          </div>
        </div>
      </div>
      <div v-if="isLoading" class="loading-message">
        <div class="loading-spinner"></div>
        <span>正在生成回复...</span>
      </div>
    </div>

    <!-- 输入区域 -->
    <div class="input-area">
      <!-- 问题输入框 -->
      <div class="question-input-wrapper">
        <textarea
          v-model="inputQuestion"
          class="question-input"
          placeholder="请输入您的问题..."
          rows="3"
        ></textarea>
      </div>

      <!-- 代码输入（集成CodeSandbox组件） -->
      <div class="code-input-wrapper" v-show="showCodeInput">
        <CodeSandbox
          ref="codeSandboxRef"
          :initialLanguage="sandboxInitialLanguage"
          :initialCode="sandboxInitialCode"
          :initialInput="sandboxInitialInput"
          @codeChange="handleCodeChange"
        />
      </div>

      <!-- 操作按钮 -->
      <div class="action-buttons">
        <el-button 
          type="default" 
          class="toggle-code-button"
          @click="toggleCodeInput"
        >
          {{ showCodeInput ? '隐藏代码输入' : '添加代码' }}
        </el-button>
        <el-button 
          type="primary" 
          class="send-button"
          @click="sendMessage"
          :loading="isLoading"
          :disabled="!canSend"
        >
          发送
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue';
import { ElButton } from 'element-plus';
import CodeSandbox from './CodeSandbox.vue';
import { teach, answer } from './AIAssistantAPI.js';

// 添加marked库用于Markdown解析
import { marked } from 'marked';

// 配置marked
marked.setOptions({
  breaks: true, // 将换行符转换为<br>
  gfm: true,    // 启用GitHub风格的Markdown
  headerIds: true, // 为标题添加ID
  mangle: false // 不处理电子邮件地址
});

// 添加Markdown解析函数
const renderMarkdown = (text) => {
  if (!text || typeof text !== 'string') return '';
  try {
    return marked(text);
  } catch (error) {
    console.error('Markdown渲染错误:', error);
    // 出错时返回原始文本，避免显示异常
    return text;
  }
};

// 响应式数据
const messages = ref([]);
const inputQuestion = ref('');
const showCodeInput = ref(false);
const selectedLanguage = ref('cpp');
const isLoading = ref(false);
const codeSandboxRef = ref(null);
const currentCode = ref('');
const currentCodeLanguage = ref('cpp');
const currentProgramInput = ref(''); // 添加程序输入状态

// 添加用于控制CodeSandbox组件props的响应式变量
const sandboxInitialLanguage = ref('cpp');
const sandboxInitialCode = ref('');
const sandboxInitialInput = ref('');

// 计算属性
const canSend = computed(() => {
  return inputQuestion.value.trim() !== '';
});

// 处理代码变化
const handleCodeChange = (codeData) => {
  currentCode.value = codeData.code;
  currentCodeLanguage.value = codeData.language;
  // 保存程序输入内容
  if (codeData.input !== undefined) {
    currentProgramInput.value = codeData.input;
  }
};

// 添加代码输入切换函数
const toggleCodeInput = () => {
  showCodeInput.value = !showCodeInput.value;
  // 当代码输入框显示时，确保props值正确传递
  if (showCodeInput.value) {
    nextTick(() => {
      // 确保显示时的值正确
      sandboxInitialLanguage.value = currentCodeLanguage.value;
      sandboxInitialCode.value = currentCode.value;
      sandboxInitialInput.value = currentProgramInput.value;
    });
  }
};

// 发送消息
  const sendMessage = async () => {
    if (!canSend.value || isLoading.value) return;
    
    const question = inputQuestion.value.trim();
    
    // 创建用户消息
    const userMessage = {
      role: 'user',
      question: question,
      code: currentCode.value,
      codeLanguage: currentCodeLanguage.value,
      codeInput: currentProgramInput.value, // 添加程序输入到用户消息中
      timestamp: new Date()
    };
    
    // 创建AI响应占位符消息
    const aiMessage = {
      role: 'assistant',
      response: '',
      isStreaming: true,
      timestamp: new Date()
    };
    
    // 添加消息到列表
    messages.value.push(userMessage, aiMessage);
    
    // 清空输入
    inputQuestion.value = '';
    
    // 滚动到底部
    scrollToBottom();
    
    // 调用API
    isLoading.value = true;
    
    try {
      // 根据是否有代码选择不同的API
      // 重要：在清空代码前进行判断
      const endpoint = currentCode.value && currentCode.value.trim() ? 'teach' : 'answer';
      
      // 构建请求参数，严格按照后端实体类设计
      // 使用userMessage中的code和codeLanguage，避免重复定义临时变量
      const teachingInput = userMessage.code ? {
        question: question,
        codeSandboxInput: {
          codeLanguage: userMessage.codeLanguage,
          code: userMessage.code,
          input: userMessage.codeInput || '' // 使用用户输入的程序输入内容，默认为空
        }
      } : {
        question: question,
        codeSandboxInput: null
      };
      
      // 现在可以安全地清空代码状态，因为已经保存了需要的值
      // 移除条件判断，确保代码总是被清空
      // 重置本地代码状态
      currentCode.value = '';
      currentProgramInput.value = ''; // 重置程序输入
      // 重置语言为默认值
      currentCodeLanguage.value = 'cpp';
      
      // 使用CodeSandbox组件暴露的reset方法清空内容
      // 这是最直接有效的方法，可以确保组件内部状态正确重置
      if (codeSandboxRef.value) {
        // 使用nextTick确保DOM更新完成后再调用reset方法
        nextTick(() => {
          codeSandboxRef.value.reset('cpp', '', '');
        });
      }
      
      // 不再需要通过props更新和highlightCode方法调用
      // 使用组件自己的reset方法即可处理所有逻辑
      // 通过更新props控制CodeSandbox组件重置（保留作为备选方案）
      sandboxInitialLanguage.value = 'cpp';
      sandboxInitialCode.value = '';
      sandboxInitialInput.value = '';
      
      // 移除多余的nextTick函数块
      // 因为我们已经在调用reset方法时使用了nextTick
      // 使用nextTick确保DOM更新完成
      // nextTick(() => {
      //   // 不需要调用highlightCode方法
      //   // CodeSandbox组件内部的watch会自动处理高亮更新
      // });
      
      // 使用TeachingAPI.js中的函数调用接口
      const response = endpoint === 'teach' ? 
        await teach(teachingInput) : 
        await answer(teachingInput);
      
      // 获取响应数据
      const responseData = response.data;
      
      // 检查是否支持流式处理
      if (responseData && typeof responseData.getReader === 'function') {
        // 支持流式处理的情况
        const reader = responseData.getReader();
        const decoder = new TextDecoder('utf-8');
        
        // 处理流式响应
        let accumulatedResponse = '';
        
        while (true) {
          const { done, value } = await reader.read();
          
          if (done) {
            // 流式传输完成
            aiMessage.isStreaming = false;
            break;
          }
          
          try {
            // 解码新的数据块
            const chunk = decoder.decode(value, { stream: true });
            accumulatedResponse += chunk;
            
            // 更新AI消息响应内容
            aiMessage.response = accumulatedResponse;
            
            // 滚动到底部
            scrollToBottom();
          } catch (decodeError) {
            console.error('流式数据解码错误:', decodeError);
            // 继续处理下一个数据块
            continue;
          }
        }
      } else {
        // 不支持流式处理，直接获取完整数据
        // 假设responseData是完整的响应内容字符串
        const fullResponse = typeof responseData === 'string' ? responseData : JSON.stringify(responseData);
        aiMessage.response = fullResponse;
        aiMessage.isStreaming = false;
        scrollToBottom();
      }
      
    } catch (error) {
      console.error('发送消息错误:', error);
      aiMessage.isStreaming = false;
      // 显示错误信息
      aiMessage.response = `抱歉，处理您的请求时出错：${error.message || '未知错误'}`;
    } finally {
      isLoading.value = false;
      // 最终滚动到底部
      scrollToBottom();
    }
  };

// 滚动到底部
const scrollToBottom = () => {
  const chatHistory = document.querySelector('.chat-history');
  if (chatHistory) {
    chatHistory.scrollTop = chatHistory.scrollHeight;
  }
};

// 格式化时间
const formatTime = (date) => {
  const now = new Date(date);
  const hours = now.getHours().toString().padStart(2, '0');
  const minutes = now.getMinutes().toString().padStart(2, '0');
  return `${hours}:${minutes}`;
};

// 组件挂载时
onMounted(() => {
  // 可以在这里初始化一些数据或状态
});
</script>

<style scoped>
/* 智能助教容器 */
.ai-assistant-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  background-color: #f0f7ff;
  border-radius: 8px;
  overflow: hidden;
}

/* 对话记录区域 */
.chat-history {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  background-color: #fff;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 40px 20px;
  color: #909399;
}

.empty-state p {
  margin: 8px 0;
}

/* 消息样式 */
.message {
  margin-bottom: 20px;
  padding: 12px;
  border-radius: 8px;
  max-width: 90%;
}

.message.user {
  background-color: #e6f7ff;
  margin-left: auto;
}

.message.assistant {
  background-color: #f0f9ff;
  border: 1px solid #bae7ff;
}

/* 消息头部 */
.message-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
  font-size: 12px;
  color: #606266;
}

.role-label {
  font-weight: 500;
}

/* 消息内容 */
.message-content {
  line-height: 1.6;
}

.question-content {
  color: #303133;
  word-wrap: break-word;
}

.code-content {
  margin-top: 10px;
}

.code-header {
  padding: 4px 8px;
  background-color: #e6f7ff;
  border-radius: 4px 4px 0 0;
  font-size: 12px;
  color: #1890ff;
}

.code-content pre {
  margin: 0;
  padding: 12px;
  background-color: #f5f7fa;
  border: 1px solid #e8e8e8;
  border-radius: 0 0 4px 4px;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 14px;
  overflow-x: auto;
  white-space: pre-wrap;
}

.response-content {
  color: #303133;
  word-wrap: break-word;
}

/* Markdown内容样式 */
.response-content [v-html] {
  line-height: 1.6;
  word-break: break-word;
}

/* Markdown标题样式 */
.response-content h1 {
  font-size: 1.5em;
  margin: 1em 0 0.5em 0;
  font-weight: bold;
  color: #333;
}

.response-content h2 {
  font-size: 1.3em;
  margin: 1em 0 0.5em 0;
  font-weight: bold;
  color: #333;
}

.response-content h3 {
  font-size: 1.1em;
  margin: 1em 0 0.5em 0;
  font-weight: bold;
  color: #333;
}

/* Markdown列表样式 */
.response-content ul, .response-content ol {
  margin: 0.5em 0;
  padding-left: 2em;
}

.response-content li {
  margin: 0.3em 0;
}

/* Markdown代码块样式 */
.response-content pre {
  background-color: #f5f7fa;
  border-radius: 4px;
  padding: 12px;
  overflow-x: auto;
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
  font-size: 14px;
  line-height: 1.5;
  margin: 1em 0;
}

.response-content code {
  background-color: #f5f7fa;
  padding: 2px 4px;
  border-radius: 3px;
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
  font-size: 0.9em;
}

.response-content pre code {
  background-color: transparent;
  padding: 0;
}

/* Markdown引用样式 */
.response-content blockquote {
  border-left: 4px solid #409eff;
  padding-left: 12px;
  margin: 1em 0;
  color: #666;
  font-style: italic;
}

/* 打字动画指示器样式 */
.typing-indicator {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-top: 8px;
}

.typing-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background-color: #909399;
  animation: typing 1.4s infinite ease-in-out both;
}

.typing-dot:nth-child(1) {
  animation-delay: -0.32s;
}

.typing-dot:nth-child(2) {
  animation-delay: -0.16s;
}

@keyframes typing {
  0%, 80%, 100% {
    transform: scale(0);
  }
  40% {
    transform: scale(1);
  }
}

/* 加载消息 */
.loading-message {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 12px;
  color: #909399;
  font-size: 14px;
}

.loading-spinner {
  width: 20px;
  height: 20px;
  border: 2px solid #e6f7ff;
  border-top: 2px solid #1890ff;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-right: 8px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* 输入区域 */
.input-area {
  padding: 16px;
  background-color: #f0f7ff;
  border-top: 1px solid #d9ecff;
}

/* 问题输入框 */
.question-input-wrapper {
  margin-bottom: 12px;
}

.question-input {
  width: 100%;
  padding: 10px;
  border: 1px solid #bae7ff;
  border-radius: 4px;
  resize: vertical;
  font-size: 14px;
  line-height: 1.5;
  background-color: #fff;
  transition: border-color 0.3s;
}

.question-input:focus {
  outline: none;
  border-color: #409eff;
}

/* 代码输入包装器 */
.code-input-wrapper {
  margin-bottom: 12px;
  background-color: #fff;
  border-radius: 4px;
  overflow: hidden;
}

/* 操作按钮 */
.action-buttons {
  display: flex;
  justify-content: space-between;
}

.toggle-code-button,
.send-button {
  padding: 8px 20px;
}

.send-button {
  background-color: #1890ff;
  border-color: #1890ff;
}

.send-button:hover {
  background-color: #40a9ff;
  border-color: #40a9ff;
}

.send-button:disabled {
  background-color: #f5f7fa;
  border-color: #d9d9d9;
  color: #bfbfbf;
}
</style>