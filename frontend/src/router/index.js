import { createRouter, createWebHistory } from 'vue-router'
import TaskView from '../views/TaskView.vue' // 引入即将创建的页面

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: TaskView // 访问根路径时显示任务页面
    }
  ]
})

export default router