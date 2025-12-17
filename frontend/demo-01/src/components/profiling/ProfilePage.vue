<template>
  <!-- 外层 wrapper 用于背景色和填充 -->
  <div class="page-wrapper">
    <div class="profile-page">
      
      <!-- 顶部个人信息 -->
      <UserHeader
        :username="user.name"
        :certificateCount="user.certs"
        :avatarUrl="user.avatar"
        @edit="openEdit"
      />

      <div class="section-title">Snapshot</div>
      <!-- 快照状态 -->
      <QuickStatus
        :progress="user.progress"
        :finished="user.finished"
        :hours="user.hours"
      />

      <!-- 进行中课程 -->
      <div class="section-title">进行中</div>
      <CourseInProgress :courses="inProgress" />

      <!-- 短课程 -->
      <div class="section-title">短期课程</div>
      <CourseShort :shortCourses="shortCourses" />

      <!-- 消息 -->
      <div class="section-title">消息与提醒</div>
      <NotificationCenter :shortCourses="shortCourses" />

      <div class="section-title">证书与成就</div>
      <!-- 成就 -->
      <Achievements :items="achievements" />

    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";

import UserHeader from "./UserHeader.vue";
import QuickStatus from "./QuickStatus.vue";
import CourseInProgress from "./CourseInProgress.vue";
import CourseShort from "./CourseShort.vue";
import Achievements from "./Achievements.vue";
import NotificationCenter from "./NotificationCenter.vue";
import avatarDefault from '@/assets/avatar-default.png';

const user = ref({
  name: "Qingtian1111",
  certs: 0,
  avatar: avatarDefault,  
  progress: 2,
  finished: 0,
  hours: 0
});

import img1 from "@/assets/image1.png";
import img2 from "@/assets/image2.png";
const inProgress = ref([
  { id: 1, title: "Learn Python with AI", teacher: "Andrew Ng", cover: img1 },
  { id: 2, title: "Chat with Your Database", teacher: "张三", cover: img2 }
]);

const shortCourses = ref([
  { id: 1, title: "短课程 A", cover: "/s1.png" },
  { id: 2, title: "短课程 B", cover: "/s2.png" }
]);

const achievements = ref([
  { id: 1, name: "AI Starter", icon: "/a1.png" },
  { id: 2, name: "Deep Learner", icon: "/a2.png" }
]);

const openEdit = () => console.log("打开编辑弹窗");
</script>

<style scoped>
/* 页面整体背景 */
.page-wrapper {
  background-color: #fff6fb;
  min-height: 100vh; /* 高度撑满视口 */
  padding: 40px 0;
  display: flex;
  justify-content: center; /* 水平居中 profile-page */
}

/* 内容区域 */
.profile-page {
  max-width: 1200px;
  width: 100%;
  background-color: #fffefe85; /* 内容区白色 */
  border-radius: 16px;
  padding: 40px 20px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.06);
}

/* 标题间距 */
.section-title {
  margin-top: 32px;
  margin-bottom: 16px; /* 上下间距更自然 */
  font-size: 20px;
  font-weight: bold;
}
</style>
