<template>
  <div class="tpl-page">
    <section class="hero">
      <div class="hero-copy">
        <p class="eyebrow">TEMPLATE LIBRARY · 模板库</p>
        <h1>选一个模板<br />让文章先好看一半</h1>
        <p class="hero-desc">
          覆盖科技、美妆、餐饮、教育、健康、金融、旅行等 14 个行业，
          排版已做好，替换文字即可提交到公众号草稿箱。
        </p>
        <div class="hero-actions">
          <a-button type="primary" size="large" @click="router.push('/editor')">自由创作</a-button>
          <a-button size="large" @click="scrollToGrid">浏览全部模板</a-button>
        </div>
        <div class="hero-tags">
          <span>微信原生内联样式</span>
          <span>手机端自适应</span>
          <span>可二次编辑</span>
        </div>
      </div>
      <div class="hero-art">
        <div class="art-phone art-phone--back">
          <div class="art-screen" v-html="thumbs['festival-greeting']"></div>
        </div>
        <div class="art-phone art-phone--front">
          <div class="art-notch"></div>
          <div class="art-screen" v-html="thumbs['tech-launch']"></div>
        </div>
        <div class="art-badge">
          <strong>{{ ARTICLE_TEMPLATES.length }}</strong>
          <span>套行业模板</span>
        </div>
      </div>
    </section>

    <section ref="gridRef" class="library">
      <div class="library-head">
        <div>
          <h2>全部模板</h2>
          <p>点击「套用」即可带着标题、摘要和正文进入编辑器。</p>
        </div>
        <a-input
          v-model:value="keyword"
          class="search"
          placeholder="搜索行业或模板名称"
          allow-clear
        >
          <template #prefix><SearchOutlined /></template>
        </a-input>
      </div>

      <div class="chips">
        <button
          v-for="item in industries"
          :key="item"
          type="button"
          class="chip"
          :class="{ active: industry === item }"
          @click="industry = item"
        >
          {{ item }}
        </button>
      </div>

      <div v-if="filtered.length" class="grid">
        <article v-for="tpl in filtered" :key="tpl.id" class="tpl-card">
          <div class="thumb" :style="{ background: tpl.gradient }">
            <div class="thumb-inner" v-html="thumbs[tpl.id]"></div>
            <span class="industry">{{ tpl.industry }}</span>
            <button type="button" class="thumb-preview" @click="openPreview(tpl)">快速预览</button>
          </div>
          <div class="card-body">
            <h3><span class="icon">{{ tpl.icon }}</span>{{ tpl.name }}</h3>
            <p class="tagline">{{ tpl.tagline }}</p>
            <div class="card-foot">
              <span class="dot" :style="{ background: tpl.accent }"></span>
              <span class="foot-text">含完整正文与排版样式</span>
              <div class="card-actions">
                <a-button size="small" @click="openPreview(tpl)">预览</a-button>
                <a-button size="small" type="primary" @click="useTemplate(tpl)">套用</a-button>
              </div>
            </div>
          </div>
        </article>
      </div>
      <a-empty v-else description="没有找到匹配的模板，换个关键词试试" />
    </section>

    <a-drawer v-model:open="drawerOpen" :width="440" title="模板预览" class="tpl-drawer">
      <template v-if="current">
        <div class="drawer-head">
          <div>
            <h3>{{ current.icon }} {{ current.name }}</h3>
            <p>{{ current.tagline }}</p>
          </div>
          <a-tag :color="current.accent">{{ current.industry }}</a-tag>
        </div>

        <div class="phone">
          <div class="phone-bar">
            <span>9:41</span>
            <span class="phone-dots"><i></i><i></i><i></i></span>
          </div>
          <div class="phone-scroll">
            <div class="wx-title">{{ current.title }}</div>
            <div class="wx-meta">
              <span>{{ current.author }}</span>
              <span class="wx-origin">原创</span>
            </div>
            <div class="wx-body" v-html="thumbs[current.id]"></div>
          </div>
        </div>

        <div class="drawer-tip">
          <b>封面建议</b>
          <p>{{ current.coverTip }}</p>
        </div>

        <div class="drawer-actions">
          <a-button size="large" block @click="aiWrite(current)">AI 按此模板写</a-button>
          <a-button type="primary" size="large" block @click="useTemplate(current)">
            套用这个模板
          </a-button>
        </div>
      </template>
    </a-drawer>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import { SearchOutlined } from '@ant-design/icons-vue'
import { message } from 'ant-design-vue'
import { ARTICLE_TEMPLATES, TEMPLATE_INDUSTRIES } from '../utils/articleTemplates.js'
import { toWechatHtml } from '../utils/wechatHtml.js'

const router = useRouter()
const gridRef = ref(null)
const industry = ref('全部')
const keyword = ref('')
const drawerOpen = ref(false)
const current = ref(null)

const industries = TEMPLATE_INDUSTRIES
const thumbs = Object.fromEntries(
  ARTICLE_TEMPLATES.map((item) => [item.id, toWechatHtml(item.content, { title: item.title })])
)

const filtered = computed(() => {
  const text = keyword.value.trim().toLowerCase()
  return ARTICLE_TEMPLATES.filter((item) => {
    const matchIndustry = industry.value === '全部' || item.industry === industry.value
    const matchText = !text ||
      item.name.toLowerCase().includes(text) ||
      item.industry.toLowerCase().includes(text) ||
      item.tagline.toLowerCase().includes(text)
    return matchIndustry && matchText
  })
})

function scrollToGrid() {
  gridRef.value?.scrollIntoView({ behavior: 'smooth', block: 'start' })
}

function openPreview(tpl) {
  current.value = tpl
  drawerOpen.value = true
}

function useTemplate(tpl) {
  drawerOpen.value = false
  message.success(`已选择「${tpl.name}」，替换文字后即可提交`)
  router.push({ path: '/editor', query: { template: tpl.id } })
}

function aiWrite(tpl) {
  drawerOpen.value = false
  router.push({ path: '/ai', query: { template: tpl.id } })
}
</script>

<style scoped>
.tpl-page {
  display: flex;
  flex-direction: column;
  gap: 22px;
}

/* ---------- Hero ---------- */
.hero {
  display: grid;
  grid-template-columns: 1.05fr 0.95fr;
  gap: 32px;
  align-items: center;
  padding: 34px 36px;
  border-radius: 22px;
  background:
    radial-gradient(circle at 88% 12%, rgba(124, 224, 195, 0.22), transparent 46%),
    linear-gradient(135deg, #14302a 0%, #1c3d34 48%, #22554a 100%);
  color: #eef7f3;
  overflow: hidden;
  position: relative;
}
.hero::after {
  content: '';
  position: absolute;
  right: -80px;
  bottom: -120px;
  width: 320px;
  height: 320px;
  border-radius: 50%;
  border: 1px solid rgba(124, 224, 195, 0.18);
}
.eyebrow {
  margin: 0 0 12px;
  font-size: 12px;
  letter-spacing: 2.4px;
  color: #7ce0c3;
  font-weight: 600;
}
.hero h1 {
  margin: 0;
  font-size: 30px;
  line-height: 1.42;
  font-weight: 700;
  color: #ffffff;
  letter-spacing: 0.5px;
}
.hero-desc {
  margin: 14px 0 22px;
  max-width: 460px;
  font-size: 14px;
  line-height: 1.9;
  color: rgba(238, 247, 243, 0.72);
}
.hero-actions {
  display: flex;
  gap: 12px;
}
.hero-actions :deep(.ant-btn-default) {
  background: transparent;
  border-color: rgba(255, 255, 255, 0.32);
  color: #eef7f3;
}
.hero-actions :deep(.ant-btn-default:hover) {
  border-color: #7ce0c3;
  color: #7ce0c3;
}
.hero-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 22px;
}
.hero-tags span {
  padding: 4px 10px;
  font-size: 12px;
  color: rgba(238, 247, 243, 0.66);
  border: 1px solid rgba(255, 255, 255, 0.14);
  border-radius: 999px;
}

.hero-art {
  position: relative;
  height: 340px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.art-phone {
  position: absolute;
  width: 196px;
  height: 300px;
  border-radius: 24px;
  background: #ffffff;
  box-shadow: 0 24px 50px rgba(0, 0, 0, 0.32);
  overflow: hidden;
}
.art-phone--back {
  transform: translate(-86px, 8px) rotate(-8deg) scale(0.9);
  opacity: 0.72;
}
.art-phone--front {
  transform: translate(40px, -6px) rotate(5deg);
  z-index: 2;
}
.art-notch {
  position: absolute;
  top: 8px;
  left: 50%;
  transform: translateX(-50%);
  width: 52px;
  height: 5px;
  border-radius: 999px;
  background: #dcdcdc;
  z-index: 3;
}
.art-screen {
  width: 375px;
  padding: 26px 18px 0;
  transform: scale(0.52);
  transform-origin: top left;
  pointer-events: none;
}
.art-badge {
  position: absolute;
  right: 4px;
  bottom: 6px;
  z-index: 3;
  display: flex;
  flex-direction: column;
  padding: 12px 16px;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(124, 224, 195, 0.34);
  backdrop-filter: blur(6px);
  text-align: center;
}
.art-badge strong {
  font-size: 24px;
  color: #7ce0c3;
  line-height: 1.1;
}
.art-badge span {
  margin-top: 2px;
  font-size: 11px;
  letter-spacing: 1px;
  color: rgba(238, 247, 243, 0.72);
}

/* ---------- Library ---------- */
.library {
  padding: 26px 28px 30px;
  background: #ffffff;
  border: 1px solid var(--wx-line);
  border-radius: 20px;
  scroll-margin-top: 18px;
}
.library-head {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 16px;
  flex-wrap: wrap;
}
.library-head h2 {
  margin: 0;
  font-size: 20px;
  color: var(--wx-ink);
}
.library-head p {
  margin: 6px 0 0;
  font-size: 13px;
  color: var(--wx-muted);
}
.search {
  width: 260px;
  border-radius: 10px;
}
.chips {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin: 18px 0 22px;
}
.chip {
  padding: 6px 14px;
  font-size: 13px;
  color: #5c6b64;
  background: #f6f6f3;
  border: 1px solid transparent;
  border-radius: 999px;
  cursor: pointer;
  transition: all 0.18s ease;
}
.chip:hover {
  color: #16302a;
  border-color: #cde9df;
}
.chip.active {
  color: #ffffff;
  background: #16302a;
  border-color: #16302a;
}

.grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(252px, 1fr));
  gap: 18px;
}
.tpl-card {
  display: flex;
  flex-direction: column;
  border: 1px solid var(--wx-line);
  border-radius: 16px;
  background: #fff;
  overflow: hidden;
  transition: transform 0.22s ease, box-shadow 0.22s ease, border-color 0.22s ease;
}
.tpl-card:hover {
  transform: translateY(-4px);
  border-color: #cde9df;
  box-shadow: 0 18px 36px rgba(22, 48, 42, 0.12);
}
.thumb {
  position: relative;
  height: 196px;
  overflow: hidden;
}
.thumb::after {
  content: '';
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  height: 54px;
  background: linear-gradient(to bottom, rgba(255, 255, 255, 0), rgba(255, 255, 255, 0.92));
}
.thumb-inner {
  position: absolute;
  top: 30px;
  left: 50%;
  width: 375px;
  transform: translateX(-50%) scale(0.46);
  transform-origin: top center;
  pointer-events: none;
}
.industry {
  position: absolute;
  top: 12px;
  left: 12px;
  z-index: 2;
  padding: 3px 9px;
  font-size: 11px;
  color: #16302a;
  background: rgba(255, 255, 255, 0.82);
  border-radius: 999px;
  backdrop-filter: blur(4px);
}
.thumb-preview {
  position: absolute;
  right: 12px;
  bottom: 12px;
  z-index: 2;
  padding: 5px 12px;
  font-size: 12px;
  color: #16302a;
  background: rgba(255, 255, 255, 0.9);
  border: 1px solid rgba(22, 48, 42, 0.12);
  border-radius: 999px;
  cursor: pointer;
  opacity: 0;
  transform: translateY(4px);
  transition: all 0.2s ease;
}
.tpl-card:hover .thumb-preview {
  opacity: 1;
  transform: translateY(0);
}
.card-body {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 14px 16px 16px;
  flex: 1;
}
.card-body h3 {
  margin: 0;
  font-size: 15px;
  color: var(--wx-ink);
  display: flex;
  align-items: center;
  gap: 6px;
}
.card-body h3 .icon {
  font-size: 16px;
}
.tagline {
  margin: 0;
  font-size: 12.5px;
  line-height: 1.7;
  color: var(--wx-muted);
  min-height: 42px;
}
.card-foot {
  margin-top: auto;
  display: flex;
  align-items: center;
  gap: 6px;
}
.dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
}
.foot-text {
  font-size: 11px;
  color: #9aa4a0;
}
.card-actions {
  margin-left: auto;
  display: flex;
  gap: 6px;
}

/* ---------- Drawer ---------- */
.drawer-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 16px;
}
.drawer-head h3 {
  margin: 0 0 6px;
  font-size: 16px;
  color: var(--wx-ink);
}
.drawer-head p {
  margin: 0;
  font-size: 12.5px;
  line-height: 1.7;
  color: var(--wx-muted);
}
.phone {
  border: 1px solid var(--wx-line);
  border-radius: 20px;
  background: #fff;
  overflow: hidden;
  box-shadow: 0 12px 30px rgba(22, 48, 42, 0.08);
}
.phone-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 16px;
  font-size: 11px;
  color: #8a938f;
  background: #fafaf8;
}
.phone-dots {
  display: flex;
  gap: 3px;
}
.phone-dots i {
  width: 5px;
  height: 5px;
  border-radius: 50%;
  background: #d3d8d5;
}
.phone-scroll {
  max-height: 460px;
  overflow-y: auto;
  padding: 20px 18px 24px;
}
.wx-title {
  font-size: 20px;
  font-weight: 700;
  line-height: 1.5;
  color: #1a1a1a;
}
.wx-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 10px 0 18px;
  font-size: 12px;
  color: #9aa4a0;
}
.wx-origin {
  padding: 0 6px;
  font-size: 10px;
  color: #1677ff;
  border: 1px solid #d6e4ff;
  border-radius: 4px;
}
.drawer-tip {
  margin: 16px 0;
  padding: 12px 14px;
  background: #f7f8f6;
  border-radius: 12px;
}
.drawer-tip b {
  font-size: 12.5px;
  color: #16302a;
}
.drawer-tip p {
  margin: 6px 0 0;
  font-size: 12.5px;
  line-height: 1.7;
  color: var(--wx-muted);
}
.drawer-actions {
  display: flex;
  gap: 10px;
}

@media (max-width: 1100px) {
  .hero {
    grid-template-columns: 1fr;
  }
  .hero-art {
    display: none;
  }
}
@media (max-width: 640px) {
  .hero {
    padding: 26px 22px;
  }
  .hero h1 {
    font-size: 24px;
  }
  .library {
    padding: 20px 16px;
  }
  .search {
    width: 100%;
  }
}
</style>
