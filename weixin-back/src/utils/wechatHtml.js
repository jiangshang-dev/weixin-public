import MarkdownIt from 'markdown-it'
import { wrapAppleCode, wrapCard } from './wxStyles.js'

const md = new MarkdownIt({ html: false, linkify: true, breaks: true })
let listDepth = 0

function esc(value) {
  return String(value || '').replace(/[&<>"']/g, (char) => ({
    '&': '&amp;',
    '<': '&lt;',
    '>': '&gt;',
    '"': '&quot;',
    "'": '&#39;'
  }[char]))
}

md.renderer.rules.heading_open = (tokens, idx) => {
  const level = Number(tokens[idx].tag.slice(1))
  const style = level <= 1
    ? 'margin:4px 0 16px;font-size:22px;line-height:1.45;font-weight:700;color:#1677ff;text-align:center;'
    : level === 2
      ? 'margin:26px 0 12px;padding:8px 12px;font-size:17px;line-height:1.5;font-weight:700;color:#1677ff;background:#f5f8ff;border-radius:6px;'
      : 'margin:18px 0 8px;font-size:16px;line-height:1.5;font-weight:700;color:#333333;'
  return `<section style="${style}">`
}
md.renderer.rules.heading_close = () => '</section>'

md.renderer.rules.paragraph_open = () => listDepth > 0
  ? ''
  : '<p style="margin:0 0 16px;font-size:16px;line-height:1.85;color:#333333;">'
md.renderer.rules.paragraph_close = () => listDepth > 0 ? '' : '</p>'
md.renderer.rules.bullet_list_open = () => {
  listDepth += 1
  return '<section style="margin:0 0 16px;">'
}
md.renderer.rules.bullet_list_close = () => {
  listDepth -= 1
  return '</section>'
}
md.renderer.rules.ordered_list_open = () => {
  listDepth += 1
  return '<section style="margin:0 0 16px;">'
}
md.renderer.rules.ordered_list_close = () => {
  listDepth -= 1
  return '</section>'
}
md.renderer.rules.list_item_open = (tokens, idx) => {
  const token = tokens[idx]
  const prefix = token.markup === '.' && token.info ? `${token.info}. ` : '· '
  return `<p style="margin:0 0 8px;font-size:16px;line-height:1.85;color:#333333;">${prefix}`
}
md.renderer.rules.list_item_close = () => '</p>'
md.renderer.rules.blockquote_open = () => '<section style="margin:0 0 16px;padding:12px 14px;background:#f7f7f7;border-left:4px solid #d9d9d9;">'
md.renderer.rules.blockquote_close = () => '</section>'
md.renderer.rules.hr = () => '<section style="margin:20px 0;border-top:1px solid #eeeeee;font-size:0;line-height:0;"> </section>'
md.renderer.rules.code_inline = (tokens, idx) => `<span style="padding:0 4px;background:#f5f5f5;border-radius:3px;font-size:14px;color:#c41d7f;">${esc(tokens[idx].content)}</span>`
md.renderer.rules.fence = (tokens, idx) => {
  const info = (tokens[idx].info || '').trim().split(/\s+/)[0].toLowerCase()
  const body = esc(tokens[idx].content)
  if (info === 'apple') {
    return wrapAppleCode(body)
  }
  return `<section style="margin:0 0 16px;padding:12px;background:#f6f8fa;border-radius:6px;font-size:14px;line-height:1.7;color:#333333;white-space:pre-wrap;">${body}</section>`
}
md.renderer.rules.image = (tokens, idx) => {
  const token = tokens[idx]
  const src = token.attrGet('src') || ''
  return `<img src="${esc(src)}" alt="${esc(token.content)}" style="width:100%;max-width:100%;display:block;margin:12px 0;border-radius:4px;" />`
}
md.renderer.rules.link_open = (tokens, idx) => `<a href="${esc(tokens[idx].attrGet('href') || '')}" style="color:#1677ff;text-decoration:none;">`
md.renderer.rules.table_open = () => '<table style="width:100%;border-collapse:collapse;margin:0 0 16px;font-size:15px;line-height:1.6;">'
md.renderer.rules.th_open = () => '<th style="border:1px solid #e5e5e5;padding:8px 10px;background:#f5f8ff;font-weight:700;text-align:left;">'
md.renderer.rules.td_open = () => '<td style="border:1px solid #e5e5e5;padding:8px 10px;">'

function renderMarkdown(markdown) {
  listDepth = 0
  return md.render(markdown || '').trim()
}

/**
 * 按 :::wx-id ... ::: 切分，普通段与卡片段分别渲染后拼接。
 */
function renderWithCards(markdown) {
  const source = markdown || ''
  const pattern = /:::wx-([a-z0-9-]+)\s*\n([\s\S]*?)\n\s*:::/gi
  let html = ''
  let lastIndex = 0
  let match
  while ((match = pattern.exec(source)) !== null) {
    const before = source.slice(lastIndex, match.index)
    if (before.trim()) {
      html += renderMarkdown(before)
    }
    html += wrapCard(match[1].toLowerCase(), renderMarkdown(match[2].trim()))
    lastIndex = pattern.lastIndex
  }
  const rest = source.slice(lastIndex)
  if (rest.trim()) {
    html += renderMarkdown(rest)
  }
  return html
}

export function toWechatHtml(markdown) {
  const inner = renderWithCards(markdown).trim()
  if (!inner) return ''
  return `<section style="font-size:16px;color:#333333;letter-spacing:0.5px;">${inner}</section>`
}
