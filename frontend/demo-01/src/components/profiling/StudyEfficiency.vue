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
  title: { text: "学习效率", left: "center" , top: "0%" },
  series: [
    {
      type: "gauge",
      progress: { show: true, width: 12 },
      axisLine: { lineStyle: { width: 12 } },
      pointer: { show: true },
      detail: {
        valueAnimation: true,
        formatter: "{value}%",
        fontSize: 20,
        offsetCenter: [0, "60%"], // ✅ 下移百分比显示
        color: "#1e293b"
      },
      title: {
        show: true,
        offsetCenter: [0, "80%"], // ✅ “专注度”再下移一点，拉开间距
        fontSize: 14,
        color: "#64748b"
      },
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
