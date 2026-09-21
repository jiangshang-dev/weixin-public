<template>
  <div class="login">
    <a-card title="公众号草稿" style="width: 380px">
      <a-form layout="vertical" :model="form" @finish="submit">
        <a-form-item label="账号"><a-input v-model:value="form.username" /></a-form-item>
        <a-form-item label="密码"><a-input-password v-model:value="form.password" /></a-form-item>
        <a-button type="primary" html-type="submit" block :loading="loading">登录</a-button>
      </a-form>
      <p class="hint">默认账号 admin / admin123。文章会直接写入公众号草稿箱，不会群发。</p>
    </a-card>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import http from '../api/http'

const router = useRouter()
const loading = ref(false)
const form = reactive({ username: 'admin', password: '' })

async function submit() {
  loading.value = true
  try {
    const data = await http.post('/api/admin/login', form)
    localStorage.setItem('weixin-token', data.token)
    router.push('/editor')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login { min-height: 100vh; display: grid; place-items: center; background: #f6f1ea; }
.hint { color: #6f675e; margin: 16px 0 0; }
</style>
