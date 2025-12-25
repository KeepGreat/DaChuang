import { useUserStore } from "@/store";
import { ElMessage } from "element-plus";
import { createRouter, createWebHashHistory } from "vue-router";

// 主界面路由
const mainRoutes = [
  {
    path: "/",
    name: "MainLanding",
    component: () => import("@/components/MainLanding.vue"),
  },
];

// Profiling 页面路由
const profilingRoutes = [
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
    component: () => import("@/components/profiling/image/CourseDetail.vue"),
    props: true,
  },
  {
    path: "CoursePlaceholder",
    name: "CoursePlaceholder",
    component: () =>
      import("@/components/profiling/image/CoursePlaceholder.vue"),
  },
];

// Teaching 页面路由
const teachingRoutes = [
  {
    path: "coursesection",
    name: "CourseSection",
    component: () => import("@/view/teaching/CourseSection.vue"),
  },
  {
    path: "teaching/course/:id",
    name: "TeachingCourse",
    component: () => import("@/view/teaching/Course.vue"),
    children: [
      {
        path: "learn",
        name: "CourseList",
        component: () => import("@/view/teaching/CourseList.vue"),
      },
      {
        path: "learn/series/:seriesId",
        name: "CourseSeriesDetail",
        component: () => import("@/view/teaching/CourseSeriesDetail.vue"),
      },
      // {
      //   path: "task",
      //   name: "TaskList",
      //   component: () => import("@/view/teaching/TaskList.vue"),
      // },
      // {
      //   path: "task/:taskId",
      //   name: "TaskDetail",
      //   component: () => import("@/view/teaching/TaskDetail.vue"),
      // },
      {
        path: "practice",
        name: "PracticeList",
        component: () => import("@/view/teaching/PracticeList.vue"),
      },
      {
        path: "exam",
        name: "ExamList",
        component: () => import("@/view/teaching/ExamList.vue"),
      },
      {
        path: "exam/:examId",
        name: "ExamDetail",
        component: () => import("@/view/teaching/ExamDetail.vue"),
      },
      // {
      //   path: "resource",
      //   name: "ResourceList",
      //   component: () => import("@/view/teaching/ResourceList.vue"),
      // },
      // {
      //   path: "resource/:resourceId",
      //   name: "ResourceDetail",
      //   component: () => import("@/view/teaching/ResourceDetail.vue"),
      // },
      // {
      //   path: "discussion",
      //   name: "DiscussionList",
      //   component: () => import("@/view/teaching/DiscussionList.vue"),
      // },
      // {
      //   path: "discussion/:discussionId",
      //   name: "DiscussionDetail",
      //   component: () => import("@/view/teaching/DiscussionDetail.vue"),
      // },
      {
        path: "knowledge",
        name: "KnowledgeGraph",
        component: () => import("@/view/teaching/KnowledgeGraph.vue"),
      },
    ],
  },
  // {
  //   path: "teach",
  //   name: "Teaching",
  //   component: () => import("@/view/teaching/Teaching.vue"),
  // },
  // {
  //   path: "teachindex",
  //   name: "TeachingIndex",
  //   component: () => import("@/view/teaching/TeachingIndex.vue"),
  // },
];

// Practice 页面路由
const practiceRoutes = [
  {
    path: "practiceIndex",
    name: "PracticeIndex",
    component: () => import("@/view/PracticeIndex.vue"),
  },
];

// 其他页面路由
const otherRoutes = [
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
];

// 独立路由 - 不使用主布局
const independentRoutes = [
  {
    path: "/practice/:courseSectionId/:practiceId",
    name: "Practice",
    component: () => import("@/view/practice/Practice.vue"),
  },
  {
    path: "/ai-companion",
    name: "AICompanion",
    component: () => import("@/view/ai/AICompanion.vue"),
  },
];

// 认证相关路由
const authRoutes = [
  {
    path: "/login",
    name: "Login",
    component: () => import("@/view/auth/Login.vue"),
  },
  {
    path: "/register",
    name: "register",
    component: () => import("@/view/auth/Register.vue"),
  },
];

// 定义路由配置
const routes = [
  ...mainRoutes,
  {
    path: "/",
    component: () => import("@/components/Layout.vue"),
    children: [...profilingRoutes, ...teachingRoutes, ...practiceRoutes, ...otherRoutes],
  },
  ...independentRoutes,
  ...authRoutes,
];

// 创建路由实例
const router = createRouter({
  history: createWebHashHistory(),
  routes,
});

// 路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore();
  const isLoggedIn = userStore.isLoggedIn;
  // 公开路由，无需登录即可访问
  const publicRoutes = ["/login", "/register"];

  if (isLoggedIn) {
    // 已登录用户访问公开路由时，重定向到首页
    if (publicRoutes.includes(to.path)) {
      ElMessage.warning("您已登录，跳转到首页");
      next("/");
    }
    // 已登录用户访问其他页面，允许通行
    else {
      next();
    }
  } else {
    // 未登录用户访问登录/注册页，允许通行
    if (publicRoutes.includes(to.path)) {
      next();
    }
    // 未登录用户访问其他页面，重定向到登录页
    else {
      ElMessage.warning("您还未登录，请先登录后再访问");
      next("/login");
    }
  }
});

export default router;
