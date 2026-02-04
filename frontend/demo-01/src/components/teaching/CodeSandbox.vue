<template>
  <div class="code-sandbox">
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

    <!-- 程序输入 -->
    <div class="input-container">
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
import { ElCard, ElSelect, ElOption } from 'element-plus';

// 注册语言
hljs.registerLanguage('cpp', cpp);
hljs.registerLanguage('python', python);
hljs.registerLanguage('java', java);

// Props 定义
const props = defineProps({
  initialLanguage: {
    type: String,
    default: 'cpp'
  },
  initialCode: {
    type: String,
    default: ''
  },
  initialInput: {
    type: String,
    default: ''
  }
});

// Emits 定义
const emit = defineEmits(['codeChange']);

// 响应式数据
const selectedLanguage = ref(props.initialLanguage);
const codeContent = ref(props.initialCode || getDefaultCode(props.initialLanguage));
const programInput = ref(props.initialInput || '');
const codeBlock = ref(null);
const codeEditor = ref(null);
const codeInput = ref(null);

// 语言映射表
const languageMap = {
  cpp: 'C++',
  python: 'Python',
  java: 'Java'
};

// 获取默认代码
function getDefaultCode(language) {
  // 返回空字符串作为默认代码
  return '';
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
  // 触发代码变化事件
  emit('codeChange', {
    language: selectedLanguage.value,
    code: codeContent.value,
    input: programInput.value
  });
}

// 同步滚动
function syncScroll(source, target) {
  if (source && target) {
    target.scrollTop = source.scrollTop;
    target.scrollLeft = source.scrollLeft;
  }
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

// 监听程序输入变化
watch(programInput, () => {
  emit('codeChange', {
    language: selectedLanguage.value,
    code: codeContent.value,
    input: programInput.value
  });
});

// 监听props变化，用于外部更新
watch(() => props.initialLanguage, (newLang) => {
  if (newLang !== selectedLanguage.value) {
    selectedLanguage.value = newLang;
    codeContent.value = getDefaultCode(newLang);
    nextTick(() => highlightCode());
  }
});

watch(() => props.initialCode, (newCode) => {
  if (newCode && newCode !== codeContent.value) {
    codeContent.value = newCode;
    nextTick(() => highlightCode());
  }
});

watch(() => props.initialInput, (newInput) => {
  if (newInput && newInput !== programInput.value) {
    programInput.value = newInput;
  }
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
});

// 暴露reset方法给父组件使用
defineExpose({
  // 重置代码编辑器内容
  reset(language = 'cpp', code = '', input = '') {
    selectedLanguage.value = language;
    codeContent.value = code || getDefaultCode(language);
    programInput.value = input || '';
    nextTick(() => {
      highlightCode();
    });
  }
});
</script>

<style scoped>
/* 代码沙箱容器 */
.code-sandbox {
  padding: 20px;
}

/* 代码编辑卡片 */
.code-editor-card {
  margin-bottom: 20px;
}

/* 卡片头部 */
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-title {
  font-size: 16px;
  font-weight: 500;
}

.language-select {
  width: 120px;
}

/* 代码编辑器包装器 */
.code-editor-wrapper {
  position: relative;
  height: 300px;
  border: 1px solid #e8e8e8;
  border-radius: 4px;
  font-size: 0.875rem; /* 使用相对单位 */
}

/* 代码显示区域 */
.code-editor {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  margin: 0;
  padding: 12px;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: inherit; /* 继承父容器字体大小 */
  line-height: 1.5;
  font-weight: normal;
  letter-spacing: 0;
  word-spacing: 0;
  overflow: auto;
  background-color: #fff;
  border: none;
  pointer-events: none;
  white-space: pre;
  word-wrap: normal;
  box-sizing: border-box;
  tab-size: 4;
}

/* 确保code元素也使用相同的字体设置 */
.code-editor code {
  font-family: inherit;
  font-size: inherit; /* 继承父容器字体大小 */
  line-height: inherit;
  font-weight: inherit;
  letter-spacing: inherit;
  word-spacing: inherit;
  display: block;
  margin: 0;
  padding: 0;
  tab-size: 4;
}

/* 代码输入区域 */
.code-input {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  width: 100%;
  height: 100%;
  margin: 0;
  padding: 12px;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: inherit; /* 继承父容器字体大小 */
  line-height: 1.5;
  font-weight: normal;
  letter-spacing: 0;
  word-spacing: 0;
  color: transparent;
  background-color: transparent;
  caret-color: #333;
  border: none;
  outline: none;
  resize: none;
  white-space: pre;
  word-wrap: normal;
  overflow: auto;
  z-index: 10;
  box-sizing: border-box;
  tab-size: 4;
  text-indent: 0;
}

/* 输入容器 */
.input-container {
  margin-bottom: 20px;
}

/* 输入卡片 */
.input-card {
  width: 100%;
}

/* 文本输入框 */
.text-input {
  width: 100%;
  height: 100px;
  padding: 12px;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: inherit; /* 继承父容器字体大小 */
  line-height: 1.5;
  border: 1px solid #e8e8e8;
  border-radius: 4px;
  resize: none;
  outline: none;
}

.text-input:focus {
  border-color: #409eff;
}

/* 响应式设计 - 针对侧边栏场景优化 */
@media (max-width: 1200px) {
  .code-sandbox {
    padding: 15px;
  }
  
  .code-editor-wrapper {
    height: 250px;
    font-size: 0.8125rem; /* 进一步缩小字体 */
  }
  
  .header-title {
    font-size: 14px;
  }
  
  .language-select {
    width: 100px;
  }
}

@media (max-width: 768px) {
  .code-editor-wrapper {
    height: 200px;
    font-size: 0.75rem; /* 最小字体大小 */
  }
  
  .text-input {
    height: 80px;
  }
}

/* 针对侧边栏集成的特殊样式调整 */
/* 确保highlight.js生成的代码完全遵循指定字体大小，不使用额外的样式 */
:deep(.hljs) {
  font-size: 100% !important; /* 强制使用100%的父容器字体大小 */
  line-height: inherit !important;
  font-family: inherit !important;
  font-weight: inherit !important;
  letter-spacing: inherit !important;
  word-spacing: inherit !important;
  padding: 0 !important; /* 移除highlight.js默认的内边距 */
  margin: 0 !important; /* 移除highlight.js默认的外边距 */
  background: transparent !important; /* 使用透明背景 */
}

/* 防止字体大小溢出容器 */
.code-editor-wrapper {
  overflow: hidden;
}

.code-editor, .code-input {
  min-width: 100%;
  box-sizing: border-box;
}

/* 确保所有高亮元素都遵循相同的字体设置 */
:deep(.hljs *),
:deep(.hljs span),
:deep(.hljs strong),
:deep(.hljs em),
:deep(.hljs code),
:deep(.hljs pre) {
  font-size: 100% !important;
  line-height: inherit !important;
  font-family: inherit !important;
  font-weight: inherit !important;
}
</style>