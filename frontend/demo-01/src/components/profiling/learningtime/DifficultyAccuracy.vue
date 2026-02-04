<template>
  <div class="chart-wrapper">
    <div class="chart-card" ref="chartRef"></div>
  </div>
</template>

<script setup>
import * as echarts from "echarts";
import { ref, onMounted } from "vue";

const chartRef = ref(null);

const props = defineProps({
  data: {
    type: Object,
    default: () => ({
      easy: 92,
      medium: 76,
      hard: 51,
    }),
  },
});

onMounted(() => {
  const chart = echarts.init(chartRef.value);

  chart.setOption({
    backgroundColor: "transparent",

    title: {
      text: "难度维度正确率",
      left: "center",
      top: 10,
      textStyle: { fontSize: 18, fontWeight: 600 },
    },

    tooltip: {
      trigger: "axis",
      axisPointer: { type: "shadow" },
      backgroundColor: "rgba(50,50,50,0.8)",
      textStyle: { color: "#fff" },
      borderWidth: 0,
    },

    xAxis: {
      type: "category",
      data: ["简单", "中等", "困难"],
      axisLine: { show: false },
      axisTick: { show: false },
      axisLabel: { color: "#666" },
    },

    yAxis: {
      type: "value",
      max: 100,
      axisLine: { show: false },
      axisLabel: { color: "#666" },
      splitLine: {
        lineStyle: { color: "rgba(0,0,0,0.1)" },
      },
    },

    series: [
      {
        type: "bar",
        barWidth: 40,
        itemStyle: {
          color: "#5470C6",
          borderRadius: 8,
        },
        data: [
          props.data.easy,
          props.data.medium,
          props.data.hard,
        ],
        label: {
          show: true,
          position: "top",
          formatter: "{c}%",
          fontSize: 14,
        },
      },
    ],

    animationDuration: 1500,
  });

  window.addEventListener("resize", () => chart.resize());
});
</script>

<style scoped>
.chart-wrapper {
  width: 100%;
  display: flex;
  justify-content: center;
}
.chart-card {
  width: 100%;
  max-width: 450px;
  height: 360px;
}
</style>
