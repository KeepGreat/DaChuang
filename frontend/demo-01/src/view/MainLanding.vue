<template>
  <div class="landing-page">
    <!-- Hero -->
    <main class="hero">
      <div class="hero-inner">
        <h1>从入门到实践 — 一站式 AI 学习平台</h1>
        <p class="lead">高质量课程、实战项目与个性化学习路径，帮你把 AI 知识落地。</p>
        <div class="cta-row">
          <button class="btn-primary" @click="go('courses')">开始学习</button>
          <button class="btn-outline" @click="go('practice')">立即练习</button>
        </div>
      </div>

      <!-- 轮播 -->
      <div class="carousel-wrapper" @mouseenter="pause = true" @mouseleave="pause = false">
        <div class="carousel-track" :style="{ transform: `translateX(-${currentIndex * 100}%)` }">
          <div class="carousel-slide" v-for="(img, idx) in images" :key="idx">
            <img :src="img" alt="课程图片">
          </div>
        </div>

        <!-- 左右箭头 -->
        <button class="arrow left" @click="prev">&#10094;</button>
        <button class="arrow right" @click="next">&#10095;</button>

        <!-- 圆点 -->
        <div class="dots">
          <span v-for="(img, idx) in images" :key="idx" :class="{active: idx === currentIndex}" @click="goTo(idx)"></span>
        </div>
      </div>
    </main>

    <!-- 热门课程 -->
    <section class="modules-preview">
      <h2>热门课程</h2>
      <div class="modules-grid">
        <div class="module-card" v-for="n in 6" :key="n" @click="go('courses')">
          <div class="m-thumb">📚</div>
          <div class="m-title">AI 课程 {{ n }}</div>
          <div class="m-sub">进度 · 0%</div>
        </div>
      </div>
    </section>

    <!-- Footer -->
    <footer class="footer">
      <div>© {{ (new Date()).getFullYear() }} 慧编未来 · AI 学习平台</div>
      <div class="footer-links">
        <a @click.prevent="go('community')">社区</a>
        <a @click.prevent="go('profile')">关于我们</a>
      </div>
    </footer>
  </div>
</template>

<script setup>
import TopNav from '@/components/TopNav.vue';
import { ref, onMounted, onUnmounted } from 'vue';
import { useRouter } from 'vue-router';
import image1 from '@/assets/mainimage1.png';
import image2 from '@/assets/mainimage2.png';


const router = useRouter();
function go(target) {
  if (target === "profile") router.push("/profile");
  else if (target === "courses") router.push("/coursesection");
  else if (target === "practice") router.push("/pracindex");
  else if (target === "community") router.push("/community");
}
function goHome() {
  router.push({ name: "MainLanding" });
}

// 轮播逻辑
const images = [image1, image2];
const currentIndex = ref(0);
const pause = ref(false);
let interval = null;

onMounted(() => {
  interval = setInterval(() => {
    if (!pause.value) next();
  }, 3000);
});

onUnmounted(() => clearInterval(interval));

function next() {
  currentIndex.value = (currentIndex.value + 1) % images.length;
}
function prev() {
  currentIndex.value = (currentIndex.value - 1 + images.length) % images.length;
}
function goTo(idx) {
  currentIndex.value = idx;
}
</script>

<style scoped>
/* --- 页面整体 --- */
.landing-page { min-height:100vh; display:flex; flex-direction:column; font-family:"Inter", Arial, sans-serif; background: #fff6fb; }
.topbar { height:64px; display:flex; justify-content:space-between; align-items:center; padding:0 36px; background:rgba(255,255,255,0.9); border-bottom:1px solid #f5dbe7; position:sticky; top:0; z-index:10; }
.brand { display:flex; align-items:center; gap:12px; cursor:pointer; }
.logo-dot { width:42px; height:42px; border-radius:10px; background:linear-gradient(135deg,#ffd6e8,#ffb6d9); display:flex; align-items:center; justify-content:center; font-weight:700; color:#9b1f54; box-shadow:0 6px 18px rgba(214,51,132,0.08); }
.brand-name { font-weight:700; color:#d63384; font-size:18px; }
.nav { display:flex; align-items:center; gap:12px; }
.nav-link { background:transparent; border:0; padding:8px 12px; border-radius:8px; cursor:pointer; font-weight:600; color:#a43b69; transition:all .18s ease; }
.nav-link:hover { background: rgba(214,51,132,0.08); color:#d63384; transform:translateY(-2px); }
.nav-ghost { display:flex; align-items:center; gap:8px; padding:6px 10px; border-radius:10px; background:linear-gradient(180deg,#fff,#fff0f4); border:1px solid #ffd6e7; cursor:pointer; transition:all .18s ease; }
.nav-ghost:hover { transform:translateY(-4px); box-shadow:0 6px 18px rgba(214,51,132,0.08); }

/* Hero */
.hero { padding:56px 6vw; }
.hero-inner { max-width:1100px; margin:0 auto; text-align:center; }
.hero h1 { font-size:40px; color:#d63384; margin:0 0 12px; font-weight:800; }
.hero .lead { color:#6b3b52; font-size:18px; margin-bottom:20px; }
.cta-row { display:flex; gap:14px; justify-content:center; margin-bottom:26px; }
.btn-primary { background: linear-gradient(90deg,#ff7ab1,#d63384); color:white; padding:12px 20px; border-radius:12px; border:0; cursor:pointer; font-weight:700; box-shadow:0 8px 24px rgba(214,51,132,0.12); transition: transform .16s ease; }
.btn-primary:hover { transform:translateY(-4px); }
.btn-outline { background:transparent; border:1px solid #ffd6e7; padding:10px 18px; border-radius:12px; color:#d63384; cursor:pointer; font-weight:700; }
.btn-outline:hover { background:#fff0f6; transform:translateY(-3px); }

/* 轮播 */
.carousel-wrapper { 
  position: relative; 
  width: 100%; 
  max-width: 1000px; 
  max-height: 350px; /* 最大高度 */
  height: auto;      /* 高度随宽度按比例缩放 */
  aspect-ratio: 16/9;       /* 保持宽高比，可自定义 */
  margin: 36px auto; 
  overflow: hidden; 
  border-radius: 14px; 
  box-shadow: 0 8px 20px rgba(0,0,0,0.06); 
}
.carousel-track { display:flex; transition: transform 0.5s ease; height:100%; }
.carousel-slide { flex-shrink:0; width:100%; height:100%; }
.carousel-slide img { 
  width: 100%; 
  height: 100%; 
  object-fit: contain;      /* 完整显示图片，不裁剪 */
  display: block; 
  background: #fff6fb;      /* 避免图片与容器背景不一致 */
}
/* 箭头 */
.arrow { position:absolute; top:50%; transform:translateY(-50%); background:rgba(0,0,0,0.3); color:white; border:none; font-size:24px; width:40px; height:40px; border-radius:50%; cursor:pointer; display:none; justify-content:center; align-items:center; }
.carousel-wrapper:hover .arrow { display:flex; }
.arrow.left { left:12px; }
.arrow.right { right:12px; }

/* 圆点 */
.dots { position:absolute; bottom:12px; left:50%; transform:translateX(-50%); display:flex; gap:8px; }
.dots span { width:10px; height:10px; border-radius:50%; background:#ddd; cursor:pointer; transition: all .2s; }
.dots span.active { background:#d63384; }

/* 模块预览 */
.modules-preview { padding:30px 6vw; }
.modules-preview h2 { margin:0 0 16px; color:#d63384; }
.modules-grid { display:grid; grid-template-columns:repeat(auto-fit,minmax(160px,1fr)); gap:16px; max-width:1100px; margin:0 auto; }
.module-card { background:#fff; padding:16px; border-radius:12px; box-shadow:0 6px 18px rgba(0,0,0,0.04); cursor:pointer; display:flex; flex-direction:column; align-items:center; gap:10px; }
.module-card:hover { transform:translateY(-6px); box-shadow:0 12px 28px rgba(214,51,132,0.08); }
.m-thumb { font-size:28px; }
.m-title { font-weight:700; color:#7b2a53; }
.m-sub { color:#9b7a88; font-size:13px; }

/* Footer */
.footer { margin-top:auto; padding:28px 6vw; display:flex; justify-content:space-between; align-items:center; color:#8b6a76; }
.footer a { color:#d63384; cursor:pointer; }

/* 响应式 */
@media(max-width:900px){
  .hero h1 { font-size:28px; }
  .hero .lead { font-size:15px; }
  .nav-text { display:none; }
  .topbar { padding:0 14px; }
  .hero { padding:32px 4vw; }
  .carousel-wrapper { height:240px; }
}
</style>
