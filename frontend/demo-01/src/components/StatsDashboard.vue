<template>
  <div class="dashboard">
    <h1 class="title">📊 学习统计面板</h1>

    <div ref="chartRef" class="chart-card"></div>

    <p class="note">数据仅用于演示，可替换为后端接口返回数据。</p>
  </div>
</template>

<script setup>
import { onMounted, ref } from "vue";
import * as echarts from "echarts";
import { courseStats } from "../data/statData";

const chartRef = ref(null);

onMounted(() => {
  const chart = echarts.init(chartRef.value);

  const option = {
    title: {
      text: "课程参与与作业完成情况",
      left: "center",
      textStyle: { fontSize: 18 },
    },
    tooltip: { trigger: "axis" },
    legend: {
      data: ["参与人数", "作业完成"],
      top: 40,
    },
    grid: {
      left: "10%",
      right: "10%",
      bottom: "10%",
      containLabel: true,
    },
    xAxis: {
      type: "category",
      data: courseStats.days,
    },
    yAxis: { type: "value" },
    series: [
      {
        name: "参与人数",
        type: "line",
        smooth: true,
        data: courseStats.participation,
        lineStyle: { color: "#5470C6" },
      },
      {
        name: "作业完成",
        type: "bar",
        data: courseStats.completion,
        itemStyle: { color: "#91cc75" },
      },
    ],
  };

  chart.setOption(option);
  window.addEventListener("resize", () => chart.resize());
});
</script>

<style scoped>
.dashboard {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: flex-start;
  background: #f9fafb;
  min-height: 100vh;
  padding: 40px;
}

.title {
  font-size: 28px;
  font-weight: bold;
  color: #333;
  margin-bottom: 30px;
}

.chart-card {
  width: 80%;
  height: 400px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  padding: 16px;
}

.note {
  color: #666;
  margin-top: 20px;
}
</style>
