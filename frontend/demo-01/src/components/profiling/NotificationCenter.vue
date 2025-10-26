<template>
  <div class="notifications">
    <h3>📢 消息与提醒</h3>
    <ul>
      <li v-for="n in notifications" :key="n.id">
        <p class="title">{{ n.title }}</p>
        <p class="time">{{ n.time }}</p>
      </li>
    </ul>
  </div>
</template>

<script setup>
const notifications = [
  { id: 1, title: "您有一门课程作业即将到期", time: "2025-10-25 20:00" },
  { id: 2, title: "算法练习成绩已更新", time: "2025-10-23 18:00" },
  { id: 3, title: "恭喜您达成‘学习达人’成就！", time: "2025-10-22 10:00" },
];
</script>

<style scoped>
.notifications {
  background: white;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.08);
}
ul { list-style: none; margin: 0; padding: 0; }
li {
  border-bottom: 1px solid #eee;
  padding: 10px 0;
}
.title { font-weight: 600; color: #333; }
.time { color: #888; font-size: 12px; }
</style>
