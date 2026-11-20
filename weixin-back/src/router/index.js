import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import AdminLayout from '../layouts/AdminLayout.vue'
import Editor from '../views/Editor.vue'
import Templates from '../views/Templates.vue'
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
        { path: 'editor', component: Editor, meta: { title: '写草稿', subtitle: 'Markdown 排版与公众号预览' } },
        { path: 'templates', component: Templates, meta: { title: '模板库', subtitle: '14 个行业模板，直接套用' } },
        { path: 'articles', component: Articles, meta: { title: '发送记录', subtitle: '查看提交状态与历史正文' } },
        { path: 'guide', component: Guide, meta: { title: '使用说明', subtitle: '从排版到草稿箱的完整流程' } }
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
