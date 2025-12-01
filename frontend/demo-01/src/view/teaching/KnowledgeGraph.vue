<template>
  <div class="knowledge-graph">
    <div class="graph-container">
      <div class="placeholder-content">
        <el-icon size="80"><Share /></el-icon>
        <h3>知识图谱</h3>
        <p>该模块正在开发中，敬请期待...</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { Share } from '@element-plus/icons-vue';
</script>

<style scoped>
.knowledge-graph {
  height: 100%;
  padding: 40px;
}

.graph-container {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.placeholder-content {
  text-align: center;
  color: #9b7a88;
}

.placeholder-content .el-icon {
  margin-bottom: 20px;
  opacity: 0.5;
}

.placeholder-content h3 {
  margin: 0 0 10px 0;
  color: #7b2a53;
  font-size: 24px;
}

.placeholder-content p {
  margin: 0;
  font-size: 16px;
}
</style>