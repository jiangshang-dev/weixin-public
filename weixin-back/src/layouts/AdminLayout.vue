<template>
  <a-layout class="admin-shell">
    <a-layout-sider theme="dark" width="200" :style="{ background: '#1c2b27' }">
      <div class="brand-side">公众号工作台</div>
      <a-menu theme="dark" mode="inline" :selected-keys="[selected]" :style="{ background: '#1c2b27' }">
        <a-menu-item key="/editor"><router-link to="/editor">写草稿</router-link></a-menu-item>
        <a-menu-item key="/articles"><router-link to="/articles">发送记录</router-link></a-menu-item>
        <a-menu-item key="/guide"><router-link to="/guide">使用说明</router-link></a-menu-item>
      </a-menu>
    </a-layout-sider>
    <a-layout>
      <a-layout-header class="top">
        <span>{{ me.realName || me.username }} · {{ me.ip }}</span>
        <a-button type="link" @click="logout">退出</a-button>
      </a-layout-header>
      <a-layout-content class="content">
        <router-view />
      </a-layout-content>
    </a-layout>
  </a-layout>
</template>

<script setup>
import { computed, onMounted, reactive } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import http from '../api/http'

const route = useRoute()
const router = useRouter()
const me = reactive({ username: '', realName: '', ip: '' })
const selected = computed(() => `/${route.path.split('/')[1] || 'editor'}`)

onMounted(async () => {
  Object.assign(me, await http.get('/api/admin/me'))
})

function logout() {
  localStorage.removeItem('weixin-token')
  router.push('/login')
}
</script>

<style scoped>
.admin-shell { min-height: 100vh; }
.brand-side { height: 56px; display: flex; align-items: center; padding: 0 20px; color: #fff; font-weight: 700; letter-spacing: 0.5px; }
.top { background: #fffdf9; display: flex; justify-content: flex-end; align-items: center; gap: 8px; padding: 0 20px; color: #5c564e; }
.content { padding: 16px; background: #f6f1ea; min-height: calc(100vh - 64px); }
</style>
