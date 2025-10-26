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
    title: { text: "练习完成进度", left: "center" },
    tooltip: { trigger: "axis" },
    xAxis: { type: "category", data: ["编程", "算法", "系统", "AI"] },
    yAxis: { type: "value", max: 100 },
    series: [
      {
        type: "bar",
        data: [90, 76, 84, 68],
        itemStyle: {
          color: "#73c0de",
          borderRadius: [6, 6, 0, 0],
        },
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
