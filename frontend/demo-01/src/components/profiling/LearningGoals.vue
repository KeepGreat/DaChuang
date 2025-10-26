<template>
  <div class="goals">
    <h3>🎯 学习目标</h3>
    <ul>
      <li v-for="goal in goals" :key="goal.title">
        <div>
          <strong>{{ goal.title }}</strong>
          <p>{{ goal.desc }}</p>
        </div>
        <progress :value="goal.progress" max="100"></progress>
      </li>
    </ul>
  </div>
</template>

<script setup>
const goals = [
  { title: "完成课程学习", desc: "目标 5 门课程", progress: 80 },
  { title: "掌握新算法", desc: "目标 3 个算法专题", progress: 60 },
  { title: "提升AI能力", desc: "完成AI实验项目", progress: 45 },
]
</script>

<style scoped>
.goals {
  background: white;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.08);
}
ul { list-style: none; padding: 0; margin: 0; }
li {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}
progress {
  width: 150px;
  height: 10px;
  border-radius: 6px;
}
</style>
