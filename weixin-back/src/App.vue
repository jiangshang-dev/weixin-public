<template>
  <router-view />
</template>

<style>
:root {
  --wx-bg: #f5f2ec;
  --wx-surface: #ffffff;
  --wx-ink: #1f2a26;
  --wx-muted: #7a847f;
  --wx-line: #eae4da;
  --wx-brand: #1677ff;
  --wx-radius: 16px;
}

* {
  box-sizing: border-box;
}

html,
body,
#app {
  min-height: 100%;
}

body {
  margin: 0;
  font-family: -apple-system, BlinkMacSystemFont, 'PingFang SC', 'Hiragino Sans GB',
    'Microsoft YaHei', 'Segoe UI', sans-serif;
  color: var(--wx-ink);
  background: var(--wx-bg);
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

::selection {
  background: rgba(124, 224, 195, 0.42);
}

::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}
::-webkit-scrollbar-thumb {
  background: #ddd8cc;
  border-radius: 8px;
}
::-webkit-scrollbar-thumb:hover {
  background: #c9c2b2;
}
::-webkit-scrollbar-track {
  background: transparent;
}

/* Ant Design 细节统一 */
.ant-btn {
  border-radius: 10px;
  box-shadow: none;
}
.ant-input,
.ant-input-affix-wrapper,
.ant-input-number,
.ant-select-selector {
  border-radius: 10px !important;
}
.ant-tag {
  border-radius: 999px;
  padding-inline: 8px;
}
.ant-drawer-content {
  border-radius: 18px 0 0 18px;
}
.ant-modal-content {
  border-radius: 18px;
}
</style>
