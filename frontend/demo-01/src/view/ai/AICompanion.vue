<template>
  <div class="ai-companion-container">
    <!-- 头部 -->
    <div class="companion-header">
      <div class="header-content">
        <div class="header-left">
          <div class="back-button" @click="goBack">
            <el-icon>
              <ArrowLeft />
            </el-icon>
          </div>
          <div class="ai-info">
            <div class="ai-avatar-header">
              <img src="@/assets/AI.png" alt="AI" />
              <div class="online-indicator"></div>
            </div>
            <div class="ai-details">
              <h1>{{ courseTitle || '智能学伴' }}</h1>
              <p class="status-text">
                <span class="online-dot"></span>
                在线 - 随时为您提供帮助
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 主体内容 -->
    <div class="companion-main">
      <div class="chat-container">
        <!-- 消息列表 -->
        <div class="messages-container" ref="messagesContainer">
          <!-- 欢迎界面 - 未开始聊天时显示 -->
          <div class="welcome-section" v-if="!chatStarted">
            <div class="welcome-card">
              <div class="ai-avatar-welcome">
                <img src="@/assets/AI.png" alt="AI" />
                <div class="pulse-ring"></div>
              </div>
              <h2 class="welcome-title">你好！我是你的智能学伴</h2>
              <p class="welcome-subtitle">快来和我一起愉快学习吧！</p>
              <el-button type="primary" size="large" class="start-btn" @click="startChat" :loading="isCreatingSession">
                开始引导式学习
              </el-button>
            </div>
          </div>

          <!-- 消息列表 - 开始聊天后显示 -->
          <template v-if="chatStarted">
            <div v-for="(message, index) in messages" :key="index" class="message-item"
              :class="{ 'user': message.type === 'user', 'ai': message.type === 'ai' }">
              <!-- 用户消息 -->
              <div v-if="message.type === 'user'" class="message-bubble user-bubble">
                <div class="bubble-content">{{ message.content }}</div>
                <div class="message-time">{{ formatMessageTime(message.time) }}</div>
              </div>
              <!-- AI消息 -->
              <div v-else class="message-bubble ai-bubble">
                <div class="ai-avatar-small">
                  <img src="@/assets/AI.png" alt="AI" />
                </div>
                <div class="bubble-content-wrapper">
                  <div class="bubble-content" v-html="message.content"></div>
                  <div class="message-meta">
                    <span class="message-time">{{ formatMessageTime(message.time) }}</span>
                    <div class="message-tools">
                      <el-tooltip content="复制" placement="top">
                        <div class="tool-btn" @click="copyMessage(message.content)">
                          <el-icon>
                            <CopyDocument />
                          </el-icon>
                        </div>
                      </el-tooltip>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <!-- AI正在输入 -->
            <div class="typing-bubble" v-if="isTyping">
              <div class="ai-avatar-small">
                <img src="@/assets/AI.png" alt="AI" />
              </div>
              <div class="typing-indicator">
                <span></span>
                <span></span>
                <span></span>
              </div>
            </div>
          </template>
        </div>
      </div>
    </div>

    <!-- 输入区域 - 仅在开始聊天后显示 -->
    <div class="input-area" v-if="chatStarted">
      <div class="input-wrapper">
        <div class="input-box">
          <el-input v-model="inputMessage" type="textarea" :rows="1" :autosize="{ minRows: 1, maxRows: 5 }"
            placeholder="输入你的问题..." @keydown.enter.prevent="handleEnterKey" resize="none" class="message-input" />
          <div class="send-button" @click="sendMessage" :class="{ 'active': inputMessage.trim() && !isTyping }">
            <el-icon>
              <Promotion />
            </el-icon>
          </div>
        </div>
        <div class="input-hint">
          按 Enter 发送，Shift + Enter 换行
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  ArrowLeft,
  CopyDocument,
  Promotion
} from '@element-plus/icons-vue'
import TeachingAPI from '@/api/modules/teaching/TeachingAPI'

const route = useRoute()
const router = useRouter()

// 数据
const courseId = ref(route.query.courseId)
const courseTitle = ref(route.query.courseTitle)
const inputMessage = ref('')
const messages = ref([])
const isTyping = ref(false)
const messagesContainer = ref(null)
const chatStarted = ref(false)
const sessionId = ref(null)
const isCreatingSession = ref(false)

const airesponse = ref('')

// 返回
const goBack = () => {
  router.go(-1)
}

// 创建会话
const createSession = async () => {
  try {
    isCreatingSession.value = true
    const res = await TeachingAPI.createSession()
    if (res.data && res.data.session_id) {
      sessionId.value = res.data.session_id
      airesponse.value = res.data.welcome_message || '智能学伴已就绪，您可以开始对话了！(本次对话最多六轮)'
    }
  } catch (error) {
    console.error('创建会话失败:', error)
  } finally {
    isCreatingSession.value = false
  }
}

// 开始聊天
const startChat = async () => {
  if (!sessionId.value) {
    await createSession()
  }
  if (!sessionId.value) {
    ElMessage.error('创建会话失败，请重试')
    return
  }
  messages.value.push({
    type: 'ai',
    content: airesponse.value,
    time: new Date()
  })
  messages.value.push({
    type: 'user',
    content: '开始引导式对话',
    time: new Date()
  })
  chatStarted.value = true
  isTyping.value = true

  try {
    const res = await TeachingAPI.chat({
      session_id: sessionId.value,
      user_input: '开始引导式对话'
    })
    messages.value.push({
      type: 'ai',
      content: res.data?.ai_response || '欢迎开始引导式学习！',
      time: new Date()
    })
  } catch (error) {
    console.error('开始对话失败:', error)
    messages.value.push({
      type: 'ai',
      content: '抱歉，网络出现问题，请稍后再试。',
      time: new Date()
    })
  } finally {
    isTyping.value = false
    await nextTick()
    scrollToBottom()
  }
}

// 发送消息
const sendMessage = async () => {
  if (!inputMessage.value.trim()) return
  // 如果正在等待响应，允许打断但不发送新消息
  if (isTyping.value) return

  const userMessage = {
    type: 'user',
    content: inputMessage.value.trim(),
    time: new Date()
  }

  messages.value.push(userMessage)
  const currentInput = inputMessage.value
  inputMessage.value = ''

  await nextTick()
  scrollToBottom()

  isTyping.value = true

  try {
    const res = await TeachingAPI.chat({
      session_id: sessionId.value,
      user_input: currentInput
    })
    messages.value.push({
      type: 'ai',
      content: res.data?.ai_response || '抱歉，我暂时无法回答这个问题。',
      time: new Date()
    })
  } catch (error) {
    console.error('发送消息失败:', error)
    messages.value.push({
      type: 'ai',
      content: '抱歉，网络出现问题，请稍后再试。',
      time: new Date()
    })
  } finally {
    isTyping.value = false
    scrollToBottom()
  }
}

// 处理Enter键
const handleEnterKey = (event) => {
  if (!event.shiftKey) {
    sendMessage()
  }
}

// 滚动到底部
const scrollToBottom = () => {
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
    }
  })
}

// 复制消息
const copyMessage = (content) => {
  navigator.clipboard.writeText(content)
  ElMessage.success('已复制到剪贴板')
}

// 格式化消息时间
const formatMessageTime = (date) => {
  return date.toLocaleTimeString('zh-CN', {
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 生命周期
onMounted(async () => {
  document.body.style.overflow = 'hidden'
  document.documentElement.style.overflow = 'hidden'
  // 进入界面时创建会话
  await createSession()
})

onUnmounted(() => {
  document.body.style.overflow = ''
  document.documentElement.style.overflow = ''
})
</script>

<style scoped>
.ai-companion-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: var(--bg-light);
  overflow: hidden;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
}

/* 头部样式 */
.companion-header {
  background: rgba(255, 255, 255, 0.98);
  backdrop-filter: blur(20px);
  box-shadow: 0 2px 20px var(--primary-alpha-10);
  z-index: 100;
  flex-shrink: 0;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 32px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.back-button {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--primary-lightest) 0%, var(--bg-primary-light) 100%);
  border: 1px solid var(--border-primary-lighter);
  cursor: pointer;
  transition: all 0.3s;
  color: var(--primary);
}

.back-button:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 12px var(--primary-alpha-10);
}

.ai-info {
  display: flex;
  align-items: center;
  gap: 16px;
}

.ai-avatar-header {
  position: relative;
  width: 56px;
  height: 56px;
}

.ai-avatar-header img {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.online-indicator {
  position: absolute;
  bottom: 2px;
  right: 2px;
  width: 14px;
  height: 14px;
  background: var(--primary);
  border: 3px solid #fff;
  border-radius: 50%;
  box-shadow: 0 0 0 1px var(--primary-alpha-10);
  animation: pulse-online 2s ease-in-out infinite;
}

@keyframes pulse-online {

  0%,
  100% {
    box-shadow: 0 0 0 1px var(--primary-alpha-10);
  }

  50% {
    box-shadow: 0 0 0 4px var(--primary-alpha-10);
  }
}

.ai-details h1 {
  margin: 0;
  font-size: 24px;
  color: var(--primary);
  font-weight: 700;
}

.status-text {
  margin: 4px 0 0 0;
  font-size: 14px;
  color: var(--text-regular);
  display: flex;
  align-items: center;
  gap: 6px;
}

.online-dot {
  width: 8px;
  height: 8px;
  background: var(--primary);
  border-radius: 50%;
  box-shadow: 0 0 0 2px var(--primary-alpha-10);
}

/* 主体内容 */
.companion-main {
  flex: 1;
  display: flex;
  overflow: hidden;
  min-height: 0;
}

.chat-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.messages-container {
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
  padding: 32px;
  display: flex;
  flex-direction: column;
  gap: 20px;
  min-height: 0;
  scroll-behavior: smooth;
}

.messages-container::-webkit-scrollbar {
  width: 6px;
}

.messages-container::-webkit-scrollbar-track {
  background: transparent;
}

.messages-container::-webkit-scrollbar-thumb {
  background: var(--primary-lighter);
  border-radius: 3px;
}

.messages-container::-webkit-scrollbar-thumb:hover {
  background: var(--primary-light);
}

/* 欢迎界面 */
.welcome-section {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100%;
  padding: 40px;
}

.welcome-card {
  text-align: center;
  max-width: 500px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  padding: 48px;
  box-shadow: 0 6px 18px var(--primary-alpha-10);
  border: 1px solid var(--border-primary-lighter);
}

.ai-avatar-welcome {
  width: 120px;
  height: 120px;
  margin: 0 auto 32px;
  position: relative;
  border-radius: 50%;
  overflow: hidden;
  background: white;
  box-shadow: 0 4px 20px var(--primary-alpha-10);
}

.ai-avatar-welcome img {
  width: 100%;
  height: 100%;
  object-fit: contain;
  border-radius: 50%;
}

.pulse-ring {
  position: absolute;
  top: -15px;
  left: -15px;
  right: -15px;
  bottom: -15px;
  border: 3px solid var(--primary-lighter);
  border-radius: 50%;
  animation: pulse-ring 2s ease-in-out infinite;
}

@keyframes pulse-ring {
  0% {
    transform: scale(1);
    opacity: 0.6;
  }

  50% {
    transform: scale(1.15);
    opacity: 0.2;
  }

  100% {
    transform: scale(1);
    opacity: 0.6;
  }
}

.welcome-title {
  margin: 0 0 16px 0;
  font-size: 28px;
  color: var(--primary);
  font-weight: 700;
}

.welcome-subtitle {
  margin: 0 0 32px 0;
  font-size: 16px;
  color: var(--text-regular);
}

.start-btn {
  background: var(--gradient-primary-soft);
  border: none;
  padding: 12px 32px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 8px;
}

.start-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px var(--primary-alpha-10);
}

/* 消息样式 */
.message-item {
  display: flex;
  animation: messageSlideIn 0.3s ease;
}

@keyframes messageSlideIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.message-item.user {
  justify-content: flex-end;
}

.message-item.ai {
  justify-content: flex-start;
}

.message-bubble {
  max-width: 70%;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.user-bubble {
  align-items: flex-end;
}

.ai-bubble {
  align-items: flex-start;
  flex-direction: row;
  gap: 12px;
}

.ai-avatar-small {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  overflow: hidden;
  flex-shrink: 0;
}

.ai-avatar-small img {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.bubble-content-wrapper {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.bubble-content {
  padding: 14px 20px;
  border-radius: 12px;
  font-size: 15px;
  line-height: 1.6;
  word-wrap: break-word;
}

.user-bubble .bubble-content {
  background: var(--gradient-primary-soft);
  color: #fff;
  border-bottom-right-radius: 4px;
}

.ai-bubble .bubble-content {
  background: rgba(255, 255, 255, 0.95);
  color: var(--primary-dark);
  border-bottom-left-radius: 4px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  border: 1px solid var(--border-primary-lighter);
}

.message-time {
  font-size: 12px;
  color: var(--text-regular);
  padding: 0 8px;
}

.message-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.message-tools {
  display: flex;
  gap: 4px;
  opacity: 0;
  transition: opacity 0.3s;
}

.ai-bubble:hover .message-tools {
  opacity: 1;
}

.tool-btn {
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.05);
  cursor: pointer;
  transition: all 0.3s;
  font-size: 14px;
  color: var(--text-regular);
}

.tool-btn:hover {
  background: var(--primary-lightest);
  color: var(--primary);
}

/* 输入提示 */
.typing-bubble {
  display: flex;
  gap: 12px;
  align-items: center;
  animation: fadeIn 0.3s ease;
}

.typing-indicator {
  display: flex;
  gap: 6px;
  align-items: center;
  background: rgba(255, 255, 255, 0.95);
  padding: 16px 20px;
  border-radius: 12px;
  border-bottom-left-radius: 4px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  border: 1px solid var(--border-primary-lighter);
}

.typing-indicator span {
  width: 8px;
  height: 8px;
  background: var(--primary-lighter);
  border-radius: 50%;
  animation: typingBounce 1.4s ease-in-out infinite;
}

.typing-indicator span:nth-child(1) {
  animation-delay: 0s;
}

.typing-indicator span:nth-child(2) {
  animation-delay: 0.2s;
}

.typing-indicator span:nth-child(3) {
  animation-delay: 0.4s;
}

@keyframes typingBounce {

  0%,
  60%,
  100% {
    transform: translateY(0);
    opacity: 0.5;
  }

  30% {
    transform: translateY(-10px);
    opacity: 1;
  }
}

/* 输入区域 */
.input-area {
  background: rgba(255, 255, 255, 0.98);
  backdrop-filter: blur(20px);
  border-top: 1px solid var(--border-primary-lighter);
  padding: 20px 32px;
  flex-shrink: 0;
  z-index: 50;
}

.input-wrapper {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.input-box {
  display: flex;
  align-items: flex-end;
  gap: 12px;
  background: rgba(255, 255, 255, 0.8);
  border: 1px solid var(--border-primary-lighter);
  border-radius: 12px;
  padding: 8px 8px 8px 20px;
  transition: all 0.3s;
}

.input-box:focus-within {
  border-color: var(--primary-light);
  box-shadow: 0 0 0 3px var(--primary-alpha-10);
}

.message-input {
  flex: 1;
}

.message-input :deep(.el-textarea__inner) {
  border: none;
  box-shadow: none;
  padding: 8px 0;
  resize: none;
  font-size: 15px;
  line-height: 1.5;
  background: transparent;
}

.send-button {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: var(--primary-lighter);
  color: #fff;
  cursor: pointer;
  transition: all 0.3s;
  font-size: 18px;
}

.send-button.active {
  background: var(--gradient-primary-soft);
  color: #fff;
  transform: scale(1.05);
}

.send-button.active:hover {
  transform: scale(1.1);
  box-shadow: 0 4px 12px var(--primary-alpha-10);
}

.input-hint {
  font-size: 12px;
  color: var(--text-regular);
  text-align: center;
}

/* 响应式 */
@media (max-width: 768px) {
  .ai-companion-container {
    position: fixed;
    width: 100vw;
    height: 100vh;
    overflow: hidden;
  }

  .header-content {
    padding: 12px 20px;
  }

  .header-left {
    gap: 12px;
  }

  .ai-details h1 {
    font-size: 20px;
  }

  .status-text {
    font-size: 13px;
  }

  .messages-container {
    padding: 20px 16px;
  }

  .message-bubble {
    max-width: 85%;
  }

  .welcome-card {
    padding: 32px 24px;
  }

  .welcome-title {
    font-size: 24px;
  }

  .welcome-subtitle {
    font-size: 14px;
  }

  .input-area {
    padding: 16px 20px;
  }
}

@media (max-width: 480px) {
  .header-content {
    padding: 8px 16px;
  }

  .ai-avatar-header {
    width: 48px;
    height: 48px;
  }

  .ai-details h1 {
    font-size: 18px;
  }

  .welcome-card {
    padding: 24px 16px;
  }

  .welcome-title {
    font-size: 22px;
  }

  .ai-avatar-welcome {
    width: 100px;
    height: 100px;
  }

  .bubble-content {
    font-size: 14px;
    padding: 12px 16px;
  }

  .input-area {
    padding: 12px 16px;
  }

  .send-button {
    width: 36px;
    height: 36px;
    font-size: 16px;
  }
}
</style>
