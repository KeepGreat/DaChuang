<template>
  <div class="chart-card" ref="chartRef"></div>
</template>

<script setup>
import * as echarts from "echarts";
import { onMounted, ref } from "vue";

const chartRef = ref(null);

onMounted(() => {
  const chart = echarts.init(chartRef.value);
  chart.setOption({
    title: { text: "综合成绩", left: "center" },
    tooltip: { trigger: "item" },
    legend: { bottom: 0 },
    series: [
      {
        name: "成绩构成",
        type: "pie",
        radius: ["40%", "70%"],
        label: { show: true, formatter: "{b}: {d}%" },
        data: [
          { value: 45, name: "考试" },
          { value: 25, name: "作业" },
          { value: 30, name: "教学进度" },
        ],
      },
    ],
  });
});
</script>

<style scoped>
.chart-card {
  height: 400px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.08);
  padding: 20px;
}
</style>
