<template>
  <!-- 外层 wrapper 用于背景色 -->
  <div class="page-wrapper">
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

      <div class="section-title">学习趋势</div>
      <div class="card">
        <LearningTrend />
      </div>

      <!-- 练习进度 -->
      <div class="card chart-full">
        <PracticeProgress />
      </div>

      <!-- 学习能力雷达图 -->
      <RadarStats
        :data="{
          codeAccuracy: 80,
          aiDependence: 60,
          knowledgeConversion: 75,
          expressionAbility: 70,
          knowledgeDepth: 85,
          codeQuality: 90,
        }"
        class="card chart-full"
      />

      <!-- 新增四个图表 -->
      <div class="section-title">学习维度统计</div>

      <!-- 第一行：学习类型 + 难度正确率 -->
      <div class="two-col">
        <div class="card small-card">
          <StudyTypeDistribution :data="studyTypeMock" />
        </div>

        <div class="card small-card">
          <DifficultyAccuracy :data="difficultyMock" />
        </div>
      </div>

      <!-- 第二行：效率趋势 -->
      <div class="card">
        <EfficiencyTrend
          :days="['周一', '周二', '周三', '周四', '周五', '周六', '周日']"
          :timePerTask="[15, 12, 14, 10, 9, 13, 16]"
        />
      </div>

      <!-- 第三行：打卡 -->
      <div class="card">
        <StudyPunchCard :calendarData="calendarMock" />
      </div>
    </div>
  </div>
</template>

<script setup>
import avatarDefault from "@/assets/avatar-default.png";
import { ref } from "vue";
import UserHeader from "./UserHeader.vue";

import LearningTrend from "@/components/profiling/learingtime/LearningTrend.vue";
import PracticeProgress from "@/components/profiling/learingtime/PracticeProgress.vue";
import StatCard from "@/components/profiling/learingtime/StatCard.vue";

import DifficultyAccuracy from "@/components/profiling/learingtime/DifficultyAccuracy.vue";
import EfficiencyTrend from "@/components/profiling/learingtime/EfficiencyTrend.vue";
import RadarStats from "@/components/profiling/learingtime/RadarStats.vue";
import StudyPunchCard from "@/components/profiling/learingtime/StudyPunchCard.vue";
import StudyTypeDistribution from "@/components/profiling/learingtime/StudyTypeDistribution.vue";

const user = ref({
  name: "Qingtian1111",
  certs: 0,
  avatar: avatarDefault,
  progress: 2,
  finished: 0,
  hours: 0,
});

const openEdit = () => console.log("打开编辑弹窗");

/* 模拟数据 */
const studyTypeMock = ref({
  video: 120,
  practice: 80,
  homework: 40,
  quiz: 25,
});

const difficultyMock = ref({
  easy: 90,
  medium: 72,
  hard: 48,
});

// 打卡模拟数据 (随机生成 30 天)
const calendarMock = ref(
  Array.from({ length: 30 }, (_, i) => {
    return [`2025-01-${String(i + 1).padStart(2, "0")}`, Math.random() > 0.3 ? 1 : 0];
  })
);
</script>

<style scoped>
/* 页面整体背景 */
.page-wrapper {
  background-color: #fff6fb;
  min-height: 100vh;
  padding: 40px 0;
  display: flex;
  justify-content: center; /* 水平居中 profile-page */
}

/* 内容区域 */
.profile-page {
  max-width: 1200px;
  width: 100%;
  padding: 40px 20px;
}

/* 标题 */
.section-title {
  margin-top: 32px;
  margin-bottom: 10px;
  font-size: 20px;
  font-weight: bold;
}

/* 统计卡片 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
  margin-top: 20px;
}

/* 卡片通用样式 */
.card {
  background: #fffefeb9;
  padding: 20px;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
  margin-bottom: 24px; /* 上下间隔 */
}

/* 两列布局 */
.two-col {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-bottom: 0;
}

/* 并列组件高度略矮 */
.small-card {
  height: 420px;
}

.chart-full {
  width: 100%;
  height: 480px; /* 根据雷达图高度调整 */
  margin-bottom: 24px;
}
</style>
