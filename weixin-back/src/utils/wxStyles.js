/** 微信草稿可用的卡片样式（纯内联 style） */
export const CARD_STYLES = [
  {
    id: 'soft',
    name: '柔灰卡片',
    wrap: (inner) =>
      `<section style="margin:0 0 16px;padding:14px 16px;background:#f5f5f5;border-radius:8px;color:#333333;font-size:15px;line-height:1.8;">${inner}</section>`
  },
  {
    id: 'blue',
    name: '蓝提示',
    wrap: (inner) =>
      `<section style="margin:0 0 16px;padding:14px 16px;background:#f0f7ff;border-left:4px solid #1677ff;border-radius:0 8px 8px 0;color:#1a1a1a;font-size:15px;line-height:1.8;">${inner}</section>`
  },
  {
    id: 'green',
    name: '绿提示',
    wrap: (inner) =>
      `<section style="margin:0 0 16px;padding:14px 16px;background:#f6ffed;border-left:4px solid #52c41a;border-radius:0 8px 8px 0;color:#1a1a1a;font-size:15px;line-height:1.8;">${inner}</section>`
  },
  {
    id: 'warn',
    name: '橙警示',
    wrap: (inner) =>
      `<section style="margin:0 0 16px;padding:14px 16px;background:#fff7e6;border-left:4px solid #fa8c16;border-radius:0 8px 8px 0;color:#1a1a1a;font-size:15px;line-height:1.8;">${inner}</section>`
  },
  {
    id: 'danger',
    name: '红警告',
    wrap: (inner) =>
      `<section style="margin:0 0 16px;padding:14px 16px;background:#fff1f0;border-left:4px solid #ff4d4f;border-radius:0 8px 8px 0;color:#1a1a1a;font-size:15px;line-height:1.8;">${inner}</section>`
  },
  {
    id: 'quote',
    name: '引用卡',
    wrap: (inner) =>
      `<section style="margin:0 0 16px;padding:14px 18px;background:#fafafa;border-left:4px solid #bfbfbf;color:#595959;font-size:15px;line-height:1.8;font-style:italic;">${inner}</section>`
  },
  {
    id: 'clip',
    name: '夹子',
    wrap: (inner) =>
      `<section style="margin:18px 0 16px;padding:18px 16px 14px;background:#fffef9;border:1px dashed #d9d9d9;border-radius:8px;position:relative;">` +
      `<p style="margin:-28px 0 10px;text-align:center;font-size:22px;line-height:1;">📎</p>` +
      `<section style="color:#333333;font-size:15px;line-height:1.8;">${inner}</section></section>`
  },
  {
    id: 'pin',
    name: '钉子',
    wrap: (inner) =>
      `<section style="margin:18px 0 16px;padding:18px 16px 14px;background:#ffffff;border:1px solid #e8e8e8;border-radius:8px;box-shadow:0 2px 8px rgba(0,0,0,0.06);">` +
      `<p style="margin:-28px 0 10px;text-align:center;font-size:20px;line-height:1;">📌</p>` +
      `<section style="color:#333333;font-size:15px;line-height:1.8;">${inner}</section></section>`
  },
  {
    id: 'wood',
    name: '木板',
    wrap: (inner) =>
      `<section style="margin:0 0 16px;padding:16px;background:#f3e5c8;border:2px solid #c4a574;border-radius:4px;color:#5c4033;font-size:15px;line-height:1.8;">${inner}</section>`
  },
  {
    id: 'blackboard',
    name: '黑板',
    wrap: (inner) =>
      `<section style="margin:0 0 16px;padding:16px;background:#2d4a3e;border:3px solid #1a2e26;border-radius:4px;color:#f5f5dc;font-size:15px;line-height:1.8;">${inner}</section>`
  },
  {
    id: 'sticky',
    name: '便签',
    wrap: (inner) =>
      `<section style="margin:0 0 16px;padding:16px;background:#fff9c4;border-radius:2px;box-shadow:2px 3px 0 rgba(0,0,0,0.08);color:#5d4e37;font-size:15px;line-height:1.8;">${inner}</section>`
  },
  {
    id: 'shadow',
    name: '阴影抬起',
    wrap: (inner) =>
      `<section style="margin:0 0 16px;padding:16px;background:#ffffff;border-radius:10px;box-shadow:0 4px 14px rgba(0,0,0,0.1);color:#333333;font-size:15px;line-height:1.8;">${inner}</section>`
  },
  {
    id: 'ribbon',
    name: '缎带顶条',
    wrap: (inner) =>
      `<section style="margin:0 0 16px;overflow:hidden;border-radius:8px;border:1px solid #e6f0ff;">` +
      `<section style="padding:8px 14px;background:#1677ff;color:#ffffff;font-size:14px;font-weight:700;">重点提示</section>` +
      `<section style="padding:14px 16px;background:#f7fbff;color:#333333;font-size:15px;line-height:1.8;">${inner}</section></section>`
  },
  {
    id: 'double',
    name: '双线边框',
    wrap: (inner) =>
      `<section style="margin:0 0 16px;padding:14px 16px;border:3px double #8c8c8c;border-radius:4px;background:#ffffff;color:#333333;font-size:15px;line-height:1.8;">${inner}</section>`
  }
]

export const CARD_STYLE_MAP = Object.fromEntries(CARD_STYLES.map((item) => [item.id, item]))

export function wrapAppleCode(escapedText) {
  const dots =
    '<span style="display:inline-block;width:10px;height:10px;border-radius:50%;background:#ff5f56;margin-right:6px;"></span>' +
    '<span style="display:inline-block;width:10px;height:10px;border-radius:50%;background:#ffbd2e;margin-right:6px;"></span>' +
    '<span style="display:inline-block;width:10px;height:10px;border-radius:50%;background:#27c93f;"></span>'
  return (
    `<section style="margin:0 0 16px;overflow:hidden;border-radius:10px;background:#1e1e1e;">` +
    `<section style="padding:10px 14px;background:#2d2d2d;">${dots}</section>` +
    `<section style="padding:14px 16px;font-size:13px;line-height:1.7;color:#d4d4d4;white-space:pre-wrap;font-family:Menlo,Monaco,Consolas,monospace;">${escapedText}</section>` +
    `</section>`
  )
}

export function wrapCard(styleId, innerHtml) {
  const style = CARD_STYLE_MAP[styleId] || CARD_STYLE_MAP.soft
  return style.wrap(innerHtml || '')
}

export function buildCardSnippet(styleId, selectedText) {
  const body = (selectedText || '在这里填写内容').trim() || '在这里填写内容'
  return `\n\n:::wx-${styleId}\n${body}\n:::\n\n`
}

export function buildAppleCodeSnippet(selectedText) {
  const body = (selectedText || 'console.log("hello")').trim() || 'console.log("hello")'
  return `\n\n\`\`\`apple\n${body}\n\`\`\`\n\n`
}
