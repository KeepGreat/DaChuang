import { createRouter, createWebHashHistory } from "vue-router";
import MainLanding from '../components/MainLanding.vue';
import Layout from '../components/Layout.vue';
const router = createRouter({
  history: createWebHashHistory(),
  routes: [
    // 主界面，没有左侧栏
    /*{ path: "/", name: "MainLanding", component: () => import("@/components/MainLanding.vue") },*/
{ path: '/', name: 'MainLanding', component: MainLanding },
    // 所有有左侧栏的页面都用 Layout 包裹
    {
      path: "/",
      component: Layout,
      children: [
         // Profiling 页面
        {
          path: 'profile',
          name: 'Profile',
          component: () => import('../components/profiling/ProfilePage.vue')
        },
        {
          path: 'LearningTime',
          name: 'LearningTime',
          component: () => import('../components/profiling/LearingTime.vue')
        },
        {
          path: 'course/:id',
          name: 'CourseView',
          component: () => import('../components/profiling/image/CourseDetail.vue'), props: true
        },
        {
          path: 'CoursePlaceholder',
          name: 'CoursePlaceholder',
          component: () => import('../components/profiling/image/CoursePlaceholder.vue')
        },

        // Teaching 页面
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
              name: "CourseContent",
              component: () => import("@/view/teaching/CourseContent.vue"),
            },
            {
              path: "task",
              name: "TaskList",
              component: () => import("@/view/teaching/TaskList.vue"),
            },
            {
              path: "task/:taskId",
              name: "TaskDetail",
              component: () => import("@/view/teaching/TaskDetail.vue"),
            },
            {
              path: "practice",
              name: "PracticeList",
              component: () => import("@/view/teaching/PracticeList.vue"),
            },
            {
              path: "practice/:practiceId",
              name: "PracticeDetail",
              component: () => import("@/view/practice/Practice.vue"),
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
            {
              path: "resource",
              name: "ResourceList",
              component: () => import("@/view/teaching/ResourceList.vue"),
            },
            {
              path: "resource/:resourceId",
              name: "ResourceDetail",
              component: () => import("@/view/teaching/ResourceDetail.vue"),
            },
            {
              path: "discussion",
              name: "DiscussionList",
              component: () => import("@/view/teaching/DiscussionList.vue"),
            },
            {
              path: "discussion/:discussionId",
              name: "DiscussionDetail",
              component: () => import("@/view/teaching/DiscussionDetail.vue"),
            },
            {
              path: "knowledge",
              name: "KnowledgeGraph",
              component: () => import("@/view/teaching/KnowledgeGraph.vue"),
            },
          ],
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
          component: () => import("@/view/practice/Practice.vue"),
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
      path: "/courses/:id/practice/:pracId",
      name: "Practices",
      props: true,
      component: () => import("@/view/practice/Practice.vue"),
    },

    {
      path: "/courses/:id",
      props: true,
      component: () => import("@/layout/course/index.vue"),
      children: [
        {
          path: "",
          name: "t",
          component: () => import("@/view/teaching/Teaching.vue"),
        },
        {
          path: "pracindex",
          name: "PracticeIndex",
          props: true,
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
