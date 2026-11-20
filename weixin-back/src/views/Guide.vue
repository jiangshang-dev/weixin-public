<template>
  <div class="guide-page">
    <section class="hero">
      <div>
        <p class="eyebrow">GUIDE · 使用说明</p>
        <h1>从排版到草稿箱，四步完成</h1>
        <p class="hero-desc">
          不需要写 HTML，也不需要记样式代码。用 Markdown 写正文，选好封面，剩下的交给我们。
        </p>
      </div>
      <a-button type="primary" size="large" @click="router.push('/templates')">去模板库看看</a-button>
    </section>

    <section class="steps">
      <article v-for="(step, index) in steps" :key="step.title" class="step">
        <span class="step-no">{{ String(index + 1).padStart(2, '0') }}</span>
        <h3>{{ step.title }}</h3>
        <p>{{ step.desc }}</p>
        <div class="step-tags">
          <span v-for="tag in step.tags" :key="tag">{{ tag }}</span>
        </div>
      </article>
    </section>

    <div class="two-col">
      <section class="panel">
        <div class="panel-head">
          <h3>可用的样式块</h3>
          <p>在编辑器工具栏点「样式」即可插入，提交时自动转成微信可用的内联样式。</p>
        </div>
        <div class="style-grid">
          <div v-for="group in CARD_STYLE_GROUPS" :key="group.name" class="style-group">
            <p class="group-name">{{ group.name }}</p>
            <div class="style-list">
              <span v-for="item in group.items" :key="item.id" class="style-chip">
                <i :style="{ background: item.sample }"></i>{{ item.name }}
              </span>
            </div>
          </div>
        </div>
        <div class="code-tip">
          <code>:::wx-title-bar</code>
          <span>样式块也可以手写，前后用 <code>:::</code> 包裹。</span>
        </div>
      </section>

      <section class="panel">
        <div class="panel-head">
          <h3>常见问题</h3>
          <p>提交前先看一遍，能省下不少排查时间。</p>
        </div>
        <div class="faq">
          <div v-for="item in faqs" :key="item.q" class="faq-item">
            <p class="q">{{ item.q }}</p>
            <p class="a">{{ item.a }}</p>
          </div>
        </div>
      </section>
    </div>

    <section class="notice">
      <div>
        <b>只写草稿，不会群发</b>
        <p>本工具通过公众号接口写入草稿箱，发布动作需要你到公众号后台手动完成。</p>
      </div>
      <a-button @click="router.push('/editor')">开始写文章</a-button>
    </section>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { CARD_STYLE_GROUPS } from '../utils/wxStyles.js'

const router = useRouter()

const steps = [
  {
    title: '选模板或从空白开始',
    desc: '模板库覆盖 14 个行业，套用后会带上标题、摘要与完整正文，替换文字即可。',
    tags: ['14 个行业', '可直接套用']
  },
  {
    title: '用 Markdown 写正文',
    desc: '标题、列表、引用、表格都能直接用。需要强调时，从工具栏「样式」插入卡片。',
    tags: ['实时预览', '样式块']
  },
  {
    title: '上传正文图片与封面',
    desc: '正文图片请用编辑器图片按钮上传到微信 CDN，外链图片发到公众号会被过滤。',
    tags: ['微信 CDN', '封面必填']
  },
  {
    title: '提交并到后台确认',
    desc: '提交后仅写入草稿箱。到公众号后台检查排版、封面与摘要，确认无误再发布。',
    tags: ['不会群发', '可载入编辑']
  }
]

const faqs = [
  {
    q: '提示 IP 不在白名单怎么办？',
    a: '到微信开发者平台「公众号 - 基础信息 - 开发密钥」添加当前服务器公网 IP 后重试。'
  },
  {
    q: '正文图片为什么不显示？',
    a: '公众号会过滤外链图片。请在编辑器里用图片按钮上传，系统会自动换成微信素材地址。'
  },
  {
    q: '历史记录能继续编辑吗？',
    a: '有 Markdown 原文的记录可以「载入编辑」，旧数据只有 HTML，只能查看预览。'
  },
  {
    q: '提交成功就算发布了吗？',
    a: '不是。提交只进入草稿箱，是否发布、何时发布，都需要到公众号后台手动操作。'
  }
]
</script>

<style scoped>
.guide-page {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.hero {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 24px;
  padding: 28px 30px;
  border-radius: 20px;
  color: #eef7f3;
  background:
    radial-gradient(circle at 90% 0%, rgba(124, 224, 195, 0.2), transparent 46%),
    linear-gradient(135deg, #14302a 0%, #1d3f35 60%, #245347 100%);
  flex-wrap: wrap;
}
.eyebrow {
  margin: 0 0 10px;
  font-size: 11px;
  letter-spacing: 2.4px;
  color: #7ce0c3;
  font-weight: 600;
}
.hero h1 {
  margin: 0;
  font-size: 26px;
  line-height: 1.4;
  color: #ffffff;
}
.hero-desc {
  margin: 10px 0 0;
  max-width: 520px;
  font-size: 13px;
  line-height: 1.9;
  color: rgba(238, 247, 243, 0.68);
}
.hero :deep(.ant-btn-primary) {
  background: #7ce0c3;
  border-color: #7ce0c3;
  color: #10231e !important;
  font-weight: 600;
}
.hero :deep(.ant-btn-primary:hover) {
  background: #9df0d6 !important;
  border-color: #9df0d6 !important;
  color: #10231e !important;
}

.steps {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 14px;
}
.step {
  padding: 18px;
  background: var(--wx-surface);
  border: 1px solid var(--wx-line);
  border-radius: 16px;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}
.step:hover {
  transform: translateY(-3px);
  box-shadow: 0 14px 30px rgba(22, 48, 42, 0.08);
}
.step-no {
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 1px;
  color: #1677ff;
}
.step h3 {
  margin: 8px 0 6px;
  font-size: 14px;
  color: var(--wx-ink);
}
.step p {
  margin: 0;
  font-size: 12.5px;
  line-height: 1.8;
  color: var(--wx-muted);
}
.step-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-top: 12px;
}
.step-tags span {
  padding: 2px 8px;
  font-size: 10.5px;
  color: #5c6b64;
  background: #f5f6f3;
  border-radius: 999px;
}

.two-col {
  display: grid;
  grid-template-columns: 1.1fr 0.9fr;
  gap: 18px;
  align-items: start;
}
.panel {
  padding: 20px;
  background: var(--wx-surface);
  border: 1px solid var(--wx-line);
  border-radius: 18px;
}
.panel-head h3 {
  margin: 0;
  font-size: 15px;
  color: var(--wx-ink);
}
.panel-head p {
  margin: 4px 0 16px;
  font-size: 12px;
  line-height: 1.7;
  color: var(--wx-muted);
}
.style-group {
  margin-bottom: 14px;
}
.group-name {
  margin: 0 0 8px;
  font-size: 11px;
  letter-spacing: 1px;
  color: #a3ada8;
}
.style-list {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}
.style-chip {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 5px 10px;
  font-size: 12px;
  color: #4b5751;
  background: #fbfaf7;
  border: 1px solid #f0ece3;
  border-radius: 8px;
}
.style-chip i {
  width: 9px;
  height: 9px;
  border-radius: 3px;
  border: 1px solid rgba(0, 0, 0, 0.06);
}
.code-tip {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-top: 8px;
  padding: 10px 12px;
  font-size: 11.5px;
  color: #7a847f;
  background: #faf9f6;
  border-radius: 10px;
  flex-wrap: wrap;
}
.code-tip code,
.panel code {
  padding: 1px 5px;
  font-size: 11px;
  color: #5c6b64;
  background: #f1efe9;
  border-radius: 4px;
}

.faq {
  display: flex;
  flex-direction: column;
}
.faq-item {
  padding: 12px 0;
  border-bottom: 1px dashed #efeae0;
}
.faq-item:last-child {
  border-bottom: 0;
  padding-bottom: 0;
}
.faq-item .q {
  margin: 0 0 4px;
  font-size: 13px;
  font-weight: 600;
  color: #3d4a44;
}
.faq-item .a {
  margin: 0;
  font-size: 12.5px;
  line-height: 1.8;
  color: var(--wx-muted);
}

.notice {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 18px;
  padding: 20px 24px;
  background: #fffdf8;
  border: 1px dashed #dcd3c2;
  border-radius: 18px;
  flex-wrap: wrap;
}
.notice b {
  font-size: 14px;
  color: var(--wx-ink);
}
.notice p {
  margin: 4px 0 0;
  font-size: 12.5px;
  color: var(--wx-muted);
}

@media (max-width: 1100px) {
  .steps {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
  .two-col {
    grid-template-columns: 1fr;
  }
}
@media (max-width: 640px) {
  .hero {
    padding: 22px;
  }
  .hero h1 {
    font-size: 22px;
  }
  .steps {
    grid-template-columns: 1fr;
  }
}
</style>
