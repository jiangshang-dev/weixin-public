<template>
  <div class="ai-page">
    <header class="page-head">
      <div class="head-copy">
        <p class="eyebrow">AI WRITING</p>
        <h1>AI 写作</h1>
        <p class="sub">说清楚要写什么，AI 会按公众号模板结构流式生成；每次生成都会保存，可随时查看和引用。</p>
      </div>
      <div class="head-meta">
        <span class="pill model">{{ config.model || '模型未配置' }}</span>
        <span class="pill">记忆 {{ config.memoryEnabled ? '开启' : '关闭' }}</span>
        <span class="pill">压缩 {{ config.compactionEnabled ? '开启' : '关闭' }}</span>
      </div>
    </header>

    <div class="layout">
      <section class="panel form-panel">
        <div class="panel-head">
          <div>
            <h3>写作要求</h3>
            <p>只有「写作要求」是必填</p>
          </div>
        </div>

        <a-form layout="vertical" class="ai-form">
          <a-form-item label="参考模板">
            <a-select
              v-model:value="form.templateId"
              placeholder="不使用模板，直接创作"
              allow-clear
              @change="onTemplateChange"
            >
              <a-select-opt-group v-for="group in templateGroups" :key="group.industry" :label="group.industry">
                <a-select-option v-for="item in group.items" :key="item.id" :value="item.id">
                  {{ item.icon }} {{ item.name }}
                </a-select-option>
              </a-select-opt-group>
            </a-select>
            <p v-if="currentTemplate" class="field-hint">{{ currentTemplate.tagline }}</p>
          </a-form-item>

          <a-row :gutter="12">
            <a-col :span="12">
              <a-form-item label="行业">
                <a-input v-model:value="form.industry" placeholder="如 科技数码" />
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item label="目标读者">
                <a-input v-model:value="form.audience" placeholder="如 通勤上班族" />
              </a-form-item>
            </a-col>
          </a-row>

          <a-form-item label="标题方向">
            <a-input v-model:value="form.title" :maxlength="40" placeholder="可留空，由 AI 起标题" />
          </a-form-item>

          <a-row :gutter="12">
            <a-col :span="12">
              <a-form-item label="语气">
                <a-select v-model:value="form.tone" :options="toneOptions" />
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item label="篇幅">
                <a-select v-model:value="form.wordCount" :options="wordOptions" />
              </a-form-item>
            </a-col>
          </a-row>

          <a-form-item label="写作要求">
            <a-textarea
              v-model:value="form.requirement"
              :rows="5"
              :maxlength="2000"
              show-count
              placeholder="例如：写一篇秋季新品发布的公众号文章，突出轻量与续航，给出三个真实使用场景，语气专业但不生硬。"
            />
          </a-form-item>

          <a-button type="primary" size="large" block :loading="streaming" @click="generate">
            {{ streaming ? '正在生成…' : '生成文章' }}
          </a-button>
        </a-form>
      </section>

      <section class="panel output-panel">
        <div class="panel-head">
          <div class="head-left">
            <h3>生成结果</h3>
            <a-tag :color="statusColor">{{ statusText }}</a-tag>
          </div>
          <div class="head-actions">
            <a-segmented v-model:value="viewMode" :options="['预览', 'Markdown']" size="small" />
            <a-button v-if="streaming" danger size="small" @click="stop">停止</a-button>
          </div>
        </div>

        <div v-if="!output && !errorMsg" class="empty">
          <div class="empty-icon"><RobotOutlined /></div>
          <h4>等待你的第一条写作要求</h4>
          <p>填好左侧信息后点击「生成文章」，内容会实时出现在这里。</p>
        </div>

        <div v-else class="output-body">
          <div v-if="errorMsg" class="error-box">{{ errorMsg }}</div>

          <div v-if="viewMode === 'Markdown'" class="markdown-box">
            <pre>{{ output }}</pre>
          </div>
          <div v-else class="paper">
            <h1 class="wx-title">{{ outputTitle }}</h1>
            <div class="wx-meta">
              <span>{{ form.industry || '公众号文章' }}</span>
              <span class="wx-origin">原创</span>
            </div>
            <div class="wx-body" v-html="previewHtml"></div>
            <span v-if="streaming" class="cursor"></span>
          </div>
        </div>

        <div class="output-foot">
          <span class="count">{{ output.length }} 字符</span>
          <div class="foot-actions">
            <a-button size="small" :disabled="!output" @click="copy">复制 Markdown</a-button>
            <a-button
              type="primary"
              size="small"
              :disabled="!output || streaming || !activeRecordId || status !== 'done'"
              @click="useInEditor()"
            >
              引用到编辑器
            </a-button>
          </div>
        </div>
      </section>
    </div>

    <section class="panel history-panel">
      <div class="panel-head">
        <div>
          <h3>生成记录</h3>
          <p>每次生成都会保存，方便之后查看与引用。</p>
        </div>
        <a-button :loading="historyLoading" @click="loadHistory">
          <ReloadOutlined /> 刷新
        </a-button>
      </div>

      <a-table
        :data-source="records"
        :columns="columns"
        row-key="id"
        size="middle"
        :pagination="{ pageSize: 8, hideOnSinglePage: true }"
        :scroll="{ x: 760 }"
        class="record-table"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'title'">
            <div class="record-title">{{ record.title || '未命名文章' }}</div>
            <div class="record-sub">{{ record.industry || '未分类' }} · {{ record.model || '-' }}</div>
          </template>
          <template v-else-if="column.key === 'status'">
            <a-tag :color="recordStatusColor(record.status)">{{ recordStatusText(record.status) }}</a-tag>
          </template>
          <template v-else-if="column.key === 'createTime'">
            <span class="time">{{ formatTime(record.createTime) }}</span>
          </template>
          <template v-else-if="column.key === 'action'">
            <a-button type="link" size="small" @click="viewRecord(record)">查看</a-button>
            <a-button type="link" size="small" :disabled="record.status !== 1" @click="useInEditor(record.id)">引用</a-button>
            <a-button type="link" size="small" danger @click="removeRecord(record)">删除</a-button>
          </template>
        </template>
      </a-table>
    </section>

    <a-drawer v-model:open="drawerOpen" :width="520" title="生成记录">
      <template v-if="current">
        <div class="drawer-head">
          <h3>{{ current.title || '未命名文章' }}</h3>
          <a-tag :color="recordStatusColor(current.status)">{{ recordStatusText(current.status) }}</a-tag>
        </div>
        <p class="drawer-meta">{{ current.industry || '未分类' }} · {{ formatTime(current.createTime) }}</p>

        <div class="drawer-requirement">
          <b>写作要求</b>
          <p>{{ current.requirement || '-' }}</p>
        </div>

        <div v-if="current.contentMd" class="drawer-paper" v-html="renderMarkdown(current.contentMd, current.title)"></div>
        <a-empty v-else description="这条记录还没有生成内容" />

        <a-button type="primary" block :disabled="!current.contentMd" @click="useInEditor(current.id)">
          引用到编辑器
        </a-button>
      </template>
    </a-drawer>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Modal, message } from 'ant-design-vue'
import { RobotOutlined, ReloadOutlined } from '@ant-design/icons-vue'
import http from '../api/http'
import { ARTICLE_TEMPLATES, TEMPLATE_INDUSTRIES } from '../utils/articleTemplates.js'
import { toWechatHtml } from '../utils/wechatHtml.js'

const route = useRoute()
const router = useRouter()

const form = reactive({
  templateId: undefined,
  title: '',
  industry: '科技数码',
  audience: '',
  tone: '专业理性',
  wordCount: 1000,
  requirement: ''
})

const config = reactive({ model: '', memoryEnabled: false, compactionEnabled: false })
const records = ref([])
const historyLoading = ref(false)
const drawerOpen = ref(false)
const current = ref(null)

const output = ref('')
const errorMsg = ref('')
const status = ref('idle')
const viewMode = ref('预览')
const activeRecordId = ref(null)
let controller = null

const toneOptions = ['专业理性', '轻松亲切', '故事叙述', '干货清单', '观点评论'].map((value) => ({ value, label: value }))
const wordOptions = [
  { value: 600, label: '约 600 字' },
  { value: 1000, label: '约 1000 字' },
  { value: 1500, label: '约 1500 字' },
  { value: 2000, label: '约 2000 字' }
]

const templateGroups = TEMPLATE_INDUSTRIES
  .filter((industry) => industry !== '全部')
  .map((industry) => ({
    industry,
    items: ARTICLE_TEMPLATES.filter((item) => item.industry === industry)
  }))

const columns = [
  { title: '文章', key: 'title' },
  { title: '状态', key: 'status', width: 110 },
  { title: '生成时间', key: 'createTime', width: 175 },
  { title: '操作', key: 'action', width: 185 }
]

const currentTemplate = computed(() => ARTICLE_TEMPLATES.find((item) => item.id === form.templateId) || null)
const streaming = computed(() => status.value === 'streaming')
const statusText = computed(() => ({
  idle: '待生成',
  streaming: '生成中',
  done: '已完成',
  error: '生成失败'
}[status.value] || '待生成'))
const statusColor = computed(() => ({
  idle: 'default',
  streaming: 'blue',
  done: 'green',
  error: 'red'
}[status.value] || 'default'))
const outputTitle = computed(() => {
  const match = output.value.match(/^\s*#\s+(.+)$/m)
  return (match?.[1] || form.title || '未命名文章').trim()
})
const previewHtml = computed(() => toWechatHtml(output.value, { title: outputTitle.value }))

function onTemplateChange(templateId) {
  const template = ARTICLE_TEMPLATES.find((item) => item.id === templateId)
  if (template) {
    form.industry = template.industry
  }
}

function recordStatusText(value) {
  if (value === 1) return '已完成'
  if (value === 2) return '失败'
  return '生成中'
}

function recordStatusColor(value) {
  if (value === 1) return 'green'
  if (value === 2) return 'red'
  return 'blue'
}

function formatTime(value) {
  if (!value) return '-'
  return String(value).replace('T', ' ').slice(0, 19)
}

function renderMarkdown(markdown, title) {
  return toWechatHtml(markdown, { title })
}

async function loadHistory() {
  historyLoading.value = true
  try {
    records.value = await http.get('/api/admin/ai/records')
  } finally {
    historyLoading.value = false
  }
}

function parseEvent(chunk) {
  let event = 'message'
  let data = ''
  for (const line of chunk.split('\n')) {
    if (line.startsWith('event:')) {
      event = line.slice(6).trim()
    } else if (line.startsWith('data:')) {
      data += line.slice(5).trim()
    }
  }
  if (!data) return
  let payload
  try {
    payload = JSON.parse(data)
  } catch (error) {
    return
  }
  if (event === 'meta') {
    activeRecordId.value = payload.recordId
  } else if (event === 'delta') {
    output.value += payload.text || ''
  } else if (event === 'done') {
    output.value = payload.content || output.value
    activeRecordId.value = payload.recordId
    status.value = 'done'
    loadHistory()
  } else if (event === 'error') {
    status.value = 'error'
    errorMsg.value = payload.message || '生成失败'
    loadHistory()
  }
}

async function generate() {
  if (!form.requirement.trim()) {
    message.warning('请填写写作要求')
    return
  }
  if (streaming.value) {
    return
  }
  output.value = ''
  errorMsg.value = ''
  activeRecordId.value = null
  status.value = 'streaming'
  controller = new AbortController()

  const base = import.meta.env.VUE_APP_BASE_API || ''
  try {
    const response = await fetch(`${base}/api/admin/ai/stream`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${localStorage.getItem('weixin-token') || ''}`
      },
      body: JSON.stringify({
        title: form.title,
        industry: form.industry,
        template: currentTemplate.value ? `${currentTemplate.value.name}：${currentTemplate.value.tagline}` : '',
        audience: form.audience,
        tone: form.tone,
        wordCount: form.wordCount,
        requirement: form.requirement
      }),
      signal: controller.signal
    })

    if (!response.ok || !(response.headers.get('content-type') || '').includes('text/event-stream')) {
      const text = await response.text()
      let msg = `请求失败（${response.status}）`
      try {
        const body = JSON.parse(text)
        msg = body.message || msg
      } catch (error) {
        /* 非 JSON 响应 */
      }
      throw new Error(msg)
    }

    const reader = response.body.getReader()
    const decoder = new TextDecoder()
    let buffer = ''
    while (true) {
      const { value, done } = await reader.read()
      if (done) break
      buffer += decoder.decode(value, { stream: true })
      let index = buffer.indexOf('\n\n')
      while (index >= 0) {
        parseEvent(buffer.slice(0, index))
        buffer = buffer.slice(index + 2)
        index = buffer.indexOf('\n\n')
      }
    }
    if (status.value === 'streaming') {
      status.value = output.value ? 'done' : 'error'
      if (!output.value) {
        errorMsg.value = '连接已结束，但没有收到内容'
      }
    }
  } catch (error) {
    if (error.name === 'AbortError') {
      status.value = 'idle'
    } else {
      status.value = 'error'
      errorMsg.value = error.message || '生成失败'
      message.error(errorMsg.value)
    }
  } finally {
    controller = null
    loadHistory()
  }
}

async function stop() {
  controller?.abort()
  status.value = 'idle'
  const recordId = activeRecordId.value
  if (recordId) {
    try {
      await http.post(`/api/admin/ai/records/${recordId}/stop`)
    } catch (error) {
      /* 已结束则忽略 */
    }
  }
}

function useInEditor(recordId = activeRecordId.value) {
  if (!recordId) {
    message.warning('生成完成后才能引用')
    return
  }
  router.push({ path: '/editor', query: { aiId: String(recordId) } })
}

async function copy() {
  try {
    await navigator.clipboard.writeText(output.value)
    message.success('已复制 Markdown')
  } catch (error) {
    message.warning('当前浏览器不支持一键复制')
  }
}

async function viewRecord(row) {
  current.value = await http.get(`/api/admin/ai/records/${row.id}`)
  drawerOpen.value = true
}

function removeRecord(row) {
  Modal.confirm({
    title: '删除生成记录',
    content: '删除后无法恢复，确定继续吗？',
    okText: '删除',
    okType: 'danger',
    cancelText: '取消',
    async onOk() {
      await http.delete(`/api/admin/ai/records/${row.id}`)
      message.success('已删除')
      loadHistory()
    }
  })
}

onMounted(async () => {
  loadHistory()
  try {
    Object.assign(config, await http.get('/api/admin/ai/config'))
  } catch (error) {
    /* 拦截器已提示 */
  }
  if (route.query.template) {
    form.templateId = route.query.template
    onTemplateChange(route.query.template)
  }
})
</script>

<style scoped>
.ai-page {
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
  max-width: 620px;
  font-size: 13px;
  line-height: 1.8;
  color: var(--wx-muted);
}
.head-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}
.pill {
  padding: 5px 11px;
  font-size: 11.5px;
  color: #5c6b64;
  background: #ffffff;
  border: 1px solid var(--wx-line);
  border-radius: 999px;
}
.pill.model {
  color: #0f5f4a;
  background: #edf9f4;
  border-color: #cdeee1;
}

/* ---------- 布局 ---------- */
.layout {
  display: grid;
  grid-template-columns: 360px minmax(0, 1fr);
  gap: 18px;
  align-items: start;
}
.panel {
  padding: 18px 20px 20px;
  background: var(--wx-surface);
  border: 1px solid var(--wx-line);
  border-radius: 18px;
}
.form-panel {
  position: sticky;
  top: 76px;
}
.panel-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 14px;
}
.panel-head h3 {
  margin: 0;
  font-size: 15px;
  color: var(--wx-ink);
}
.panel-head p {
  margin: 4px 0 0;
  font-size: 12px;
  color: var(--wx-muted);
}
.head-left {
  display: flex;
  align-items: center;
  gap: 8px;
}
.head-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

/* ---------- 表单 ---------- */
.ai-form :deep(.ant-form-item) {
  margin-bottom: 14px;
}
.ai-form :deep(.ant-form-item-label > label) {
  font-size: 12.5px;
  color: #5c6b64;
}
.field-hint {
  margin: 6px 0 0;
  font-size: 11.5px;
  line-height: 1.6;
  color: #8a948f;
}

/* ---------- 输出 ---------- */
.output-panel {
  display: flex;
  flex-direction: column;
  min-height: 620px;
}
.empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  flex: 1;
  padding: 40px 20px;
  text-align: center;
}
.empty-icon {
  display: grid;
  place-items: center;
  width: 56px;
  height: 56px;
  margin-bottom: 14px;
  font-size: 26px;
  color: #1677ff;
  background: #eef4ff;
  border-radius: 16px;
}
.empty h4 {
  margin: 0 0 6px;
  font-size: 15px;
  color: var(--wx-ink);
}
.empty p {
  margin: 0;
  font-size: 12.5px;
  color: var(--wx-muted);
}
.output-body {
  flex: 1;
  min-height: 0;
}
.error-box {
  margin-bottom: 12px;
  padding: 10px 12px;
  font-size: 12.5px;
  line-height: 1.7;
  color: #a8071a;
  background: #fff1f0;
  border: 1px solid #ffccc7;
  border-radius: 10px;
}
.markdown-box {
  max-height: 560px;
  overflow-y: auto;
  padding: 16px;
  background: #fbfaf7;
  border: 1px solid var(--wx-line);
  border-radius: 12px;
}
.markdown-box pre {
  margin: 0;
  font-size: 12.5px;
  line-height: 1.9;
  color: #3d4a44;
  white-space: pre-wrap;
  word-break: break-word;
  font-family: Menlo, Monaco, Consolas, 'PingFang SC', monospace;
}
.paper {
  max-height: 560px;
  overflow-y: auto;
  padding: 22px 20px 26px;
  background: #ffffff;
  border: 1px solid var(--wx-line);
  border-radius: 14px;
}
.wx-title {
  margin: 0;
  font-size: 21px;
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
.cursor {
  display: inline-block;
  width: 7px;
  height: 16px;
  margin-left: 2px;
  vertical-align: -2px;
  background: #1677ff;
  animation: blink 1s steps(2, start) infinite;
}
@keyframes blink {
  to { visibility: hidden; }
}
.output-foot {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding-top: 14px;
  margin-top: 14px;
  border-top: 1px solid #f2eee6;
}
.count {
  font-size: 11.5px;
  color: #a3ada8;
}
.foot-actions {
  display: flex;
  gap: 8px;
}

/* ---------- 记录 ---------- */
.history-panel {
  padding-bottom: 8px;
}
.record-table :deep(.ant-table-thead > tr > th) {
  background: #fbfaf7;
  color: #6f7b75;
  font-size: 12.5px;
  font-weight: 600;
  border-bottom: 1px solid var(--wx-line);
}
.record-table :deep(.ant-table-tbody > tr > td) {
  border-bottom: 1px solid #f4f1ea;
}
.record-title {
  font-size: 13.5px;
  font-weight: 500;
  color: var(--wx-ink);
}
.record-sub {
  margin-top: 2px;
  font-size: 11.5px;
  color: #a3ada8;
}
.time {
  font-size: 12.5px;
  color: #7a847f;
  white-space: nowrap;
}

/* ---------- 抽屉 ---------- */
.drawer-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
}
.drawer-head h3 {
  margin: 0;
  font-size: 17px;
  line-height: 1.6;
  color: var(--wx-ink);
}
.drawer-meta {
  margin: 6px 0 16px;
  font-size: 12px;
  color: #9aa4a0;
}
.drawer-requirement {
  margin-bottom: 16px;
  padding: 12px 14px;
  background: #faf9f6;
  border-radius: 12px;
}
.drawer-requirement b {
  font-size: 12px;
  color: #5c6b64;
}
.drawer-requirement p {
  margin: 6px 0 0;
  font-size: 12.5px;
  line-height: 1.8;
  color: var(--wx-muted);
}
.drawer-paper {
  max-height: 480px;
  overflow-y: auto;
  margin-bottom: 16px;
  padding: 18px 16px;
  border: 1px solid var(--wx-line);
  border-radius: 14px;
}

@media (max-width: 1000px) {
  .layout {
    grid-template-columns: 1fr;
  }
  .form-panel {
    position: static;
  }
}
@media (max-width: 640px) {
  .panel {
    padding: 16px;
  }
  .output-panel {
    min-height: 480px;
  }
  .paper,
  .markdown-box {
    max-height: 420px;
  }
}
</style>
