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
        <div class="header-actions">
          <el-tooltip content="历史记录" placement="bottom">
            <div class="action-btn" @click="showHistory = !showHistory">
              <el-icon>
                <Clock />
              </el-icon>
            </div>
          </el-tooltip>
          <el-tooltip content="设置" placement="bottom">
            <div class="action-btn" @click="showSettings = true">
              <el-icon>
                <Setting />
              </el-icon>
            </div>
          </el-tooltip>
        </div>
      </div>
    </div>

    <!-- 主体内容 - 包含历史记录和聊天区域 -->
    <div class="companion-main">
      <!-- 历史记录侧边栏 -->
      <transition name="slide">
        <div class="history-sidebar" v-show="showHistory">
          <div class="sidebar-header">
            <h3>历史对话</h3>
            <el-button type="text" size="small" @click="clearHistory" class="clear-btn">
              <el-icon>
                <Delete />
              </el-icon>
              清空
            </el-button>
          </div>
          <div class="history-list">
            <div v-for="(item, index) in chatHistory" :key="index" class="history-item" @click="loadHistory(index)">
              <div class="history-date">{{ formatTime(item.time) }}</div>
              <div class="history-preview">{{ item.messages[0]?.content || '新对话' }}</div>
            </div>
          </div>
        </div>
      </transition>

      <!-- 聊天区域 - 中间内容区域 -->
      <div class="chat-container" :class="{ 'with-history': showHistory }">
        <!-- 消息列表 - 中间可滚动区域 -->
        <div class="messages-container" ref="messagesContainer">
          <!-- 欢迎消息 -->
          <div class="welcome-section" v-if="messages.length === 0">
            <div class="welcome-card">
              <div class="ai-avatar-welcome">
                <img src="@/assets/AI.png" alt="AI" />
                <div class="pulse-ring"></div>
              </div>
              <h2 class="welcome-title">你好！我是你的智能学伴 👋</h2>
              <p class="welcome-subtitle">我可以帮助你解答问题、制定学习计划、推荐资源等</p>

              <div class="quick-actions-grid">
                <div v-for="action in quickActions" :key="action.text" class="action-card"
                  @click="sendQuickAction(action)">
                  <div class="action-icon">{{ action.icon }}</div>
                  <span>{{ action.text }}</span>
                </div>
              </div>
            </div>
          </div>

          <!-- 消息列表 -->
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
                    <el-tooltip content="重新生成" placement="top">
                      <div class="tool-btn" @click="regenerateResponse(index)">
                        <el-icon>
                          <Refresh />
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
        </div>
      </div>

      </div>

    <!-- 输入区域 - 独立的底部区域 -->
    <div class="input-area">
      <div class="input-wrapper">
        <div class="input-box">
          <div class="input-toolbar">
            <el-upload action="#" :auto-upload="false" :show-file-list="false" accept="image/*,.pdf,.doc,.docx"
              :on-change="handleFileUpload">
              <div class="tool-btn">
                <el-icon>
                  <Paperclip />
                </el-icon>
              </div>
            </el-upload>
          </div>
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

    <!-- 设置对话框 -->
    <el-dialog v-model="showSettings" title="设置" width="500px" :before-close="handleSettingsClose">
      <div class="settings-content">
        <div class="setting-item" v-for="setting in settingItems" :key="setting.key">
          <div class="setting-label">
            <span class="setting-icon">{{ setting.icon }}</span>
            <span>{{ setting.label }}</span>
          </div>
          <div class="setting-control">
            <el-switch v-if="setting.type === 'switch'" v-model="settings[setting.key]" />
            <el-select v-else-if="setting.type === 'select'" v-model="settings[setting.key]">
              <el-option v-for="option in setting.options" :key="option.value" :label="option.label"
                :value="option.value" />
            </el-select>
          </div>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showSettings = false">取消</el-button>
          <el-button type="primary" @click="saveSettings">保存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted, nextTick, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  ArrowLeft,
  Clock,
  Setting,
  Delete,
  CopyDocument,
  Refresh,
  Paperclip,
  More,
  Promotion
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

// 数据
const courseId = ref(route.query.courseId)
const courseTitle = ref(route.query.courseTitle)
const inputMessage = ref('')
const messages = ref([])
const isTyping = ref(false)
const showHistory = ref(false)
const showSettings = ref(false)
const showEmojiPicker = ref(false)
const inputRows = ref(1)
const inputTips = ref(false)
const messagesContainer = ref(null)

// 设置
const settings = reactive({
  model: 'gpt-4',
  style: 'friendly',
  autoRead: false,
  saveHistory: true
})

// 聊天历史
const chatHistory = ref([])

// 快捷操作
const quickActions = ref([
  { text: '介绍一下这门课程', icon: '📚', prompt: '请介绍一下这门课程的主要内容和学习目标' },
  { text: '推荐学习计划', icon: '📅', prompt: '请为我制定一个详细的学习计划' },
  { text: '解释难点概念', icon: '💡', prompt: '我遇到了一些难以理解的概念，请帮我解释' },
  { text: '练习题推荐', icon: '✏️', prompt: '请推荐一些适合我的练习题' }
])


// 设置项配置
const settingItems = ref([
  {
    key: 'model',
    label: 'AI模型',
    icon: '🤖',
    type: 'select',
    options: [
      { label: 'GPT-4', value: 'gpt-4' },
      { label: 'GPT-3.5', value: 'gpt-3.5' }
    ]
  },
  {
    key: 'style',
    label: '回复风格',
    icon: '🎨',
    type: 'select',
    options: [
      { label: '专业', value: 'professional' },
      { label: '友好', value: 'friendly' },
      { label: '简洁', value: 'concise' }
    ]
  },
  {
    key: 'autoRead',
    label: '自动朗读',
    icon: '🔊',
    type: 'switch'
  },
  {
    key: 'saveHistory',
    label: '保存历史',
    icon: '💾',
    type: 'switch'
  }
])

// 返回
const goBack = () => {
  router.go(-1)
}

// 发送消息
const sendMessage = async () => {
  if (!inputMessage.value.trim() || isTyping.value) return

  const userMessage = {
    type: 'user',
    content: inputMessage.value.trim(),
    time: new Date()
  }

  messages.value.push(userMessage)
  const currentInput = inputMessage.value
  inputMessage.value = ''
  inputRows.value = 1

  // 滚动到底部
  await nextTick()
  scrollToBottom()

  // 显示AI正在输入
  isTyping.value = true

  // 模拟AI回复
  setTimeout(() => {
    const aiResponse = generateAIResponse(currentInput)
    messages.value.push({
      type: 'ai',
      content: aiResponse,
      time: new Date()
    })
    isTyping.value = false
    scrollToBottom()
  }, 1000 + Math.random() * 1000)
}

// 生成AI回复（模拟）
const generateAIResponse = (userInput) => {
  const responses = [
    `关于您提到的"${userInput}"，这是一个很好的问题。让我为您详细解答：

根据我的理解，这个问题涉及到课程的核心概念。建议您从以下几个方面来理解：

1. **基本概念**：首先需要掌握相关的基础知识
2. **实际应用**：结合具体的案例来加深理解
3. **练习巩固**：通过练习来检验和巩固学习成果

如果您还有其他问题，随时可以问我！`,
    `我理解您想要了解"${userInput}"相关的内容。

这里有一些学习建议：

📚 **学习路径**：
- 先掌握基础理论
- 再进行实践操作
- 最后进行总结反思

💡 **学习技巧**：
- 制定明确的学习目标
- 保持持续的学习节奏
- 及时复习和总结

需要我为您提供更详细的指导吗？`,
    `收到您关于"${userInput}"的询问！

作为您的智能学伴，我很乐意为您提供帮助。基于当前的课程内容，我建议您可以：

1. **重点关注**：
   - 理解核心概念
   - 掌握关键方法
   - 完成实践练习

2. **学习资源**：
   - 课程教材第X章
   - 相关视频教程
   - 在线练习平台

3. **学习计划**：
   - 每天1-2小时学习
   - 理论与实践结合
   - 定期复习

有什么具体需要我协助的吗？`
  ]

  return responses[Math.floor(Math.random() * responses.length)]
}

// 快捷操作
const sendQuickAction = (action) => {
  inputMessage.value = action.prompt
  sendMessage()
}

// 处理Enter键
const handleEnterKey = (event) => {
  if (!event.ctrlKey) {
    sendMessage()
  }
}

// 处理输入变化
const handleInputChange = () => {
  const lines = inputMessage.value.split('\n')
  inputRows.value = Math.min(Math.max(lines.length, 1), 5)
  inputTips.value = inputMessage.value.length > 0
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

// 重新生成回复
const regenerateResponse = (index) => {
  const userMessage = messages.value[index - 1]
  if (userMessage && userMessage.type === 'user') {
    messages.value = messages.value.slice(0, index)
    isTyping.value = true
    setTimeout(() => {
      const aiResponse = generateAIResponse(userMessage.content)
      messages.value.push({
        type: 'ai',
        content: aiResponse,
        time: new Date()
      })
      isTyping.value = false
    }, 1000)
  }
}

// 处理文件上传
const handleFileUpload = (file) => {
  ElMessage.info(`文件 ${file.name} 已准备上传`)
  // TODO: 实现文件上传逻辑
}

// 清空历史
const clearHistory = () => {
  chatHistory.value = []
  ElMessage.success('历史记录已清空')
}

// 加载历史
const loadHistory = (index) => {
  const history = chatHistory.value[index]
  if (history) {
    messages.value = [...history.messages]
    showHistory.value = false
  }
}

// 格式化时间
const formatTime = (date) => {
  const now = new Date()
  const diff = now - date
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))

  if (days === 0) {
    return '今天'
  } else if (days === 1) {
    return '昨天'
  } else if (days < 7) {
    return `${days}天前`
  } else {
    return date.toLocaleDateString()
  }
}

// 格式化消息时间
const formatMessageTime = (date) => {
  return date.toLocaleTimeString('zh-CN', {
    hour: '2-digit',
    minute: '2-digit'
  })
}


// 保存设置
const saveSettings = () => {
  localStorage.setItem('aiCompanionSettings', JSON.stringify(settings))
  showSettings.value = false
  ElMessage.success('设置已保存')
}

// 处理设置关闭
const handleSettingsClose = () => {
  showSettings.value = false
}

// 生命周期
onMounted(() => {
  // 防止页面滚动
  document.body.style.overflow = 'hidden'
  document.documentElement.style.overflow = 'hidden'

  // 加载设置
  const savedSettings = localStorage.getItem('aiCompanionSettings')
  if (savedSettings) {
    Object.assign(settings, JSON.parse(savedSettings))
  }

  // 加载历史记录
  const savedHistory = localStorage.getItem('aiCompanionHistory')
  if (savedHistory) {
    chatHistory.value = JSON.parse(savedHistory)
  }

  // 监听输入框变化
  watch(inputMessage, () => {
    inputTips.value = inputMessage.value.length > 0
  })
})

// 组件卸载时恢复页面滚动
onUnmounted(() => {
  document.body.style.overflow = ''
  document.documentElement.style.overflow = ''
})
</script>

<style scoped>
/* 防止页面滚动 */
.ai-companion-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: linear-gradient(135deg, #e0f2fe 0%, #e6f7ff 25%, #f0fdf4 50%, #e6f4ea 100%);
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
  box-shadow: 0 2px 20px rgba(59, 130, 246, 0.1);
  z-index: 100;
  flex-shrink: 0;
  height: auto;
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
  background: linear-gradient(135deg, #f0f9ff 0%, #e0f2fe 100%);
  border: 1px solid rgba(59, 130, 246, 0.2);
  cursor: pointer;
  transition: all 0.3s;
}

.back-button:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.2);
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
  background: #3b82f6;
  border: 3px solid #fff;
  border-radius: 50%;
  box-shadow: 0 0 0 1px rgba(59, 130, 246, 0.3);
  animation: pulse-online 2s ease-in-out infinite;
}

@keyframes pulse-online {

  0%,
  100% {
    box-shadow: 0 0 0 1px rgba(34, 197, 94, 0.3);
  }

  50% {
    box-shadow: 0 0 0 4px rgba(34, 197, 94, 0.1);
  }
}

.ai-details h1 {
  margin: 0;
  font-size: 24px;
  background: linear-gradient(135deg, #22c55e 0%, #06b6d4 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.status-text {
  margin: 4px 0 0 0;
  font-size: 14px;
  color: #606266;
  display: flex;
  align-items: center;
  gap: 6px;
}

.online-dot {
  width: 8px;
  height: 8px;
  background: #22c55e;
  border-radius: 50%;
  box-shadow: 0 0 0 2px rgba(34, 197, 94, 0.2);
}

.header-actions {
  display: flex;
  gap: 8px;
}

.action-btn {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.9);
  border: 1px solid rgba(34, 197, 94, 0.1);
  cursor: pointer;
  transition: all 0.3s;
  font-size: 18px;
  color: #606266;
}

.action-btn:hover {
  background: linear-gradient(135deg, #f0fdf4 0%, #ecfdf5 100%);
  transform: scale(1.05);
}

/* 主体内容 - 中间聊天区域 */
.companion-main {
  flex: 1;
  display: flex;
  overflow: hidden;
  position: relative;
  min-height: 0;
  height: calc(100vh - 172px); /* 减去头部和输入框的高度 */
}

/* 动画过渡 */
.slide-enter-active,
.slide-leave-active {
  transition: transform 0.3s ease;
}

.slide-enter-from,
.slide-leave-to {
  transform: translateX(-100%);
}

.slide-right-enter-active,
.slide-right-leave-active {
  transition: transform 0.3s ease;
}

.slide-right-enter-from,
.slide-right-leave-to {
  transform: translateX(100%);
}

/* 历史记录侧边栏 */
.history-sidebar {
  width: 300px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-right: 1px solid rgba(34, 197, 94, 0.1);
  display: flex;
  flex-direction: column;
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  z-index: 10;
}

.sidebar-header {
  padding: 20px;
  border-bottom: 1px solid rgba(34, 197, 94, 0.1);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.sidebar-header h3 {
  margin: 0;
  font-size: 18px;
  color: #303133;
}

.clear-btn {
  color: #f56565;
}

.history-list {
  flex: 1;
  overflow-y: auto;
  padding: 12px;
}

.history-item {
  padding: 16px;
  cursor: pointer;
  border-radius: 12px;
  margin-bottom: 8px;
  transition: all 0.3s;
  background: rgba(255, 255, 255, 0.6);
  border: 1px solid rgba(34, 197, 94, 0.05);
}

.history-item:hover {
  background: rgba(240, 253, 244, 0.8);
  transform: translateX(4px);
}

.history-date {
  font-size: 12px;
  color: #909399;
  margin-bottom: 6px;
}

.history-preview {
  font-size: 14px;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-weight: 500;
}

/* 聊天容器 */
.chat-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  transition: margin-left 0.3s ease;
  min-height: 0;
}

.chat-container.with-history {
  margin-left: 300px;
}

/* 消息容器 */
.messages-container {
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
  padding: 32px;
  display: flex;
  flex-direction: column;
  gap: 20px;
  min-height: 0;
  max-height: 100%;
  scroll-behavior: smooth;
}

/* 确保滚动条样式美观 */
.messages-container::-webkit-scrollbar {
  width: 6px;
}

.messages-container::-webkit-scrollbar-track {
  background: transparent;
}

.messages-container::-webkit-scrollbar-thumb {
  background: rgba(34, 197, 94, 0.3);
  border-radius: 3px;
}

.messages-container::-webkit-scrollbar-thumb:hover {
  background: rgba(34, 197, 94, 0.5);
}

/* 欢迎消息 */
.welcome-section {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100%;
  padding: 40px;
}

.welcome-card {
  text-align: center;
  max-width: 600px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 24px;
  padding: 48px;
  box-shadow: 0 8px 32px rgba(34, 197, 94, 0.1);
  border: 1px solid rgba(34, 197, 94, 0.1);
}

.ai-avatar-welcome {
  width: 120px;
  height: 120px;
  margin: 0 auto 32px;
  position: relative;
  border-radius: 50%;
  overflow: hidden;
  background: white;
  box-shadow: 0 4px 20px rgba(34, 197, 94, 0.15);
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
  border: 3px solid rgba(34, 197, 94, 0.3);
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
  font-size: 32px;
  background: linear-gradient(135deg, #22c55e 0%, #06b6d4 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.welcome-subtitle {
  margin: 0 0 40px 0;
  font-size: 18px;
  color: #606266;
}

.quick-actions-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.action-card {
  padding: 20px;
  background: linear-gradient(135deg, #f0fdf4 0%, #ecfdf5 100%);
  border: 1px solid rgba(34, 197, 94, 0.2);
  border-radius: 16px;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}

.action-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(34, 197, 94, 0.2);
}

.action-icon {
  font-size: 32px;
}

.action-card span {
  font-size: 16px;
  color: #16a34a;
  font-weight: 500;
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
  border-radius: 20px;
  font-size: 15px;
  line-height: 1.6;
  word-wrap: break-word;
}

.user-bubble .bubble-content {
  background: linear-gradient(135deg, #22c55e 0%, #16a34a 100%);
  color: #fff;
  border-bottom-right-radius: 6px;
}

.ai-bubble .bubble-content {
  background: rgba(255, 255, 255, 0.95);
  color: #303133;
  border-bottom-left-radius: 6px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.message-time {
  font-size: 12px;
  color: #909399;
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
  color: #606266;
}

.tool-btn:hover {
  background: rgba(34, 197, 94, 0.1);
  color: #22c55e;
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
  border-radius: 20px;
  border-bottom-left-radius: 6px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.typing-indicator span {
  width: 8px;
  height: 8px;
  background: #909399;
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

/* 输入区域 - 固定在底部 */
.input-area {
  background: rgba(255, 255, 255, 0.98);
  backdrop-filter: blur(20px);
  border-top: 1px solid rgba(59, 130, 246, 0.1);
  padding: 20px 32px;
  flex-shrink: 0;
  position: sticky;
  bottom: 0;
  z-index: 50;
  height: 92px; /* 固定输入框高度 */
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
  border: 1px solid rgba(34, 197, 94, 0.2);
  border-radius: 24px;
  padding: 8px 8px 8px 20px;
  transition: all 0.3s;
}

.input-box:focus-within {
  border-color: rgba(34, 197, 94, 0.5);
  box-shadow: 0 0 0 3px rgba(34, 197, 94, 0.1);
}

.input-toolbar {
  display: flex;
  align-items: center;
}

.input-toolbar .tool-btn {
  width: 32px;
  height: 32px;
  background: transparent;
  color: #909399;
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
  background: #e5e7eb;
  color: #9ca3af;
  cursor: pointer;
  transition: all 0.3s;
  font-size: 18px;
}

.send-button.active {
  background: linear-gradient(135deg, #22c55e 0%, #16a34a 100%);
  color: #fff;
  transform: scale(1.05);
}

.send-button.active:hover {
  transform: scale(1.1);
  box-shadow: 0 4px 12px rgba(34, 197, 94, 0.4);
}

.input-hint {
  font-size: 12px;
  color: #909399;
  text-align: center;
}


/* 设置对话框 */
.settings-content {
  padding: 12px 0;
}

.setting-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 0;
  border-bottom: 1px solid #f0f0f0;
}

.setting-item:last-child {
  border-bottom: none;
}

.setting-label {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 15px;
  color: #303133;
}

.setting-icon {
  font-size: 20px;
}

.setting-control {
  display: flex;
  align-items: center;
}

/* 对话框样式覆盖 */
:deep(.el-dialog) {
  border-radius: 16px;
  overflow: hidden;
}

:deep(.el-dialog__header) {
  background: linear-gradient(135deg, #f0fdf4 0%, #ecfdf5 100%);
  padding: 20px 24px;
  margin: 0;
}

:deep(.el-dialog__title) {
  font-size: 20px;
  color: #303133;
}

:deep(.el-dialog__body) {
  padding: 0 24px;
}

:deep(.el-dialog__footer) {
  padding: 16px 24px 24px;
  text-align: right;
}

:deep(.el-button--primary) {
  background: linear-gradient(135deg, #22c55e 0%, #16a34a 100%);
  border: none;
}

:deep(.el-switch.is-checked .el-switch__core) {
  background-color: #22c55e;
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

  .companion-main {
    height: calc(100vh - 152px); /* 移动端调整高度 */
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
    font-size: 28px;
  }

  .welcome-subtitle {
    font-size: 16px;
  }

  .quick-actions-grid {
    grid-template-columns: 1fr;
  }

  .action-card {
    padding: 16px;
  }

  .history-sidebar {
    width: 100%;
  }

  
  .input-area {
    padding: 16px 20px;
    height: 80px; /* 移动端减小高度 */
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

  .action-btn {
    width: 36px;
    height: 36px;
    font-size: 16px;
  }

  .welcome-card {
    padding: 24px 16px;
  }

  .welcome-title {
    font-size: 24px;
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