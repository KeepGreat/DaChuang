<template>
  <div class="landing-page">
    <TopNav />
    <HeroSection />
    <HotCoursesSection :course-images="courseImages" />
    <CourseSection :course-images="courseImages" />
    <FooterSection />
  </div>
</template>

<script setup>
import TopNav from '@/components/homepage/TopNav.vue';
import HeroSection from '@/components/homepage/HeroSection.vue';
import HotCoursesSection from '@/components/homepage/HotCoursesSection.vue';
import CourseSection from '@/components/homepage/CourseSection.vue';
import FooterSection from '@/components/homepage/FooterSection.vue';

import c1 from '@/assets/homepage/top rated1.png';
import c2 from '@/assets/homepage/top rated2.png';
import c3 from '@/assets/homepage/top rated3.png';
import c4 from '@/assets/homepage/top rated4.png';
import c5 from '@/assets/homepage/top rated5.png';
import c6 from '@/assets/homepage/top rated6.png';

const courseImages = [c1, c2, c3, c4, c5, c6];
</script>


<style scoped>
.landing-page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  font-family: "Inter", Arial, sans-serif;
  background: #f8f6ff;
}
</style>
