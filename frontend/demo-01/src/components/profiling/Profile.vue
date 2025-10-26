<template>
  <div class="profile-container">
    <!-- 用户基本信息 -->
    <div class="card">
      <ProfileCard @edit-profile="openEditor" />
    </div>

    <!-- 资料编辑弹窗 -->
    <ProfileEditor ref="editorRef" />

    <!-- 学习统计 -->
    <div class="card stats-card-wrapper">
      <div class="stats-grid">
        <StatCard title="学习天数" :value="128" unit="天" />
        <StatCard title="完成作业" :value="56" unit="份" />
        <StatCard title="课程参与" :value="92" unit="%" />
      </div>
    </div>

    <!-- 学习趋势 -->
    <div class="card">
      <LearningTrend />
    </div>

    <!-- 第一行：成绩 + 能力 -->
    <div class="charts-row">
      <div class="card chart-large">
        <ScoreOverview />
      </div>
      <div class="card chart-small">
        <OtherStats />
      </div>
    </div>

    <!-- 第二行：练习进度 -->
    <div class="card chart-full">
      <PracticeProgress />
    </div>

    <!-- 技能进度 & 活跃度 -->
    <div class="charts-row">
      <div class="card chart-large">
        <SkillProgress />
      </div>
      <div class="card chart-small">
        <WeeklyActivity />
      </div>
    </div>

    <!-- 成就展示 -->
    <div class="card">
      <AchievementList />
    </div>

    <!-- 学习目标与效率 -->
    <div class="charts-row">
      <div class="card chart-large">
        <LearningGoals />
      </div>
      <div class="card chart-small">
        <StudyEfficiency />
      </div>
    </div>

    <!-- 消息中心 -->
    <div class="card">
      <NotificationCenter />
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";

// ✅ 导入所有子组件
import ProfileCard from "./ProfileCard.vue";
import ProfileEditor from "./ProfileEditor.vue";
import StatCard from "./StatCard.vue";
import ScoreOverview from "./ScoreOverview.vue";
import PracticeProgress from "./PracticeProgress.vue";
import OtherStats from "./OtherStats.vue";
import LearningTrend from "./LearningTrend.vue";
import SkillProgress from "./SkillProgress.vue";
import WeeklyActivity from "./WeeklyActivity.vue";
import AchievementList from "./AchievementList.vue";
import LearningGoals from "./LearningGoals.vue";
import StudyEfficiency from "./StudyEfficiency.vue";
import NotificationCenter from "./NotificationCenter.vue";

// ✅ 编辑资料弹窗控制
const editorRef = ref(null);
const openEditor = () => editorRef.value?.open();
</script>

<style scoped>
.profile-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  background: linear-gradient(180deg, #f8fafc 0%, #eef2f7 100%);
  min-height: 100vh;
  padding: 40px;
}

/* 通用卡片样式 */
.card {
  background: white;
  border-radius: 16px;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.08);
  padding: 24px;
  width: 90%;
  margin-top: 24px;
  transition: all 0.25s ease;
}
.card:hover {
  box-shadow: 0 8px 28px rgba(0, 0, 0, 0.12);
  transform: translateY(-3px);
}

/* 统计卡片区域 */
.stats-grid {
  display: flex;
  justify-content: space-between;
  gap: 20px;
}
.stats-grid :deep(.stat-card) {
  flex: 1;
}

/* 图表布局 */
.charts-row {
  display: flex;
  gap: 24px;
  width: 90%;
  margin-top: 24px;
}
.chart-large {
  flex: 2;
}
.chart-small {
  flex: 1;
}
.chart-full {
  width: 90%;
}

/* 响应式适配 */
@media (max-width: 1024px) {
  .charts-row {
    flex-direction: column;
  }
  .chart-large,
  .chart-small {
    flex: none;
    width: 100%;
  }
}
</style>
