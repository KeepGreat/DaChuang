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
  data: {
    type: Object,
    default: () => ({
      video: 120,
      practice: 60,
      homework: 40,
      quiz: 30,
    }),
  },
});

onMounted(() => {
  let chart = echarts.init(chartRef.value);

  chart.setOption({
    backgroundColor: "transparent",

    title: {
      text: "学习行为时长占比",
      left: "center",
      top: 10,
      textStyle: {
        fontSize: 18,
        fontWeight: 600,
      },
    },

    tooltip: {
      trigger: "item",
      backgroundColor: "rgba(50,50,50,0.8)",
      textStyle: { color: "#fff" },
      borderWidth: 0,
    },

    series: [
      {
        type: "pie",
        radius: ["45%", "70%"],
        avoidLabelOverlap: false,
        label: {
          show: true,
          formatter: "{b}: {d}%",
          fontSize: 14,
        },
        itemStyle: {
          borderColor: "#fff",
          borderWidth: 2,
        },
        data: [
          { value: props.data.video, name: "视频" },
          { value: props.data.practice, name: "练习" },
          { value: props.data.homework, name: "作业" },
          { value: props.data.quiz, name: "测验" },
        ],
      },
    ],
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
  max-width: 450px;
  height: 360px;
}
</style>
