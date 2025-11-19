<template>
  <div class="main-container">
    <div class="siderbar">
      <SiderBar></SiderBar>
    </div>
    <div class="content-container">
      <router-view></router-view>
    </div>
  </div>
</template>

<script setup>
import SiderBar from "@/components/Siderbar.vue";
</script>

<style scoped>
.main-container {
  height: 100vh;
  width: 100%;
  display: flex;
}
.siderbar {
  height: 100%;
  width: 220px;
}
.content-container {
  width: calc(100% - 220px);
  overflow-y: auto;
}
</style>