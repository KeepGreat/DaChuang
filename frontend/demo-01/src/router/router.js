import { createRouter, createWebHashHistory } from 'vue-router'

const router = createRouter({
    history: createWebHashHistory(),
    routes: [{
        path: '/home',
        component: () => import('../components/Home.vue'),
    },{
        path: '/login',
        component: () => import('../components/Login.vue'),
    },{
        path: '/',
        component: () => import('../components/Home.vue'),
        children: [{
            path: '/prac',
            component: () => import('../components/Practice.vue'),
        },{
            path: '/profile',
            component: () => import('../components/profiling/Profile.vue'),
        },{
            path: '/pracindex',
            component: () => import('../components/PracticeIndex.vue'),
        },{
            path: '/teachindex',
            component: () => import('../components/teaching/TeachingIndex.vue'),
        },{
            path: '/exp',
            component: () => import('../components/Experiment.vue'),
        },{
            path: '/teach',
            component: () => import('../components/teaching/Teaching.vue'),
        },{
            path: '/filetest',
            component: () => import('../components/FileTest.vue'),
        },{
            path: '/coursesection',
            component: () => import('../components/teaching/CourseSection.vue'),
        },{
            path: '/course',
            component: () => import('../components/teaching/Course.vue'),
        }]
    }]
})

export default router;