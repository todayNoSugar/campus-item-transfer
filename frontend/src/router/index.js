import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../store/user'

const routes = [
  { path: '/login', name: 'login', component: () => import('../views/Login.vue') },
  {
    path: '/',
    component: () => import('../views/Layout.vue'),
    children: [
      { path: '', name: 'home', component: () => import('../views/Home.vue') },
      { path: 'goods/:id', name: 'goodsDetail', component: () => import('../views/GoodsDetail.vue') },
      { path: 'publish', name: 'publish', component: () => import('../views/Publish.vue'), meta: { auth: true } },
      { path: 'my/goods', name: 'myGoods', component: () => import('../views/MyGoods.vue'), meta: { auth: true } },
      { path: 'my/reserve', name: 'myReserve', component: () => import('../views/MyReserve.vue'), meta: { auth: true } },
      { path: 'my/favorite', name: 'favorites', component: () => import('../views/Favorites.vue'), meta: { auth: true } },
      { path: 'profile', name: 'profile', component: () => import('../views/Profile.vue'), meta: { auth: true } },
      { path: 'admin/users', name: 'adminUsers', component: () => import('../views/admin/Users.vue'), meta: { auth: true, admin: true } },
      { path: 'admin/goods', name: 'adminGoods', component: () => import('../views/admin/GoodsManage.vue'), meta: { auth: true, admin: true } },
      { path: 'admin/messages', name: 'adminMessages', component: () => import('../views/admin/Messages.vue'), meta: { auth: true, admin: true } },
      { path: 'admin/stats', name: 'adminStats', component: () => import('../views/admin/Stats.vue'), meta: { auth: true, admin: true } }
    ]
  },
  { path: '/:pathMatch(.*)*', redirect: '/' }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫：登录校验 + 管理员校验
router.beforeEach((to, from, next) => {
  const store = useUserStore()
  if (to.meta.auth && !store.isLogin) {
    next({ path: '/login', query: { redirect: to.fullPath } })
  } else if (to.meta.admin && !store.isAdmin) {
    next('/')
  } else {
    next()
  }
})

export default router
