<template>
  <div class="achievements">
    <div v-for="a in achievements" :key="a.title" class="achievement-card">
      <img :src="a.icon" class="icon" />
      <h3>{{ a.title }}</h3>
      <p>{{ a.desc }}</p>
    </div>
  </div>
</template>

<script setup>
const achievements = [
  { title: "学习达人", desc: "累计学习超过 100 小时", icon: "https://img.icons8.com/color/96/000000/trophy.png" },
  { title: "作业高手", desc: "完成作业 50 份", icon: "https://img.icons8.com/color/96/000000/task-completed.png" },
  { title: "算法新星", desc: "算法练习排名前 10%", icon: "https://img.icons8.com/color/96/000000/idea.png" },
];
</script>

<style scoped>
.achievements {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-around;
  gap: 20px;
}

.achievement-card {
  background: #f9fafb;
  border-radius: 12px;
  padding: 20px;
  width: 220px;
  text-align: center;
  box-shadow: 0 3px 10px rgba(0, 0, 0, 0.1);
  transition: all 0.2s ease;
}

.achievement-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.12);
}

.icon {
  width: 64px;
  height: 64px;
  margin-bottom: 10px;
}
</style>
