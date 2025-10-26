<template>
  <div class="card">
    <h3 class="card-title">📅 课程提醒中心</h3>
    <ul class="mt-3 space-y-2">
      <li v-for="(course, i) in courses" :key="i" class="flex justify-between">
        <span>{{ course.name }}</span>
        <span class="text-sm text-gray-500">{{ course.time }}</span>
      </li>
    </ul>
  </div>
</template>

<script setup>
const courses = [
  { name: "高等数学（上）", time: "今天 14:00 - 16:00" },
  { name: "人工智能导论", time: "明天 10:00 - 11:30" },
  { name: "数据库系统", time: "周三 09:00 - 10:30" }
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
