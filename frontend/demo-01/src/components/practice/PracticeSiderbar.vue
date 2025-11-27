<template>
  <div class="practice-sidebar">
    <div class="sidebar-header">
      <h3 class="sidebar-title">题型导航</h3>
    </div>
    <div class="sidebar-content">
      <ul class="question-type-list">
        <li 
          v-for="type in questionTypes" 
          :key="type.id"
          class="question-type-item"
          :class="{ active: activeTypeId === type.id }"
          @click="switchQuestionType(type.id)"
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
import { ref } from 'vue';
import { ArrowRight } from '@element-plus/icons-vue';

// 定义题型数据（实际项目中可从API获取）
const questionTypes = ref([
  { id: 'single_choice', name: '单选题', answered: 2, total: 5 },
  { id: 'multiple_choice', name: '多选题', answered: 1, total: 3 },
  { id: 'judgment', name: '判断题', answered: 0, total: 4 },
  { id: 'fill_blank', name: '填空题', answered: 3, total: 5 },
  { id: 'short_answer', name: '简答题', answered: 0, total: 2 },
  { id: 'programming', name: '编程题', answered: 1, total: 3 }
]);

// 当前激活的题型ID
const activeTypeId = ref('single_choice');

// 切换题型的方法
const switchQuestionType = (typeId) => {
  activeTypeId.value = typeId;
  // 此处可以添加切换题型的逻辑，如触发事件或调用API获取对应题型的题目
  console.log('切换到题型：', typeId);
};
</script>

<style scoped>
.practice-sidebar {
  width: 260px;
  height: 100%;
  background-color: #ffffff;
  border-right: 1px solid #f5dbe7;
  display: flex;
  flex-direction: column;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.05);
}

.sidebar-header {
  padding: 20px 24px;
  border-bottom: 1px solid #f5dbe7;
  background-color: #fff5f8;
}

.sidebar-title {
  font-size: 18px;
  font-weight: 700;
  color: #d63384;
  margin: 0;
}

.sidebar-content {
  flex: 1;
  overflow-y: auto;
  padding: 16px 0;
}

.question-type-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.question-type-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  cursor: pointer;
  transition: all 0.18s ease;
  border-left: 3px solid transparent;
}

.question-type-item:hover {
  background-color: rgba(214, 51, 132, 0.08);
  transform: translateX(2px);
}

.question-type-item.active {
  background-color: rgba(214, 51, 132, 0.12);
  border-left-color: #d63384;
  font-weight: 600;
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
  color: #d63384;
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
  color: #d63384;
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