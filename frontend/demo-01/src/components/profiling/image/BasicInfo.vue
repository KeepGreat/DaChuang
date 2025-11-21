<template>
  <div class="basic-info">
    <div class="info-item">
      <div class="label">课程名称</div>
      <div class="value">{{ course.title }}</div>

      <div class="label teacher-label">教师</div>
      <div class="value">{{ course.teacher }}</div>
    </div>

    <div class="info-item">
      <div class="label">考核知识点总数</div>
      <div class="value">{{ course.totalPoints }}</div>
    </div>

    <div class="info-item">
      <div class="label">平均掌握度</div>
      <div class="value">{{ course.avgMastery }}%</div>
    </div>

    <div class="info-item">
      <div class="label">薄弱点个数</div>
      <div class="value">{{ course.weakPoints }}</div>
    </div>

    <div class="info-item">
      <div class="label">遗漏点个数</div>
      <div class="value">{{ course.missedPoints }}</div>
    </div>

    <div class="info-item">
      <div class="label">免考个数</div>
      <div class="value">{{ course.exemptPoints }}</div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  course: {
    type: Object,
    default: () => ({
      title: '示例课程名称很长也要处理',
      teacher: '示例教师名字',
      totalPoints: 52,
      avgMastery: 83,
      weakPoints: 6,
      missedPoints: 3,
      exemptPoints: 2
    })
  }
});
</script>

<style scoped>
/* 容器：始终 3 列（不折行），总宽度跟随父容器 */
.basic-info {
  display: grid;
  grid-template-columns: repeat(3, 1fr); /* 三列固定结构 */
  gap: 18px;
  width: 100%;
  box-sizing: border-box;
  padding: 12px;
  background: #fff;
  border-radius: 12px;
  /* 不允许水平滚动或换行，卡片与文字会缩小以适配 */
  overflow: visible;
}

/* 单个卡片：
   - 使用 box-sizing 保证 padding 不影响占比
   - 不允许卡片自己横向伸出父容器
*/
.info-item {
  box-sizing: border-box;
  background: #f8f9ff;
  padding: clamp(8px, 1.2vw, 16px); /* padding 会随视口缩放，但受最小最大限制 */
  border-radius: 12px;
  border: 1px solid #e1e5ff;
  transition: transform .18s ease, box-shadow .18s ease;
  cursor: pointer;
  min-height: 56px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: flex-start;
}

/* 悬停微动效 */
.info-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 20px rgba(0,0,0,0.06);
  background: #eef2ff;
  border-color: #cbd5ff;
}

/* 标签（小字）——禁止换行，超出省略 */
.label {
  font-size: clamp(11px, 0.95vw, 13px); /* 最小 11px，首选按 vw 缩放，最大 13px */
  color: #555;
  margin-bottom: 6px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 主值（大字）——同样缩放并且不换行 */
.value {
  font-size: clamp(14px, 1.8vw, 20px); /* 最小 14px，首选按 vw 缩放，最大 20px */
  font-weight: 700;
  color: #7b8cff;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 课程/教师之间的间距 */
.teacher-label {
  margin-top: 8px;
}


</style>
