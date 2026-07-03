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
      { path: 'home', name: 'Home', component: () => import('@/views/home/index.vue'), meta: { title: '首页管理' } },
      { path: 'user', name: 'User', component: () => import('@/views/user/index.vue'), meta: { title: '用户管理' } },
      { path: 'song', name: 'Song', component: () => import('@/views/song/index.vue'), meta: { title: '歌曲管理' } },
      { path: 'order', name: 'Order', component: () => import('@/views/order/index.vue'), meta: { title: '订单管理' } },
      { path: 'leyuComment', name: 'LeyuComment', component: () => import('@/views/leyuComment/index.vue'), meta: { title: '乐语留言管理' } },
      { path: 'songComment', name: 'SongComment', component: () => import('@/views/songComment/index.vue'), meta: { title: '歌曲留言管理' } }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  console.log('路由跳转:', to.path, 'Token存在:', !!token)
  if (to.path !== '/login' && !token) {
    console.log('未登录，跳转到登录页')
    next('/login')
  } else {
    next()
  }
})

export default router
