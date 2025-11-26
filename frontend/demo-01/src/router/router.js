import { createRouter, createWebHashHistory } from "vue-router";

const router = createRouter({
  history: createWebHashHistory(),
  routes: [
    // 主界面，没有左侧栏
    /*{ path: "/", name: "MainLanding", component: () => import("@/components/MainLanding.vue") },*/

    // 所有有左侧栏的页面都用 Layout 包裹
    {
      path: "/",
      component: () => import("@/layout/main/index.vue"),
      children: [
        // Profiling 页面
        {
          path: "/",
          name: "MainLanding",
          component: () => import("@/view/MainLanding.vue"),
        },
        {
          path: "profile",
          name: "Profile",
          component: () => import("@/components/profiling/ProfilePage.vue"),
        },
        {
          path: "LearningTime",
          name: "LearningTime",
          component: () => import("@/components/profiling/LearingTime.vue"),
        },
        {
          path: "course/:id",
          name: "CourseView",
          component: () =>
            import("@/components/profiling/image/CourseDetail.vue"),
          props: true,
        },
        {
          path: "CoursePlaceholder",
          name: "CoursePlaceholder",
          component: () =>
            import("@/components/profiling/image/CoursePlaceholder.vue"),
        },

        // Teaching 页面
        {
          path: "coursesection",
          name: "CourseSection",
          component: () => import("@/view/teaching/CourseSection.vue"),
        },
        {
          path: "courselist/:id",
          name: "Course",
          component: () => import("@/view/teaching/Course.vue"),
        },
        {
          path: "teach",
          name: "Teaching",
          component: () => import("@/view/teaching/Teaching.vue"),
        },
        {
          path: "teachindex",
          name: "TeachingIndex",
          component: () => import("@/view/teaching/TeachingIndex.vue"),
        },

        // Practice 页面
        {
          path: "pracindex",
          name: "Pracindex",
          component: () => import("@/view/PracticeIndex.vue"),
        },
        {
          path: "prac",
          name: "Practice",
          component: () => import("@/view/Practice.vue"),
        },

        // 其他页面
        {
          path: "exp",
          name: "Experiment",
          component: () => import("@/view/Experiment.vue"),
        },
        {
          path: "filetest",
          name: "FileTest",
          component: () => import("@/view/FileTest.vue"),
        },
      ],
    },

    {
      path: "/courses/:id",
      props: true,
      component: () => import("@/layout/course/index.vue"),
      children: [
        {
          path: "/",
          name: "t",
          component: () => import("@/view/teaching/Teaching.vue"),
        },
        {
          path: "pracindex",
          name: "PracticeIndex",
          component: () => import("@/view/practice/PracticeIndex.vue")
        }
      ]
    },

    // 登录页面
    { path: "/login", name: "Login", component: () => import("@/view/Login/Login.vue") },
    { path: "/register", name: "register", component: () => import("@/view/Login/Register.vue") },
  ],
});

export default router;
