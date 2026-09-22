# weixin-public

<p align="center">
  <strong>微信公众号 Markdown 写作 → 一键写入草稿箱</strong><br/>
  自己写、自己排、自己同步。告别「秀米排版 → 复制粘贴 → 格式乱掉」。
</p>

<p align="center">
  <a href="https://github.com/jiangshang-dev/weixin-public"><img src="https://img.shields.io/github/stars/jiangshang-dev/weixin-public?style=for-the-badge&logo=github" alt="Stars"/></a>
  <a href="https://github.com/jiangshang-dev/weixin-public/fork"><img src="https://img.shields.io/github/forks/jiangshang-dev/weixin-public?style=for-the-badge" alt="Forks"/></a>
  <img src="https://img.shields.io/badge/Spring%20Boot-4.0.8-6DB33F?style=for-the-badge&logo=springboot&logoColor=white" alt="Spring Boot"/>
  <img src="https://img.shields.io/badge/Vue-3-42b883?style=for-the-badge&logo=vue.js&logoColor=white" alt="Vue 3"/>
  <img src="https://img.shields.io/badge/License-MIT-blue?style=for-the-badge" alt="License"/>
</p>

---

## ⭐ 先 Star，再使用

> **开源不是义务劳动，Star 是最低成本的尊重。**
>
> 能帮到你 → 点右上角 **Star**  
> 想二开 / 商用 → **Fork** 并在项目里注明来源  
> 白嫖代码、删版权、当自己产品卖 → **不欢迎**

| 你怎么用 | 我们怎么看 |
| --- | --- |
| Star 后自用、学习、改着玩 | 欢迎 |
| 提 Issue / PR（请先 Star） | 欢迎 |
| 商用部署、定制开发 | 先沟通，别当空气 |
| Clone 完删作者信息、闭源倒卖 | 禁止 |

**用不了、嫌麻烦、只想白嫖？请直接关掉页面，别占用 Issues。**

👉 [点这里 Star](https://github.com/jiangshang-dev/weixin-public) · 花 1 秒，比「谢谢」有用。

---

## 这是什么

面向运营 / 内容同学的 **公众号草稿工作台**：

1. 后台用 **Markdown** 写文章（带手机预览）
2. 正文图上传到 **微信 CDN**（外链图会被过滤）
3. 一键调用微信接口，写入公众号 **草稿箱**
4. 你在微信公众平台检查后，再手动点击发布

**不会自动群发。** 最后发布权留在人手里，更安全。

```text
Markdown 编辑器 ──► 微信兼容 HTML ──► 封面/图片素材
                                      │
                                      ▼
                              公众号草稿箱
                                      │
                                      ▼
                         微信后台人工确认发布
```

---

## 功能亮点

- **Markdown 写作**：工具栏排版，右侧实时「公众号效果」预览
- **样式块**：柔灰 / 蓝绿橙红提示、引用、夹子、钉子、木板、黑板、便签、阴影、缎带、双线边框、苹果三色点代码框（`:::wx-xxx` / `` ```apple ``）
- **素材直传微信**：正文图 `uploadimg`，封面 `add_material`
- **草稿箱同步**：调用 `draft/add`，记录同步状态与失败信息
- **发送记录**：历史文章可预览，有 Markdown 原文可载入再发
- **后台登录**：Spring Security + JWT 简单鉴权

---

## 技术栈

| 层 | 技术 |
| --- | --- |
| 后端 | Java 21 · Spring Boot 4.0.8 · Spring Security · MyBatis-Plus · Hutool · MySQL |
| 管理后台 | Vue 3 · Vite · Ant Design Vue · md-editor-v3 |
| 用户前台 | Vue 3 · Vite · Ant Design Vue |
| 微信 | stable_token · 素材上传 · 草稿箱 |

目录结构：

```text
weixin-public/
├── src/                 # Spring Boot 后端（默认端口 8090）
├── weixin-back/         # 管理后台（写文章 / 同步草稿）
├── weixin-views/        # 用户前台
├── sql/                 # 建表脚本
└── docs/                # 设计说明（可参考，勿提交密钥）
```

---

## 快速开始

### 0. 请先 Star（认真的）

没 Star 也能跑，但别指望作者免费当客服。

### 1. 环境

- JDK 21+
- Maven 3.8+
- MySQL 8+
- Node.js 18+（建议 pnpm / npm）
- 已认证或具备草稿箱权限的 **微信公众号**，且服务器公网 IP 已加入开发者 IP 白名单

### 2. 初始化数据库

```bash
mysql -u root -p < sql/init.sql
# 如有 Markdown 字段升级：
# mysql -u root -p < sql/alter_content_md.sql
```

### 3. 配置后端（密钥自己填，别提交到 Git）

编辑 `src/main/resources/application.yml`：

```yaml
server:
  port: 8090

spring:
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/weixin?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true
    username: root
    password: your_password

weixin:
  jwt-secret: change-me-to-a-long-random-string
  jwt-expire-hours: 168
  mp:
    app-id: your_app_id
    app-secret: your_app_secret
```

> **安全提醒**：`AppSecret`、数据库密码、JWT 密钥不要提交到仓库。泄露了立刻去微信公众平台重置。

### 4. 启动后端

```bash
mvn spring-boot:run
# 或
mvn -DskipTests package && java -jar target/weixin-public-1.0-SNAPSHOT.jar
```

健康检查（开放接口）：`GET http://127.0.0.1:8090/api/open/ping`

### 5. 启动管理后台

```bash
cd weixin-back
npm install
npm run dev
```

按需改 `.env.development` / `.env.production` 中的 `VUE_APP_SERVER_URL`。

### 6. （可选）用户前台

```bash
cd weixin-views
npm install
npm run dev
```

### 7. 微信侧必做

1. 公众平台 → **开发 → 基本配置**，拿到 AppID / AppSecret  
2. 把 **后端所在机器的公网 IP** 加进 IP 白名单  
3. 后台写完文章 → 选封面 → **提交到草稿箱** → 去微信公众平台草稿箱核对 → 再发布

常见报错：`invalid ip ... not in whitelist` → 白名单没加对，不是本项目坏了。

---

## 使用流程

```text
登录后台
  → Markdown 写正文（样式块 / 苹果代码框）
  → 图片用编辑器上传到微信 CDN
  → 选择封面
  → 提交到草稿箱
  → 微信公众平台检查
  → 人工发布
```

更多操作说明见后台「使用说明」页，或仓库内 `weixin-back/src/views/Guide.vue`。

---

## 贡献与支持

1. **先 Star**，再提 Issue / PR  
2. PR 请说清楚：解决了什么、怎么验证  
3. 不要在 Issue 里贴真实 AppSecret  
4. 功能建议欢迎；「帮我免费部署到生产」请自备服务器与耐心

觉得项目有用？

- ⭐ [Star 一下](https://github.com/jiangshang-dev/weixin-public)
- 🍴 Fork 二开时保留原作者与仓库链接
- 💬 分享给同样做公众号运营的朋友

---

## 许可证与「禁止白嫖」约定

本项目以 **MIT License** 开源（见 [`LICENSE`](./LICENSE)）。

在法律允许的前提下，额外约定（社交契约，不是法律条款）：

1. **个人学习 / 自用**：请 Star，注明出处更佳  
2. **二次开发**：保留原仓库链接与致谢  
3. **商用 / 对外售卖**：允许基于 MIT 使用，但禁止声称「完全自主研发」并抹掉来源；大额商用建议先联系作者  
4. **白嫖行为**（只拿代码不 Star、删版权信息、闭源倒卖包装成自家 SaaS）：作者有权拒绝任何支持，并公开点名

**代码可以免费，尊重不能免费。**

---

## 免责声明

- 微信接口权限、草稿箱能力因账号类型与微信政策而异，使用前请自行核对  
- 本工具只写入草稿箱，不负责群发结果与内容合规  
- 因密钥泄露、IP 未加白名单、微信侧限制导致的问题，请先自查配置  

---

<p align="center">
  如果这个项目帮你省下了一次「复制粘贴改格式」的痛苦，<br/>
  <a href="https://github.com/jiangshang-dev/weixin-public"><strong>请给一个 Star ⭐</strong></a> —— 这是对开源最实在的反馈。
</p>
