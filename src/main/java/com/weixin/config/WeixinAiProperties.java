package com.weixin.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 绑定 agentscope.ai.*，配置见 application-ai.yml。
 * AI 写作默认不启用记忆与压缩，开关集中放在配置文件里。
 */
@Data
@Component
@ConfigurationProperties(prefix = "agentscope.ai")
public class WeixinAiProperties {

    private Llm llm = new Llm();
    private Memory memory = new Memory();
    private Writing writing = new Writing();

    /** OpenAI 兼容模型（vLLM / Ollama / DeepSeek 等） */
    @Data
    public static class Llm {
        private String baseUrl = "http://192.168.1.62:8000/v1";
        private String apiKey = "EMPTY";
        private String modelName = "Qwen3.5-35B-A3B-GPTQ-Int4";
        private boolean stream = true;
        private double temperature = 0.8;
        private int maxTokens = 4096;
    }

    /** 记忆与对话压缩，写作场景默认全部关闭 */
    @Data
    public static class Memory {
        /** true=保留会话记忆；写作场景建议 false */
        private boolean enabled = false;
        /** true=开启上下文压缩（仅记忆开启时有意义） */
        private boolean compactionEnabled = false;
        private int compactionTriggerMessages = 30;
        private int compactionKeepMessages = 10;
    }

    /** 写作行为配置 */
    @Data
    public static class Writing {
        private String workspace = "./.agentscope/ai-writer";
        /** SSE 超时时间（秒） */
        private long timeoutSeconds = 300;
        /** 单次写作要求最大字数 */
        private int maxRequirementChars = 2000;
    }
}
