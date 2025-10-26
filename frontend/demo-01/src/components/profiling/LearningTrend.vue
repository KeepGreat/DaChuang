<template>
  <div class="chart-card" ref="chartRef"></div>
</template>

<script setup>
import * as echarts from "echarts";
import { ref, onMounted } from "vue";

const chartRef = ref(null);
onMounted(() => {
  const chart = echarts.init(chartRef.value);
  chart.setOption({
    title: { text: "学习活跃度趋势", left: "center" },
    tooltip: { trigger: "axis" },
    xAxis: { type: "category", data: ["周一", "周二", "周三", "周四", "周五", "周六", "周日"] },
    yAxis: { type: "value", name: "学习时长(h)" },
    series: [
      {
        data: [2, 3.5, 4, 3, 5.2, 6, 2.8],
        type: "line",
        smooth: true,
        areaStyle: { color: "rgba(84,112,198,0.2)" },
        lineStyle: { color: "#5470C6", width: 3 },
        symbol: "circle",
        symbolSize: 8,
      },
    ],
  });
});
</script>

<style scoped>
.chart-card {
  height: 400px;
}
</style>
