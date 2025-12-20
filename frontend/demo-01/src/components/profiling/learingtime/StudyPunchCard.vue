<template>
  <div class="chart-wrapper">
    <div class="chart-card" ref="chartRef"></div>
  </div>
</template>

<script setup>
import * as echarts from "echarts";
import { ref, onMounted, onBeforeUnmount } from "vue";

const chartRef = ref(null);

const props = defineProps({
  calendarData: {
    type: Array,
    default: () => [
      ["2025-01-01", 1],
      ["2025-01-02", 0],
      ["2025-01-03", 1],
      ["2025-01-05", 1],
      ["2025-01-06", 1],
    ],
  },
});

onMounted(() => {
  let chart = echarts.init(chartRef.value);

  chart.setOption({
    backgroundColor: "transparent",

    title: {
      text: "连续学习天数（打卡趋势）",
      left: "center",
      top: 10,
      textStyle: { fontSize: 18, fontWeight: 600 },
    },

    tooltip: {
      formatter: (p) => `${p.data[0]}：${p.data[1] ? "已学习" : "未学习"}`,
      backgroundColor: "rgba(50,50,50,0.8)",
      borderWidth: 0,
      textStyle: { color: "#fff" },
    },

    visualMap: {
      show: false,
      min: 0,
      max: 1,
      inRange: { color: ["#eee", "#5470C6"] },
    },

    calendar: {
      top: 60,
      cellSize: ["auto", 22],
      range: "2025",
      itemStyle: {
        borderWidth: 0.5,
        borderColor: "#ccc",
      },
    },

    series: [
      {
        type: "heatmap",
        coordinateSystem: "calendar",
        data: props.calendarData,
      },
    ],

    animationDuration: 1200,
  });

  const handleResize = () => chart && chart.resize();
  window.addEventListener("resize", handleResize);

  onBeforeUnmount(() => {
    window.removeEventListener("resize", handleResize);
    try { chart && chart.dispose(); } catch (e) {}
    chart = null;
  });
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
  height: 300px;
}
</style>
