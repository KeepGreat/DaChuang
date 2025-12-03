<template>
  <div class="practice-sidebar">
    <div class="sidebar-header">
      <h3 class="sidebar-title">题型导航</h3>
    </div>
    <div class="sidebar-content">
      <div class="background-box" :style="{ top: backgroundBoxTop + 'px', height: backgroundBoxHeight + 'px' }" ref="backgroundBox"></div>
      <ul class="question-type-list" @mouseleave="resetBackgroundBox">
        <li 
          v-for="type in questionTypes" 
          :key="type.id"
          class="question-type-item"
          :class="{ active: activeTypeId === type.id }"
          @click="switchQuestionType(type.id)"
          @mouseenter="updateBackgroundBox($event)"
          ref="questionTypeItems"
        >
          <div class="type-info">
            <span class="type-name">{{ type.name }}</span>
            <span class="type-count">{{ type.answered }}/{{ type.total }}</span>
          </div>
          <el-icon class="type-icon"><ArrowRight /></el-icon>
        </li>
      </ul>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, watch } from 'vue';
import { ArrowRight } from '@element-plus/icons-vue';

// 接收父组件传递的props
const props = defineProps({
  questionTypes: {
    type: Array,
    required: true
  },
  activeTypeId: {
    type: [String, Number],
    default: 'all'
  }
});

// 定义事件
const emit = defineEmits(['type-change']);

// 背景框位置和高度
const backgroundBoxTop = ref(0);
const backgroundBoxHeight = ref(0);

// 题型项引用
const questionTypeItems = ref([]);

// 切换题型的方法
const switchQuestionType = (typeId) => {
  // 触发事件通知父组件
  emit('type-change', typeId);
  // 更新背景框位置
  nextTick(() => {
    const activeItem = questionTypeItems.value.find(item => 
      item.classList.contains('active')
    );
    if (activeItem) {
      updateBackgroundBoxFromElement(activeItem);
    }
  });
};

// 监听activeTypeId变化，更新背景框
watch(() => props.activeTypeId, (newTypeId) => {
  nextTick(() => {
    const activeItem = questionTypeItems.value.find(item => 
      item.classList.contains('active')
    );
    if (activeItem) {
      updateBackgroundBoxFromElement(activeItem);
    }
  });
});

// 从元素更新背景框位置
const updateBackgroundBoxFromElement = (element) => {
  if (element) {
    const rect = element.getBoundingClientRect();
    const containerRect = element.parentElement.parentElement.getBoundingClientRect();
    backgroundBoxTop.value = rect.top - containerRect.top;
    backgroundBoxHeight.value = rect.height;
  }
};

// 更新背景框位置
const updateBackgroundBox = (event) => {
  updateBackgroundBoxFromElement(event.currentTarget);
};

// 重置背景框到active项
const resetBackgroundBox = () => {
  const activeItem = questionTypeItems.value.find(item => 
    item.classList.contains('active')
  );
  if (activeItem) {
    updateBackgroundBoxFromElement(activeItem);
  }
};

// 初始化背景框位置
onMounted(() => {
  nextTick(() => {
    const activeItem = questionTypeItems.value.find(item => 
      item.classList.contains('active')
    );
    if (activeItem) {
      updateBackgroundBoxFromElement(activeItem);
    } else if (questionTypeItems.value.length > 0) {
      // 默认选中第一个
      updateBackgroundBoxFromElement(questionTypeItems.value[0]);
    }
  });
});
</script>

<style scoped>
.practice-sidebar {
  width: 240px;
  height: calc(100vh - 56px);
  background-color: #ffffff;
  border-right: 1px solid #dbeafe;
  display: flex;
  flex-direction: column;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.05);
  overflow: hidden;
}

.sidebar-header {
  padding: 16px 20px;
  border-bottom: 1px solid #dbeafe;
  background-color: #f0f9ff;
  width: 100%;
  box-sizing: border-box;
}

.sidebar-title {
  font-size: 18px;
  font-weight: 700;
  margin: 0;
  background: linear-gradient(45deg, #2563eb, #1d4ed8);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.sidebar-content {
  flex: 1;
  overflow-y: auto;
  padding: 8px 0;
  overflow-x: hidden;
  position: relative;
}

/* 背景框样式 */
.background-box {
  position: absolute;
  left: 0;
  width: 100%;
  background-color: rgba(37, 99, 235, 0.12);
  transition: top 0.25s ease, height 0.25s ease;
  z-index: 0;
  border-left: 3px solid #2563eb;
}

.question-type-list {
  list-style: none;
  padding: 0;
  margin: 0;
  position: relative;
  z-index: 1;
}

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
  color: #333;
  transition: color 0.18s ease;
}

.question-type-item.active .type-name {
  color: #2563eb;
  background: linear-gradient(45deg, #2563eb, #1d4ed8);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.type-count {
  font-size: 12px;
  color: #999;
  background-color: #f8f9fa;
  padding: 2px 8px;
  border-radius: 10px;
  align-self: flex-start;
}

.type-icon {
  font-size: 16px;
  color: #ccc;
  transition: all 0.18s ease;
}

.question-type-item:hover .type-icon,
.question-type-item.active .type-icon {
  color: #2563eb;
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