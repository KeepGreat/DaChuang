// src/router/index.js
import { createRouter, createWebHashHistory } from 'vue-router';
import MainLanding from '../components/MainLanding.vue';
import Layout from '../components/Layout.vue';

const router = createRouter({
  history: createWebHashHistory(),
  routes: [
    // 主界面，没有左侧栏
    { path: '/', name: 'MainLanding', component: MainLanding },

    // 所有有左侧栏的页面都用 Layout 包裹
    {
      path: '/',
      component: Layout,
      children: [
        // Profiling 页面
        { path: 'profile', name: 'Profile', component: () => import('../components/profiling/ProfilePage.vue') },
        { path: 'LearningTime', name: 'LearningTime', component: () => import('../components/profiling/LearingTime.vue') },
        { path: 'course/:id', name: 'CourseView', component: () => import('../components/profiling/image/CourseDetail.vue'), props: true },
        { path: 'CoursePlaceholder', name: 'CoursePlaceholder', component: () => import('../components/profiling/image/CoursePlaceholder.vue') },

        // Teaching 页面
        { path: 'coursesection', name: 'CourseSection', component: () => import('../components/teaching/CourseSection.vue') },
        { path: 'course', name: 'Course', component: () => import('../components/teaching/Course.vue') },
        { path: 'teach', name: 'Teaching', component: () => import('../components/teaching/Teaching.vue') },
        { path: 'teachindex', name: 'TeachingIndex', component: () => import('../components/teaching/TeachingIndex.vue') },

        // Practice 页面
        { path: 'pracindex', name: 'PracticeIndex', component: () => import('../components/PracticeIndex.vue') },
        { path: 'prac', name: 'Practice', component: () => import('../components/Practice.vue') },

        // 其他页面
        { path: 'exp', name: 'Experiment', component: () => import('../components/Experiment.vue') },
        { path: 'filetest', name: 'FileTest', component: () => import('../components/FileTest.vue') },
      ],
    },

    // 登录页面
    { path: '/login', name: 'Login', component: () => import('../components/Login.vue') },
  ],
});

export default router;
