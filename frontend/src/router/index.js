import { createRouter, createWebHistory } from 'vue-router'
import TaskView from '../views/TaskView.vue'
import LoginView from '../views/LoginView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'tasks',
      component: TaskView
    },
    {
      path: '/login',
      name: 'login',
      component: LoginView
    }
  ]
})

// 简单的路由守卫：未登录只能去登录页
router.beforeEach((to, from, next) => {
  const user = localStorage.getItem('user')
  if (!user && to.path !== '/login') {
    next('/login')
  } else {
    next()
  }
})

export default router