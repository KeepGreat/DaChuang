import { createRouter, createWebHashHistory } from "vue-router";

const router = createRouter({
  history: createWebHashHistory(),
  routes: [
    {
      path: "/home",
      component: () => import("@/view/Home.vue"),
    },
    {
      path: "/login",
      component: () => import("@/view/Login.vue"),
    },
    {
      path: "/",
      component: () => import("@/view/Home.vue"),
      children: [
        {
          path: "/prac",
          component: () => import("@/view/Practice.vue"),
        },
        {
          path: "/pracindex",
          component: () => import("@/view/PracticeIndex.vue"),
        },
        {
          path: "/teachindex",
          component: () => import("@/view/teaching/TeachingIndex.vue"),
        },
        {
          path: "/exp",
          component: () => import("@/view/Experiment.vue"),
        },
        {
          path: "/teach",
          component: () => import("@/view/teaching/Teaching.vue"),
        },
        {
          path: "/filetest",
          component: () => import("@/view/FileTest.vue"),
        },
        {
          path: "/coursesection",
          component: () => import("@/view/teaching/CourseSection.vue"),
        },
        {
          path: "/course",
          component: () => import("@/view/teaching/Course.vue"),
        },
      ],
    },
  ],
});

export default router;
