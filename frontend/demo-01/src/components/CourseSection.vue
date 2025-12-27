<template>
  <section class="modules-section">
    <!-- 左侧分类 -->
    <aside class="category-sidebar">
      <h3>课程分类</h3>
      <ul>
        <li>All</li>
        <li>AI Agents</li>
        <li>Machine Learning</li>
        <li>Deep Learning</li>
        <li>Data Science</li>
        <li>Robotics</li>
      </ul>
    </aside>

    <!-- 右侧课程横向滑动 -->
    <div class="course-grid">
      <div v-for="(row, rowIndex) in rows" :key="row.title" class="course-row">
         <!-- ⭐ only AI Agents 第一个模块显示单独 banner -->
        <div v-if="row.title === 'AI Agents'" class="special-banner">
         <img :src="bannerImage" alt="banner" class="banner-img" />
      </div>


        <h2>{{ row.title }}</h2>

        <div class="course-row-wrapper">
          <!-- 左箭头 -->
          <button class="course-arrow left" @click="prevCourse(rowIndex)">‹</button>

          <!-- 滚动区域 -->
          <div class="course-row-grid-wrapper">
            <div
              class="course-row-grid"
              :style="{ transform: `translateX(-${row.index * (100 / visibleCount)}%)` }"
            >
              <img
                v-for="(img, idx) in row.images"
                :key="idx"
                :src="img"
                class="course-thumb"
              />
            </div>
          </div>

          <!-- 右箭头 -->
          <button class="course-arrow right" @click="nextCourse(rowIndex)">›</button>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { reactive } from "vue";

import bannerImage from "@/assets/mainimage1.png";

// 示例图片，你可以用 props 或自己导入
import ai1 from "@/assets/Land/ai1.png";
import ai2 from "@/assets/Land/ai2.png";
import ai3 from "@/assets/Land/ai3.png";
import ai4 from "@/assets/Land/ai4.png";
import ai5 from "@/assets/Land/ai5.png";
import ai6 from "@/assets/Land/ai6.png";

import mac1 from "@/assets/Land/mac1.png";
import mac2 from "@/assets/Land/mac2.png";
import mac3 from "@/assets/Land/mac3.png";
import mac4 from "@/assets/Land/mac4.png";
import mac5 from "@/assets/Land/mac5.png";
import mac6 from "@/assets/Land/mac6.png";

import deep1 from "@/assets/Land/deep1.png";
import deep2 from "@/assets/Land/deep2.png";
import deep3 from "@/assets/Land/deep3.png";
import deep4 from "@/assets/Land/deep4.png";
import deep5 from "@/assets/Land/deep5.png";
import deep6 from "@/assets/Land/deep6.png";

const courseImagesai = [ai1, ai2, ai3, ai4, ai5, ai6];
const courseImagesmac = [mac1, mac2, mac3, mac4, mac5, mac6];
const courseImagesdeep = [deep1, deep2, deep3, deep4, deep5, deep6];

// 每行显示 4 张图片，实际有 6 张
const visibleCount = 4;

const rows = reactive([
  { title: "AI Agents", images: courseImagesai.slice(0,6), index: 0 },
  { title: "Machine Learning", images: courseImagesmac.slice(0,6), index: 0 },
  { title: "Deep Learning", images: courseImagesdeep.slice(0,6), index: 0 }
]);

function nextCourse(rowIndex) {
  if (rows[rowIndex].index < rows[rowIndex].images.length - visibleCount)
    rows[rowIndex].index++;
}

function prevCourse(rowIndex) {
  if (rows[rowIndex].index > 0) rows[rowIndex].index--;
}
</script>

<style scoped>
.modules-section {
  display:flex;
  padding:30px 6vw;
  gap:20px;
}

/* ⭐ 新增 banner 样式 */
.special-banner {
  width: 100%;
  overflow: hidden; /* 防止放大后溢出 */
  border-radius: 18px;
}

/* 默认状态 */
.banner-img {
  width: 100%;
  height: 280px;
  object-fit: cover;
  transition: 
    transform 0.45s ease,
    filter 0.45s ease,
    box-shadow 0.45s ease;
  transform-origin: center;
}

/* 鼠标悬停时 ONLY 图片生效 */
.banner-img:hover {
  transform: scale(1.01);
  filter: brightness(0.8);
  box-shadow: 0px 10px 30px rgba(0,0,0,0.22);
}


/* 左侧分类栏 */
.category-sidebar {
  width:25%;
  background:#f0f5ff;
  padding:20px;
  border-radius:12px;
  position: sticky;
  top: 80px;
  align-self: flex-start;
}

.category-sidebar h3 { font-size:18px; font-weight:bold; margin-bottom:12px; color:#6488f4; }
.category-sidebar ul { list-style:none; padding:0; margin:0; }
.category-sidebar ul li { padding:8px 0; cursor:pointer; color:#4683f3; transition: color 0.3s; }
.category-sidebar ul li:hover { color:#ff72b9; }

/* 右侧课程区域 */
.course-grid { width:75%; display:flex; flex-direction:column; gap:30px; }

.course-row h2 { font-size:22px; color:#6488f4; font-weight:bold; margin-bottom:12px; }

.course-row-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  gap: 12px; /* 左右箭头和图片之间空隙 */
}

.course-row-grid-wrapper {
  overflow: hidden;
  width: 100%;
}

.course-row-grid {
  display: flex;
  gap: 12px; /* 图片间距 */
  transition: transform 0.35s ease-in-out;
}

.course-thumb {
  flex: 0 0 calc((100% - 12px*3)/4); /* 每行显示4张图片 */
  height: 220px;
  object-fit: contain;
  border-radius: 12px;
  cursor: pointer;
  transition: transform .25s ease, box-shadow .25s ease;
}

.course-thumb:hover {
  transform: scale(1.05);
  box-shadow: 0 6px 18px rgba(51, 135, 214, 0.25);
}

/* 箭头样式 */
.course-arrow {
   background: rgba(51, 162, 214, 0.18); border:none; color:#3e33d6; font-size:28px; width:36px; height:36px; border-radius:50%; cursor:pointer; display:flex; align-items:center; justify-content:center; transition:0.25s ease; opacity:0.5; 
}

.course-arrow.left { margin-right: 6px; }
.course-arrow.right { margin-left: 6px; }

.course-arrow:hover { background: rgba(51, 59, 214, 0.35); transform:scale(1.15); }

/* 响应式 */
@media (max-width:900px){
  .modules-section { flex-direction:column; }
  .category-sidebar { width:100%; position:relative; top:0; margin-bottom:20px; }
  .course-grid { width:100%; }
  .course-thumb { flex: 0 0 calc((100% - 20px)/2); height: 140px; }
}
</style>
