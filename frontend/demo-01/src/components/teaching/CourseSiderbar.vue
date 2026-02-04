<template>
  <!-- 侧边栏容器 -->
  <el-aside width="60px" class="sidebar-container">
    <!-- 侧边菜单 -->
    <el-menu default-active="1" class="sidebar-menu" background-color="#f5f7fa" text-color="#333"
      active-text-color="#409eff" @select="handleMenuSelect">
      <!-- 菜单项：对应截图中的功能 -->
      <el-menu-item index="">
        <el-icon>
          <VideoPlay />
        </el-icon>
        <span>课程学习</span>
      </el-menu-item>
      <el-menu-item index="2">
        <el-icon>
          <MagicStick />
        </el-icon>
        <span>知识图谱</span>
      </el-menu-item>
      <el-menu-item index="3">
        <el-icon>
          <Goblet />
        </el-icon>
        <span>成绩分析</span>
      </el-menu-item>
      <el-menu-item index="4">
        <el-icon>
          <Timer />
        </el-icon>
        <span>掌握度</span>
      </el-menu-item>
      <el-menu-item index="practiceIndex">
        <el-icon>
          <Document />
        </el-icon>
        <span>练习考试</span>
      </el-menu-item>
      <el-menu-item index="6">
        <el-icon>
          <Folder />
        </el-icon>
        <span>课程资料</span>
      </el-menu-item>
    </el-menu>
  </el-aside>
</template>

<script setup>
import { ref } from 'vue';
// 引入Element Plus图标
import { VideoPlay, MagicStick, Goblet, Timer, Document, Folder } from '@element-plus/icons-vue';
import router from '@/router/router.js';

// 接收父组件传递的courseId
const props = defineProps({
  courseId: {
    type: [String, Number],
    required: true
  }
});

// 菜单选择事件
const handleMenuSelect = (index) => {
  try {
    const routePath = `/courses/${props.courseId}/${index}`
    router.push(routePath)
  } catch (error) {
    console.error('路由跳转失败:', error)
  }
};
</script>

<style scoped>
.sidebar-container {
  height: calc(100vh - 64px);
  background-color: #f5f7fa;
  border-right: 1px solid #e5e7eb;
  overflow: hidden;
}

.sidebar-menu {
  border-right: none;
  /* 取消Element默认的右侧边框 */
  width: 100%;
  /* 确保菜单宽度适配容器 */
  display: flex;
  flex-direction: column;
  align-items: center;
}

/* 调整菜单项样式，适配60px宽度 */
.sidebar-menu :deep(.el-menu-item) {
  padding: 0 !important;
  /* 重置内边距 */
  width: 100%;
  /* 确保宽度充满容器 */
  height: 60px;
  /* 设置合适的高度 */
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

/* 调整图标大小和位置 */
.sidebar-menu :deep(.el-menu-item .el-icon) {
  font-size: 18px;
  margin-right: 0;
  /* 移除图标右侧间距 */
}

/* 调整el-menu-item内部span的样式 */
.sidebar-menu :deep(.el-menu-item span) {
  /* 字体样式相关属性 */
  font-size: 12px;
  /* 设置字体大小 */
  font-family: 'Arial', sans-serif;
  /* 设置字体家族 */
  font-weight: 400;
  /* 设置字体粗细：normal(400), bold(700), 100-900 */
  font-style: normal;
  /* 设置字体样式：normal, italic(斜体), oblique */
  line-height: 1.2;
  /* 设置行高 */
  letter-spacing: 0;
  /* 设置字间距 */
  text-transform: none;
  /* 文本转换：none, uppercase, lowercase, capitalize */
  text-decoration: none;
  /* 文本装饰：none, underline, overline, line-through */

  /* 其他样式属性 */
  color: #606266;
  /* 设置文字颜色 */
  margin-top: 4px;
  /* 调整与图标的间距 */
  text-align: center;
  /* 文字居中对齐 */
  width: 100%;
  /* 宽度充满容器 */
  height: 18px;
  overflow: hidden;
  /* 处理溢出 */
  text-overflow: ellipsis;
  /* 文本溢出显示省略号 */
  white-space: nowrap;
  /* 不换行 */
}

/* 调整激活状态样式 */
.sidebar-menu :deep(.el-menu-item.is-active) {
  background-color: #e6f0fa;
}

/* 调整激活状态下的文字颜色 */
.sidebar-menu :deep(.el-menu-item.is-active span) {
  color: #409eff;
  /* 激活状态文字颜色 */
}

.resource-stats {
  margin: 16px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.stats-collapse {
  --el-collapse-item-content-padding: 0;
}

.stats-list {
  --el-list-item-padding: 8px 16px;
}

.stats-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.stats-icon {
  width: 20px;
  height: 20px;
  color: #666;
}

.stats-text {
  flex: 1;
  font-size: 14px;
  color: #333;
}

.stats-badge {
  min-width: 24px;
  height: 20px;
  line-height: 20px;
  font-size: 12px;
}
</style>