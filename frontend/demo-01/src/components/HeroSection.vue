<template>
  <main class="hero">
    <div class="hero-inner">
      <h1>从入门到实践 — 一站式 AI 学习平台</h1>
      <p class="lead">高质量课程、实战项目与个性化学习路径，帮你把 AI 知识落地。</p>
      <div class="cta-row">
        <button class="btn-primary" @click="go('courses')">开始学习</button>
        <button class="btn-outline" @click="go('practice')">立即练习</button>
      </div>
    </div>

    <!-- Hero轮播 -->
    <div class="carousel-wrapper" @mouseenter="pause = true" @mouseleave="pause = false">
      <div class="carousel-track" :style="{ transform: `translateX(-${currentIndex * 100}%)` }">
        <div class="carousel-slide" v-for="(img, idx) in images" :key="idx">
          <img :src="img" alt="轮播图片">
        </div>
      </div>
      <button class="arrow left" @click="prev">❮</button>
      <button class="arrow right" @click="next">❯</button>
    </div>

    <!-- 圆点 -->
    <div class="progress-dots">
      <span v-for="(img, idx) in images" :key="idx"
            :class="['dot', { active: idx === currentIndex }]"
            @click="goTo(idx)" />
    </div>
  </main>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import { useRouter } from 'vue-router';
import image1 from '@/assets/mainimage1.png';
import image2 from '@/assets/mainimage2.png';

const router = useRouter();
function go(target) {
  if (target === "courses") router.push("/coursesection");
  else if (target === "practice") router.push("/pracindex");
}

const images = [image1, image2];
const currentIndex = ref(0);
const pause = ref(false);
let interval = null;

function next() {
  currentIndex.value = (currentIndex.value + 1) % images.length;
}
function prev() {
  currentIndex.value = (currentIndex.value - 1 + images.length) % images.length;
}
function goTo(idx) {
  currentIndex.value = idx;
}

onMounted(() => {
  interval = setInterval(() => {
    if (!pause.value) next();
  }, 3000);
});
onUnmounted(() => clearInterval(interval));
</script>

<style scoped>
.hero { padding:56px 6vw; text-align:center; position: relative; }
.hero-inner h1 { font-size:40px; color:#d63384; margin-bottom:12px; font-weight:800; }
.hero-inner .lead { font-size:18px; color:#6b3b52; margin-bottom:20px; }
.cta-row { display:flex; gap:14px; justify-content:center; margin-bottom:26px; }
.btn-primary { background: linear-gradient(90deg,#ff7ab1,#d63384); color:white; padding:12px 20px; border-radius:12px; border:0; cursor:pointer; font-weight:700; }
.btn-outline { background:transparent; border:1px solid #ffd6e7; padding:10px 18px; border-radius:12px; color:#d63384; cursor:pointer; font-weight:700; }

.carousel-wrapper { position:relative; width:100%; max-width:1000px; margin:36px auto; overflow:hidden; border-radius:14px; }
.carousel-track { display:flex; transition: transform 0.5s ease; }
.carousel-slide { flex-shrink:0; width:100%; }
.carousel-slide img { width:100%; height:100%; object-fit:contain; display:block; background:#fff6fb; }

.arrow {
  background:rgba(0,0,0,0.35); border:none; color:white; font-size:28px;
  width:40px; height:40px; border-radius:50%; cursor:pointer;
  display:flex; align-items:center; justify-content:center;
  position:absolute; top:50%; transform:translateY(-50%);
  transition:0.25s ease; opacity:0; pointer-events:none;
}
.arrow.left { left:12px; }
.arrow.right { right:12px; }
.carousel-wrapper:hover .arrow { opacity:1; pointer-events:auto; }
.arrow:hover { transform:translateY(-50%) scale(1.15); background:rgba(0,0,0,0.55); }

.progress-dots { margin-top: 12px; display: flex; justify-content: center; gap: 10px; }
.progress-dots .dot { width:10px; height:10px; background:#bababa80; border-radius:999px; cursor:pointer; transition: all .3s ease; }
.progress-dots .dot.active { width:32px; background:rgb(71, 71, 71); }
</style>
