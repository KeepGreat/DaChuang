<template>
  <div class="chart-wrapper">
    <div class="chart-card" ref="chartRef"></div>
  </div>
</template>

<script setup>
import * as echarts from "echarts";
import { onMounted, ref, onBeforeUnmount } from "vue";

const chartRef = ref(null);

onMounted(() => {
  let chart = echarts.init(chartRef.value);

  chart.setOption({
    backgroundColor: "transparent",

    title: {
      text: "练习完成进度（单位：百分比）",
      left: "center",
      top: 10,
      textStyle: {
        fontSize: 18,
        fontWeight: 600
      }
    },

    tooltip: {
      trigger: "axis",
       formatter: "{b}：{c}%",
      backgroundColor: "rgba(0,0,0,0.75)",
      borderWidth: 0,
      padding: 12,
      textStyle: { color: "#fff" },
      axisPointer: {
        type: "shadow",
        shadowStyle: {
          color: "rgba(120,120,255,0.08)"
        }
      }
    },

    xAxis: {
      type: "category",
      data: ["编程", "算法", "系统", "AI"],
      axisTick: { show: false },
      axisLine: {
        lineStyle: { color: "#ccc" }
      },
      axisLabel: {
        color: "#555",
        fontSize: 14
      }
    },

    yAxis: {
      type: "value",
      name: "完成度 (%)",
      max: 100,
      axisLine: { show: false },
      axisTick: { show: false },
      splitLine: {
        lineStyle: {
          color: "rgba(0,0,0,0.1)"
        }
      },
      axisLabel: {
        color: "#666"
      }
    },

    series: [
      {
        type: "bar",
        data: [90, 76, 84, 68],

        barWidth: "45%",
        itemStyle: {
          borderRadius: [8, 8, 0, 0],
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: "rgba(60,120,255,0.95)" },
            { offset: 1, color: "rgba(80,180,255,0.65)" }
          ])
        },

        emphasis: {
          itemStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: "rgba(255,140,90,1)" },
              { offset: 1, color: "rgba(255,180,120,0.7)" }
            ]),
            borderColor: "#fff",
            borderWidth: 2,
            shadowBlur: 20,
            shadowColor: "rgba(255,140,90,0.6)"
          }
        },

        animationDuration: 1600,
        animationEasing: "elasticOut"
      }
    ]
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
  height: 420px;

  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 10px rgba(0,0,0,0.08);
  padding: 20px;
}
</style>
