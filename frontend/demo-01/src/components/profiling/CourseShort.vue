<template>
  <div class="short-grid">
    <div class="short" v-for="c in shortCourses" :key="c.id">
      <img :src="c.cover" class="cover" />
      <div class="title">{{ c.title }}</div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  shortCourses: Array
});
</script>

<style scoped>
.short-grid {
  display: flex;
  gap: 20px;
  margin-top: 16px;
}

.short {
  width: 280px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 4px 16px rgba(0,0,0,0.06);
  padding: 12px;
}

.cover {
  width: 100%;
  border-radius: 12px;
}
</style>
