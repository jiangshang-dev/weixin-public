<template>
  <div class="page">
    <header class="bar">
      <strong>写入公众号草稿箱</strong>
      <span>{{ me.realName || me.username }} · {{ me.ip }}</span>
      <a-button type="link" @click="logout">退出</a-button>
    </header>
    <div class="grid">
      <a-card title="文章">
        <a-form layout="vertical">
          <a-form-item label="标题"><a-input v-model:value="form.title" :maxlength="64" /></a-form-item>
          <a-row :gutter="12">
            <a-col :span="12"><a-form-item label="作者"><a-input v-model:value="form.author" :maxlength="16" /></a-form-item></a-col>
            <a-col :span="12"><a-form-item label="摘要"><a-input v-model:value="form.digest" :maxlength="120" /></a-form-item></a-col>
          </a-row>
          <a-form-item label="封面">
            <input type="file" accept="image/*" @change="onCover" />
            <img v-if="coverUrl" :src="coverUrl" alt="" class="cover" />
          </a-form-item>
          <a-form-item label="正文">
            <p class="tools">用工具栏排版即可，不用写 HTML。图片请点编辑器里的图片按钮上传，外链图片发到公众号会被滤掉。</p>
            <MdEditor v-model="form.content" language="zh-CN" :preview="false" :on-upload-img="onUploadImg" style="height: 460px" />
          </a-form-item>
          <a-button type="primary" :loading="publishing" @click="publish">提交到草稿箱</a-button>
        </a-form>
      </a-card>
      <div>
        <a-card title="公众号效果">
          <div class="phone" v-html="previewHtml"></div>
        </a-card>
        <a-card title="最近提交" style="margin-top: 16px">
          <div v-for="item in records" :key="item.id" class="row">
            <b>{{ item.title }}</b>
            <span>{{ statusText(item.status) }}</span>
            <p>{{ item.syncMessage || '还没同步' }}</p>
          </div>
          <a-empty v-if="!records.length" />
        </a-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { MdEditor } from 'md-editor-v3'
import 'md-editor-v3/lib/style.css'
import http from '../api/http'
import { toWechatHtml } from '../utils/wechatHtml'

const router = useRouter()
const me = reactive({ username: '', realName: '', ip: '' })
const form = reactive({
  title: '新品发布',
  author: '极构',
  digest: '这是一篇从系统直接写入草稿箱的测试文章。',
  content: `# 新品发布

把这段文字换成产品介绍。选中文字后，用工具栏设置标题、加粗和列表。

## 核心亮点

- 旗舰处理器
- 120Hz 高刷
- 5000mAh 电池

> 图片请用编辑器工具栏上传，不要粘贴外链。`
})
const coverFile = ref(null)
const coverUrl = ref('')
const publishing = ref(false)
const records = ref([])
const previewHtml = computed(() => toWechatHtml(form.content) || '<p style="color:#8c8378">正文会显示在这里</p>')

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

async function publish() {
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
    data.append('content', toWechatHtml(form.content))
    data.append('cover', coverFile.value)
    const article = await http.post('/api/admin/draft', data)
    message.success(article.syncMessage || '已写入草稿箱')
    await load()
  } finally {
    publishing.value = false
  }
}

function statusText(status) {
  if (status === 1) return '已进草稿箱'
  if (status === 2) return '失败'
  return '未同步'
}

async function load() {
  records.value = await http.get('/api/admin/articles')
}

function logout() {
  localStorage.removeItem('weixin-token')
  router.push('/login')
}

onMounted(async () => {
  Object.assign(me, await http.get('/api/admin/me'))
  await load()
})
</script>

<style scoped>
.page { min-height: 100vh; background: #f6f1ea; }
.bar { display: flex; align-items: center; gap: 12px; padding: 12px 20px; background: #1c2b27; color: #fff; }
.bar span { margin-left: auto; color: #d9d0c3; }
.grid { display: grid; grid-template-columns: 1.2fr 0.8fr; gap: 16px; padding: 16px; align-items: start; }
.cover { display: block; width: 160px; height: 90px; object-fit: cover; margin-top: 8px; border-radius: 8px; }
.tools { display: flex; gap: 8px; align-items: center; margin-bottom: 8px; color: #6f675e; }
.phone { width: 320px; max-width: 100%; min-height: 420px; margin: 0 auto; padding: 16px; background: #fff; border: 1px solid #efe8dc; border-radius: 24px; }
.row { padding: 8px 0; border-bottom: 1px solid #efe8dc; }
.row p { margin: 4px 0 0; color: #6f675e; }
@media (max-width: 900px) { .grid { grid-template-columns: 1fr; } }
</style>
