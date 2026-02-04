<template>
  <div class="stat-card">
    <p class="title">{{ title }}</p>
    <h2 class="value">{{ value }} <span class="unit">{{ unit }}</span></h2>
  </div>
</template>

<script setup>
defineProps({
  title: String,
  value: [String, Number],
  unit: String,
});
</script>

<style scoped>
.stat-card {
  background: white;
  border-radius: 16px;
  text-align: center;
  padding: 24px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.08);
  transition: transform 0.2s ease;
}

.stat-card:hover {
  transform: translateY(-5px);
}

.title {
  font-size: 16px;
  color: #666;
}

.value {
  font-size: 28px;
  font-weight: bold;
  color: #2b5cf6;
}

.unit {
  font-size: 16px;
  color: #888;
}
</style>
