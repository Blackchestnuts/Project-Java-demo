import { createRouter, createWebHistory } from 'vue-router'
import TaskView from '../views/TaskView.vue'
import LoginView from '../views/LoginView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'tasks',
      component: TaskView,
      meta: { title: '员工任务管理系统' } // 设置主页标题
    },
    {
      path: '/login',
      name: 'login',
      component: LoginView,
      meta: { title: '员工任务管理系统 - 登录' } // 设置登录页标题
    }
  ]
})

// 路由守卫：登录验证 + 动态标题设置
router.beforeEach((to, from, next) => {
  // 1. 动态修改页面标题
  if (to.meta.title) {
    document.title = to.meta.title
  } else {
    document.title = '员工任务管理系统' // 默认标题
  }

  // 2. 登录验证逻辑
  const user = localStorage.getItem('user')
  if (!user && to.path !== '/login') {
    next('/login')
  } else {
    next()
  }
})

export default router