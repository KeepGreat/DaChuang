<template>
  <div class="landing-page">
    
    <HeroSection />
    <HotCoursesSection :course-images="courseImages" />
    <CourseSection :course-images="courseImages" />
    <FooterSection />
  </div>
</template>

<script setup>
import TopNav from './TopNav.vue'; 
import HeroSection from './HeroSection.vue';
import HotCoursesSection from './HotCoursesSection.vue';
import CourseSection from './CourseSection.vue';
import FooterSection from './FooterSection.vue';

import c1 from '@/assets/top rated1.png';
import c2 from '@/assets/top rated2.png';
import c3 from '@/assets/top rated3.png';
import c4 from '@/assets/top rated4.png';
import c5 from '@/assets/top rated5.png';
import c6 from '@/assets/top rated6.png';

const courseImages = [c1, c2, c3, c4, c5, c6];
</script>


<style scoped>
.landing-page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  font-family: "Inter", Arial, sans-serif;
  background: #fff6fb;
}
</style>
