<template>
  <div class="course-detail-page">
    <!-- 左侧栏 -->
    <div class="left-panel">
      <BasicInfo :course="course" />

      <KnowledgeChart :stats="knowledgeStats" />

      <AIAssistantSummary :summary="aiSummary" />
    </div>

    <!-- 右侧栏 -->
    <div class="right-panel">
      <MasteryHeatmap :heatmapData="heatmapData" />
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";

// 组件引入
import BasicInfo from "@/components/profiling/image/BasicInfo.vue";
import KnowledgeChart from "@/components/profiling/image/KnowledgeChart.vue";
import AIAssistantSummary from "@/components/profiling/image/AIAssistantSummary.vue";
import MasteryHeatmap from "@/components/profiling/image/MasteryHeatmap.vue";

// 示例数据
const course = ref({
  title: "算法与数据结构",
  teacher: "Andrew Ng",
  progress: 75,
  totalPoints: 0,
  avgMastery: 1,
  weakPoints: 1,
  missedPoints: 1,
  exemptPoints: 1,
});

const knowledgeStats = ref([
  { topic: "数组", percentage: 80 },
  { topic: "链表", percentage: 60 },
  { topic: "树", percentage: 45 },
  { topic: "图", percentage: 30 },
]);

const aiSummary = ref("本课程学习情况良好，建议重点复习图与树相关知识点，AI助教生成了个性化复习计划。");

const heatmapData = ref([
  { topic: "数组", mastery: 90 },
  { topic: "链表", mastery: 70 },
  { topic: "树", mastery: 50 },
  { topic: "图", mastery: 30 },
]);
</script>

<style scoped>
.course-detail-page {
  display: flex;
  height: 100vh; /* 整屏高度 */
  gap: 20px;
  padding: 20px;
  box-sizing: border-box;
}

/* 左侧栏 37% */
.left-panel {
  width: 33%;
  display: flex;
  flex-direction: column;
  gap: 20px;
  overflow-y: auto;
}

/* 右侧栏 63% */
.right-panel {
  width: 63%;
  overflow-y: auto;
}
</style>
