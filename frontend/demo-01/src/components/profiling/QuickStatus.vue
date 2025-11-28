<template>
  <div class="quick-status">
    <!-- 进行中 -->
    <div class="item" @click="goToProgress">
      <div class="title">进行中📘</div>
      <div class="count">{{ progress }}</div>
      <div class="sub">课程</div>
    </div>

    <!-- 完成 -->
    <div class="item" @click="goToFinished">
      <div class="title">完成✅</div>
      <div class="count">{{ finished }}</div>
      <div class="sub">课程</div>
    </div>

    <!-- 学习时间 -->
    <div class="item" @click="goToHours">
      <div class="title">学习时间⏱️</div>
      <div class="count">{{ hours }}</div>
      <div class="sub">小时</div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

defineProps({
  progress: Number,
  finished: Number,
  hours: Number
})

// 点击跳转函数
// function goToProgress() {
//   router.push('/progress')
// }
// function goToFinished() {
//   router.push('/finished')
// }
function goToHours() {
  router.push('/LearningTime')
}
</script>

<style scoped>
.quick-status {
  display: flex;               /* 水平排列三个块 */
  gap: 16px;                   /* 块间距 */
  margin-top: 24px;
}

/* 每个独立块 */
.item {
  flex: 1;                     /* 平均分宽度 */
  background: #fff;             /* 白色背景 */
  border-radius: 16px;          /* 圆角 */
  padding: 20px;                /* 内边距 */
  text-align: center;           /* 内容居中 */
  cursor: pointer;              /* 鼠标手型 */
  box-shadow: 0 4px 12px rgba(0,0,0,0.08); /* 初始阴影 */
  transition: all 0.2s ease;    /* 悬停动画 */
  display: flex;
  flex-direction: column;
  align-items: center;
}

/* 悬停效果：放大 + 阴影加深 */
.item:hover {
  transform: scale(1.05);
  box-shadow: 0 8px 20px rgba(0,0,0,0.15);
}

/* 标题 */
.title {
  font-size: 14px;
  color: #777;
  margin-bottom: 4px;
}

/* 数字 */
.count {
  font-size: 28px;
  font-weight: bold;
  margin-bottom: 2px;
}

/* 子标题 */
.sub {
  font-size: 12px;
  color: #aaa;
}

/* 响应式处理 */
@media (max-width: 640px) {
  .quick-status {
    flex-direction: column;  /* 小屏幕垂直排列 */
    gap: 12px;
  }
  .item {
    padding: 16px;
  }
  .count { font-size: 24px; }
}
</style>
