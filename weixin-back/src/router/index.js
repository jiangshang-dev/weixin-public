import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import Editor from '../views/Editor.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/login', component: Login },
    { path: '/', component: Editor }
  ]
})

router.beforeEach((to) => {
  if (to.path !== '/login' && !localStorage.getItem('weixin-token')) {
    return '/login'
  }
})

export default router
