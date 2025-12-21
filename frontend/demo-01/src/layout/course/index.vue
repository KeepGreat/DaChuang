<template>
  <div class="layout-root">
    <CourseNavbar />
    <div class="main-container">
      <CourseSiderbar :courseId="props.id" />
      <div class="content-container">
        <RouterView v-slot="{Component, route}">
          <transition name="fade" mode="out-in">
            <component :is="Component" :key="route.fullPath" />
          </transition>
        </RouterView>
      </div>
    </div>
  </div>
</template>

<script setup>
import CourseSiderbar from '@/components/course/CourseSiderbar.vue';
import CourseNavbar from '@/components/course/CourseNavbar.vue';

// 获取路由参数
const props = defineProps({
  id: {
    type: String, // 路由参数默认是字符串类型，若需数字可转类型
    required: true
  }
});
</script>

<style>
/* 进入/离开的过渡状态 */
.fade-enter-from,
.fade-leave-to {
  opacity: 0; /* 示例：透明度从0→1 */
}

/* 过渡的时长/曲线 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}
</style>

<style scoped>
.layout-root {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.main-container {
  display: flex;
  flex: 1;
  width: 100%;
}

.sidebar {
  width: 220px;
  height: calc(100vh - 64px); /* 顶部导航高度 */
}

.content-container {
  flex: 1;
  overflow-y: auto;
  height: calc(100vh - 64px);
}
</style>