<template>
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
          <p v-if="fromHistory" class="hint">从记录载入后需重新选择封面再提交。</p>
        </a-form-item>
        <a-form-item label="正文">
          <p class="tools">用工具栏排版；点「样式」可选卡片或苹果代码框。图片请用编辑器图片按钮上传。</p>
          <MdEditor
            ref="editorRef"
            v-model="form.content"
            language="zh-CN"
            :preview="false"
            :toolbars="toolbars"
            :on-upload-img="onUploadImg"
            style="height: 620px"
          >
            <template #defToolbars>
              <DropdownToolbar title="样式" :visible="styleOpen" @onChange="(v) => (styleOpen = v)">
                <template #trigger>
                  <span class="style-trigger">样式</span>
                </template>
                <template #overlay>
                  <div class="style-menu">
                    <button type="button" class="style-item accent" @click="insertApple">苹果代码框</button>
                    <button
                      v-for="item in CARD_STYLES"
                      :key="item.id"
                      type="button"
                      class="style-item"
                      @click="insertCard(item.id)"
                    >
                      {{ item.name }}
                    </button>
                  </div>
                </template>
              </DropdownToolbar>
            </template>
          </MdEditor>
        </a-form-item>
        <a-button type="primary" :loading="publishing" @click="publish">提交到草稿箱</a-button>
      </a-form>
    </a-card>
    <a-card title="公众号效果">
      <div class="phone" v-html="previewHtml"></div>
    </a-card>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { message } from 'ant-design-vue'
import { DropdownToolbar, MdEditor } from 'md-editor-v3'
import 'md-editor-v3/lib/style.css'
import http from '../api/http'
import { toWechatHtml } from '../utils/wechatHtml.js'
import { CARD_STYLES, buildAppleCodeSnippet, buildCardSnippet } from '../utils/wxStyles.js'

const route = useRoute()
const editorRef = ref(null)
const styleOpen = ref(false)
const fromHistory = ref(false)
const form = reactive({
  title: '新品发布',
  author: '极构',
  digest: '这是一篇从系统直接写入草稿箱的测试文章。',
  content: `# 新品发布

把这段文字换成产品介绍。选中文字后，用工具栏「样式」套卡片。

## 核心亮点

- 旗舰处理器
- 120Hz 高刷
- 5000mAh 电池

:::wx-soft
可以在工具栏点「样式」插入夹子、木板、黑板等卡片。
:::

\`\`\`apple
console.log('苹果风格代码框')
\`\`\`
`
})
const coverFile = ref(null)
const coverUrl = ref('')
const publishing = ref(false)
const previewHtml = computed(() => toWechatHtml(form.content) || '<p style="color:#8c8378">正文会显示在这里</p>')

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

watch(() => route.query.id, (id) => {
  if (id) loadFromHistory(id)
})

onMounted(() => {
  if (route.query.id) {
    loadFromHistory(route.query.id)
  }
})
</script>

<style scoped>
.grid { display: grid; grid-template-columns: 1.15fr 0.85fr; gap: 16px; align-items: start; }
.cover { display: block; width: 160px; height: 90px; object-fit: cover; margin-top: 8px; border-radius: 8px; }
.tools, .hint { margin-bottom: 8px; color: #6f675e; font-size: 13px; }
.phone { width: 320px; max-width: 100%; min-height: 480px; margin: 0 auto; padding: 16px; background: #fff; border: 1px solid #efe8dc; border-radius: 24px; }
.style-trigger { font-size: 13px; padding: 0 4px; }
.style-menu { min-width: 140px; max-height: 320px; overflow: auto; padding: 6px; background: #fff; border: 1px solid #eee; border-radius: 8px; box-shadow: 0 6px 18px rgba(0,0,0,0.08); }
.style-item { display: block; width: 100%; text-align: left; border: 0; background: transparent; padding: 8px 10px; border-radius: 6px; cursor: pointer; color: #333; }
.style-item:hover { background: #f5f8ff; color: #1677ff; }
.style-item.accent { font-weight: 600; color: #1677ff; border-bottom: 1px solid #f0f0f0; margin-bottom: 4px; }
@media (max-width: 960px) { .grid { grid-template-columns: 1fr; } }
</style>
