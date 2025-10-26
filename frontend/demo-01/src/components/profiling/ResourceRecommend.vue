<template>
  <div class="card">
    <h3 class="card-title">📚 学习资料推荐</h3>
    <ul class="mt-3 space-y-2">
      <li v-for="(res, i) in resources" :key="i" class="flex items-center justify-between">
        <span>{{ res.title }}</span>
        <a :href="res.link" target="_blank" class="text-blue-600 hover:underline text-sm">查看</a>
      </li>
    </ul>
  </div>
</template>

<script setup>
const resources = [
  { title: "机器学习入门电子书", link: "#" },
  { title: "Vue3 项目实战视频", link: "#" },
  { title: "数据库索引优化指南", link: "#" }
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
