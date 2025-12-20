<template>
  <div class="knowledge-graph">
    <div class="graph-header">
      <h2>知识图谱</h2>
      <div class="header-actions">
        <el-button @click="resetView">
          <el-icon><Refresh /></el-icon>
          重置视图
        </el-button>
        <el-button @click="exportGraph">
          <el-icon><Download /></el-icon>
          导出图谱
        </el-button>
      </div>
    </div>

    <!-- 筛选和搜索 -->
    <div class="filter-section">
      <div class="filter-left">
        <el-select v-model="nodeFilter" placeholder="节点类型" style="width: 150px">
          <el-option label="全部" value="all"></el-option>
          <el-option label="章节" value="chapter"></el-option>
          <el-option label="知识点" value="knowledge"></el-option>
          <el-option label="重点" value="keypoint"></el-option>
        </el-select>
        <el-select v-model="relationFilter" placeholder="关系类型" style="width: 150px">
          <el-option label="全部" value="all"></el-option>
          <el-option label="包含" value="contains"></el-option>
          <el-option label="前置" value="prerequisite"></el-option>
          <el-option label="相关" value="related"></el-option>
        </el-select>
      </div>
      <div class="search-box">
        <el-input
          v-model="searchQuery"
          placeholder="搜索知识点..."
          style="width: 300px"
          clearable
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
      </div>
    </div>

    <!-- 知识图谱主体 -->
    <div class="graph-container">
      <div class="graph-main">
        <div ref="graphRef" class="graph-canvas"></div>
        <!-- 节点信息面板 -->
        <div v-if="selectedNode" class="node-info-panel">
          <div class="panel-header">
            <h3>{{ selectedNode.name }}</h3>
            <el-button text @click="closeNodeInfo">
              <el-icon><Close /></el-icon>
            </el-button>
          </div>
          <div class="panel-content">
            <div class="info-item">
              <span class="label">类型：</span>
              <el-tag :type="getNodeTypeTagType(selectedNode.type)">
                {{ getNodeTypeText(selectedNode.type) }}
              </el-tag>
            </div>
            <div class="info-item">
              <span class="label">权重：</span>
              <el-progress
                :percentage="selectedNode.weight || 50"
                :stroke-width="6"
                style="width: 100px;"
              />
            </div>
            <div class="info-item">
              <span class="label">描述：</span>
              <p>{{ selectedNode.description || '暂无描述' }}</p>
            </div>
            <div v-if="selectedNode.resources" class="info-item">
              <span class="label">相关资源：</span>
              <div class="resource-list">
                <el-tag
                  v-for="resource in selectedNode.resources"
                  :key="resource"
                  type="info"
                  size="small"
                >
                  {{ resource }}
                </el-tag>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 侧边栏 -->
      <div class="graph-sidebar">
        <!-- 图例 -->
        <el-card class="legend-card">
          <template #header>
            <h4>图例</h4>
          </template>
          <div class="legend-items">
            <div class="legend-item">
              <div class="legend-node chapter"></div>
              <span>章节</span>
            </div>
            <div class="legend-item">
              <div class="legend-node knowledge"></div>
              <span>知识点</span>
            </div>
            <div class="legend-item">
              <div class="legend-node keypoint"></div>
              <span>重点</span>
            </div>
          </div>
          <div class="legend-relations">
            <h5>关系类型</h5>
            <div class="legend-item">
              <div class="legend-line contains"></div>
              <span>包含</span>
            </div>
            <div class="legend-item">
              <div class="legend-line prerequisite"></div>
              <span>前置</span>
            </div>
            <div class="legend-item">
              <div class="legend-line related"></div>
              <span>相关</span>
            </div>
          </div>
        </el-card>

        <!-- 统计信息 -->
        <el-card class="stats-card">
          <template #header>
            <h4>统计信息</h4>
          </template>
          <div class="stat-items">
            <div class="stat-item">
              <span class="stat-number">{{ nodeStats.total }}</span>
              <span class="stat-label">总节点数</span>
            </div>
            <div class="stat-item">
              <span class="stat-number">{{ nodeStats.chapters }}</span>
              <span class="stat-label">章节数</span>
            </div>
            <div class="stat-item">
              <span class="stat-number">{{ nodeStats.knowledge }}</span>
              <span class="stat-label">知识点数</span>
            </div>
            <div class="stat-item">
              <span class="stat-number">{{ nodeStats.keypoints }}</span>
              <span class="stat-label">重点数</span>
            </div>
          </div>
        </el-card>

        <!-- 学习路径 -->
        <el-card class="path-card">
          <template #header>
            <div class="card-header">
              <h4>推荐学习路径</h4>
              <el-button type="primary" size="small" @click="generatePath">
                生成路径
              </el-button>
            </div>
          </template>
          <div v-if="learningPath.length === 0" class="empty-path">
            <el-empty description="暂无学习路径" :image-size="80" />
          </div>
          <div v-else class="path-list">
            <div
              v-for="(item, index) in learningPath"
              :key="item.id"
              class="path-item"
            >
              <div class="path-index">{{ index + 1 }}</div>
              <div class="path-content">
                <div class="path-name">{{ item.name }}</div>
                <div class="path-desc">{{ item.description }}</div>
              </div>
              <div class="path-status">
                <el-icon v-if="item.completed" color="#67c23a"><Check /></el-icon>
                <el-icon v-else color="#909399"><Clock /></el-icon>
              </div>
            </div>
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Refresh, Download, Search, Close, Check, Clock
} from '@element-plus/icons-vue'

// 模拟知识图谱数据
const knowledgeData = ref({
  nodes: [
    { id: 1, name: 'Python基础', type: 'chapter', weight: 90, x: 400, y: 100 },
    { id: 2, name: '变量与数据类型', type: 'knowledge', weight: 80, x: 200, y: 200 },
    { id: 3, name: '运算符', type: 'knowledge', weight: 70, x: 600, y: 200 },
    { id: 4, name: '流程控制', type: 'chapter', weight: 85, x: 400, y: 300 },
    { id: 5, name: 'if语句', type: 'keypoint', weight: 90, x: 200, y: 400 },
    { id: 6, name: '循环语句', type: 'keypoint', weight: 95, x: 600, y: 400 },
    { id: 7, name: '函数', type: 'chapter', weight: 88, x: 400, y: 500 }
  ],
  relations: [
    { from: 1, to: 2, type: 'contains' },
    { from: 1, to: 3, type: 'contains' },
    { from: 2, to: 4, type: 'prerequisite' },
    { from: 3, to: 4, type: 'prerequisite' },
    { from: 4, to: 5, type: 'contains' },
    { from: 4, to: 6, type: 'contains' },
    { from: 5, to: 7, type: 'prerequisite' },
    { from: 6, to: 7, type: 'prerequisite' }
  ]
})

// 响应式数据
const graphRef = ref(null)
const selectedNode = ref(null)
const nodeFilter = ref('all')
const relationFilter = ref('all')
const searchQuery = ref('')
const learningPath = ref([])

// 计算属性
const nodeStats = computed(() => ({
  total: knowledgeData.value.nodes.length,
  chapters: knowledgeData.value.nodes.filter(n => n.type === 'chapter').length,
  knowledge: knowledgeData.value.nodes.filter(n => n.type === 'knowledge').length,
  keypoints: knowledgeData.value.nodes.filter(n => n.type === 'keypoint').length
}))

const filteredNodes = computed(() => {
  let nodes = knowledgeData.value.nodes

  if (nodeFilter.value !== 'all') {
    nodes = nodes.filter(n => n.type === nodeFilter.value)
  }

  if (searchQuery.value.trim()) {
    const query = searchQuery.value.toLowerCase()
    nodes = nodes.filter(n => n.name.toLowerCase().includes(query))
  }

  return nodes
})

const filteredRelations = computed(() => {
  if (relationFilter.value === 'all') {
    return knowledgeData.value.relations
  }
  return knowledgeData.value.relations.filter(r => r.type === relationFilter.value)
})

// 方法
const initGraph = () => {
  nextTick(() => {
    if (!graphRef.value) return
    drawGraph()
  })
}

const drawGraph = () => {
  const canvas = graphRef.value
  const ctx = canvas.getContext('2d')

  // 设置canvas大小
  canvas.width = canvas.offsetWidth
  canvas.height = canvas.offsetHeight

  // 清空画布
  ctx.clearRect(0, 0, canvas.width, canvas.height)

  // 绘制关系线
  filteredRelations.value.forEach(relation => {
    const fromNode = knowledgeData.value.nodes.find(n => n.id === relation.from)
    const toNode = knowledgeData.value.nodes.find(n => n.id === relation.to)

    if (fromNode && toNode && filteredNodes.value.includes(fromNode) && filteredNodes.value.includes(toNode)) {
      drawRelation(ctx, fromNode, toNode, relation.type)
    }
  })

  // 绘制节点
  filteredNodes.value.forEach(node => {
    drawNode(ctx, node)
  })
}

const drawNode = (ctx, node) => {
  const { x, y, type, name } = node

  // 节点样式
  const nodeColors = {
    chapter: '#409eff',
    knowledge: '#67c23a',
    keypoint: '#e6a23c'
  }

  // 绘制节点
  ctx.beginPath()
  ctx.arc(x, y, 30, 0, 2 * Math.PI)
  ctx.fillStyle = nodeColors[type] || '#909399'
  ctx.fill()
  ctx.strokeStyle = '#fff'
  ctx.lineWidth = 2
  ctx.stroke()

  // 绘制文字
  ctx.fillStyle = '#333'
  ctx.font = '14px Arial'
  ctx.textAlign = 'center'
  ctx.fillText(name, x, y + 50)
}

const drawRelation = (ctx, fromNode, toNode, type) => {
  // 关系线样式
  const lineColors = {
    contains: '#409eff',
    prerequisite: '#e6a23c',
    related: '#67c23a'
  }

  // 绘制连线
  ctx.beginPath()
  ctx.moveTo(fromNode.x, fromNode.y)
  ctx.lineTo(toNode.x, toNode.y)
  ctx.strokeStyle = lineColors[type] || '#909399'
  ctx.lineWidth = 2
  ctx.stroke()

  // 绘制箭头
  const angle = Math.atan2(toNode.y - fromNode.y, toNode.x - fromNode.x)
  const arrowLength = 15
  ctx.beginPath()
  ctx.moveTo(toNode.x - 30 * Math.cos(angle), toNode.y - 30 * Math.sin(angle))
  ctx.lineTo(
    toNode.x - 30 * Math.cos(angle) - arrowLength * Math.cos(angle - Math.PI / 6),
    toNode.y - 30 * Math.sin(angle) - arrowLength * Math.sin(angle - Math.PI / 6)
  )
  ctx.moveTo(toNode.x - 30 * Math.cos(angle), toNode.y - 30 * Math.sin(angle))
  ctx.lineTo(
    toNode.x - 30 * Math.cos(angle) - arrowLength * Math.cos(angle + Math.PI / 6),
    toNode.y - 30 * Math.sin(angle) - arrowLength * Math.sin(angle + Math.PI / 6)
  )
  ctx.stroke()
}

const handleCanvasClick = (event) => {
  const canvas = graphRef.value
  const rect = canvas.getBoundingClientRect()
  const x = event.clientX - rect.left
  const y = event.clientY - rect.top

  // 检查是否点击了节点
  const clickedNode = filteredNodes.value.find(node => {
    const distance = Math.sqrt((x - node.x) ** 2 + (y - node.y) ** 2)
    return distance <= 30
  })

  selectedNode.value = clickedNode
}

const closeNodeInfo = () => {
  selectedNode.value = null
}

const resetView = () => {
  nodeFilter.value = 'all'
  relationFilter.value = 'all'
  searchQuery.value = ''
  selectedNode.value = null
  drawGraph()
}

const exportGraph = () => {
  const canvas = graphRef.value
  const url = canvas.toDataURL('image/png')
  const link = document.createElement('a')
  link.download = 'knowledge-graph.png'
  link.href = url
  link.click()
  ElMessage.success('图谱已导出')
}

const generatePath = () => {
  // 模拟生成学习路径
  learningPath.value = [
    {
      id: 1,
      name: 'Python基础入门',
      description: '了解Python的基本概念和环境搭建',
      completed: true
    },
    {
      id: 2,
      name: '变量与数据类型',
      description: '掌握Python的变量定义和各种数据类型',
      completed: true
    },
    {
      id: 3,
      name: '运算符使用',
      description: '学习Python的各种运算符及其优先级',
      completed: false
    },
    {
      id: 4,
      name: '流程控制语句',
      description: '掌握if条件语句和循环语句的使用',
      completed: false
    }
  ]
  ElMessage.success('学习路径已生成')
}

const getNodeTypeTagType = (type) => {
  const typeMap = {
    'chapter': 'primary',
    'knowledge': 'success',
    'keypoint': 'warning'
  }
  return typeMap[type] || 'info'
}

const getNodeTypeText = (type) => {
  const textMap = {
    'chapter': '章节',
    'knowledge': '知识点',
    'keypoint': '重点'
  }
  return textMap[type] || '未知'
}

// 生命周期
onMounted(() => {
  initGraph()
  window.addEventListener('resize', initGraph)

  // 添加canvas点击事件
  nextTick(() => {
    if (graphRef.value) {
      graphRef.value.addEventListener('click', handleCanvasClick)
    }
  })
})

onUnmounted(() => {
  window.removeEventListener('resize', initGraph)

  // 移除canvas点击事件
  if (graphRef.value) {
    graphRef.value.removeEventListener('click', handleCanvasClick)
  }
})
</script>

<style scoped>
.knowledge-graph {
  height: 100%;
  display: flex;
  flex-direction: column;
  background: #f5f7fa;
}

.graph-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  background: #fff;
  border-bottom: 1px solid #e4e7ed;
}

.graph-header h2 {
  margin: 0;
  color: #2c3e50;
  font-size: 24px;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.filter-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  background: #fff;
  border-bottom: 1px solid #e4e7ed;
}

.filter-left {
  display: flex;
  gap: 10px;
}

.graph-container {
  flex: 1;
  display: flex;
  padding: 20px;
  gap: 20px;
  overflow: hidden;
}

.graph-main {
  flex: 1;
  position: relative;
}

.graph-canvas {
  width: 100%;
  height: 100%;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
  cursor: pointer;
}

.node-info-panel {
  position: absolute;
  top: 20px;
  right: 20px;
  width: 300px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.15);
  z-index: 10;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  border-bottom: 1px solid #e4e7ed;
}

.panel-header h3 {
  margin: 0;
  font-size: 16px;
  color: #303133;
}

.panel-content {
  padding: 20px;
}

.info-item {
  margin-bottom: 15px;
}

.info-item:last-child {
  margin-bottom: 0;
}

.info-item .label {
  display: block;
  color: #606266;
  font-size: 14px;
  margin-bottom: 8px;
}

.info-item p {
  margin: 0;
  color: #303133;
  font-size: 14px;
  line-height: 1.6;
}

.resource-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 8px;
}

.graph-sidebar {
  width: 300px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.legend-card,
.stats-card,
.path-card {
  background: #fff;
}

.legend-card :deep(.el-card__header),
.stats-card :deep(.el-card__header),
.path-card :deep(.el-card__header) {
  padding: 15px 20px;
  border-bottom: 1px solid #e4e7ed;
}

.legend-card :deep(.el-card__body),
.stats-card :deep(.el-card__body),
.path-card :deep(.el-card__body) {
  padding: 20px;
}

.legend-card h4,
.stats-card h4,
.path-card h4 {
  margin: 0;
  font-size: 16px;
  color: #303133;
}

.legend-items {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-bottom: 20px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 10px;
}

.legend-node {
  width: 20px;
  height: 20px;
  border-radius: 50%;
}

.legend-node.chapter {
  background: #409eff;
}

.legend-node.knowledge {
  background: #67c23a;
}

.legend-node.keypoint {
  background: #e6a23c;
}

.legend-relations h5 {
  margin: 0 0 10px 0;
  font-size: 14px;
  color: #606266;
}

.legend-line {
  width: 40px;
  height: 2px;
  position: relative;
}

.legend-line.contains {
  background: #409eff;
}

.legend-line.prerequisite {
  background: #e6a23c;
}

.legend-line.related {
  background: #67c23a;
}

.legend-line::after {
  content: '';
  position: absolute;
  right: 0;
  top: -4px;
  width: 0;
  height: 0;
  border-left: 8px solid;
  border-top: 5px solid transparent;
  border-bottom: 5px solid transparent;
}

.legend-line.contains::after {
  border-left-color: #409eff;
}

.legend-line.prerequisite::after {
  border-left-color: #e6a23c;
}

.legend-line.related::after {
  border-left-color: #67c23a;
}

.stat-items {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 15px;
}

.stat-item {
  text-align: center;
  padding: 10px;
  background: #f8f9fa;
  border-radius: 6px;
}

.stat-number {
  display: block;
  font-size: 24px;
  font-weight: 600;
  color: #409eff;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 12px;
  color: #909399;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.path-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.path-item {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 6px;
}

.path-index {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: #409eff;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 600;
}

.path-content {
  flex: 1;
}

.path-name {
  font-weight: 500;
  color: #303133;
  margin-bottom: 4px;
}

.path-desc {
  font-size: 12px;
  color: #909399;
}

.empty-path {
  padding: 20px;
  text-align: center;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .graph-sidebar {
    width: 250px;
  }
}

@media (max-width: 768px) {
  .graph-container {
    flex-direction: column;
  }

  .graph-sidebar {
    width: 100%;
    flex-direction: row;
    overflow-x: auto;
  }

  .legend-card,
  .stats-card,
  .path-card {
    min-width: 200px;
  }

  .filter-section {
    flex-direction: column;
    gap: 10px;
  }

  .filter-left {
    width: 100%;
    justify-content: space-between;
  }

  .search-box {
    width: 100%;
  }

  .search-box :deep(.el-input) {
    width: 100% !important;
  }

  .node-info-panel {
    width: calc(100% - 40px);
    right: 20px;
  }
}
</style>