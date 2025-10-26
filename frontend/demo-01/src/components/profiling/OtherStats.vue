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
    title: { text: "综合能力分析", left: "center" },
    radar: {
      indicator: [
        { name: "编程能力", max: 100 },
        { name: "算法思维", max: 100 },
        { name: "团队协作", max: 100 },
        { name: "学习效率", max: 100 },
        { name: "知识掌握", max: 100 },
      ],
    },
    series: [
      {
        name: "能力分布",
        type: "radar",
        data: [
          {
            value: [85, 90, 78, 88, 80],
            name: "当前学生",
            areaStyle: { color: "rgba(43, 92, 246, 0.3)" },
          },
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
