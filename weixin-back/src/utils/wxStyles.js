/** 微信草稿可用的卡片样式（纯内联 style） */
export const CARD_STYLES = [
  {
    id: 'soft',
    name: '柔灰卡片',
    group: '基础卡片',
    sample: '#f5f5f5',
    wrap: (inner) =>
      `<section style="margin:0 0 16px;padding:14px 16px;background:#f5f5f5;border-radius:8px;color:#333333;font-size:15px;line-height:1.8;">${inner}</section>`
  },
  {
    id: 'blue',
    name: '蓝提示',
    group: '提示强调',
    sample: '#1677ff',
    wrap: (inner) =>
      `<section style="margin:0 0 16px;padding:14px 16px;background:#f0f7ff;border-left:4px solid #1677ff;border-radius:0 8px 8px 0;color:#1a1a1a;font-size:15px;line-height:1.8;">${inner}</section>`
  },
  {
    id: 'green',
    name: '绿提示',
    group: '提示强调',
    sample: '#52c41a',
    wrap: (inner) =>
      `<section style="margin:0 0 16px;padding:14px 16px;background:#f6ffed;border-left:4px solid #52c41a;border-radius:0 8px 8px 0;color:#1a1a1a;font-size:15px;line-height:1.8;">${inner}</section>`
  },
  {
    id: 'warn',
    name: '橙警示',
    group: '提示强调',
    sample: '#fa8c16',
    wrap: (inner) =>
      `<section style="margin:0 0 16px;padding:14px 16px;background:#fff7e6;border-left:4px solid #fa8c16;border-radius:0 8px 8px 0;color:#1a1a1a;font-size:15px;line-height:1.8;">${inner}</section>`
  },
  {
    id: 'danger',
    name: '红警告',
    group: '提示强调',
    sample: '#ff4d4f',
    wrap: (inner) =>
      `<section style="margin:0 0 16px;padding:14px 16px;background:#fff1f0;border-left:4px solid #ff4d4f;border-radius:0 8px 8px 0;color:#1a1a1a;font-size:15px;line-height:1.8;">${inner}</section>`
  },
  {
    id: 'quote',
    name: '引用卡',
    group: '装饰元素',
    sample: '#bfbfbf',
    wrap: (inner) =>
      `<section style="margin:0 0 16px;padding:14px 18px;background:#fafafa;border-left:4px solid #bfbfbf;color:#595959;font-size:15px;line-height:1.8;font-style:italic;">${inner}</section>`
  },
  {
    id: 'clip',
    name: '夹子',
    group: '装饰元素',
    sample: '#fffef9',
    wrap: (inner) =>
      `<section style="margin:18px 0 16px;padding:18px 16px 14px;background:#fffef9;border:1px dashed #d9d9d9;border-radius:8px;position:relative;">` +
      `<p style="margin:-28px 0 10px;text-align:center;font-size:22px;line-height:1;">📎</p>` +
      `<section style="color:#333333;font-size:15px;line-height:1.8;">${inner}</section></section>`
  },
  {
    id: 'pin',
    name: '钉子',
    group: '装饰元素',
    sample: '#ffffff',
    wrap: (inner) =>
      `<section style="margin:18px 0 16px;padding:18px 16px 14px;background:#ffffff;border:1px solid #e8e8e8;border-radius:8px;box-shadow:0 2px 8px rgba(0,0,0,0.06);">` +
      `<p style="margin:-28px 0 10px;text-align:center;font-size:20px;line-height:1;">📌</p>` +
      `<section style="color:#333333;font-size:15px;line-height:1.8;">${inner}</section></section>`
  },
  {
    id: 'wood',
    name: '木板',
    group: '装饰元素',
    sample: '#c4a574',
    wrap: (inner) =>
      `<section style="margin:0 0 16px;padding:16px;background:#f3e5c8;border:2px solid #c4a574;border-radius:4px;color:#5c4033;font-size:15px;line-height:1.8;">${inner}</section>`
  },
  {
    id: 'blackboard',
    name: '黑板',
    group: '装饰元素',
    sample: '#2d4a3e',
    dark: true,
    wrap: (inner) =>
      `<section style="margin:0 0 16px;padding:16px;background:#2d4a3e;border:3px solid #1a2e26;border-radius:4px;color:#f5f5dc;font-size:15px;line-height:1.8;">${inner}</section>`
  },
  {
    id: 'sticky',
    name: '便签',
    group: '装饰元素',
    sample: '#fff9c4',
    wrap: (inner) =>
      `<section style="margin:0 0 16px;padding:16px;background:#fff9c4;border-radius:2px;box-shadow:2px 3px 0 rgba(0,0,0,0.08);color:#5d4e37;font-size:15px;line-height:1.8;">${inner}</section>`
  },
  {
    id: 'shadow',
    name: '阴影抬起',
    group: '装饰元素',
    sample: '#ffffff',
    wrap: (inner) =>
      `<section style="margin:0 0 16px;padding:16px;background:#ffffff;border-radius:10px;box-shadow:0 4px 14px rgba(0,0,0,0.1);color:#333333;font-size:15px;line-height:1.8;">${inner}</section>`
  },
  {
    id: 'ribbon',
    name: '缎带顶条',
    group: '提示强调',
    sample: '#1677ff',
    wrap: (inner) =>
      `<section style="margin:0 0 16px;overflow:hidden;border-radius:8px;border:1px solid #e6f0ff;">` +
      `<section style="padding:8px 14px;background:#1677ff;color:#ffffff;font-size:14px;font-weight:700;">重点提示</section>` +
      `<section style="padding:14px 16px;background:#f7fbff;color:#333333;font-size:15px;line-height:1.8;">${inner}</section></section>`
  },
  {
    id: 'double',
    name: '双线边框',
    group: '装饰元素',
    sample: '#8c8c8c',
    wrap: (inner) =>
      `<section style="margin:0 0 16px;padding:14px 16px;border:3px double #8c8c8c;border-radius:4px;background:#ffffff;color:#333333;font-size:15px;line-height:1.8;">${inner}</section>`
  },
  {
    id: 'title-bar',
    name: '章节标题条',
    group: '基础卡片',
    sample: '#1677ff',
    wrap: (inner) =>
      `<section style="margin:26px 0 14px;padding:2px 0 2px 12px;border-left:4px solid #1677ff;font-size:16px;line-height:1.7;font-weight:700;color:#1a1a1a;">${inner}</section>`
  },
  {
    id: 'accent',
    name: '深色金句',
    group: '提示强调',
    sample: '#16302a',
    dark: true,
    wrap: (inner) =>
      `<section style="margin:0 0 18px;padding:20px 18px;background:#16302a;border-radius:12px;color:#eaf3ef;font-size:15px;line-height:1.9;">${inner}</section>`
  },
  {
    id: 'notice',
    name: '小喇叭提示',
    group: '提示强调',
    sample: '#fa8c16',
    wrap: (inner) =>
      `<section style="margin:0 0 16px;padding:14px 16px;background:#fffaf0;border:1px solid #ffe0b2;border-radius:10px;">` +
      `<p style="margin:0 0 6px;font-size:14px;font-weight:700;color:#d46b08;">📢 温馨提示</p>` +
      `<section style="font-size:15px;line-height:1.85;color:#5c4718;">${inner}</section></section>`
  },
  {
    id: 'cta',
    name: '文末引导',
    group: '基础卡片',
    sample: '#f7fbff',
    wrap: (inner) =>
      `<section style="margin:8px 0 18px;padding:18px 16px;background:#f7fbff;border:1px solid #dbe7ff;border-radius:12px;text-align:center;">` +
      `<section style="font-size:14px;line-height:2;color:#4b5563;">${inner}</section></section>`
  },
  {
    id: 'dashed',
    name: '虚线手账',
    group: '装饰元素',
    sample: '#d6c9b4',
    wrap: (inner) =>
      `<section style="margin:0 0 18px;padding:16px;background:#fffdf8;border:1px dashed #d6c9b4;border-radius:12px;color:#4a4238;font-size:15px;line-height:1.85;">${inner}</section>`
  },
  {
    id: 'timeline',
    name: '时间轴',
    group: '装饰元素',
    sample: '#dbeafe',
    wrap: (inner) =>
      `<section style="margin:0 0 18px;padding:2px 0 2px 16px;border-left:2px solid #dbeafe;color:#333333;font-size:15px;line-height:1.85;">${inner}</section>`
  },
  {
    id: 'quote-mark',
    name: '大引号',
    group: '装饰元素',
    sample: '#c9d3e3',
    wrap: (inner) =>
      `<section style="margin:0 0 18px;padding:16px 18px 14px;background:#f7f8fa;border-radius:12px;">` +
      `<p style="margin:0 0 4px;font-size:30px;line-height:1;color:#c9d3e3;font-family:Georgia,serif;">“</p>` +
      `<section style="font-size:15px;line-height:1.9;color:#4a5568;font-style:italic;">${inner}</section></section>`
  }
]

export const CARD_STYLE_MAP = Object.fromEntries(CARD_STYLES.map((item) => [item.id, item]))

export const CARD_STYLE_GROUPS = CARD_STYLES.reduce((groups, item) => {
  const group = groups.find((entry) => entry.name === (item.group || '其他'))
  if (group) {
    group.items.push(item)
  } else {
    groups.push({ name: item.group || '其他', items: [item] })
  }
  return groups
}, [])

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
