<template>
  <div class="practice-page-container">
    <!-- 顶部导航栏 -->
    <PracticeNavbar />
    
    <!-- 主要内容区域 -->
    <div class="practice-main-content">
      <!-- 左侧边栏 -->
      <PracticeSiderbar />
      
      <!-- 右侧练习内容 -->
      <div class="practice-content-area">
        <div class="practice-content-wrapper">
          <h2>练习内容区域</h2>
          <p>这里将显示具体的练习题目和作答界面</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import PracticeNavbar from '@/components/practice/PracticeNavbar.vue';
import PracticeSiderbar from '@/components/practice/PracticeSiderbar.vue';
</script>

<style scoped>
.practice-page-container {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background-color: #fafafa;
}

.practice-main-content {
  display: flex;
  flex: 1;
  overflow: hidden;
}

.practice-content-area {
  flex: 1;
  padding: 24px;
  overflow-y: auto;
  background-color: #fafafa;
}

.practice-content-wrapper {
  background: white;
  padding: 32px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.practice-content-wrapper h2 {
  color: #d63384;
  margin-bottom: 16px;
  font-size: 20px;
  font-weight: 700;
}

.practice-content-wrapper p {
  color: #606266;
  margin: 0;
  font-size: 14px;
}

/* 响应式设计 */
@media(max-width: 768px) {
  .practice-main-content {
    flex-direction: column;
  }
  
  .practice-content-area {
    padding: 16px;
  }
  
  .practice-content-wrapper {
    padding: 20px;
  }
}
</style>