<template>
  <div class="editor-page">
    <header class="page-head">
      <div class="head-copy">
        <p class="eyebrow">DRAFT WORKSPACE</p>
        <h1>写草稿</h1>
        <p class="sub">Markdown 排版，实时预览公众号效果，提交后仅写入草稿箱，不会群发。</p>
      </div>
      <div class="head-actions">
        <a-button size="large" @click="router.push('/templates')">
          <AppstoreOutlined /> 模板库
        </a-button>
        <a-button type="primary" size="large" :loading="publishing" @click="publish">
          提交到草稿箱
        </a-button>
      </div>
    </header>

    <div class="layout">
      <div class="main-col">
        <section class="panel">
          <div class="section-head">
            <div>
              <h3>文章信息</h3>
              <p>标题与摘要在公众号列表页展示</p>
            </div>
            <a-tag v-if="fromHistory" color="orange">来自历史记录</a-tag>
          </div>

          <a-form layout="vertical" class="meta-form">
            <a-form-item label="标题">
              <a-input v-model:value="form.title" :maxlength="64" show-count placeholder="给文章起一个具体的标题" />
            </a-form-item>
            <a-row :gutter="16">
              <a-col :xs="24" :sm="8">
                <a-form-item label="作者">
                  <a-input v-model:value="form.author" :maxlength="16" placeholder="作者或公众号名" />
                </a-form-item>
              </a-col>
              <a-col :xs="24" :sm="16">
                <a-form-item label="摘要">
                  <a-input v-model:value="form.digest" :maxlength="120" show-count placeholder="一句话说明文章讲了什么" />
                </a-form-item>
              </a-col>
            </a-row>

            <a-form-item label="封面">
              <div class="cover-field">
                <label class="cover-drop" :class="{ 'has-image': coverUrl }">
                  <input type="file" accept="image/*" @change="onCover" />
                  <img v-if="coverUrl" :src="coverUrl" alt="封面预览" />
                  <div v-else class="cover-empty">
                    <PictureOutlined class="cover-icon" />
                    <b>选择封面图片</b>
                    <em>建议 2.35:1，不超过 10MB</em>
                  </div>
                  <span v-if="coverUrl" class="cover-change">点击更换</span>
                </label>
                <div class="cover-meta">
                  <p>封面会随草稿一起同步到公众号后台。</p>
                  <p v-if="fromHistory" class="warn">从历史记录载入后，需要重新选择封面再提交。</p>
                  <p v-else>提交前需完成封面选择。</p>
                </div>
              </div>
            </a-form-item>
          </a-form>
        </section>

        <section class="panel editor-panel">
          <div class="section-head">
            <div>
              <h3>正文</h3>
              <p>工具栏可插入卡片样式；图片请用编辑器图片按钮上传到微信</p>
            </div>
            <div class="section-actions">
              <a-select
                v-model:value="pickedTemplate"
                class="tpl-select"
                placeholder="快速套用模板"
                :bordered="false"
                @change="onPickTemplate"
              >
                <a-select-opt-group v-for="group in templateGroups" :key="group.industry" :label="group.industry">
                  <a-select-option v-for="item in group.items" :key="item.id" :value="item.id">
                    {{ item.icon }} {{ item.name }}
                  </a-select-option>
                </a-select-opt-group>
              </a-select>
              <a-button @click="router.push('/templates')">更多模板</a-button>
            </div>
          </div>

          <MdEditor
            ref="editorRef"
            v-model="form.content"
            language="zh-CN"
            :preview="false"
            :toolbars="toolbars"
            :on-upload-img="onUploadImg"
            class="md-editor"
          >
            <template #defToolbars>
              <DropdownToolbar title="样式" :visible="styleOpen" @onChange="(v) => (styleOpen = v)">
                <template #trigger>
                  <span class="style-trigger">样式</span>
                </template>
                <template #overlay>
                  <div class="style-menu">
                    <button type="button" class="style-item accent" @click="insertApple">🍎 苹果代码框</button>
                    <div v-for="group in CARD_STYLE_GROUPS" :key="group.name" class="style-group">
                      <p class="style-group-title">{{ group.name }}</p>
                      <button
                        v-for="item in group.items"
                        :key="item.id"
                        type="button"
                        class="style-item"
                        @click="insertCard(item.id)"
                      >
                        <span class="swatch" :style="{ background: item.sample }"></span>
                        {{ item.name }}
                      </button>
                    </div>
                  </div>
                </template>
              </DropdownToolbar>
            </template>
          </MdEditor>

          <div class="editor-foot">
            <span>支持 Markdown 与 <code>:::wx-xxx</code> 样式块</span>
            <a-button type="primary" :loading="publishing" @click="publish">提交到草稿箱</a-button>
          </div>
        </section>
      </div>

      <aside class="side-col">
        <section class="panel preview-panel">
          <div class="section-head">
            <div>
              <h3>公众号效果</h3>
              <p>与草稿箱中的展示基本一致</p>
            </div>
            <a-tag color="green">实时</a-tag>
          </div>

          <div class="device-bar">
            <span class="device-label">设备</span>
            <a-select
              v-model:value="device"
              size="small"
              :options="DEVICE_OPTIONS"
              class="device-select"
            />
          </div>

          <div class="device-stage">
            <div class="device" :class="`device--${device}`">
              <div class="device-frame">
                <div class="device-screen">
                  <span v-if="device !== 'classic' && device !== 'duo'" class="device-island"></span>
                  <span v-if="device === 'duo'" class="duo-crease"></span>
                  <div class="device-status">
                    <span class="device-time">9:41</span>
                    <span class="device-signal"><i></i><i></i><i></i><b></b></span>
                  </div>
                  <div class="device-body">
                    <h1 class="wx-title">{{ form.title || '文章标题' }}</h1>
                    <div class="wx-meta">
                      <span>{{ form.author || '作者' }}</span>
                      <span class="wx-origin">原创</span>
                    </div>
                    <div class="wx-body" v-html="previewHtml"></div>
                  </div>
                  <span v-if="device !== 'classic'" class="home-indicator"></span>
                </div>
              </div>
            </div>
          </div>

          <div class="preview-stats">
            <span><b>{{ wordCount }}</b> 字</span>
            <span class="divider"></span>
            <span>约 <b>{{ readMinutes }}</b> 分钟读完</span>
            <a-button type="link" size="small" class="clear-btn" @click="clearContent">清空正文</a-button>
          </div>
        </section>
      </aside>
    </div>
  </div>
</template>

<script setup>
import { computed, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { message, Modal } from 'ant-design-vue'
import { AppstoreOutlined, PictureOutlined } from '@ant-design/icons-vue'
import { DropdownToolbar, MdEditor } from 'md-editor-v3'
import 'md-editor-v3/lib/style.css'
import http from '../api/http'
import { toWechatHtml } from '../utils/wechatHtml.js'
import { CARD_STYLE_GROUPS, buildAppleCodeSnippet, buildCardSnippet } from '../utils/wxStyles.js'
import { ARTICLE_TEMPLATES, TEMPLATE_INDUSTRIES, getTemplate } from '../utils/articleTemplates.js'

const route = useRoute()
const router = useRouter()
const editorRef = ref(null)
const styleOpen = ref(false)
const fromHistory = ref(false)
const pickedTemplate = ref(undefined)

const DEVICE_OPTIONS = [
  { value: 'classic', label: '经典预览' },
  { value: 'duo', label: 'iPhone Duo' },
  { value: 'iphone18', label: 'iPhone 18' },
  { value: 'iphone18pro', label: 'iPhone 18 Pro' },
  { value: 'iphone18promax', label: 'iPhone 18 Pro Max' }
]
const device = ref('classic')

const form = reactive({
  title: '从这里开始写',
  author: '极构',
  digest: '',
  content: `# 从这里开始写

> 用一句话写出文章的核心观点，让读者愿意继续读下去。

## 小标题

正文段落放在这里。支持加粗、列表、引用、表格与图片。

- 一个具体的观点
- 一个可执行的建议
- 一个真实的例子

:::wx-soft
工具栏点「样式」可以插入章节标题条、金句卡、时间轴、手帐卡等模块。
:::

:::wx-cta
文末可以放一句引导，例如：如果这篇内容对你有帮助，欢迎点个「在看」。
:::
`
})

const coverFile = ref(null)
const coverUrl = ref('')
const publishing = ref(false)

const previewHtml = computed(() => toWechatHtml(form.content, { title: form.title }) || '<p style="color:#9aa4a0">正文会显示在这里</p>')
const wordCount = computed(() => form.content.replace(/\s/g, '').length)
const readMinutes = computed(() => Math.max(1, Math.round(wordCount.value / 400)))

const templateGroups = TEMPLATE_INDUSTRIES
  .filter((industry) => industry !== '全部')
  .map((industry) => ({
    industry,
    items: ARTICLE_TEMPLATES.filter((item) => item.industry === industry)
  }))

const toolbars = [
  'bold', 'underline', 'italic', 'strikeThrough', '-',
  'title', 'sub', 'sup', 'quote', 'unorderedList', 'orderedList', 'task', '-',
  'codeRow', 'code', 'link', 'image', 'table', '-',
  0, '-',
  'revoke', 'next', '=', 'pageFullscreen', 'fullscreen', 'preview', 'catalog'
]

function onCover(event) {
  const file = event.target.files?.[0]
  coverFile.value = file || null
  coverUrl.value = file ? URL.createObjectURL(file) : ''
}

async function onUploadImg(files, callback) {
  const urls = []
  for (const file of files) {
    const data = new FormData()
    data.append('file', file)
    const result = await http.post('/api/admin/wechat/image', data)
    urls.push(result.url)
  }
  callback(urls)
  message.success('图片已上传到微信')
}

function insertCard(styleId) {
  styleOpen.value = false
  editorRef.value?.insert((selected) => ({
    targetValue: buildCardSnippet(styleId, selected),
    select: true,
    deviationStart: 0,
    deviationEnd: 0
  }))
}

function insertApple() {
  styleOpen.value = false
  editorRef.value?.insert((selected) => ({
    targetValue: buildAppleCodeSnippet(selected),
    select: true,
    deviationStart: 0,
    deviationEnd: 0
  }))
}

function applyTemplate(templateId) {
  const tpl = getTemplate(templateId)
  if (!tpl) return
  form.title = tpl.title
  form.author = tpl.author
  form.digest = tpl.digest
  form.content = tpl.content
  fromHistory.value = false
  message.success(`已套用「${tpl.name}」，替换文字后即可提交`)
}

function onPickTemplate(templateId) {
  if (!templateId) return
  const hasContent = form.content.trim() && !form.content.includes('用一句话写出文章的核心观点')
  const run = () => {
    applyTemplate(templateId)
    pickedTemplate.value = undefined
  }
  if (!hasContent) {
    run()
    return
  }
  Modal.confirm({
    title: '套用模板会覆盖当前正文',
    content: '当前正文内容将被模板替换，确定继续吗？',
    okText: '覆盖套用',
    cancelText: '取消',
    onOk: run,
    onCancel() {
      pickedTemplate.value = undefined
    }
  })
}

function clearContent() {
  if (!form.content.trim()) return
  Modal.confirm({
    title: '清空正文',
    content: '正文将被清空，且无法撤销，确定继续吗？',
    okText: '清空',
    okType: 'danger',
    cancelText: '取消',
    onOk() {
      form.content = ''
    }
  })
}

async function publish() {
  if (!form.title.trim()) {
    message.warning('请填写文章标题')
    return
  }
  if (!coverFile.value) {
    message.warning('请先选择封面')
    return
  }
  publishing.value = true
  try {
    const data = new FormData()
    data.append('title', form.title)
    data.append('author', form.author)
    data.append('digest', form.digest)
    data.append('content', toWechatHtml(form.content, { title: form.title }))
    data.append('contentMd', form.content)
    data.append('cover', coverFile.value)
    const article = await http.post('/api/admin/draft', data)
    message.success(article.syncMessage || '已写入草稿箱')
    fromHistory.value = false
  } finally {
    publishing.value = false
  }
}

async function loadFromHistory(id) {
  if (!id) return
  const article = await http.get(`/api/admin/articles/${id}`)
  form.title = article.title || ''
  form.author = article.author || ''
  form.digest = article.digest || ''
  form.content = article.contentMd || ''
  coverFile.value = null
  coverUrl.value = ''
  fromHistory.value = true
  if (!article.contentMd) {
    message.warning('该记录没有 Markdown 原文，只能查看发送记录里的 HTML 预览')
  } else {
    message.success('已载入历史正文，请重新选择封面后提交')
  }
}

async function loadAiRecord(id) {
  if (!id) return
  const record = await http.get(`/api/admin/ai/records/${id}`)
  if (!record.contentMd) {
    message.warning('这条 AI 记录还没有生成内容')
    return
  }
  form.content = record.contentMd
  const heading = record.contentMd.match(/^\s*#\s+(.+)$/m)
  form.title = record.title || (heading ? heading[1].trim() : '')
  form.digest = ''
  coverFile.value = null
  coverUrl.value = ''
  fromHistory.value = false
  message.success('已引用 AI 生成内容，选择封面后即可提交')
}

watch(
  () => [route.query.template, route.query.id, route.query.aiId],
  ([templateId, id, aiId]) => {
    if (templateId) {
      applyTemplate(templateId)
    } else if (aiId) {
      loadAiRecord(aiId)
    } else if (id) {
      loadFromHistory(id)
    }
  },
  { immediate: true }
)
</script>

<style scoped>
.editor-page {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

/* ---------- 页头 ---------- */
.page-head {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 20px;
  flex-wrap: wrap;
}
.eyebrow {
  margin: 0 0 8px;
  font-size: 11px;
  letter-spacing: 2.4px;
  font-weight: 600;
  color: #8a948f;
}
.page-head h1 {
  margin: 0;
  font-size: 26px;
  line-height: 1.3;
  color: var(--wx-ink);
}
.sub {
  margin: 8px 0 0;
  font-size: 13px;
  color: var(--wx-muted);
}
.head-actions {
  display: flex;
  gap: 10px;
}

/* ---------- 布局 ---------- */
.layout {
  display: grid;
  grid-template-columns: minmax(0, 1.5fr) minmax(320px, 0.8fr);
  gap: 18px;
  align-items: start;
}
.main-col {
  display: flex;
  flex-direction: column;
  gap: 18px;
  min-width: 0;
}
.side-col {
  position: sticky;
  top: 18px;
}

.panel {
  padding: 18px 20px 20px;
  background: var(--wx-surface);
  border: 1px solid var(--wx-line);
  border-radius: 18px;
}
.section-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 14px;
}
.section-head h3 {
  margin: 0;
  font-size: 15px;
  color: var(--wx-ink);
}
.section-head p {
  margin: 4px 0 0;
  font-size: 12px;
  color: var(--wx-muted);
}
.section-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}
.tpl-select {
  min-width: 150px;
  background: #f7f8f6;
  border-radius: 8px;
}

.meta-form :deep(.ant-form-item) {
  margin-bottom: 16px;
}
.meta-form :deep(.ant-form-item:last-child) {
  margin-bottom: 0;
}

/* ---------- 封面上传 ---------- */
.cover-field {
  display: flex;
  gap: 16px;
  align-items: flex-start;
  flex-wrap: wrap;
}
.cover-drop {
  position: relative;
  display: grid;
  place-items: center;
  width: 240px;
  height: 104px;
  overflow: hidden;
  border: 1px dashed #d8d2c6;
  border-radius: 12px;
  background: #fbfaf7;
  cursor: pointer;
  transition: border-color 0.2s ease, background 0.2s ease;
}
.cover-drop:hover {
  border-color: #1677ff;
  background: #f5f9ff;
}
.cover-drop input {
  display: none;
}
.cover-drop img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.cover-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 3px;
  color: #8a948f;
  text-align: center;
}
.cover-icon {
  font-size: 20px;
  color: #b7c0bb;
}
.cover-empty b {
  font-size: 13px;
  color: #5c6b64;
  font-weight: 600;
}
.cover-empty em {
  font-size: 11px;
  font-style: normal;
  color: #a3ada8;
}
.cover-change {
  position: absolute;
  inset: auto 0 0 0;
  padding: 5px 0;
  font-size: 11px;
  text-align: center;
  color: #fff;
  background: rgba(22, 48, 42, 0.62);
  opacity: 0;
  transition: opacity 0.2s ease;
}
.cover-drop:hover .cover-change {
  opacity: 1;
}
.cover-meta {
  flex: 1;
  min-width: 180px;
  padding-top: 4px;
}
.cover-meta p {
  margin: 0 0 6px;
  font-size: 12px;
  line-height: 1.7;
  color: var(--wx-muted);
}
.cover-meta .warn {
  color: #d46b08;
}

/* ---------- 编辑器 ---------- */
.editor-panel :deep(.md-editor) {
  border-radius: 12px;
  border-color: var(--wx-line);
  height: 560px;
}
.editor-panel :deep(.md-editor-toolbar-wrapper) {
  border-radius: 12px 12px 0 0;
  background: #fbfaf7;
}
.style-trigger {
  display: inline-block;
  padding: 0 4px;
  font-size: 13px;
  color: #3d4a44;
}
.style-menu {
  width: 210px;
  max-height: 340px;
  overflow-y: auto;
  padding: 8px;
  background: #fff;
  border: 1px solid #eee;
  border-radius: 12px;
  box-shadow: 0 12px 30px rgba(0, 0, 0, 0.1);
}
.style-item {
  display: flex;
  align-items: center;
  gap: 8px;
  width: 100%;
  padding: 7px 9px;
  border: 0;
  border-radius: 7px;
  background: transparent;
  font-size: 13px;
  color: #3d4a44;
  text-align: left;
  cursor: pointer;
}
.style-item:hover {
  background: #f3f8f5;
  color: #16302a;
}
.style-item.accent {
  font-weight: 600;
  color: #1677ff;
  border-bottom: 1px solid #f2f2f2;
  border-radius: 7px 7px 0 0;
  margin-bottom: 4px;
}
.swatch {
  width: 10px;
  height: 10px;
  flex: none;
  border-radius: 3px;
  border: 1px solid rgba(0, 0, 0, 0.08);
}
.style-group {
  margin-top: 4px;
}
.style-group-title {
  margin: 6px 9px 4px;
  font-size: 11px;
  letter-spacing: 1px;
  color: #a3ada8;
}
.editor-foot {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-top: 14px;
  font-size: 12px;
  color: #a3ada8;
}
.editor-foot code {
  padding: 1px 5px;
  font-size: 11px;
  color: #5c6b64;
  background: #f5f4f0;
  border-radius: 4px;
}

/* ---------- 预览 ---------- */
.preview-panel {
  padding-bottom: 14px;
}

/* ---------- 设备切换 ---------- */
.device-bar {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 14px;
}
.device-label {
  flex: none;
  font-size: 11.5px;
  letter-spacing: 1px;
  color: #a3ada8;
}
.device-select {
  flex: 1;
}
.device-select :deep(.ant-select-selector) {
  border-radius: 8px !important;
  background: #fbfaf7;
  font-size: 12.5px;
}

/* ---------- 手机外壳 ---------- */
.device-stage {
  display: flex;
  justify-content: center;
  padding: 2px 0 4px;
}
.device {
  --screen-w: 262px;
  --frame-pad: 3px;
  --ratio: 0.461;
  --frame-bg: linear-gradient(145deg, #f6f7f8 0%, #d9dde1 34%, #a9afb6 66%, #eceef0 100%);
  --frame-radius: 42px;
  --screen-radius: 35px;
  width: 100%;
  display: flex;
  justify-content: center;
}
.device-frame {
  position: relative;
  width: min(calc(var(--screen-w) + var(--frame-pad) * 2), 100%);
  padding: var(--frame-pad);
  border-radius: var(--frame-radius);
  background: var(--frame-bg);
  box-shadow:
    0 18px 36px rgba(19, 35, 30, 0.18),
    0 3px 8px rgba(19, 35, 30, 0.12),
    inset 0 0 0 1px rgba(255, 255, 255, 0.4);
}
.device-frame::before,
.device-frame::after {
  content: '';
  position: absolute;
  right: -2px;
  width: 3px;
  border-radius: 2px;
  background: linear-gradient(180deg, #8a9097, #3c4146);
}
.device-frame::before {
  top: 118px;
  height: 56px;
}
.device-frame::after {
  top: 186px;
  height: 32px;
}
.device-screen {
  position: relative;
  display: flex;
  flex-direction: column;
  width: 100%;
  aspect-ratio: var(--ratio);
  overflow: hidden;
  border: 6px solid #0b0c0e;
  border-radius: var(--screen-radius);
  background: #ffffff;
}
.device-island {
  position: absolute;
  top: 7px;
  left: 50%;
  z-index: 3;
  width: 76px;
  height: 22px;
  border-radius: 12px;
  background: #050607;
  transform: translateX(-50%);
}
.device-island::after {
  content: '';
  position: absolute;
  right: 8px;
  top: 50%;
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: radial-gradient(circle at 35% 35%, #33475a, #0a1118 70%);
  transform: translateY(-50%);
}
.device--iphone18 .device-island {
  width: 68px;
  height: 20px;
}

.device-status {
  position: relative;
  z-index: 2;
  flex: none;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 40px;
  padding: 12px 20px 0;
  font-size: 10.5px;
  font-weight: 600;
  color: #2f3b36;
  background: #ffffff;
}
.device-time {
  letter-spacing: 0.3px;
}
.device-signal {
  display: flex;
  align-items: center;
  gap: 3px;
}
.device-signal i {
  width: 4px;
  height: 4px;
  border-radius: 50%;
  background: #c2cbc7;
}
.device-signal b {
  position: relative;
  width: 17px;
  height: 8px;
  border: 1px solid #c2cbc7;
  border-radius: 2.5px;
}
.device-signal b::after {
  content: '';
  position: absolute;
  inset: 1px;
  border-radius: 1px;
  background: #8fd1bb;
}
.device-body {
  flex: 1;
  min-height: 0;
  overflow-y: auto;
  padding: 2px 12px 24px;
}
.device-body::-webkit-scrollbar {
  width: 3px;
}
.device-body::-webkit-scrollbar-thumb {
  background: rgba(0, 0, 0, 0.12);
  border-radius: 3px;
}
.home-indicator {
  position: absolute;
  left: 50%;
  bottom: 6px;
  z-index: 4;
  width: 104px;
  height: 4px;
  border-radius: 3px;
  background: #111111;
  opacity: 0.8;
  transform: translateX(-50%);
}

/* ---------- 各机型 ---------- */
.device--iphone18pro {
  --screen-w: 270px;
  --frame-pad: 4px;
  --frame-bg: linear-gradient(145deg, #66707c, #2f353d 40%, #12161a 70%, #4d5560 100%);
}
.device--iphone18promax {
  --screen-w: 282px;
  --frame-pad: 5px;
  --frame-bg: linear-gradient(145deg, #8a8069, #453e34 42%, #1a1816 72%, #6b5f49 100%);
}
.device--duo {
  --screen-w: 320px;
  --frame-pad: 4px;
  --ratio: 0.95;
  --frame-radius: 26px;
  --screen-radius: 20px;
  --frame-bg: linear-gradient(145deg, #565c63, #2a2e33 42%, #131518 72%, #454a50);
}
.device--duo .device-frame::before {
  top: 64px;
  height: 46px;
}
.device--duo .device-frame::after {
  top: 128px;
  height: 26px;
}
.device--duo .device-status {
  height: 34px;
  padding: 0 16px;
}
.duo-crease {
  position: absolute;
  top: 0;
  bottom: 0;
  left: 50%;
  z-index: 2;
  width: 18px;
  pointer-events: none;
  transform: translateX(-50%);
  background: linear-gradient(
    90deg,
    rgba(0, 0, 0, 0) 0%,
    rgba(0, 0, 0, 0.02) 35%,
    rgba(0, 0, 0, 0.055) 50%,
    rgba(0, 0, 0, 0.02) 65%,
    rgba(0, 0, 0, 0) 100%
  );
  box-shadow: inset 0 0 4px rgba(255, 255, 255, 0.25);
}

/* 保留原有经典预览 */
.device--classic {
  --screen-w: 320px;
  --ratio: 0.5714;
  --frame-radius: 24px;
  --screen-radius: 24px;
}
.device--classic .device-frame {
  width: min(320px, 100%);
  padding: 0;
  background: none;
  box-shadow: none;
}
.device--classic .device-frame::before,
.device--classic .device-frame::after {
  display: none;
}
.device--classic .device-screen {
  border: 1px solid var(--wx-line);
}
.device--classic .device-status {
  height: 30px;
  padding: 0 14px;
  font-weight: 400;
  color: #8a938f;
  background: #fafaf8;
}
.device--classic .device-signal b::after {
  background: #d3d8d5;
}
.device--classic .device-body {
  padding: 14px 16px 20px;
}
.wx-title {
  margin: 0;
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
.preview-stats {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 12px;
  font-size: 12px;
  color: #8a948f;
}
.preview-stats b {
  color: #3d4a44;
  font-weight: 600;
}
.preview-stats .divider {
  width: 1px;
  height: 10px;
  background: #e4e0d8;
}
.clear-btn {
  margin-left: auto;
  font-size: 12px;
}

@media (max-width: 1180px) {
  .layout {
    grid-template-columns: 1fr;
  }
  .side-col {
    position: static;
  }
}
@media (max-width: 640px) {
  .page-head {
    align-items: flex-start;
  }
  .head-actions {
    width: 100%;
  }
  .head-actions :deep(.ant-btn) {
    flex: 1;
  }
  .panel {
    padding: 16px;
  }
  .cover-drop {
    width: 100%;
  }
  .section-actions {
    flex-wrap: wrap;
  }
}
</style>
