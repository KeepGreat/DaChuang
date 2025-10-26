<template>
  <div class="card">
    <h3 class="card-title">🧑‍🏫 教师反馈区</h3>
    <div class="mt-3 space-y-2">
      <div v-for="(fb, i) in feedback" :key="i" class="p-3 bg-gray-50 rounded-lg">
        <p class="font-semibold">{{ fb.teacher }}</p>
        <p class="text-gray-600 text-sm">{{ fb.comment }}</p>
      </div>
    </div>
  </div>
</template>

<script setup>
const feedback = [
  { teacher: "李老师", comment: "你的代码结构清晰，建议加强注释与文档编写。" },
  { teacher: "王教授", comment: "实验报告分析很到位，下次可以补充算法复杂度对比。" }
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
