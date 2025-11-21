<template>
  <div class="profile-page">

    <!-- 顶部个人信息 -->
    <UserHeader
      :username="user.name"
      :certificateCount="user.certs"
      :avatarUrl="user.avatar"
      @edit="openEdit"
    />
    <div class="section-title">学习总结</div>

    <!-- 三个统计卡片 -->
    <div class="card stats-card-wrapper">
      <div class="stats-grid">
        <StatCard title="学习天数" :value="128" unit="天" />
        <StatCard title="完成作业" :value="56" unit="份" />
        <StatCard title="课程参与" :value="92" unit="%" />
      </div>
    </div>

    <div class="section-title">学习总结</div>
    <div class="card">
      <LearningTrend />
    </div>

    <!-- 第二行：练习进度 -->
    <div class="card chart-full">
      <PracticeProgress />
    </div>

  </div>
</template>



<script setup>
import { ref } from "vue";
import UserHeader from "./UserHeader.vue";
import avatarDefault from '@/assets/avatar-default.png';
import StatCard from "@/components/profiling/learingtime/StatCard.vue"; 
import LearningTrend from "@/components/profiling/learingtime/LearningTrend.vue"; 
import PracticeProgress from "@/components/profiling/learingtime/PracticeProgress.vue"; 

const user = ref({
  name: "Qingtian1111",
  certs: 0,
  avatar: avatarDefault,  
  progress: 2,
  finished: 0,
  hours: 0
});


const openEdit = () => console.log("打开编辑弹窗");
</script>

<style scoped>
.profile-page {
  max-width: 1200px;
  margin: auto;
  padding: 40px 20px;
}

.section-title {
  margin-top: 32px;      /* 顶部间距保持不变 */
  margin-bottom: -16px;   /* 改小底部间距，让它离下面内容更近 */
  font-size: 20px;
  font-weight: bold;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
  margin-top: 20px;
}

.card {
  background: #fff;
  padding: 20px;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.06);
}

.section-title {
  margin-top: 32px;      /* 顶部间距保持不变 */
  margin-bottom: 5px;   /* 改小底部间距，让它离下面内容更近 */
  font-size: 20px;
  font-weight: bold;
}

</style>
