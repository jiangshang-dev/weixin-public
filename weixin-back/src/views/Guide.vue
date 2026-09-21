<template>
  <a-card title="使用说明">
    <a-typography>
      <a-typography-title :level="4">写草稿</a-typography-title>
      <a-typography-paragraph>
        用 Markdown 工具栏排版即可，不必写 HTML。右侧「公众号效果」是转换后的微信内联样式预览。
      </a-typography-paragraph>

      <a-typography-title :level="4">样式块</a-typography-title>
      <a-typography-paragraph>
        工具栏点「样式」，可选柔灰/蓝绿橙红提示、引用、夹子、钉子、木板、黑板、便签、阴影、缎带、双线边框，以及苹果三色点代码框。
        插入后会生成 <code>:::wx-xxx</code> 或 <code>```apple</code>，提交时自动转成微信可用 HTML。
      </a-typography-paragraph>

      <a-typography-title :level="4">图片</a-typography-title>
      <a-typography-paragraph>
        正文图片必须点编辑器图片按钮上传到微信 CDN。粘贴外链图片发到公众号会被滤掉。
        封面单独选择，提交草稿箱必填。
      </a-typography-paragraph>

      <a-typography-title :level="4">发送记录</a-typography-title>
      <a-typography-paragraph>
        「发送记录」可查看历史提交状态与正文预览。有 Markdown 原文的记录可「载入编辑」后再发（需重新选封面）。
      </a-typography-paragraph>

      <a-typography-title :level="4">接口与白名单</a-typography-title>
      <a-typography-paragraph>
        本工具只写入公众号草稿箱，不会群发。若提示 IP 不在白名单，请到微信开发者平台「公众号 - 基础信息 - 开发密钥」添加当前服务器公网 IP。
      </a-typography-paragraph>
    </a-typography>
  </a-card>
</template>
