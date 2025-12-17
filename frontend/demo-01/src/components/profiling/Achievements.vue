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
import ai1 from "@/assets/achievements1.png";
import ai2 from "@/assets/achievements2.png";

const items = [
  { id: 1, icon: ai1, name: "AI认证" },
  { id: 2, icon: ai2, name: "智能体项目完成" },
];

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

  display: flex;
  flex-direction: column;
  align-items: center; /* 水平居中 */
  justify-content: center; /* 垂直居中，可选 */
}


.item img {
  width: 60px;
  display: block;
  margin: 0 auto;
}

.name {
  font-size: 16px;
  font-weight: 600;  /* 加粗但不过度 */
  color: #d63384; /* 粉系主题色 */
  margin-top: 10px;
}

</style>
