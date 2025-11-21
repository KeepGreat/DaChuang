<template>
  <div class="achievements">
  

    <div class="list">
      <div class="item" v-for="a in items" :key="a.id">
        <img :src="a.icon" />
        <div class="name">{{ a.name }}</div>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  items: Array
});
</script>

<style scoped>
.achievements {
  margin-top: 30px;
}

.list {
  display: flex;
  gap: 20px;
  flex-wrap: wrap;
}

.item {
  width: 180px;
  text-align: center;
  padding: 16px;
  background: #fff;
  border-radius: 14px;
  box-shadow: 0 4px 16px rgba(0,0,0,0.06);
}

.item img {
  width: 60px;
}
</style>
