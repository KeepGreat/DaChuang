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
    title: { text: "学习效率", left: "center" },
    series: [
      {
        type: "gauge",
        progress: { show: true, width: 12 },
        axisLine: { lineStyle: { width: 12 } },
        pointer: { show: true },
        detail: { valueAnimation: true, formatter: "{value}%" },
        data: [{ value: 86, name: "专注度" }]
      }
    ]
  });
});
</script>

<style scoped>
.chart-card {
  height: 300px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 10px rgba(0,0,0,0.08);
  padding: 20px;
}
</style>
