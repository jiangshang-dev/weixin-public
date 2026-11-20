import MarkdownIt from 'markdown-it'
import { CARD_STYLE_MAP, wrapAppleCode, wrapCard } from './wxStyles.js'

const md = new MarkdownIt({ html: false, linkify: true, breaks: true })

let listDepth = 0
let darkMode = false
let headingLevel = 0
const listStack = []

const INK = { light: '#333333', dark: '#e9f1ee' }
const HEAD = { light: '#1a1a1a', dark: '#ffffff' }
const ACCENT = { light: '#1677ff', dark: '#7ce0c3' }

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
  headingLevel = Number(tokens[idx].tag.slice(1))
  const head = darkMode ? HEAD.dark : HEAD.light
  const accent = darkMode ? ACCENT.dark : ACCENT.light
  const style = headingLevel <= 1
    ? `margin:6px 0 18px;font-size:21px;line-height:1.55;font-weight:700;color:${head};text-align:center;letter-spacing:0.5px;`
    : headingLevel === 2
      ? `margin:28px 0 14px;padding-left:10px;border-left:4px solid ${accent};font-size:17px;line-height:1.6;font-weight:700;color:${head};`
      : `margin:20px 0 10px;font-size:16px;line-height:1.6;font-weight:700;color:${accent};`
  return `<section style="${style}">`
}
md.renderer.rules.heading_close = () => {
  const bar = headingLevel === 1
    ? `<section style="width:36px;height:3px;border-radius:2px;margin:12px auto 0;font-size:0;line-height:0;background:${darkMode ? ACCENT.dark : ACCENT.light};"> </section>`
    : ''
  return `${bar}</section>`
}

md.renderer.rules.paragraph_open = () => listDepth > 0
  ? ''
  : `<p style="margin:0 0 16px;font-size:16px;line-height:1.9;color:${darkMode ? INK.dark : INK.light};">`
md.renderer.rules.paragraph_close = () => listDepth > 0 ? '' : '</p>'

md.renderer.rules.bullet_list_open = () => {
  listDepth += 1
  listStack.push({ ordered: false, index: 0 })
  return ''
}
md.renderer.rules.bullet_list_close = () => {
  listDepth -= 1
  listStack.pop()
  return ''
}
md.renderer.rules.ordered_list_open = () => {
  listDepth += 1
  listStack.push({ ordered: true, index: 0 })
  return ''
}
md.renderer.rules.ordered_list_close = () => {
  listDepth -= 1
  listStack.pop()
  return ''
}
md.renderer.rules.list_item_open = () => {
  const top = listStack[listStack.length - 1]
  if (top?.ordered) top.index += 1
  const prefix = top?.ordered ? `${top.index}. ` : '· '
  const indent = (listDepth - 1) * 16 + 4
  return `<p style="margin:0 0 8px;padding-left:${indent}px;font-size:16px;line-height:1.9;color:${darkMode ? INK.dark : INK.light};">${prefix}`
}
md.renderer.rules.list_item_close = () => '</p>'

md.renderer.rules.blockquote_open = () => darkMode
  ? '<section style="margin:0 0 16px;padding:14px 16px;background:#1d312c;border-left:4px solid #4d7c6f;border-radius:0 8px 8px 0;">'
  : '<section style="margin:0 0 16px;padding:14px 16px;background:#f7f6f2;border-left:4px solid #ddd6c9;border-radius:0 8px 8px 0;">'
md.renderer.rules.blockquote_close = () => '</section>'
md.renderer.rules.hr = () => '<section style="margin:26px 0;border-top:1px solid #ece8df;font-size:0;line-height:0;"> </section>'
md.renderer.rules.code_inline = (tokens, idx) => {
  const bg = darkMode ? '#2b3f39' : '#f5f5f5'
  const color = darkMode ? '#9fe8d6' : '#c41d7f'
  return `<span style="padding:0 4px;background:${bg};border-radius:3px;font-size:14px;color:${color};">${esc(tokens[idx].content)}</span>`
}
md.renderer.rules.fence = (tokens, idx) => {
  const info = (tokens[idx].info || '').trim().split(/\s+/)[0].toLowerCase()
  const body = esc(tokens[idx].content)
  if (info === 'apple') {
    return wrapAppleCode(body)
  }
  const bg = darkMode ? '#21332e' : '#f6f8fa'
  const color = darkMode ? '#dfeae6' : '#333333'
  return `<section style="margin:0 0 16px;padding:12px 14px;background:${bg};border-radius:8px;font-size:14px;line-height:1.8;color:${color};white-space:pre-wrap;font-family:Menlo,Monaco,Consolas,monospace;">${body}</section>`
}
md.renderer.rules.image = (tokens, idx) => {
  const token = tokens[idx]
  const src = token.attrGet('src') || ''
  return `<img src="${esc(src)}" alt="${esc(token.content)}" style="width:100%;max-width:100%;display:block;margin:12px 0;border-radius:8px;" />`
}
md.renderer.rules.link_open = (tokens, idx) => `<a href="${esc(tokens[idx].attrGet('href') || '')}" style="color:${darkMode ? ACCENT.dark : ACCENT.light};text-decoration:none;">`
md.renderer.rules.table_open = () => '<table style="width:100%;border-collapse:collapse;margin:0 0 16px;font-size:15px;line-height:1.7;">'
md.renderer.rules.th_open = () => `<th style="border:1px solid ${darkMode ? '#3c534c' : '#e8e2d7'};padding:9px 10px;background:${darkMode ? '#1f3731' : '#f7f5f0'};font-weight:700;text-align:left;color:${darkMode ? HEAD.dark : HEAD.light};">`
md.renderer.rules.td_open = () => `<td style="border:1px solid ${darkMode ? '#3c534c' : '#e8e2d7'};padding:9px 10px;color:${darkMode ? INK.dark : INK.light};">`
md.renderer.rules.strong_open = () => '<strong style="font-weight:700;">'
md.renderer.rules.strong_close = () => '</strong>'
md.renderer.rules.em_open = () => '<em style="font-style:italic;">'

function renderMarkdown(markdown, dark = false) {
  listDepth = 0
  listStack.length = 0
  darkMode = dark
  const html = md.render(markdown || '').trim()
  darkMode = false
  return html
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
    const id = match[1].toLowerCase()
    const style = CARD_STYLE_MAP[id] || CARD_STYLE_MAP.soft
    html += wrapCard(id, renderMarkdown(match[2].trim(), !!style.dark))
    lastIndex = pattern.lastIndex
  }
  const rest = source.slice(lastIndex)
  if (rest.trim()) {
    html += renderMarkdown(rest)
  }
  return html
}

export function toWechatHtml(markdown, options = {}) {
  let source = markdown || ''
  const articleTitle = String(options.title || '').trim()
  if (articleTitle) {
    const heading = source.match(/^\s*#\s+(.+?)[ \t]*(?:\n|$)/)
    if (heading && heading[1].trim() === articleTitle) {
      source = source.slice(heading[0].length).replace(/^\s+/, '')
    }
  }
  const inner = renderWithCards(source).trim()
  if (!inner) return ''
  return `<section style="font-size:16px;color:#333333;letter-spacing:0.5px;">${inner}</section>`
}
