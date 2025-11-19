<template>
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
.profile-page {
  max-width: 1200px;
  margin: auto;
  padding: 40px 20px;
   margin-top: 20px;
}

.section-title {
  margin-top: 32px;      /* 顶部间距保持不变 */
  margin-bottom: -16px;   /* 改小底部间距，让它离下面内容更近 */
  font-size: 20px;
  font-weight: bold;
}

</style>
