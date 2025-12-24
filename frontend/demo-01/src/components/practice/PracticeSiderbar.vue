<template>
  <!-- 练习侧边栏组件：用于题型导航 -->
  <div class="practice-sidebar">
    <!-- 侧边栏头部 -->
    <div class="sidebar-header">
      <h3 class="sidebar-title">题型导航</h3>
    </div>

    <!-- 侧边栏内容区域 -->
    <div class="sidebar-content">
      <!-- 选中项背景框：用于高亮当前选中或鼠标悬停的题型 -->
      <div
        class="background-box"
        :style="{
          top: backgroundBoxTop + 'px',
          height: backgroundBoxHeight + 'px',
        }"
        ref="backgroundBox"
      ></div>

      <!-- 题型列表 -->
      <ul class="question-type-list" @mouseleave="resetBackgroundBox">
        <li
          v-for="type in questionTypes"
          :key="type.id"
          class="question-type-item"
          :class="{ active: activeTypeId === type.id }"
          @click="switchQuestionType(type.id)"
          @mouseenter="updateBackgroundBoxFromEvent($event)"
          ref="questionTypeItems"
        >
          <!-- 题型信息 -->
          <div class="type-info">
            <span class="type-name">{{ type.name }}</span>
            <span class="type-count">{{ type.answered }}/{{ type.total }}</span>
          </div>
          <!-- 箭头图标 -->
          <el-icon class="type-icon"><ArrowRight /></el-icon>
        </li>
      </ul>
    </div>
  </div>
</template>

<script setup>
// 导入必要的库和组件
import { ArrowRight } from "@element-plus/icons-vue";
import { nextTick, onMounted, ref, watch } from "vue";

// ==========================================================================\n// Props 定义：父组件传递的数据
// ==========================================================================
const props = defineProps({
  // 题型列表数据
  questionTypes: {
    type: Array,
    required: true,
    description: "包含所有题型的数组，每个题型包含id、name、answered和total属性",
  },
  // 当前激活的题型ID
  activeTypeId: {
    type: [String, Number],
    default: "all",
    description: "当前选中的题型ID",
  },
});

// ==========================================================================\n// Emits 定义：向父组件传递的事件
// ==========================================================================
const emit = defineEmits([
  "type-change", // 题型切换事件，传递选中的题型ID
]);

// ==========================================================================\n// 响应式状态管理
// ==========================================================================
// 背景框位置和高度
const backgroundBoxTop = ref(0); // 背景框距离顶部的距离
const backgroundBoxHeight = ref(0); // 背景框的高度

// DOM 引用
const backgroundBox = ref(null); // 背景框DOM引用
const questionTypeItems = ref([]); // 所有题型项DOM引用的数组

// ==========================================================================\n// 生命周期钩子
// ==========================================================================
// 组件挂载完成后初始化背景框位置
onMounted(() => {
  initializeBackgroundBox();
});

// ==========================================================================\n// 监听逻辑：响应数据变化
// ==========================================================================
// 监听激活题型ID变化，更新背景框位置
watch(
  () => props.activeTypeId,
  (newTypeId) => {
    // 使用nextTick确保DOM已更新
    nextTick(() => {
      updateBackgroundBoxToActiveItem();
    });
  }
);

// ==========================================================================\n// 核心功能函数
// ==========================================================================
/**
 * 初始化背景框位置
 */
function initializeBackgroundBox() {
  nextTick(() => {
    // 查找当前激活的题型项
    const activeItem = findActiveQuestionTypeItem();
    if (activeItem) {
      // 如果有激活项，将背景框设置到激活项位置
      updateBackgroundBoxPosition(activeItem);
    } else if (questionTypeItems.value.length > 0) {
      // 如果没有激活项但有题型项，默认将背景框设置到第一个题型项位置
      updateBackgroundBoxPosition(questionTypeItems.value[0]);
    }
  });
}

/**
 * 切换题型
 * @param {String|Number} typeId - 要切换到的题型ID
 */
function switchQuestionType(typeId) {
  // 向父组件发出题型切换事件
  emit("type-change", typeId);

  // 更新背景框位置到新选中的题型项
  nextTick(() => {
    updateBackgroundBoxToActiveItem();
  });
}

/**
 * 从鼠标事件中更新背景框位置
 * @param {Event} event - 鼠标事件对象
 */
function updateBackgroundBoxFromEvent(event) {
  updateBackgroundBoxPosition(event.currentTarget);
}

/**
 * 更新背景框位置到指定元素
 * @param {HTMLElement} element - 目标元素
 */
function updateBackgroundBoxPosition(element) {
  if (element) {
    const rect = element.getBoundingClientRect();
    const containerRect = element.parentElement.parentElement.getBoundingClientRect();

    // 计算背景框的位置和高度
    backgroundBoxTop.value = rect.top - containerRect.top;
    backgroundBoxHeight.value = rect.height;
  }
}

/**
 * 更新背景框位置到当前激活的题型项
 */
function updateBackgroundBoxToActiveItem() {
  const activeItem = findActiveQuestionTypeItem();
  if (activeItem) {
    updateBackgroundBoxPosition(activeItem);
  }
}

/**
 * 重置背景框位置到当前激活的题型项
 */
function resetBackgroundBox() {
  updateBackgroundBoxToActiveItem();
}

// ==========================================================================\n// 辅助函数
// ==========================================================================
/**
 * 查找当前激活的题型项DOM元素
 * @returns {HTMLElement|null} - 激活的题型项DOM元素，如果没有则返回null
 */
function findActiveQuestionTypeItem() {
  return questionTypeItems.value.find((item) => item.classList.contains("active"));
}
</script>

<style scoped>
/* 侧边栏整体样式 */
.practice-sidebar {
  width: 240px;
  height: 500px;
  background-color: var(--bg-white);
  border-right: 1px solid var(--border-primary-lighter);
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.05);
  overflow: hidden;
  margin: 20px 0;
}

/* 侧边栏头部样式 */
.sidebar-header {
  padding: 16px 20px;
  border-bottom: 1px solid var(--border-primary-lighter);
  background-color: var(--bg-primary-light);
  width: 100%;
  box-sizing: border-box;
}

.sidebar-title {
  font-size: 18px;
  font-weight: 700;
  margin: 0;
  background: var(--gradient-brand);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

/* 侧边栏内容区域样式 */
.sidebar-content {
  flex: 1;
  overflow-y: auto;
  padding: 8px 0;
  overflow-x: hidden;
  position: relative;
}

/* 选中项背景框样式 */
.background-box {
  position: absolute;
  left: 0;
  width: 100%;
  background-color: var(--primary-alpha-10);
  transition: top 0.25s ease, height 0.25s ease;
  z-index: 0;
  border-left: 3px solid var(--primary);
}

/* 题型列表样式 */
.question-type-list {
  list-style: none;
  padding: 0;
  margin: 0;
  position: relative;
  z-index: 1;
}

/* 题型项样式 */
.question-type-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 20px;
  cursor: pointer;
  transition: all 0.18s ease;
  border-left: 3px solid transparent;
  width: 100%;
  box-sizing: border-box;
  position: relative;
  z-index: 1;
}

.question-type-item:hover {
  background-color: transparent;
}

.question-type-item.active {
  background-color: transparent;
  font-weight: 600;
  /* 移除active状态的左侧边框，由background-box显示 */
}

/* 题型信息样式 */
.type-info,
.type-icon {
  position: relative;
  z-index: 1;
}

.type-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.type-name {
  font-size: 14px;
  color: var(--text-primary);
  transition: color 0.18s ease;
}

.question-type-item.active .type-name {
  color: var(--primary);
  background: var(--gradient-brand);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.type-count {
  font-size: 12px;
  color: #999;
  background-color: var(--bg-light);
  padding: 2px 8px;
  border-radius: 10px;
  align-self: flex-start;
}

/* 箭头图标样式 */
.type-icon {
  font-size: 16px;
  color: #ccc;
  transition: all 0.18s ease;
}

.question-type-item:hover .type-icon,
.question-type-item.active .type-icon {
  color: var(--primary);
  transform: translateX(2px);
}

/* 滚动条样式 */
.sidebar-content::-webkit-scrollbar {
  width: 6px;
}

.sidebar-content::-webkit-scrollbar-track {
  background: #f1f1f1;
}

.sidebar-content::-webkit-scrollbar-thumb {
  background: #ddd;
  border-radius: 3px;
}

.sidebar-content::-webkit-scrollbar-thumb:hover {
  background: #ccc;
}
</style>
