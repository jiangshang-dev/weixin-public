<template>
  <div class="login-page">
    <div class="login-card">
      <section class="brand-panel">
        <div class="brand-top">
          <span class="logo">微</span>
          <span class="brand-name">WECHAT STUDIO</span>
        </div>

        <div class="brand-mid">
          <h1>把排版的时间<br />还给内容本身</h1>
          <p>Markdown 写作、行业模板、公众号效果预览，一键写入草稿箱。</p>
        </div>

        <ul class="feature-list">
          <li><b>01</b><span>实时预览公众号排版效果</span></li>
          <li><b>02</b><span>14 个行业模板，替换文字即可发布</span></li>
          <li><b>03</b><span>只写草稿，不会误触群发</span></li>
        </ul>

        <div class="brand-foot">WeChat Draft Studio</div>
      </section>

      <section class="form-panel">
        <h2>登录工作台</h2>
        <p class="welcome">使用管理员账号继续</p>

        <a-form layout="vertical" :model="form" @finish="submit">
          <a-form-item label="账号">
            <a-input v-model:value="form.username" size="large" placeholder="请输入账号" />
          </a-form-item>
          <a-form-item label="密码">
            <a-input-password v-model:value="form.password" size="large" placeholder="请输入密码" />
          </a-form-item>
          <a-button type="primary" html-type="submit" size="large" block :loading="loading">
            登录
          </a-button>
        </a-form>

        <p class="hint">默认账号 admin / admin123。文章会直接写入公众号草稿箱，不会群发。</p>
      </section>
    </div>
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
.login-page {
  display: grid;
  place-items: center;
  min-height: 100vh;
  padding: 24px;
  background:
    radial-gradient(circle at 18% 16%, rgba(124, 224, 195, 0.16), transparent 40%),
    radial-gradient(circle at 84% 84%, rgba(22, 119, 255, 0.07), transparent 42%),
    #f4f1ea;
}
.login-card {
  display: grid;
  grid-template-columns: 1.05fr 0.95fr;
  width: min(920px, 100%);
  min-height: 540px;
  background: #ffffff;
  border-radius: 24px;
  overflow: hidden;
  box-shadow: 0 30px 70px rgba(22, 48, 42, 0.16);
}

/* ---------- 左侧品牌 ---------- */
.brand-panel {
  position: relative;
  display: flex;
  flex-direction: column;
  padding: 40px 42px;
  color: #e8f5f0;
  background:
    radial-gradient(circle at 82% 8%, rgba(124, 224, 195, 0.22), transparent 44%),
    linear-gradient(160deg, #0f231e 0%, #16302a 52%, #1f463b 100%);
  overflow: hidden;
}
.brand-panel::after {
  content: '';
  position: absolute;
  right: -90px;
  bottom: -110px;
  width: 280px;
  height: 280px;
  border-radius: 50%;
  border: 1px solid rgba(157, 240, 214, 0.16);
}
.brand-top {
  display: flex;
  align-items: center;
  gap: 10px;
}
.logo {
  display: grid;
  place-items: center;
  width: 34px;
  height: 34px;
  font-size: 15px;
  font-weight: 700;
  color: #10231e;
  background: linear-gradient(140deg, #9df0d6, #5ec9ab);
  border-radius: 10px;
}
.brand-name {
  font-size: 10px;
  letter-spacing: 2.4px;
  color: rgba(232, 245, 240, 0.5);
}
.brand-mid {
  margin-top: 54px;
}
.brand-mid h1 {
  margin: 0;
  font-size: 30px;
  line-height: 1.5;
  font-weight: 700;
  color: #ffffff;
  letter-spacing: 0.6px;
}
.brand-mid p {
  margin: 16px 0 0;
  max-width: 340px;
  font-size: 13px;
  line-height: 1.9;
  color: rgba(232, 245, 240, 0.62);
}
.feature-list {
  margin: 34px 0 0;
  padding: 0;
  list-style: none;
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.feature-list li {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 12.5px;
  color: rgba(232, 245, 240, 0.78);
}
.feature-list b {
  font-size: 10px;
  font-weight: 600;
  letter-spacing: 1px;
  color: #7ce0c3;
}
.brand-foot {
  margin-top: auto;
  padding-top: 26px;
  font-size: 10.5px;
  letter-spacing: 1.6px;
  color: rgba(232, 245, 240, 0.32);
}

/* ---------- 右侧表单 ---------- */
.form-panel {
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 46px 46px 40px;
}
.form-panel h2 {
  margin: 0;
  font-size: 22px;
  color: var(--wx-ink);
}
.welcome {
  margin: 8px 0 30px;
  font-size: 13px;
  color: var(--wx-muted);
}
.form-panel :deep(.ant-form-item) {
  margin-bottom: 18px;
}
.form-panel :deep(.ant-form-item-label > label) {
  font-size: 12.5px;
  color: #5c6b64;
}
.hint {
  margin: 22px 0 0;
  padding-top: 18px;
  font-size: 11.5px;
  line-height: 1.8;
  color: #a3ada8;
  border-top: 1px dashed #ece7dc;
}

@media (max-width: 860px) {
  .login-card {
    grid-template-columns: 1fr;
    min-height: auto;
  }
  .brand-panel {
    display: none;
  }
  .form-panel {
    padding: 36px 26px;
  }
}
</style>
