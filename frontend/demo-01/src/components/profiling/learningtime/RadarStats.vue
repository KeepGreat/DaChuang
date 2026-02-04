<template>
  <div class="card radar-card">
    <div class="card-title">学习能力雷达图</div>
    <div ref="chartRef" class="radar-chart"></div>
  </div>
</template>

<script setup>
import * as echarts from 'echarts';
import { onMounted, ref, watch } from 'vue';

const props = defineProps({
  data: {
    type: Object,
    default: () => ({
      codeAccuracy: 80,
      aiDependence: 60,
      knowledgeConversion: 75,
      expressionAbility: 70,
      knowledgeDepth: 85,
      codeQuality: 90
    })
  }
});

const chartRef = ref(null);
let chartInstance = null;

const initChart = () => {
  if (!chartRef.value) return;

  chartInstance = echarts.init(chartRef.value);
  const option = {
    tooltip: {},
    radar: {
      indicator: [
        { name: '代码准确率', max: 100 },
        { name: 'AI依赖度', max: 100 },
        { name: '知识转化率', max: 100 },
        { name: '表达能力', max: 100 },
        { name: '知识掌握深度', max: 100 },
        { name: '代码质量', max: 100 }
      ],
      radius: '65%',
      name: {
        textStyle: {
          color: '#333',
          fontSize: 14
        }
      },
      splitLine: {
        lineStyle: {
          color: ['#eee']
        }
      },
      splitArea: {
        areaStyle: {
          color: ['#f9f9f9', '#fff']
        }
      }
    },
    series: [{
      name: '能力雷达',
      type: 'radar',
      data: [
        {
          value: [
            props.data.codeAccuracy,
            props.data.aiDependence,
            props.data.knowledgeConversion,
            props.data.expressionAbility,
            props.data.knowledgeDepth,
            props.data.codeQuality
          ],
          name: '学习能力'
        }
      ],
      itemStyle: {
        color: '#ff69b4'
      },
      lineStyle: {
        color: '#ff69b4'
      },
      areaStyle: {
        color: 'rgba(255,105,180,0.2)'
      }
    }]
  };
  chartInstance.setOption(option);
};

onMounted(() => {
  initChart();
});

// 支持 props 数据更新
watch(() => props.data, () => {
  initChart();
}, { deep: true });
</script>

<style scoped>
.radar-card {
  padding: 24px;
  height: 480px;
}

.radar-chart {
  width: 100%;
  height: 100%;
}

.card-title {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 16px;
}
</style>
