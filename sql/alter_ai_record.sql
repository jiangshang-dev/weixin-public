USE weixin;

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
