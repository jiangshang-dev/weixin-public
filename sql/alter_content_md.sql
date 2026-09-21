-- 保存 Markdown 原文，便于发送记录回看与再编辑
ALTER TABLE wx_article
    ADD COLUMN content_md MEDIUMTEXT NULL AFTER content;
