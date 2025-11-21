<template>
  <div class="teaching-container">
    <!-- 优化后的导航栏 -->
    <div class="nav-bar">
      <!-- 返回区域 -->
      <div class="nav-left">
        <el-button 
          type="default" 
          size="small"
          class="back-button"
          @click="goBackToCourse"
        >
          <el-icon><ArrowLeft /></el-icon>
          返回课程
        </el-button>
      </div>

      <!-- 标签页区域 -->
      <div class="nav-center">
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

      <!-- 右侧留空，保持居中 -->
      <div class="nav-right"></div>
    </div>

    <!-- 主内容区域 -->
    <div class="main-content">
      <!-- 主体内容 -->
      <div class="content-area">
        <!-- 教学资料 -->
        <div v-if="activeTab === 'teaching'" class="teaching-materials">
          <el-card class="teaching-card" v-for="(material, index) in materials" :key="index">
            <template #header>
              <div class="card-header">
                <span class="header-title">{{ material.description || '教学资料' }}</span>
              </div>
            </template>
            <div class="teaching-content">
              <div v-if="material.type === 'application/pdf' && pdfFile">
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
          
          <div v-if="materials.length === 0" class="no-materials">
            <el-empty description="暂无教学资料"></el-empty>
          </div>
        </div>

        <!-- 代码沙箱 -->
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

          <div class="input-output-container">
            <el-card class="input-card">
              <template #header>
                <div class="card-header">
                  <span class="header-title">程序输入</span>
                <el-button 
                  class="run-button" 
                  @click="runProgram"
                >
                <el-icon><Flag /></el-icon>
                  运行代码
                </el-button>
                </div>
              </template>
              <textarea
                v-model="programInput"
                class="text-input"
                placeholder="请输入程序运行所需的输入..."
              ></textarea>
            </el-card>

            <el-card class="output-card">
              <template #header>
                <div class="card-header">
                  <span class="header-title">运行结果</span>
                </div>
              </template>
              <pre class="output-display">{{ runOutput || '运行程序后显示结果...' }}</pre>
            </el-card>
          </div>
        </div>
      </div>

      <!-- AI 侧边栏 -->
      <div class="sidebar">
        <div class="sidebar-header">
          <h3>AI 编程助手</h3>
        </div>
        <div class="chat-messages" ref="chatMessages">
          <div class="message system-message">
            <div class="message-avatar">
              <el-icon><Compass /></el-icon>
            </div>
            <div class="message-content">
              <p>你好！我是你的编程助手。有什么可以帮助你的吗？</p>
            </div>
          </div>
          
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
            placeholder="请输入你的问题（支持 Ctrl+Enter 发送）..."
            @keydown.enter.ctrl.prevent="sendQuestion"
            @keydown.enter.meta.prevent="sendQuestion"
          ></textarea>
          <el-button 
            type="primary" 
            class="send-button" 
            @click="sendQuestion" 
            :loading="isSending" 
            :disabled="isSending || !inputMessage.trim()"
          >
            <el-icon><Upload /></el-icon>
            发送
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
// ========== 以下 JS 逻辑完全保留，不做修改 ==========
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

hljs.registerLanguage('cpp', cpp);
hljs.registerLanguage('python', python);
hljs.registerLanguage('java', java);

const activeTab = ref('teaching');
const selectedLanguage = ref('cpp');
const codeContent = ref(getDefaultCode('cpp'));
const programInput = ref('');
const runOutput = ref('');
const codeBlock = ref(null);
const codeEditor = ref(null);
const codeInput = ref(null);

const router = useRouter();
const materials = ref([]);
const pdfFile = ref(null);
const selectedCourseId = ref('');

const CodeSandboxInput = {
  codeLanguage: '',
  code: '',
  input: ''
};

const languageMap = {
  cpp: 'C++',
  python: 'Python',
  java: 'Java'
};

const messages = ref([]);
const inputMessage = ref('');
const chatMessages = ref(null);
const isSending = ref(false);
const chatHistory = ref([]);

const teachingInput = {
  question: '',
  codeSandboxInput: {
    codeLanguage: '',
    code: '',
    input: ''
  }
}

function loadChatHistory() {
  const savedHistory = sessionStorage.getItem('chatHistory');
  if (savedHistory) {
    try {
      chatHistory.value = JSON.parse(savedHistory);
      messages.value = chatHistory.value.map(item => ({
        type: item.type,
        content: item.content
      }));
    } catch (error) {
      console.error('加载聊天历史失败:', error);
      chatHistory.value = [];
    }
  }
}

function saveChatHistory() {
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
  
  const userMessage = inputMessage.value.trim();
  messages.value.push({ type: 'user-message', content: userMessage });
  nextTick(() => scrollToBottom());
  
  isSending.value = true;
  updateTeachingInput();
  inputMessage.value = '';
  
  try{
    let response;
    if (activeTab.value === 'teaching') response = await answer(teachingInput);
    else response = await teach(teachingInput);
    
    if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
    
    const reader = response.body.getReader();
    const decoder = new TextDecoder();
    
    let aiMessageIndex = messages.value.length;
    messages.value.push({ type: 'ai-message', content: '' });
    
    let fullResponse = '';
    while (true) {
      const { done, value } = await reader.read();
      if (done) break;
      const chunk = decoder.decode(value, { stream: true });
      fullResponse += chunk;
      messages.value[aiMessageIndex].content = fullResponse;
      nextTick(() => scrollToBottom());
    }
    
    chatHistory.value.push({ type: 'user-message', content: userMessage });
    chatHistory.value.push({ type: 'ai-message', content: fullResponse });
    saveChatHistory();
    
  } catch(err){
    console.error('发送问题失败:', err);
    ElMessage.error('发送问题失败');
    messages.value.push({
      type: 'ai-message',
      content: '抱歉，我现在无法回答您的问题，请稍后再试。'
    });
    nextTick(() => scrollToBottom());
  } finally {
    isSending.value = false;
  }
}

function getDefaultCode(language) {
  switch(language) {
    case 'cpp': return '#include <iostream>\nusing namespace std;\n\nint main() {\n    cout << "Hello, World!" << endl;\n    return 0;\n}';
    case 'python': return 'print("Hello, World!")';
    case 'java': return 'public class HelloWorld {\n    public static void main(String[] args) {\n        System.out.println("Hello, World!");\n    }\n}';
    default: return '';
  }
}

function highlightCode() {
  if (codeBlock.value && codeContent.value) {
    codeBlock.value.className = `language-${selectedLanguage.value}`;
    codeBlock.value.textContent = codeContent.value;
    delete codeBlock.value.dataset.highlighted;
    hljs.highlightElement(codeBlock.value);
  }
}

function syncScroll(source, target) {
  if (source && target) {
    target.scrollTop = source.scrollTop;
    target.scrollLeft = source.scrollLeft;
  }
}

function runProgram() {
  CodeSandboxInput.codeLanguage = selectedLanguage.value;
  CodeSandboxInput.code = codeContent.value;
  CodeSandboxInput.input = programInput.value;
  executeCode(CodeSandboxInput).then(res => {
    if (res.status === 200) runOutput.value = res.data;
  }).catch(err => console.error('运行代码失败:', err));
}

function handleKeyDown(event) {
  if (event.key === 'Tab') {
    event.preventDefault();
    const textarea = event.target;
    const start = textarea.selectionStart;
    const end = textarea.selectionEnd;
    const spaces = '    ';
    codeContent.value = codeContent.value.substring(0, start) + spaces + codeContent.value.substring(end);
    nextTick(() => textarea.selectionStart = textarea.selectionEnd = start + spaces.length);
  }
  
  const brackets = { '(': ')', '[': ']', '{': '}' };
  if (brackets[event.key]) {
    event.preventDefault();
    const textarea = event.target;
    const start = textarea.selectionStart;
    const end = textarea.selectionEnd;
    const open = event.key;
    const close = brackets[open];
    codeContent.value = codeContent.value.substring(0, start) + open + close + codeContent.value.substring(end);
    nextTick(() => textarea.selectionStart = textarea.selectionEnd = start + 1);
  }
  
  const closingBrackets = [')', ']', '}'];
  if (closingBrackets.includes(event.key)) {
    const textarea = event.target;
    const start = textarea.selectionStart;
    if (codeContent.value[start] === event.key) {
      event.preventDefault();
      nextTick(() => textarea.selectionStart = textarea.selectionEnd = start + 1);
    }
  }
}

async function fetchMaterials() {
  try {
    selectedCourseId.value = sessionStorage.getItem('selectedCourseId');
    if (!selectedCourseId.value) {
      ElMessage.error('未找到课程信息');
      return;
    }
    
    const response = await getMaterials(null, null, null, null, selectedCourseId.value);
    if (response && response.status === 200) {
      materials.value = response.data || [];
      const pdfMaterial = materials.value.find(mat => mat.type === 'application/pdf');
      if (pdfMaterial) {
        const fileContentResponse = await getFileContents(null, null, null, null, pdfMaterial.id);
        if (fileContentResponse?.status === 200 && fileContentResponse.data?.length > 0) {
          const fileContent = fileContentResponse.data[0];
          try {
            const fileResponse = await downloadFile(fileContent.name);
            if (fileResponse?.status === 200) {
              let blob;
              if (fileResponse.data instanceof Blob) blob = fileResponse.data;
              else if (fileResponse.data instanceof ArrayBuffer) blob = new Blob([fileResponse.data], { type: 'application/pdf' });
              else if (typeof fileResponse.data === 'string') {
                const byteCharacters = atob(fileResponse.data.split(',')[1]);
                const byteNumbers = new Array(byteCharacters.length);
                for (let i = 0; i < byteCharacters.length; i++) byteNumbers[i] = byteCharacters.charCodeAt(i);
                const byteArray = new Uint8Array(byteNumbers);
                blob = new Blob([byteArray], { type: 'application/pdf' });
              } else blob = new Blob([JSON.stringify(fileResponse.data)], { type: 'application/pdf' });
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

function goBackToCourse() {
  sessionStorage.removeItem('selectedCourseId');
  router.go(-1);
}

function parseMarkdown(text) {
  if (!text) return '';
  try { return marked(text); } 
  catch (error) { console.error('Markdown 解析失败:', error); return text; }
}

function scrollToBottom() {
  if (chatMessages.value) chatMessages.value.scrollTop = chatMessages.value.scrollHeight;
}

watch(selectedLanguage, (newLang) => {
  codeContent.value = getDefaultCode(newLang);
  nextTick(() => highlightCode());
});

watch(codeContent, () => nextTick(() => highlightCode()));

onMounted(() => {
  nextTick(() => highlightCode());
  if (codeInput.value && codeEditor.value) {
    codeInput.value.addEventListener('scroll', () => syncScroll(codeInput.value, codeEditor.value));
  }
  loadChatHistory();
  fetchMaterials();
});
</script>

<style scoped>
.teaching-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  width: 100%;
  background-color: #f9fbfd;
}

/* ========== 导航栏优化 ========== */
.nav-bar {
  height: 60px;
  background: #ffffff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  display: flex;
  align-items: center;
  padding: 0 24px;
  z-index: 10;
}

.nav-left, .nav-right {
  flex: 0 0 auto;
}

.nav-center {
  flex: 1;
  display: flex;
  justify-content: center;
  gap: 2px;
  height: 35px;
}

.back-button {
  font-weight: 500;
  color: #409eff;
}

.nav-tab {
  padding: 7.5px 24px;
  cursor: pointer;
  border-radius: 6px 6px 6px 6px;
  font-size: 15px;
  font-weight: 500;
  color: #606266;
  background: #f5f7fa;
  transition: all 0.25s ease;
  border: 1px solid transparent;
  border-bottom: none;
}

.nav-tab:hover {
  background: #eef2f7;
  color: #409eff;
}

.nav-tab.active {
  background: #409eff;
  color: white;
  border-color: #409eff;
}

/* ========== 主内容区 ========== */
.main-content {
  flex: 1;
  display: flex;
  overflow: hidden;
}

.content-area {
  flex: 1;
  padding: 24px;
  overflow-y: auto;
  background: #f9fbfd;
}

.teaching-card {
  margin-bottom: 24px;
  border-radius: 10px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.language-select {
  width: 140px;
}

/* ========== 代码沙箱 ========== */
.code-editor-card {
  margin-bottom: 20px;
  border-radius: 10px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
}

.code-editor-wrapper {
  position: relative;
  height: 48vh;
  border-radius: 6px;
  overflow: hidden;
}

.code-editor,
.code-input {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  margin: 0;
  padding: 16px;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 14px;
  line-height: 1.5;
  border: none;
  resize: none;
  outline: none;
}

.code-editor {
  background: #f8f9fa;
  color: #333;
  pointer-events: none;
  z-index: 1;
}

.code-editor code {
  background: transparent !important;
  padding: 0 !important;
  font-family: inherit !important;
}

.code-input {
  background: transparent;
  color: transparent;
  caret-color: #333;
  z-index: 2;
}

.input-output-container {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-bottom: 20px;
}

@media (max-width: 900px) {
  .input-output-container {
    grid-template-columns: 1fr;
  }
}

.text-input,
.output-display {
  width: 100%;
  min-height: 160px;
  padding: 14px;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 14px;
  line-height: 1.5;
  border-radius: 6px;
  border: 1px solid #dcdfe6;
  resize: vertical;
}

.text-input:focus {
  border-color: #409eff;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
}

.output-display {
  background: #f8f9fa;
  margin: 0;
  white-space: pre-wrap;
  word-wrap: break-word;
}

.button-container {
  display: flex;
  justify-content: center;
}

.run-button {
  padding: 6px 16px;
  font-size: 14px;
  font-weight: 600;
  color: white;
  background: linear-gradient(135deg, #66b1ff, #409eff);
  border: none;
  border-radius: 6px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 6px;
  transition: all 0.2s ease;
  box-shadow: 0 1px 4px rgba(64, 158, 255, 0.2);
  height: auto;
  min-height: auto;
}

.run-button:hover {
  background: linear-gradient(135deg, #53a8ff, #3a8fdb);
  transform: translateY(-1px);
  box-shadow: 0 2px 6px rgba(64, 158, 255, 0.3);
}

.run-button:active {
  transform: translateY(0);
  box-shadow: 0 1px 3px rgba(64, 158, 255, 0.25);
}

.run-button:focus {
  outline: none;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.25);
}

/* ========== 侧边栏 ========== */
.sidebar {
  width: 380px;
  background: white;
  border-left: 1px solid #ebeef5;
  display: flex;
  flex-direction: column;
  box-shadow: -2px 0 12px rgba(0, 0, 0, 0.06);
}

.sidebar-header {
  padding: 18px 20px;
  border-bottom: 1px solid #ebeef5;
  background: #fafafa;
}

.sidebar-header h3 {
  margin: 0;
  font-weight: 600;
  color: #303133;
}

.chat-messages {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 16px;
  background: #f9fbfd;
}

.message {
  display: flex;
  gap: 12px;
  max-width: 90%;
}

.message-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  background: #f0f2f5;
  color: #606266;
}

.user-message .message-avatar {
  background: #409eff;
  color: white;
}

.ai-message .message-avatar {
  background: #67c23a;
  color: white;
}

.system-message .message-avatar {
  background: #e6a23c;
  color: white;
}

.message-content {
  padding: 12px 16px;
  border-radius: 12px;
  line-height: 1.5;
  font-size: 14px;
  word-break: break-word;
}

.user-message .message-content {
  background: #409eff;
  color: white;
  align-self: flex-end;
  margin-left: auto;
}

.ai-message .message-content,
.system-message .message-content {
  background: white;
  border: 1px solid #ebeef5;
}

.chat-input-area {
  padding: 16px;
  border-top: 1px solid #ebeef5;
  display: flex;
  gap: 12px;
}

.chat-input {
  flex: 1;
  min-height: 70px;
  max-height: 140px;
  padding: 12px 14px;
  border: 1px solid #dcdfe6;
  border-radius: 6px;
  font-size: 14px;
  line-height: 1.5;
  resize: vertical;
}

.chat-input:focus {
  outline: none;
  border-color: #409eff;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
}

.send-button {
  padding: 12px 20px;
  height: fit-content;
  border-radius: 6px;
}

/* ========== 响应式 ========== */
@media (max-width: 1100px) {
  .sidebar {
    width: 340px;
  }
}

@media (max-width: 768px) {
  .main-content {
    flex-direction: column;
  }
  .sidebar {
    width: 100%;
    height: 45vh;
    border-left: none;
    border-top: 1px solid #ebeef5;
  }
  .nav-center {
    justify-content: flex-start;
    gap: 8px;
  }
  .nav-tab {
    padding: 8px 16px;
    font-size: 14px;
  }
}
</style>