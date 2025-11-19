<!-- src/components/Layout.vue -->
<template>
  <div class="layout-root">
    <TopNav />
    <div class="main-container">
      <div class="sidebar">
        <SiderBar />
      </div>
      <div class="content-container">
        <router-view />
      </div>
    </div>
  </div>
</template>

<script setup>
import SiderBar from "@/components/Siderbar.vue";
import TopNav from "@/components/TopNav.vue";
</script>

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
