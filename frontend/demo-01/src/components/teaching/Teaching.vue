<template>
  <div class="teaching-container">
    <!-- 导航栏 -->
    <div class="nav-bar">
      <div class="nav-tabs">
        <div style="padding: 10px;">
          <!-- 返回按钮 -->
          <el-button 
            type="primary" 
            icon="ArrowLeft" 
            class="back-button" 
            @click="goBackToCourse"
            size="small"
          >
            <el-icon><ArrowLeft /></el-icon>
            返回课程
          </el-button>
        </div>
        <div 
          class="nav-tab" 
          :class="{ active: activeTab === 'teaching' }"
          @click="activeTab = 'teaching'"
        >
          教学资料
        </div>
        <div 
          class="nav-tab" 
          :class="{ active: activeTab === 'code' }"
          @click="activeTab = 'code'; nextTick(() => highlightCode())"
        >
          代码沙箱
        </div>
      </div>
    </div>

    <!-- 主内容区域 -->
    <div class="main-content">
      <!-- 主体部分 -->
      <div class="content-area">
        <!-- 教学资料部分 -->
        <div v-if="activeTab === 'teaching'" class="teaching-materials">
          <el-card class="teaching-card" v-for="(material, index) in materials" :key="index">
            <template #header>
              <div class="card-header">
                <span class="header-title">{{ material.description || '教学资料' }}</span>
              </div>
            </template>
            <div class="teaching-content">
              <div v-if="material.type === 'application/pdf' && pdfFile">
                <!-- 使用vue3-pdf-app组件展示PDF -->
                <VuePdfApp
                  :pdf="pdfFile" 
                  class="pdf-viewer"
                  :options="{
                    sidebarViewOnLoad: false,
                    toolbarOnLoad: true
                  }"
                />
              </div>
              <div v-else-if="material.description">
                <p>{{ material.description }}</p>
              </div>
              <div v-else>
                <p>暂无资料内容</p>
              </div>
            </div>
          </el-card>
          
          <!-- 如果没有资料 -->
          <div v-if="materials.length === 0" class="no-materials">
            <el-empty description="暂无教学资料"></el-empty>
          </div>
        </div>

        <!-- 代码沙箱部分 -->
        <div v-else-if="activeTab === 'code'" class="code-sandbox">
          <el-card class="code-editor-card">
            <template #header>
              <div class="card-header">
                <span class="header-title">代码编辑区</span>
                <el-select v-model="selectedLanguage" class="language-select" placeholder="选择语言">
                  <el-option label="C++" value="cpp"></el-option>
                  <el-option label="Python" value="python"></el-option>
                  <el-option label="Java" value="java"></el-option>
                </el-select>
              </div>
            </template>
            <div class="code-editor-wrapper">
              <pre ref="codeEditor" class="code-editor"><code ref="codeBlock" :class="`language-${selectedLanguage}`">{{ codeContent }}</code></pre>
              <textarea
                ref="codeInput"
                v-model="codeContent"
                class="code-input"
                :placeholder="`请输入${languageMap[selectedLanguage]}代码...`"
                @input="highlightCode"
                @keydown="handleKeyDown"
              ></textarea>
            </div>
          </el-card>

          <!-- 程序输入和运行结果 -->
          <div class="input-output-container">
            <!-- 程序输入 -->
            <el-card class="input-card">
              <template #header>
                <div class="card-header">
                  <span class="header-title">程序输入</span>
                </div>
              </template>
              <textarea
                v-model="programInput"
                class="text-input"
                placeholder="请输入程序运行所需的输入..."
              ></textarea>
            </el-card>

            <!-- 运行结果 -->
            <el-card class="output-card">
              <template #header>
                <div class="card-header">
                  <span class="header-title">运行结果</span>
                </div>
              </template>
              <pre class="output-display">{{ runOutput || '运行程序后显示结果...' }}</pre>
            </el-card>
          </div>

          <!-- 运行按钮 -->
          <div class="button-container">
            <el-button type="primary" size="large" class="run-button" @click="runProgram">
              <el-icon><Flag /></el-icon>
              运行
            </el-button>
          </div>
        </div>
      </div>

      <!-- 侧边栏 (AI对话框) -->
      <div class="sidebar">
        <div class="sidebar-header">
          <h3>AI助手</h3>
        </div>
        <div class="chat-messages" ref="chatMessages">
          <!-- 系统欢迎消息 -->
          <div class="message system-message">
            <div class="message-avatar">
              <el-icon><Compass /></el-icon>
            </div>
            <div class="message-content">
              <p>你好！我是你的编程助手。有什么可以帮助你的吗？</p>
            </div>
          </div>
          
          <!-- 用户消息和AI回复 -->
          <div v-for="(message, index) in messages" :key="index" class="message" :class="message.type">
            <div class="message-avatar">
              <el-icon v-if="message.type === 'user-message'" class="user-icon"><User /></el-icon>
              <el-icon v-else class="ai-icon"><Compass /></el-icon>
            </div>
            <div class="message-content">
              <div v-if="message.type === 'ai-message'" class="markdown-content" v-html="parseMarkdown(message.content)"></div>
              <p v-else>{{ message.content }}</p>
            </div>
          </div>
        </div>
        <div class="chat-input-area">
          <textarea
            v-model="inputMessage"
            class="chat-input"
            placeholder="请输入你的问题..."
            @keydown.enter.ctrl="sendQuestion"
            @keydown.enter.meta="sendQuestion"
          ></textarea>
          <el-button type="primary" class="send-button" @click="sendQuestion" :loading="isSending" :disabled="isSending">
              <el-icon><Upload /></el-icon>
              发送
            </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, nextTick, computed } from 'vue';
import hljs from 'highlight.js/lib/core';
import cpp from 'highlight.js/lib/languages/cpp';
import python from 'highlight.js/lib/languages/python';
import java from 'highlight.js/lib/languages/java';
import 'highlight.js/styles/atom-one-light.css';
import { ElCard, ElSelect, ElOption, ElButton, ElIcon, ElMessage } from 'element-plus';
import { Flag, User, Compass, Upload, ArrowLeft } from '@element-plus/icons-vue';
import { executeCode } from '@/api';
import { marked } from 'marked';
import { getMaterials } from '@/api';
import { getFileContents, downloadFile } from '@/api';
import { teach, answer } from '@/api'
import { useRouter } from 'vue-router';
import VuePdfApp from 'vue3-pdf-app';
import 'vue3-pdf-app/dist/icons/main.css';

// 配置 marked 以支持代码高亮
marked.setOptions({
  highlight: function(code, lang) {
    if (lang && hljs.getLanguage(lang)) {
      try {
        return hljs.highlight(code, { language: lang }).value;
      } catch (err) {
        console.error('代码高亮失败:', err);
      }
    }
    return code;
  },
  breaks: true,
  gfm: true
});

// 注册语言
hljs.registerLanguage('cpp', cpp);
hljs.registerLanguage('python', python);
hljs.registerLanguage('java', java);

// 响应式数据
// 导航相关
const activeTab = ref('teaching'); // 'teaching' 或 'code'

// 代码沙箱相关
const selectedLanguage = ref('cpp');
const codeContent = ref(getDefaultCode('cpp'));
const programInput = ref('');
const runOutput = ref('');
const codeBlock = ref(null);
const codeEditor = ref(null);
const codeInput = ref(null);

// 课程资料相关
const router = useRouter();
const materials = ref([]);
const pdfFile = ref(null);
const selectedCourseId = ref('');

// 代码沙箱输入格式
const CodeSandboxInput = {
  codeLanguage: '',
  code: '',
  input: ''
};

// 语言映射表
const languageMap = {
  cpp: 'C++',
  python: 'Python',
  java: 'Java'
};

// 聊天相关
const messages = ref([]);
const inputMessage = ref('');
const chatMessages = ref(null);

const isSending = ref(false); // 控制是否正在发送请求
const chatHistory = ref([]); // 聊天历史记录，最多存储10条

const teachingInput = {
  question: '',
  codeSandboxInput: {
    codeLanguage: '',
    code: '',
    input: ''
  }
}

// 从 sessionStorage 加载聊天历史
function loadChatHistory() {
  const savedHistory = sessionStorage.getItem('chatHistory');
  if (savedHistory) {
    try {
      chatHistory.value = JSON.parse(savedHistory);
      // 将历史记录加载到 messages 中
      messages.value = chatHistory.value.map(item => ({
        type: item.type, // 'user-message' 或 'ai-message'
        content: item.content
      }));
    } catch (error) {
      console.error('加载聊天历史失败:', error);
      chatHistory.value = [];
    }
  }
}

// 保存聊天历史到 sessionStorage
function saveChatHistory() {
  // 保持历史记录不超过10条
  if (chatHistory.value.length > 10) {
    chatHistory.value = chatHistory.value.slice(-10);
  }
  sessionStorage.setItem('chatHistory', JSON.stringify(chatHistory.value));
}

function updateTeachingInput(){
  teachingInput.question = inputMessage.value;
  teachingInput.codeSandboxInput.codeLanguage = selectedLanguage.value;
  teachingInput.codeSandboxInput.code = codeContent.value;
  teachingInput.codeSandboxInput.input = programInput.value;
}

async function sendQuestion(){
  if(!inputMessage.value.trim()) {
    ElMessage.error('请输入问题');
    return;
  }
  
  // 添加用户消息到界面
  const userMessage = inputMessage.value.trim();
  messages.value.push({
    type: 'user-message',
    content: userMessage
  });
  
  // 滚动到底部
  nextTick(() => {
    scrollToBottom();
  });
  
  // 设置发送状态为正在发送
  isSending.value = true;
  
  updateTeachingInput();
  console.log('发送问题如下');
  console.log(teachingInput);
  
  // 在更新teachingInput和打印后再清空输入框
  inputMessage.value = '';
  try{
    let response;
    if (activeTab.value === 'teaching'){response = await answer(teachingInput)}
    else if (activeTab.value === 'code'){response = await teach(teachingInput);}
  
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    
    // 获取响应的可读流
    const reader = response.body.getReader();
    const decoder = new TextDecoder();
    
    // 创建AI回复消息对象
    let aiMessageIndex = messages.value.length;
    messages.value.push({
      type: 'ai-message',
      content: ''
    });
    
    // 流式处理响应
    let fullResponse = '';
    while (true) {
      const { done, value } = await reader.read();
      
      if (done) {
        break;
      }
      
      // 解码接收到的数据
      const chunk = decoder.decode(value, { stream: true });
      fullResponse += chunk;
      
      // 更新AI回复内容
      messages.value[aiMessageIndex].content = fullResponse;
      
      // 滚动到底部
      nextTick(() => {
        scrollToBottom();
      });
    }
    
    // 所有数据接收完成后，更新聊天历史
    chatHistory.value.push({
      type: 'user-message',
      content: userMessage
    });
    chatHistory.value.push({
      type: 'ai-message',
      content: fullResponse
    });
    
    // 保存聊天历史
    saveChatHistory();
    
  } catch(err){
    console.error('发送问题失败:', err);
    ElMessage.error('发送问题失败');
    
    // 添加错误消息到界面
    messages.value.push({
      type: 'ai-message',
      content: '抱歉，我现在无法回答您的问题，请稍后再试。'
    });
    
    // 滚动到底部
    nextTick(() => {
      scrollToBottom();
    });
  } finally {
    // 设置发送状态为完成
    isSending.value = false;
  }
}

// 获取默认代码
function getDefaultCode(language) {
  switch(language) {
    case 'cpp':
      return '#include <iostream>\nusing namespace std;\n\nint main() {\n    cout << "Hello, World!" << endl;\n    return 0;\n}';
    case 'python':
      return 'print("Hello, World!")';
    case 'java':
      return 'public class HelloWorld {\n    public static void main(String[] args) {\n        System.out.println("Hello, World!");\n    }\n}';
    default:
      return '';
  }
}

// 代码高亮
function highlightCode() {
  if (codeBlock.value && codeContent.value) {
    // 清除之前的高亮类
    codeBlock.value.className = `language-${selectedLanguage.value}`;
    // 更新代码内容
    codeBlock.value.textContent = codeContent.value;
    // 清除之前的高亮状态，允许重复高亮
    delete codeBlock.value.dataset.highlighted;
    // 应用新的高亮
    hljs.highlightElement(codeBlock.value);
  }
}

// 同步滚动
function syncScroll(source, target) {
  if (source && target) {
    target.scrollTop = source.scrollTop;
    target.scrollLeft = source.scrollLeft;
  }
}

// 运行程序
function runProgram() {
  CodeSandboxInput.codeLanguage = selectedLanguage.value;
  CodeSandboxInput.code = codeContent.value;
  CodeSandboxInput.input = programInput.value;
  executeCode(CodeSandboxInput).then(res => {
    if (res.status === 200) {
      runOutput.value = res.data;
    }
  }).catch(err => {
    console.error('运行代码失败:', err);
  })
}

// 处理按键事件，实现括号自动补齐和Tab转换为空格
function handleKeyDown(event) {
  // 处理Tab键，替换为4个空格
  if (event.key === 'Tab') {
    event.preventDefault();
    
    const textarea = event.target;
    const start = textarea.selectionStart;
    const end = textarea.selectionEnd;
    const spaces = '    '; // 4个空格
    
    // 在光标位置插入4个空格
    codeContent.value = codeContent.value.substring(0, start) + spaces + codeContent.value.substring(end);
    
    // 移动光标到插入空格之后
    nextTick(() => {
      textarea.selectionStart = textarea.selectionEnd = start + spaces.length;
    });
  }
  
  // 处理括号自动补齐
  const brackets = {
    '(': ')',
    '[': ']',
    '{': '}'
  };
  
  if (brackets[event.key]) {
    event.preventDefault();
    
    const textarea = event.target;
    const start = textarea.selectionStart;
    const end = textarea.selectionEnd;
    const openBracket = event.key;
    const closeBracket = brackets[openBracket];
    
    // 在光标位置插入括号对
    codeContent.value = codeContent.value.substring(0, start) + openBracket + closeBracket + codeContent.value.substring(end);
    
    // 移动光标到括号中间
    nextTick(() => {
      textarea.selectionStart = textarea.selectionEnd = start + 1;
    });
  }
  
  // 处理右括号自动跳过
  const closingBrackets = [')', ']', '}'];
  if (closingBrackets.includes(event.key)) {
    const textarea = event.target;
    const start = textarea.selectionStart;
    
    // 如果光标后已经是对应的右括号，则跳过
    if (codeContent.value[start] === event.key) {
      event.preventDefault();
      
      // 移动光标到右括号之后
      nextTick(() => {
        textarea.selectionStart = textarea.selectionEnd = start + 1;
      });
    }
  }
}

// 获取课程资料
async function fetchMaterials() {
  try {
    // 从sessionStorage中读取selectedCourseId
    selectedCourseId.value = sessionStorage.getItem('selectedCourseId');
    if (!selectedCourseId.value) {
      ElMessage.error('未找到课程信息');
      return;
    }
    
    // 获取相关资料信息
    const response = await getMaterials(null, null, null, null, selectedCourseId.value);
    if (response && response.status === 200) {
      materials.value = response.data || [];
      
      // 查找PDF类型的资料
      const pdfMaterial = materials.value.find(mat => mat.type === 'application/pdf');
      if (pdfMaterial) {
        // 获取文件信息
        const fileContentResponse = await getFileContents(null, null, null, null, pdfMaterial.id);
        if (fileContentResponse && fileContentResponse.status === 200 && fileContentResponse.data && fileContentResponse.data.length > 0) {
          const fileContent = fileContentResponse.data[0];
          // 下载文件数据，确保使用正确的响应类型
          try {
            const fileResponse = await downloadFile(fileContent.name);
            if (fileResponse && fileResponse.status === 200) {
              // 确保响应是Blob对象
              let blob;
              if (fileResponse.data instanceof Blob) {
                blob = fileResponse.data;
              } else if (fileResponse.data instanceof ArrayBuffer) {
                blob = new Blob([fileResponse.data], { type: 'application/pdf' });
              } else if (typeof fileResponse.data === 'string') {
                // 处理base64编码的字符串
                const byteCharacters = atob(fileResponse.data.split(',')[1]);
                const byteNumbers = new Array(byteCharacters.length);
                for (let i = 0; i < byteCharacters.length; i++) {
                  byteNumbers[i] = byteCharacters.charCodeAt(i);
                }
                const byteArray = new Uint8Array(byteNumbers);
                blob = new Blob([byteArray], { type: 'application/pdf' });
              } else {
                blob = new Blob([JSON.stringify(fileResponse.data)], { type: 'application/pdf' });
              }
              
              // 创建URL并确保vue3-pdf-app能正确处理
              pdfFile.value = URL.createObjectURL(blob);
            }
          } catch (pdfError) {
            console.error('处理PDF文件失败:', pdfError);
            ElMessage.error('PDF文件加载失败');
          }
        }
      }
    }
  } catch (error) {
    console.error('获取课程资料失败:', error);
    ElMessage.error('获取课程资料失败');
  }
}

// 返回课程页面
function goBackToCourse() {
  // 从sessionStorage删除selectCourseId信息
  sessionStorage.removeItem('selectedCourseId');
  // 跳转到课程页面
  router.push('/course');
}

// 解析 Markdown 文本为 HTML
function parseMarkdown(text) {
  if (!text) return '';
  try {
    return marked(text);
  } catch (error) {
    console.error('Markdown 解析失败:', error);
    return text; // 解析失败时返回原始文本
  }
}

// 滚动聊天窗口到底部
function scrollToBottom() {
  if (chatMessages.value) {
    chatMessages.value.scrollTop = chatMessages.value.scrollHeight;
  }
}

// 监听语言变化
watch(selectedLanguage, (newLang) => {
  codeContent.value = getDefaultCode(newLang);
  // 使用nextTick确保DOM已更新
  nextTick(() => {
    highlightCode();
  });
});

// 监听代码内容变化，确保输入时也能高亮
watch(codeContent, () => {
  nextTick(() => {
    highlightCode();
  });
});

// 组件挂载时初始化
onMounted(() => {
  // 代码编辑器初始化
  nextTick(() => {
    highlightCode();
  });
  
  // 添加滚动同步事件监听
  if (codeInput.value && codeEditor.value) {
    codeInput.value.addEventListener('scroll', () => {
      syncScroll(codeInput.value, codeEditor.value);
    });
  }
  
  // 加载聊天历史
  loadChatHistory();
  
  // 获取课程资料
  fetchMaterials();
});
</script>

<style scoped>
/* 主容器样式 */
.teaching-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  width: 100%;
  background-color: #f0f7ff;
}

/* 导航栏样式 */
.nav-bar {
  height: 60px;
  background-color: #fff;
  border-bottom: 1px solid #e8e8e8;
  display: flex;
  align-items: center;
  padding: 0 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  gap: 20px;
}

/* 返回按钮样式 */
.back-button {
  margin-right: auto;
}

.nav-tabs {
  display: flex;
  gap: 2px;
}

.nav-tab {
  padding: 10px 30px;
  cursor: pointer;
  border-radius: 4px 4px 0 0;
  font-size: 16px;
  font-weight: 500;
  color: #666;
  transition: all 0.3s ease;
}

.nav-tab:hover {
  background-color: #f5f5f5;
  color: #1890ff;
}

.nav-tab.active {
  background-color: #1890ff;
  color: #fff;
}

/* 主内容区域样式 */
.main-content {
  flex: 1;
  display: flex;
  overflow: hidden;
}

/* 内容区域样式 */
.content-area {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  background-color: #f0f7ff;
}

/* 教学资料部分样式 */
.teaching-materials {
  width: 100%;
}

.teaching-card {
  width: 100%;
  min-height: 80vh;
}

.teaching-content {
  padding: 20px;
  font-size: 16px;
  line-height: 1.8;
}

.teaching-content h2 {
  color: #1890ff;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 2px solid #1890ff;
}

.teaching-content h3 {
  color: #333;
  margin-top: 30px;
  margin-bottom: 15px;
}

.teaching-content h4 {
  color: #555;
  margin-top: 20px;
  margin-bottom: 10px;
}

.teaching-content p {
  margin-bottom: 15px;
  color: #666;
}

.teaching-content ul {
  margin-bottom: 15px;
  padding-left: 20px;
}

.teaching-content ul li {
  margin-bottom: 8px;
  color: #666;
}

.teaching-content pre {
  background-color: #f8f8f8;
  padding: 15px;
  border-radius: 4px;
  overflow-x: auto;
  margin: 15px 0;
  border: 1px solid #ddd;
}

.teaching-content code {
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 14px;
  line-height: 1.5;
}

/* 代码沙箱部分样式 */
.code-sandbox {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.code-editor-card {
  width: 100%;
  height: 60vh;
}

.input-card,
.output-card {
  width: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-title {
  font-size: 16px;
  font-weight: bold;
}

.language-select {
  width: 150px;
}

/* 代码编辑器样式 */
.code-editor-wrapper {
  position: relative;
  min-height: 300px;
  height: 45vh;
}

/* 统一字体样式，确保pre和textarea完全一致 */
.code-editor,
.code-input {
  position: absolute;
  top: 0;
  left: 0;
  width: 98%;
  height: 100%;
  margin: 0;
  padding: 15px;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 14px;
  line-height: 1.5;
  letter-spacing: 0;
  word-spacing: 0;
  border: 1px solid #ddd;
  border-radius: 4px;
  overflow: auto;
}

/* 代码高亮显示区域 */
.code-editor {
  background: #f8f8f8;
  color: #333;
  pointer-events: none;
  z-index: 1;
}

/* 确保代码块内部的样式不会覆盖我们的设置 */
.code-editor code {
  background: transparent !important;
  padding: 0 !important;
  font-family: inherit !important;
  font-size: inherit !important;
  line-height: inherit !important;
  position: relative !important;
  width: auto !important;
  height: auto !important;
}

/* 确保内部span标签可以正常显示高亮颜色 */
.code-editor code span {
  color: inherit !important;
}

/* 代码输入区域 */
.code-input {
  background: transparent;
  color: transparent;
  caret-color: #000;
  resize: none;
  z-index: 2;
}

/* 确保光标在所有状态下都明显可见 */
.code-input:focus {
  outline: none;
  border-color: #409eff;
  caret-color: #000;
}

.code-input:focus {
  outline: none;
  border-color: #409eff;
}

/* 输入输出容器 */
.input-output-container {
  display: flex;
  gap: 20px;
  width: 100%;
}

.input-card,
.output-card {
  flex: 1;
}

/* 文本输入样式 */
.text-input {
  width: 95%;
  min-height: 200px;
  padding: 15px;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 14px;
  line-height: 1.5;
  border: 1px solid #ddd;
  border-radius: 4px;
  resize: vertical;
  overflow-y: auto;
}

.text-input:focus {
  outline: none;
  border-color: #409eff;
}

/* 输出显示样式 */
.output-display {
  width: 95%;
  min-height: 200px;
  margin: 0;
  padding: 15px;
  background-color: #f5f5f5;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 14px;
  line-height: 1.5;
  border: 1px solid #ddd;
  border-radius: 4px;
  overflow: auto;
  white-space: pre-wrap;
  word-wrap: break-word;
}

/* 按钮容器 */
.button-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
  margin-bottom: 20px;
}

.run-button {
  font-size: 16px;
  padding: 12px 40px;
}

/* 侧边栏样式 */
.sidebar {
  width: 400px;
  background-color: #fff;
  border-left: 1px solid #e8e8e8;
  display: flex;
  flex-direction: column;
  height: 100%;
  box-shadow: -2px 0 4px rgba(0, 0, 0, 0.1);
}

.sidebar-header {
  padding: 20px;
  border-bottom: 1px solid #e8e8e8;
  background-color: #fafafa;
}

.sidebar-header h3 {
  margin: 0;
  font-size: 18px;
  color: #333;
}

.chat-messages {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 15px;
  background-color: #f0f7ff;
}

.message {
  display: flex;
  gap: 10px;
  align-items: flex-start;
}

.message-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background-color: #f0f0f0;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.user-message .message-avatar {
  background-color: #1890ff;
  color: #fff;
}

.ai-message .message-avatar {
  background-color: #52c41a;
  color: #fff;
}

.system-message .message-avatar {
  background-color: #faad14;
  color: #fff;
}

.message-content {
  flex: 1;
  background-color: #f5f5f5;
  padding: 12px 16px;
  border-radius: 8px;
  max-width: 80%;
}

.user-message .message-content {
  background-color: #1890ff;
  color: #fff;
  align-self: flex-end;
  margin-left: auto;
}

.message-content p {
  margin: 0;
  line-height: 1.5;
}

.chat-input-area {
  padding: 20px;
  border-top: 1px solid #e8e8e8;
  display: flex;
  gap: 10px;
  align-items: flex-end;
}

.chat-input {
  flex: 1;
  min-height: 80px;
  max-height: 160px;
  padding: 10px 15px;
  border: 1px solid #ddd;
  border-radius: 4px;
  resize: none;
  font-size: 14px;
  line-height: 1.5;
  font-family: inherit;
}

.chat-input:focus {
  outline: none;
  border-color: #409eff;
}

.send-button {
  padding: 10px 20px;
  height: fit-content;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .sidebar {
    width: 350px;
  }
}

@media (max-width: 768px) {
  .main-content {
    flex-direction: column;
  }
  
  .sidebar {
    width: 100%;
    height: 50vh;
    border-left: none;
    border-top: 1px solid #e8e8e8;
  }
  
  .input-output-container {
    flex-direction: column;
  }
  
  .code-editor-wrapper,
  .text-input,
  .output-display {
    min-height: 200px;
  }
}

/* Markdown 内容样式 */
.markdown-content {
font-size: 14px;
line-height: 1.6;
color: #333;
}

/* PDF查看器样式 */
.pdf-viewer {
  width: 100%;
  height: 80vh;
  border: 1px solid #e8e8e8;
  border-radius: 4px;
  overflow: hidden;
}

.markdown-content h1, 
.markdown-content h2, 
.markdown-content h3, 
.markdown-content h4, 
.markdown-content h5, 
.markdown-content h6 {
margin-top: 16px;
margin-bottom: 8px;
color: #1890ff;
font-weight: 600;
}

.markdown-content h1 { font-size: 24px; }
.markdown-content h2 { font-size: 20px; }
.markdown-content h3 { font-size: 18px; }

.markdown-content p {
margin-bottom: 12px;
margin-top: 0;
}

.markdown-content a {
color: #1890ff;
text-decoration: none;
}

.markdown-content a:hover {
text-decoration: underline;
}

.markdown-content ul, 
.markdown-content ol {
margin-bottom: 12px;
padding-left: 24px;
}

.markdown-content ul li, 
.markdown-content ol li {
margin-bottom: 4px;
}

.markdown-content strong {
font-weight: 600;
}

.markdown-content em {
font-style: italic;
}

.markdown-content code {
background-color: #f5f5f5;
padding: 2px 4px;
border-radius: 3px;
font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
font-size: 13px;
}

.markdown-content pre {
background-color: #f5f5f5;
padding: 12px;
border-radius: 4px;
overflow-x: auto;
margin-bottom: 12px;
border: 1px solid #ddd;
}

.markdown-content pre code {
background-color: transparent;
padding: 0;
}

.markdown-content blockquote {
border-left: 4px solid #1890ff;
padding-left: 16px;
margin-left: 0;
margin-right: 0;
color: #666;
font-style: italic;
}

</style>