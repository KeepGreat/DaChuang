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
    title: { text: "技能掌握进度", left: "center" },
    tooltip: { trigger: "item" },
    series: [
      {
        type: "pie",
        radius: ["50%", "70%"],
        label: { show: true, formatter: "{b}\n{d}%" },
        data: [
          { value: 80, name: "编程基础" },
          { value: 65, name: "算法" },
          { value: 70, name: "数据库" },
          { value: 55, name: "AI基础" },
        ],
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
