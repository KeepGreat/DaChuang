<!-- src/components/Layout.vue -->
<template>
  <div class="layout-root">
    <TopNav />
    <div class="main-container">
      <div class="content-container full">
        <router-view />
      </div>
    </div>
  </div>
</template>

<script setup>
import TopNav from './TopNav.vue';
// sidebar removed to allow content full width
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

.content-container {
  flex: 1;
  overflow-y: auto;
  height: calc(100vh - 64px);
  width: 100%;
}
.content-container.full {
  width: 100%;
}
</style>
