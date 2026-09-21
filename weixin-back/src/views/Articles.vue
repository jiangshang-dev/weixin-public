<template>
  <a-card title="发送记录">
    <a-table :data-source="records" :columns="columns" row-key="id" :loading="loading" :pagination="{ pageSize: 10 }">
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'status'">
          <a-tag :color="statusColor(record.status)">{{ statusText(record.status) }}</a-tag>
        </template>
        <template v-else-if="column.key === 'createTime'">
          {{ formatTime(record.createTime) }}
        </template>
        <template v-else-if="column.key === 'action'">
          <a-button type="link" @click="openDetail(record)">查看</a-button>
          <a-button type="link" @click="loadToEditor(record)" :disabled="!record.contentMd">载入编辑</a-button>
        </template>
      </template>
    </a-table>

    <a-drawer v-model:open="drawerOpen" title="文章详情" width="420">
      <template v-if="current">
        <p><b>标题：</b>{{ current.title }}</p>
        <p><b>作者：</b>{{ current.author || '-' }}</p>
        <p><b>状态：</b>{{ statusText(current.status) }}</p>
        <p><b>时间：</b>{{ formatTime(current.createTime) }}</p>
        <p><b>同步信息：</b>{{ current.syncMessage || '-' }}</p>
        <p><b>草稿 media_id：</b>{{ current.draftMediaId || '-' }}</p>
        <a-divider>公众号正文预览</a-divider>
        <div class="preview" v-html="current.content"></div>
        <a-button type="primary" block style="margin-top: 16px" :disabled="!current.contentMd" @click="loadToEditor(current)">
          载入到编辑器
        </a-button>
      </template>
    </a-drawer>
  </a-card>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import http from '../api/http'

const router = useRouter()
const records = ref([])
const loading = ref(false)
const drawerOpen = ref(false)
const current = ref(null)

const columns = [
  { title: '标题', dataIndex: 'title', key: 'title' },
  { title: '作者', dataIndex: 'author', key: 'author', width: 100 },
  { title: '状态', key: 'status', width: 120 },
  { title: '时间', key: 'createTime', width: 180 },
  { title: '操作', key: 'action', width: 180 }
]

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
  router.push({ path: '/editor', query: { id: String(row.id) } })
}

onMounted(load)
</script>

<style scoped>
.preview { padding: 12px; background: #fff; border: 1px solid #efe8dc; border-radius: 12px; }
</style>
