package com.weixin.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.agentscope.core.model.GenerateOptions;
import io.agentscope.core.model.Model;
import io.agentscope.core.model.OpenAIChatModel;
import io.agentscope.core.model.transport.HttpTransportConfig;
import io.agentscope.core.model.transport.HttpVersion;
import io.agentscope.core.model.transport.JdkHttpTransport;
import io.agentscope.core.tool.Toolkit;
import io.agentscope.harness.agent.HarnessAgent;
import io.agentscope.harness.agent.memory.MemoryConfig;
import io.agentscope.harness.agent.memory.compaction.CompactionConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

/**
 * AI 写作 Agent 配置。
 * 记忆与压缩由 application-ai.yml 的 agentscope.ai.memory.* 控制，默认关闭。
 */
@Slf4j
@Configuration
public class AgentScopeConfig {

    /** Spring Boot 4 使用 Jackson 3，部分场景需显式提供 Jackson 2 的 ObjectMapper */
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    /**
     * LLM 模型。
     * 强制 HTTP/1.1：本地 vLLM 对 HTTP/2 支持不完整。
     */
    @Bean
    public Model chatModel(WeixinAiProperties properties) {
        WeixinAiProperties.Llm llm = properties.getLlm();
        JdkHttpTransport transport = JdkHttpTransport.builder()
                .config(HttpTransportConfig.builder().httpVersion(HttpVersion.HTTP_1_1).build())
                .build();
        return OpenAIChatModel.builder()
                .baseUrl(llm.getBaseUrl())
                .apiKey(llm.getApiKey())
                .modelName(llm.getModelName())
                .stream(llm.isStream())
                .generateOptions(GenerateOptions.builder()
                        .temperature(llm.getTemperature())
                        .maxTokens(llm.getMaxTokens())
                        .build())
                .httpTransport(transport)
                .build();
    }

    /**
     * 写作 Agent：单轮生成，不挂工具，不读写工作区。
     */
    @Bean(destroyMethod = "close")
    public HarnessAgent aiWritingAgent(Model chatModel,
                                       WeixinAiProperties properties,
                                       @Value("classpath:prompts/system-prompt.st") Resource promptResource)
            throws IOException {
        String systemPrompt = promptResource.getContentAsString(StandardCharsets.UTF_8);
        WeixinAiProperties.Memory memory = properties.getMemory();
        Path workspace = Path.of(properties.getWriting().getWorkspace());
        Files.createDirectories(workspace);

        HarnessAgent.Builder builder = HarnessAgent.builder()
                .name("wechat-writer")
                .description("公众号文章写作助手")
                .sysPrompt(systemPrompt)
                .model(chatModel)
                .toolkit(new Toolkit())
                .workspace(workspace)
                .checkRunning(false)
                .maxIters(1)
                .disableFilesystemTools()
                .disableShellTool()
                .disableWorkspaceContext()
                .disableSubagents()
                .disableToolsConfig()
                .disableDynamicSkills()
                .disableDefaultWorkspaceSkills()
                .disableDynamicSubagents()
                .disableAtPathExpansion();

        if (memory.isEnabled()) {
            builder.memory(MemoryConfig.builder()
                    .flushTrigger(MemoryConfig.FlushTrigger.throttled(Duration.ofMinutes(5)))
                    .build());
            log.info("AI 写作已开启会话记忆");
        } else {
            builder.disableMemoryTools().disableMemoryHooks().disableSessionPersistence();
            log.info("AI 写作未开启记忆，每次生成相互独立");
        }

        if (memory.isCompactionEnabled()) {
            builder.compaction(CompactionConfig.builder()
                    .triggerMessages(memory.getCompactionTriggerMessages())
                    .keepMessages(memory.getCompactionKeepMessages())
                    .build());
            log.info("AI 写作已开启上下文压缩 trigger={} keep={}",
                    memory.getCompactionTriggerMessages(), memory.getCompactionKeepMessages());
        } else {
            builder.disableCompaction();
            log.info("AI 写作未开启上下文压缩");
        }

        return builder.build();
    }
}
