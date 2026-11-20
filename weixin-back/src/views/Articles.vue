<template>
  <div class="articles-page">
    <div class="stats">
      <article v-for="item in stats" :key="item.key" class="stat" :class="item.tone">
        <span class="stat-label">{{ item.label }}</span>
        <strong class="stat-value">{{ item.value }}</strong>
        <span class="stat-hint">{{ item.hint }}</span>
      </article>
    </div>

    <section class="panel">
      <div class="panel-head">
        <div>
          <h3>发送记录</h3>
          <p>共 {{ records.length }} 条记录，成功提交后可到公众号后台查看草稿</p>
        </div>
        <a-button :loading="loading" @click="load">
          <ReloadOutlined /> 刷新
        </a-button>
      </div>

      <a-table
        :data-source="records"
        :columns="columns"
        row-key="id"
        :loading="loading"
        :pagination="{ pageSize: 10, hideOnSinglePage: true }"
        :scroll="{ x: 720 }"
        class="records-table"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'title'">
            <div class="title-cell">
              <span class="title-text">{{ record.title }}</span>
              <span class="title-meta">
                <em v-if="record.author">{{ record.author }}</em>
                <span v-if="record.digest" class="digest">{{ record.digest }}</span>
              </span>
            </div>
          </template>
          <template v-else-if="column.key === 'status'">
            <a-tag :color="statusColor(record.status)">{{ statusText(record.status) }}</a-tag>
          </template>
          <template v-else-if="column.key === 'createTime'">
            <span class="time">{{ formatTime(record.createTime) }}</span>
          </template>
          <template v-else-if="column.key === 'action'">
            <a-button type="link" size="small" @click="openDetail(record)">查看</a-button>
            <a-button type="link" size="small" :disabled="!record.contentMd" @click="loadToEditor(record)">
              载入编辑
            </a-button>
          </template>
        </template>
      </a-table>
    </section>

    <a-drawer v-model:open="drawerOpen" :width="480" title="文章详情">
      <template v-if="current">
        <div class="detail-head">
          <h3>{{ current.title }}</h3>
          <a-tag :color="statusColor(current.status)">{{ statusText(current.status) }}</a-tag>
        </div>

        <div class="detail-grid">
          <div><span>作者</span><b>{{ current.author || '-' }}</b></div>
          <div><span>提交时间</span><b>{{ formatTime(current.createTime) }}</b></div>
          <div><span>草稿 media_id</span><b class="mono">{{ current.draftMediaId || '-' }}</b></div>
          <div><span>同步信息</span><b>{{ current.syncMessage || '-' }}</b></div>
        </div>

        <div class="preview-head">
          <span>公众号正文预览</span>
          <a-button type="link" size="small" :disabled="!current.contentMd" @click="loadToEditor(current)">
            载入编辑
          </a-button>
        </div>
        <div class="preview-paper" v-html="current.content"></div>

        <a-button
          type="primary"
          block
          size="large"
          class="detail-action"
          :disabled="!current.contentMd"
          @click="loadToEditor(current)"
        >
          载入到编辑器
        </a-button>
      </template>
    </a-drawer>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { ReloadOutlined } from '@ant-design/icons-vue'
import http from '../api/http'

const router = useRouter()
const records = ref([])
const loading = ref(false)
const drawerOpen = ref(false)
const current = ref(null)

const columns = [
  { title: '文章', dataIndex: 'title', key: 'title' },
  { title: '状态', key: 'status', width: 130 },
  { title: '提交时间', key: 'createTime', width: 180 },
  { title: '操作', key: 'action', width: 155 }
]

const stats = computed(() => {
  const total = records.value.length
  const success = records.value.filter((item) => item.status === 1).length
  const failed = records.value.filter((item) => item.status === 2).length
  return [
    { key: 'total', label: '全部草稿', value: total, hint: '累计提交记录', tone: 'neutral' },
    { key: 'success', label: '已进草稿箱', value: success, hint: '可到公众号后台查看', tone: 'green' },
    { key: 'failed', label: '提交失败', value: failed, hint: '检查 IP 白名单或接口配置', tone: 'red' },
    { key: 'pending', label: '未同步', value: Math.max(0, total - success - failed), hint: '等待同步或已归档', tone: 'amber' }
  ]
})

function statusText(status) {
  if (status === 1) return '已进草稿箱'
  if (status === 2) return '失败'
  return '未同步'
}

function statusColor(status) {
  if (status === 1) return 'green'
  if (status === 2) return 'red'
  return 'default'
}

function formatTime(value) {
  if (!value) return '-'
  return String(value).replace('T', ' ').slice(0, 19)
}

async function load() {
  loading.value = true
  try {
    records.value = await http.get('/api/admin/articles')
  } finally {
    loading.value = false
  }
}

async function openDetail(row) {
  current.value = await http.get(`/api/admin/articles/${row.id}`)
  drawerOpen.value = true
}

function loadToEditor(row) {
  if (!row.contentMd) {
    message.warning('该记录没有 Markdown 原文（旧数据），无法载入编辑')
    return
  }
  drawerOpen.value = false
  router.push({ path: '/editor', query: { id: String(row.id) } })
}

onMounted(load)
</script>

<style scoped>
.articles-page {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

/* ---------- 统计卡 ---------- */
.stats {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 14px;
}
.stat {
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding: 16px 18px;
  background: var(--wx-surface);
  border: 1px solid var(--wx-line);
  border-radius: 16px;
  position: relative;
  overflow: hidden;
}
.stat::before {
  content: '';
  position: absolute;
  left: 0;
  top: 16px;
  bottom: 16px;
  width: 3px;
  border-radius: 0 3px 3px 0;
  background: #d8d2c6;
}
.stat.green::before { background: #52c41a; }
.stat.red::before { background: #ff4d4f; }
.stat.amber::before { background: #fa8c16; }
.stat-label {
  font-size: 12px;
  color: var(--wx-muted);
}
.stat-value {
  font-size: 26px;
  line-height: 1.3;
  color: var(--wx-ink);
  font-weight: 700;
}
.stat-hint {
  font-size: 11px;
  color: #a8b1ac;
}

/* ---------- 列表 ---------- */
.panel {
  padding: 18px 20px 8px;
  background: var(--wx-surface);
  border: 1px solid var(--wx-line);
  border-radius: 18px;
}
.panel-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 14px;
  margin-bottom: 12px;
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
.records-table :deep(.ant-table-thead > tr > th) {
  background: #fbfaf7;
  color: #6f7b75;
  font-size: 12.5px;
  font-weight: 600;
  border-bottom: 1px solid var(--wx-line);
}
.records-table :deep(.ant-table-tbody > tr > td) {
  border-bottom: 1px solid #f4f1ea;
}
.records-table :deep(.ant-table-tbody > tr:hover > td) {
  background: #fafcfb;
}
.title-cell {
  display: flex;
  flex-direction: column;
  gap: 3px;
  min-width: 0;
}
.title-text {
  font-size: 13.5px;
  color: var(--wx-ink);
  font-weight: 500;
}
.title-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  min-width: 0;
}
.title-meta em {
  flex: none;
  font-style: normal;
  font-size: 11px;
  color: #8a948f;
}
.digest {
  font-size: 11.5px;
  color: #a3ada8;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 420px;
}
.time {
  font-size: 12.5px;
  color: #7a847f;
  white-space: nowrap;
}

/* ---------- 详情 ---------- */
.detail-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
}
.detail-head h3 {
  margin: 0;
  font-size: 17px;
  line-height: 1.6;
  color: var(--wx-ink);
}
.detail-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px 14px;
  margin: 16px 0 20px;
  padding: 14px;
  background: #faf9f6;
  border-radius: 12px;
}
.detail-grid div {
  display: flex;
  flex-direction: column;
  gap: 2px;
  min-width: 0;
}
.detail-grid span {
  font-size: 11px;
  color: #a3ada8;
}
.detail-grid b {
  font-size: 12.5px;
  font-weight: 500;
  color: #3d4a44;
  word-break: break-all;
}
.detail-grid .mono {
  font-family: Menlo, Monaco, Consolas, monospace;
  font-size: 11.5px;
}
.preview-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 10px;
  font-size: 13px;
  font-weight: 600;
  color: var(--wx-ink);
}
.preview-paper {
  max-height: 460px;
  overflow-y: auto;
  padding: 18px 16px;
  background: #fff;
  border: 1px solid var(--wx-line);
  border-radius: 14px;
}
.detail-action {
  margin-top: 18px;
}

@media (max-width: 900px) {
  .stats {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
  .digest {
    max-width: 220px;
  }
}
</style>
