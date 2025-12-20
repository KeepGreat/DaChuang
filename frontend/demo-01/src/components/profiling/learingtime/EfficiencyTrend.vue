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
  days: {
    type: Array,
    default: () => ["周一", "周二", "周三", "周四", "周五", "周六", "周日"],
  },
  timePerTask: {
    type: Array,
    default: () => [16, 14, 18, 12, 10, 11, 15],
  },
});

onMounted(() => {
  const chart = echarts.init(chartRef.value);

  chart.setOption({
    backgroundColor: "transparent",

    title: {
      text: "学习效率趋势（单位成果所需时间）",
      left: "center",
      top: 10,
      textStyle: { fontSize: 18, fontWeight: 600 },
    },

    tooltip: {
      trigger: "axis",
      backgroundColor: "rgba(50,50,50,0.8)",
      textStyle: { color: "#fff" },
      borderWidth: 0,
    },

    xAxis: {
      type: "category",
      data: props.days,
      axisLine: { show: false },
      axisTick: { show: false },
      axisLabel: { color: "#666" },
    },

    yAxis: {
      type: "value",
      name: "分钟/题",
      axisLine: { show: false },
      axisLabel: { color: "#666" },
      splitLine: {
        lineStyle: { color: "rgba(0,0,0,0.1)" },
      },
    },

    series: [
      {
        type: "line",
        smooth: true,
        data: props.timePerTask,
        symbolSize: 8,
        itemStyle: {
          color: "#5470C6",
          borderWidth: 2,
          borderColor: "#fff",
        },
        lineStyle: {
          width: 4,
          color: "#5470C6",
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: "rgba(84,112,198,0.45)" },
            { offset: 1, color: "rgba(84,112,198,0.05)" },
          ]),
        },
      },
    ],

    animationDuration: 1600,
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
  max-width: 900px;
  height: 400px;
}
</style>
