<template>
  <div class="experiment-container">
    <!-- 代码输入块 -->
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
</template>

<script setup>
import { ref, onMounted, watch, nextTick } from 'vue';
import hljs from 'highlight.js/lib/core';
import cpp from 'highlight.js/lib/languages/cpp';
import python from 'highlight.js/lib/languages/python';
import java from 'highlight.js/lib/languages/java';
import 'highlight.js/styles/atom-one-light.css';
import { ElCard, ElSelect, ElOption, ElButton, ElIcon } from 'element-plus';
import { Flag } from '@element-plus/icons-vue';
import { executeCode } from '@/api';

// 注册语言
hljs.registerLanguage('cpp', cpp);
hljs.registerLanguage('python', python);
hljs.registerLanguage('java', java);

// 响应式数据
const selectedLanguage = ref('cpp');
const codeContent = ref(getDefaultCode('cpp'));
const programInput = ref('');
const runOutput = ref('');
const codeBlock = ref(null);
const codeEditor = ref(null);
const codeInput = ref(null);

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

// 组件挂载时初始化
onMounted(() => {
  highlightCode();
  
  // 添加滚动同步事件监听
  if (codeInput.value && codeEditor.value) {
    codeInput.value.addEventListener('scroll', () => {
      syncScroll(codeInput.value, codeEditor.value);
    });
  }
});
</script>

<style scoped>
.experiment-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
  overflow-y: auto;
}

/* 卡片样式 */
.code-editor-card {
  width: 99%;
  height: 60vh;
  margin: auto;
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

/* 响应式设计 */
@media (max-width: 768px) {
  .experiment-container {
    padding: 10px;
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
</style>