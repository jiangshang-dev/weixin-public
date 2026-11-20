package com.weixin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AI 写作相关 DTO。
 */
public final class AiDtos {

    private AiDtos() {
    }

    /** POST /api/admin/ai/stream 请求体 */
    @Data
    public static class AiWriteRequest {
        /** 标题方向，可留空由模型生成 */
        private String title;
        /** 行业，例如「科技数码」 */
        private String industry;
        /** 模板风格，例如「科技 · 新品发布」 */
        private String template;
        /** 目标读者 */
        private String audience;
        /** 语气，例如「专业理性」 */
        private String tone;
        /** 期望字数 */
        private Integer wordCount;
        /** 具体写作要求 */
        @NotBlank(message = "请填写写作要求")
        @Size(max = 2000, message = "写作要求不能超过 2000 字")
        private String requirement;
    }

    /** SSE：生成开始，返回记录 ID */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StreamMeta {
        private Long recordId;
    }

    /** SSE：增量文本 */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AiDelta {
        private String text;
    }

    /** SSE：生成完成 */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AiDone {
        private Long recordId;
        private String title;
        private String content;
    }

    /** SSE：生成失败 */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AiError {
        private String message;
    }
}
