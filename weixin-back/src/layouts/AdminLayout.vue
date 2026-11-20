<template>
  <div class="shell">
    <aside class="sider">
      <div class="brand">
        <span class="logo">微</span>
        <div class="brand-text">
          <strong>公众号工作台</strong>
          <span>WECHAT STUDIO</span>
        </div>
      </div>

      <nav class="nav">
        <router-link
          v-for="item in menus"
          :key="item.path"
          :to="item.path"
          class="nav-item"
          active-class="active"
        >
          <component :is="item.icon" class="nav-icon" />
          <span class="nav-label">{{ item.label }}</span>
          <span class="nav-dot"></span>
        </router-link>
      </nav>

      <div class="nav-tip">
        <b>只写草稿，不群发</b>
        <span>提交后请到公众号后台确认后再发布</span>
      </div>

      <div class="sider-foot">
        <div class="avatar">{{ initials }}</div>
        <div class="who">
          <strong>{{ me.realName || me.username || '管理员' }}</strong>
          <span>{{ me.ip || '本地环境' }}</span>
        </div>
        <button class="logout" title="退出登录" @click="logout">
          <LogoutOutlined />
        </button>
      </div>
    </aside>

    <div class="main">
      <header class="topbar">
        <div class="crumb">
          <span class="crumb-root">工作台</span>
          <span class="slash">/</span>
          <span class="crumb-now">{{ route.meta.title || '写草稿' }}</span>
          <span v-if="route.meta.subtitle" class="crumb-sub">{{ route.meta.subtitle }}</span>
        </div>
        <div class="top-actions">
          <span class="status"><i></i>草稿模式</span>
          <span class="top-user">{{ me.username }}</span>
        </div>
      </header>

      <main class="content">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </main>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  AppstoreOutlined,
  EditOutlined,
  HistoryOutlined,
  LogoutOutlined,
  QuestionCircleOutlined,
  RobotOutlined
} from '@ant-design/icons-vue'
import http from '../api/http'

const route = useRoute()
const router = useRouter()
const me = reactive({ username: '', realName: '', ip: '' })

const menus = [
  { path: '/editor', label: '写草稿', icon: EditOutlined },
  { path: '/ai', label: 'AI 写作', icon: RobotOutlined },
  { path: '/templates', label: '模板库', icon: AppstoreOutlined },
  { path: '/articles', label: '发送记录', icon: HistoryOutlined },
  { path: '/guide', label: '使用说明', icon: QuestionCircleOutlined }
]

const initials = computed(() => {
  const name = me.realName || me.username || '管理'
  return name.slice(0, 1).toUpperCase()
})

onMounted(async () => {
  try {
    Object.assign(me, await http.get('/api/admin/me'))
  } catch (error) {
    /* 拦截器已提示 */
  }
})

function logout() {
  localStorage.removeItem('weixin-token')
  router.push('/login')
}
</script>

<style scoped>
.shell {
  display: flex;
  min-height: 100vh;
  background:
    radial-gradient(circle at 82% -10%, rgba(124, 224, 195, 0.12), transparent 42%),
    radial-gradient(circle at 6% 100%, rgba(22, 119, 255, 0.06), transparent 40%),
    var(--wx-bg);
}

/* ---------- 侧栏 ---------- */
.sider {
  position: sticky;
  top: 0;
  display: flex;
  flex-direction: column;
  width: 218px;
  height: 100vh;
  flex: none;
  padding: 22px 14px 16px;
  background: linear-gradient(175deg, #10231e 0%, #16302a 55%, #1b3b32 100%);
  color: #dbe9e3;
}
.brand {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 0 8px 20px;
}
.logo {
  display: grid;
  place-items: center;
  width: 34px;
  height: 34px;
  flex: none;
  font-size: 15px;
  font-weight: 700;
  color: #10231e;
  background: linear-gradient(140deg, #9df0d6, #5ec9ab);
  border-radius: 10px;
}
.brand-text {
  display: flex;
  flex-direction: column;
  line-height: 1.3;
}
.brand-text strong {
  font-size: 14px;
  color: #f2faf6;
  letter-spacing: 0.5px;
}
.brand-text span {
  font-size: 9px;
  letter-spacing: 1.6px;
  color: rgba(219, 233, 227, 0.42);
}

.nav {
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.nav-item {
  position: relative;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 12px;
  font-size: 13.5px;
  color: rgba(219, 233, 227, 0.7);
  border-radius: 10px;
  text-decoration: none;
  transition: all 0.18s ease;
}
.nav-item:hover {
  color: #ffffff;
  background: rgba(255, 255, 255, 0.06);
}
.nav-item.active {
  color: #10231e;
  background: linear-gradient(120deg, #9df0d6, #6fd6b8);
  font-weight: 600;
  box-shadow: 0 8px 20px rgba(111, 214, 184, 0.22);
}
.nav-icon {
  font-size: 16px;
}
.nav-dot {
  width: 5px;
  height: 5px;
  margin-left: auto;
  border-radius: 50%;
  background: currentColor;
  opacity: 0;
}
.nav-item.active .nav-dot {
  opacity: 0.6;
}

.nav-tip {
  margin: 22px 6px 0;
  padding: 12px;
  border: 1px dashed rgba(157, 240, 214, 0.26);
  border-radius: 12px;
  background: rgba(157, 240, 214, 0.05);
}
.nav-tip b {
  display: block;
  font-size: 12px;
  color: #9df0d6;
  font-weight: 600;
}
.nav-tip span {
  display: block;
  margin-top: 4px;
  font-size: 11px;
  line-height: 1.7;
  color: rgba(219, 233, 227, 0.48);
}

.sider-foot {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-top: auto;
  padding: 12px 8px 0;
  border-top: 1px solid rgba(255, 255, 255, 0.08);
}
.avatar {
  display: grid;
  place-items: center;
  width: 32px;
  height: 32px;
  flex: none;
  font-size: 13px;
  font-weight: 700;
  color: #10231e;
  background: #d9efe7;
  border-radius: 50%;
}
.who {
  display: flex;
  flex-direction: column;
  min-width: 0;
  line-height: 1.4;
}
.who strong {
  font-size: 12.5px;
  color: #f2faf6;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.who span {
  font-size: 10.5px;
  color: rgba(219, 233, 227, 0.42);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.logout {
  margin-left: auto;
  padding: 4px;
  font-size: 15px;
  color: rgba(219, 233, 227, 0.56);
  background: transparent;
  border: 0;
  cursor: pointer;
  transition: color 0.18s ease;
}
.logout:hover {
  color: #ff9f9f;
}

/* ---------- 主区域 ---------- */
.main {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
}
.topbar {
  position: sticky;
  top: 0;
  z-index: 20;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  height: 58px;
  padding: 0 26px;
  background: rgba(250, 248, 244, 0.86);
  backdrop-filter: blur(12px);
  border-bottom: 1px solid var(--wx-line);
}
.crumb {
  display: flex;
  align-items: baseline;
  gap: 8px;
  font-size: 13px;
}
.crumb-root {
  color: #9aa4a0;
}
.slash {
  color: #ccc7bd;
}
.crumb-now {
  color: var(--wx-ink);
  font-weight: 600;
}
.crumb-sub {
  padding-left: 10px;
  margin-left: 2px;
  font-size: 12px;
  color: #a3ada8;
  border-left: 1px solid #e6e1d7;
}
.top-actions {
  display: flex;
  align-items: center;
  gap: 14px;
}
.status {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 4px 10px;
  font-size: 11.5px;
  color: #3f7a63;
  background: #eef8f3;
  border-radius: 999px;
}
.status i {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #3fbf8f;
  box-shadow: 0 0 0 3px rgba(63, 191, 143, 0.16);
}
.top-user {
  font-size: 12.5px;
  color: #7a847f;
}

.content {
  width: 100%;
  max-width: 1480px;
  margin: 0 auto;
  padding: 22px 26px 44px;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.18s ease, transform 0.18s ease;
}
.fade-enter-from {
  opacity: 0;
  transform: translateY(6px);
}
.fade-leave-to {
  opacity: 0;
}

@media (max-width: 900px) {
  .sider {
    width: 68px;
    padding: 18px 10px 14px;
    align-items: center;
  }
  .brand {
    padding: 0 0 18px;
  }
  .brand-text,
  .nav-label,
  .nav-dot,
  .nav-tip,
  .who {
    display: none;
  }
  .nav-item {
    justify-content: center;
    padding: 11px;
  }
  .sider-foot {
    width: 100%;
    justify-content: center;
    padding-top: 12px;
  }
  .logout {
    display: none;
  }
  .topbar {
    padding: 0 16px;
  }
  .crumb-sub {
    display: none;
  }
  .content {
    padding: 16px 14px 32px;
  }
}
</style>
