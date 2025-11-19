<template>
  <div class="course-modules">
    <div v-for="(module, mIndex) in modules" :key="mIndex" class="module">
      <div class="module-name">{{ module.name }}</div>
      <div class="topics">
        <div v-for="(topic, tIndex) in module.topics" :key="tIndex" class="topic">
          <span class="topic-name">{{ topic.name }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";

const modules = ref([
  {
    name: "基础理论",
    topics: [
      { name: "AI概念" },
      { name: "机器学习基础" },
      { name: "深度学习基础" },
      { name: "数据处理" },
      { name: "模型评估" },
      { name: "优化算法" },
      { name: "梯度下降" },
      { name: "激活函数" },
    ]
  },
  {
    name: "神经网络",
    topics: [
      { name: "感知机" },
      { name: "前馈网络" },
      { name: "反向传播" },
      { name: "卷积网络" },
      { name: "池化层" },
      { name: "循环网络" },
      { name: "LSTM" },
      { name: "GRU" },
    ]
  },
  {
    name: "自然语言处理",
    topics: [
      { name: "词向量" },
      { name: "RNN" },
      { name: "Transformer" },
      { name: "BERT" },
      { name: "GPT" },
      { name: "情感分析" },
      { name: "文本生成" },
      { name: "机器翻译" },
    ]
  },
  {
    name: "计算机视觉",
    topics: [
      { name: "图像分类" },
      { name: "目标检测" },
      { name: "语义分割" },
      { name: "实例分割" },
      { name: "图像生成" },
      { name: "风格迁移" },
      { name: "GAN" },
      { name: "图像增强" },
    ]
  },
  {
    name: "强化学习",
    topics: [
      { name: "Markov决策过程" },
      { name: "Q-learning" },
      { name: "策略梯度" },
      { name: "深度强化学习" },
      { name: "奖励函数设计" },
      { name: "环境建模" },
      { name: "探索策略" },
      { name: "稳定性分析" },
    ]
  },
  {
    name: "AI工具与框架",
    topics: [
      { name: "TensorFlow" },
      { name: "PyTorch" },
      { name: "Keras" },
      { name: "Scikit-learn" },
      { name: "OpenCV" },
      { name: "NLTK" },
      { name: "HuggingFace" },
      { name: "FastAI" },
    ]
  },
  {
    name: "AI应用与伦理",
    topics: [
      { name: "自动驾驶" },
      { name: "医疗AI" },
      { name: "推荐系统" },
      { name: "AI伦理" },
      { name: "隐私保护" },
      { name: "模型偏差" },
      { name: "可解释性" },
      { name: "AI安全" },
    ]
  },
]);
</script>

<style scoped>
.course-modules {
  display: flex;
  flex-direction: column;
  gap: 24px;
  padding: 16px;
  background: #ffffff;
  border-radius: 12px;
}

.module {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.module-name {
  font-weight: bold;
  color: #7b8cff;
  margin-bottom: 6px;
}

.topics {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

/* 每个知识点小方块 */
.topic {
  flex: 0 0 12.5%; /* 每行8个 */
  aspect-ratio: 1 / 1;
  background: #f8f9ff;
  border: 1px solid #d4d4d4;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.25s ease;
  padding: 4px;
  box-sizing: border-box;
  overflow: hidden;
}

.topic:hover {
  background: #eef2ff;
  transform: translateY(-4px);
  box-shadow: 0 6px 12px rgba(0,0,0,0.12);
}

/* 字体固定大小，不换行 */
.topic-name {
  font-size: 12px;
  line-height: 1.2;
  white-space: nowrap;      /* 不换行 */
  overflow: hidden;         /* 超出隐藏 */
  text-overflow: ellipsis;  /* 显示省略号 */
  text-align: center;
}
</style>
