<template>
  <div class="course-grid">
    <div class="course" v-for="c in courses" :key="c.id" @click="goCourse(c.id)">
      <img class="cover" :src="c.cover" />
      <div class="title">{{ c.title }}</div>
      <div class="teacher">{{ c.teacher }}</div>
    </div>
  </div>
</template>



<script setup>
import { useRouter } from 'vue-router'
defineProps({
  courses: Array
})

const router = useRouter()

const goCourse = (id) => {
  router.push(`/course/${id}`);
};

</script>


<style scoped>
.course-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 20px;
  margin-top: 20px;
}

.course {
  background: #fff;
  padding: 16px;
  border-radius: 16px;
  box-shadow: 0 4px 16px rgba(0,0,0,0.06);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  cursor: pointer; /* 鼠标悬停手型 */
}

.course:hover {
  transform: translateY(-5px) scale(1.03); /* 上浮 + 放大 */
  box-shadow: 0 12px 28px rgba(0,0,0,0.15); /* 阴影加深 */
}

.cover {
  width: 100%;
  border-radius: 12px;
  transition: transform 0.3s ease;
}

.course:hover .cover {
  transform: scale(1.05); /* 图片微放大 */
}

.title {
  font-size: 16px;
  font-weight: bold;
  margin-top: 10px;
  transition: color 0.3s ease;
}

.course:hover .title {
  color: #2b5cf6; /* 悬停标题变色 */
}

.teacher {
  color: #555;
  margin-top: 4px;
  transition: color 0.3s ease;
}

.course:hover .teacher {
  color: #1d4ed8; /* 悬停教师名字变色 */
}
</style>
