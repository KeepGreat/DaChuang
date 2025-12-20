<template>
  <section class="modules-preview">
    <h2>热门课程</h2>
    <div class="course-carousel">
      <button class="course-arrow left" @click="prevCourse">‹</button>
      <div class="course-track-wrapper">
        <div class="course-track" :style="{ transform: `translateX(-${courseIndex * 25}%)` }">
          <img v-for="(img, idx) in courseImages" :key="idx" class="course-thumb" :src="img" @click="go('courses')" />
        </div>
      </div>
      <button class="course-arrow right" @click="nextCourse">›</button>
    </div>
  </section>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';

const props = defineProps({
  courseImages: { type: Array, required: true }
});

const courseIndex = ref(0);
const visibleCount = 5;
function nextCourse() { if (courseIndex.value < props.courseImages.length - visibleCount) courseIndex.value++; }
function prevCourse() { if (courseIndex.value > 0) courseIndex.value--; }

const router = useRouter();
function go(target) {
  if (target === "courses") router.push("/coursesection");
}
</script>

<style scoped>
.modules-preview { padding: 40px 6vw; background: #fffefe85; }
.modules-preview h2 { color:#f464ac; font-size: 22px; font-weight: bold; margin-bottom: 6px; }

.course-carousel { display:flex; align-items:center; width:100%; padding:0 4vw; position:relative; box-sizing:border-box; }

.course-track-wrapper { 
overflow:hidden;
width:100%;
height:290px;
display: flex;
justify-content: center;   /* ⭐ 水平居中 */
align-items: center;       /* 垂直居中 */
     }

.course-track { 
    display:flex;
    gap:18px; 
    transition:
    transform 0.35s ease-in-out;
 }

.course-thumb { 
    flex: 0 0 calc((100% - 18px*4)/5);
    height:260px; object-fit:contain;
    background:white; border-radius:12px; 
    cursor:pointer; transition: transform .25s ease, box-shadow .25s ease; }

.course-thumb:hover {
     transform: scale(1.07); 
     box-shadow:0 6px 18px rgba(214,51,132,0.25); }

.course-arrow { background: rgba(214, 51, 132, 0.18); border:none; color:#d63384; font-size:28px; width:36px; height:36px; border-radius:50%; cursor:pointer; display:flex; align-items:center; justify-content:center; transition:0.25s ease; opacity:0.5; }
.course-carousel:hover .course-arrow { opacity:1; }
.course-arrow:hover { background: rgba(214,51,132,0.35); transform:scale(1.15); }
.course-arrow.left { margin-right:12px; }
.course-arrow.right { margin-left:12px; }
</style>
