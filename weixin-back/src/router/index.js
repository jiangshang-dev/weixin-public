import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import AdminLayout from '../layouts/AdminLayout.vue'
import Editor from '../views/Editor.vue'
import Articles from '../views/Articles.vue'
import Guide from '../views/Guide.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/login', component: Login },
    {
      path: '/',
      component: AdminLayout,
      children: [
        { path: '', redirect: '/editor' },
        { path: 'editor', component: Editor },
        { path: 'articles', component: Articles },
        { path: 'guide', component: Guide }
      ]
    }
  ]
})

router.beforeEach((to) => {
  if (to.path !== '/login' && !localStorage.getItem('weixin-token')) {
    return '/login'
  }
})

export default router
