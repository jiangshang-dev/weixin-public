CREATE DATABASE IF NOT EXISTS weixin DEFAULT CHARACTER SET utf8mb4;

USE weixin;

CREATE TABLE IF NOT EXISTS sys_user (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    username    VARCHAR(64)  NOT NULL,
    password    VARCHAR(128) NOT NULL,
    real_name   VARCHAR(64)  NULL,
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_username (username)
) COMMENT '后台管理员';

CREATE TABLE IF NOT EXISTS wx_article (
    id             BIGINT PRIMARY KEY AUTO_INCREMENT,
    title          VARCHAR(64)   NOT NULL,
    author         VARCHAR(16)   NULL,
    digest         VARCHAR(120)  NULL,
    content        MEDIUMTEXT    NOT NULL,
    content_md     MEDIUMTEXT    NULL,
    thumb_media_id VARCHAR(128)  NULL,
    draft_media_id VARCHAR(128)  NULL,
    status         TINYINT       NOT NULL DEFAULT 0 COMMENT '0 未同步 1 已进草稿箱 2 失败',
    sync_message   VARCHAR(512)  NULL,
    create_time    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT '准备同步到公众号草稿箱的文章';

CREATE TABLE IF NOT EXISTS wx_ai_record (
    id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    title         VARCHAR(128)  NULL,
    industry      VARCHAR(32)   NULL,
    model         VARCHAR(64)   NULL,
    requirement   VARCHAR(2000) NULL,
    content_md    MEDIUMTEXT    NULL,
    status        TINYINT       NOT NULL DEFAULT 0 COMMENT '0 生成中 1 已完成 2 失败',
    error_message VARCHAR(512)  NULL,
    create_time   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_create_time (create_time)
) COMMENT 'AI 文章生成记录';
