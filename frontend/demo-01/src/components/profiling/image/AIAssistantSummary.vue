<template>
  <div class="ai-summary">
    <h3>AI助教总结</h3>
    <p>{{ summary }}</p>
  </div>
</template>

<script setup>
defineProps({ summary: String });
</script>

<style scoped>
.ai-summary {
  background: #fff;
  padding: 16px;
  border-radius: 16px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.08);
}
.ai-summary h3 {
  margin-bottom: 12px;
}
</style>
