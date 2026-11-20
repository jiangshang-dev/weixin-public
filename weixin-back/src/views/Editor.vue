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

          <div class="phone">
            <div class="phone-bar">
              <span>9:41</span>
              <span class="phone-dots"><i></i><i></i><i></i></span>
            </div>
            <div class="phone-scroll">
              <h1 class="wx-title">{{ form.title || '文章标题' }}</h1>
              <div class="wx-meta">
                <span>{{ form.author || '作者' }}</span>
                <span class="wx-origin">原创</span>
              </div>
              <div class="wx-body" v-html="previewHtml"></div>
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

watch(
  () => [route.query.template, route.query.id],
  ([templateId, id]) => {
    if (templateId) {
      applyTemplate(templateId)
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
.phone {
  border: 1px solid var(--wx-line);
  border-radius: 18px;
  background: #fff;
  overflow: hidden;
}
.phone-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 14px;
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
  max-height: 560px;
  overflow-y: auto;
  padding: 18px 16px 24px;
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
