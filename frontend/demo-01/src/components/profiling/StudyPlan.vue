<template>
  <div class="card">
    <h3 class="card-title">🎯 学习计划</h3>
    <div class="space-y-3 mt-3">
      <div v-for="plan in plans" :key="plan.id">
        <div class="flex justify-between mb-1">
          <span>{{ plan.title }}</span>
          <span class="text-sm text-gray-500">{{ plan.progress }}%</span>
        </div>
        <div class="w-full bg-gray-200 rounded-full h-2">
          <div class="bg-blue-500 h-2 rounded-full" :style="{ width: plan.progress + '%' }"></div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
const plans = [
  { id: 1, title: "完成课程笔记", progress: 60 },
  { id: 2, title: "复习人工智能章节", progress: 30 },
  { id: 3, title: "准备期末论文", progress: 80 }
];
</script>

<style scoped>
.card {
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.06);
  padding: 20px;
}
.card-title {
  font-size: 1.1rem;
  font-weight: 600;
}
</style>
