import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue')
  },
  {
    path: '/',
    component: () => import('@/layouts/MainLayout.vue'),
    redirect: '/dashboard',
    children: [
      { path: 'dashboard', name: 'Dashboard', component: () => import('@/views/dashboard/index.vue'), meta: { title: '仪表盘' } },
      { path: 'user', name: 'User', component: () => import('@/views/user/index.vue'), meta: { title: '用户管理' } },
      { path: 'song', name: 'Song', component: () => import('@/views/song/index.vue'), meta: { title: '歌曲管理' } },
      { path: 'order', name: 'Order', component: () => import('@/views/order/index.vue'), meta: { title: '订单管理' } },
      { path: 'comment', name: 'Comment', component: () => import('@/views/comment/index.vue'), meta: { title: '留言管理' } }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.path !== '/login' && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router
