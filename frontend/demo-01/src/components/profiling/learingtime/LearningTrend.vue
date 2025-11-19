<template>
  <div class="chart-wrapper">
    <div class="chart-card" ref="chartRef"></div>
  </div>
</template>

<script setup>
import * as echarts from "echarts";
import { ref, onMounted } from "vue";

const chartRef = ref(null);

onMounted(() => {
  const chart = echarts.init(chartRef.value);

  const colors = ["#5470C6"];

  chart.setOption({
    backgroundColor: "transparent",

    title: {
      text: "学习活跃度趋势",
      left: "center",
      top: 10,
      textStyle: {
        fontSize: 18,
        fontWeight: "600",
      },
    },

    tooltip: {
      trigger: "axis",
      backgroundColor: "rgba(50,50,50,0.8)",
      borderWidth: 0,
      textStyle: { color: "#fff" },
      padding: 10,
      axisPointer: {
        type: "line",
        lineStyle: {
          color: "#5470C6",
          width: 2,
          type: "dashed",
        },
      },
    },

    xAxis: {
      type: "category",
      data: ["周一", "周二", "周三", "周四", "周五", "周六", "周日"],
      axisLine: { show: false },
      axisTick: { show: false },
      axisLabel: { color: "#666", fontSize: 14 },
    },

    yAxis: {
      type: "value",
      name: "学习时长 (h)",
      axisLine: { show: false },
      axisTick: { show: false },
      splitLine: {
        lineStyle: { color: "rgba(0,0,0,0.1)" },
      },
      axisLabel: { color: "#666" },
    },

    series: [
      {
        data: [2, 3.5, 4, 3, 5.2, 6, 2.8],
        type: "line",
        smooth: true,
        symbolSize: 10,
        itemStyle: {
          color: colors[0],
          borderColor: "#fff",
          borderWidth: 2,
        },
        lineStyle: {
          width: 4,
          color: colors[0],
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: "rgba(84,112,198,0.45)" },
            { offset: 1, color: "rgba(84,112,198,0.05)" },
          ]),
        },

        emphasis: {
          focus: "series",
          itemStyle: {
            color: "#ff7f50",
            borderColor: "#fff",
            borderWidth: 3,
          },
          lineStyle: {
            width: 5,
          },
        },

        animationDuration: 1800,
        animationEasing: "cubicOut",
      },
    ],
  });

  // 解决窗口缩放导致图表变形
  window.addEventListener("resize", () => chart.resize());
});
</script>

<style scoped>
/* 外层保证居中 */
.chart-wrapper {
  width: 100%;
  display: flex;
  justify-content: center;
}

/* 图表本体区域 */
.chart-card {
  width: 100%;
  max-width: 900px;
  height: 420px;
  margin: 0 auto;
}
</style>
