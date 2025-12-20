<template>
  <div class="course-grid">
    <div class="course" v-for="(img, index) in shortCourses" :key="index" @click="goCourse(index)">
      <img class="cover" :src="img" />
      <div class="title">AI 课程 {{ index + 1 }}</div>
      <div class="teacher">AI专家导师</div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

// 导入你的图片
import ai1 from "@/assets/Land/ai1.png";
import ai2 from "@/assets/Land/ai2.png";

// 用旧的数据结构，但用于新 UI
const shortCourses = [ai1, ai2];

// 定义 props（如果外部要传）
defineProps({
  courses: Array
})

const router = useRouter()

const goCourse = (id) => {
  router.push(`/course/${id}`)
}
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
  cursor: pointer;
}

.course:hover {
  transform: translateY(-5px) scale(1.03);
  box-shadow: 0 12px 28px rgba(0,0,0,0.15);
}

.cover {
  width: 100%;
  border-radius: 12px;
  transition: transform 0.3s ease;
}

.course:hover .cover {
  transform: scale(1.05);
}

.title {
  font-size: 16px;
  font-weight: bold;
  margin-top: 10px;
  transition: color 0.3s ease;
}

.course:hover .title {
  color: #2b5cf6;
}

.teacher {
  color: #555;
  margin-top: 4px;
  transition: color 0.3s ease;
}

.course:hover .teacher {
  color: #1d4ed8;
}
</style>
