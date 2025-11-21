<template>
  <div class="notifications">
    <h3>📢 消息与提醒</h3>
    <ul>
      <li v-for="n in notifications" :key="n.id" @click="goNotification(n.id)">
        <p class="title">{{ n.title }}</p>
        <p class="time">{{ n.time }}</p>
      </li>
    </ul>
  </div>
</template>

<script setup>
import { useRouter } from "vue-router";

const router = useRouter();

const notifications = [
  { id: 1, title: "您有一门课程作业即将到期", time: "2025-10-25 20:00" },
  { id: 2, title: "算法练习成绩已更新", time: "2025-10-23 18:00" },
  { id: 3, title: "恭喜您达成‘学习达人’成就！", time: "2025-10-22 10:00" },
];

const goNotification = (id) => {
  router.push(`/notification/${id}`);
};
</script>

<style scoped>
.notifications {
  background: white;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.08);
  margin-top: 20px;
}

ul {
  list-style: none;
  margin: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 12px; /* 各通知块间距 */
}

li {
  background: #fff;
  border-radius: 12px;      /* 圆角 */
  padding: 12px 16px;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  box-shadow: 0 2px 6px rgba(0,0,0,0.08);
}

li:hover {
  transform: scale(1.02);
  box-shadow: 0 8px 20px rgba(0,0,0,0.15);
}

.title {
  font-weight: 600;
  color: #333;
}

.time {
  color: #888;
  font-size: 12px;
  margin-top: 4px;
}
</style>
