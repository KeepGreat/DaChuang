<template>
  <div class="chart-card" ref="chartRef"></div>
</template>

<script setup>
import * as echarts from "echarts";
import { ref, onMounted } from "vue";

const chartRef = ref(null);
onMounted(() => {
  const chart = echarts.init(chartRef.value);
  const hours = Array.from({ length: 7 }, (_, i) => `周${"一二三四五六日"[i]}`);
  const data = hours.map((h) => [h, Math.round(Math.random() * 10)]);
  chart.setOption({
    title: { text: "近一周学习活跃度", left: "center" },
    xAxis: { type: "category", data: hours },
    yAxis: { type: "value", max: 10, show: false },
    series: [
      {
        type: "bar",
        data: data.map((d) => d[1]),
        itemStyle: {
          color: (params) => `rgba(84,112,198,${0.3 + params.value / 15})`,
          borderRadius: 6,
        },
      },
    ],
  });
});
</script>

<style scoped>
.chart-card {
  height: 300px;
}
</style>
