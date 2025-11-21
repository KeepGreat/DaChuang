<template>
  <div class="knowledge-chart" ref="chartRef"></div>
</template>

<script setup>
import * as echarts from "echarts";
import { ref, onMounted, watch } from "vue";

const props = defineProps({
  stats: {
    type: Array,
    default: () => []
  }
});

const chartRef = ref(null);
let chart = null;

// 监听 + 初始化
onMounted(() => initChart());
watch(() => props.stats, () => initChart(), { deep: true });

// 统计数据
function formatData() {
  let good = 0, normal = 0, weak = 0, missed = 0, exempt = 0;

  props.stats.forEach(item => {
    if (item.category === "good") good++;
    else if (item.category === "normal") normal++;
    else if (item.category === "weak") weak++;
    else if (item.category === "missed") missed++;
    else if (item.category === "exempt") exempt++;
  });

  return [
    { name: "掌握较好", value: good },
    { name: "掌握一般", value: normal },
    { name: "薄弱点", value: weak },
    { name: "遗漏点", value: missed },
    { name: "免考", value: exempt }
  ];
}

function initChart() {
  if (!chartRef.value) return;

  if (!chart) chart = echarts.init(chartRef.value);

  const data = formatData();

  chart.setOption({
    title: {
      text: "课程知识点占比",
      left: "center",
      top: "8"
    },
    tooltip: {
      trigger: "item",
      formatter: "{b}: {c} ({d}%)"
    },
    legend: {
      bottom: 0,
      orient: "horizontal"
    },

    series: [
      {
        name: "知识点占比",
        type: "pie",
        radius: ["45%", "70%"],    // 控制孔型图大小
        avoidLabelOverlap: false,
        itemStyle: {
          borderColor: "#fff",
          borderWidth: 2
        },
        label: {
          show: false
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 16,
            fontWeight: "bold",
            formatter: "{b}\n{d}%"
          }
        },
        labelLine: { show: false },

        // 颜色配置（可换）
        color: [
          "#ffbbbb", // 掌握较好
          "#dbffbb", // 掌握一般
          "#bbf5ff", // 薄弱点
          "#e9bbff", // 遗漏点
          "#d4d4d4"  // 免考
        ],

        data
      }
    ]
  });

  chart.resize();
}
</script>

<style scoped>
.knowledge-chart {
  width: 100%;               
  max-width: 600px;          
  height: 360px;
  margin: 0 auto;            
  background: #ffffff;
  border-radius: 16px;
  padding: 16px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.08);
  display: flex;             
  align-items: center;
  justify-content: center;
  cursor: pointer;           /* 鼠标手型 */
  transition: all 0.3s ease; /* 悬停动画 */
}

/* 鼠标悬停效果 */
.knowledge-chart:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 20px rgba(0,0,0,0.15);
  background-color: #f5f7ff; /* 可改为想要的高亮色 */
}
</style>


