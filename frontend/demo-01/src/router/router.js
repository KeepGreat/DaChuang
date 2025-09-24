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
            path: '/pracindex',
            component: () => import('../components/PracticeIndex.vue'),
        },{
            path: '/teachindex',
            component: () => import('../components/TeachingIndex.vue'),
        },{
            path: '/exp',
            component: () => import('../components/Experiment.vue'),
        },{
            path: '/teach',
            component: () => import('../components/Teaching.vue'),
        }]
    }]
})

export default router;