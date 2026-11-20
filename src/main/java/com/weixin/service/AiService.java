package com.weixin.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.weixin.config.WeixinAiProperties;
import com.weixin.domain.entity.AiRecord;
import com.weixin.dto.AiDtos;
import com.weixin.mapper.AiRecordMapper;
import com.weixin.utils.AssertUtil;
import io.agentscope.core.agent.RuntimeContext;
import io.agentscope.core.event.TextBlockDeltaEvent;
import io.agentscope.core.message.Msg;
import io.agentscope.core.message.MsgRole;
import io.agentscope.harness.agent.HarnessAgent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.Disposable;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * AI 写作：单轮流式生成，生成记录落库，便于之后查看与引用。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiService {

    private static final Pattern H1 = Pattern.compile("(?m)^#\\s+(.+?)\\s*$");

    private final HarnessAgent aiWritingAgent;
    private final AiRecordMapper aiRecordMapper;
    private final WeixinAiProperties properties;

    /** recordId -> 取消回调，用于前端停止生成 */
    private final Map<Long, Runnable> running = new ConcurrentHashMap<>();

    public SseEmitter stream(AiDtos.AiWriteRequest request) {
        String requirement = StrUtil.trim(request.getRequirement());
        AssertUtil.isNotBlank(requirement, "请填写写作要求");
        AssertUtil.isTrue(requirement.length() <= properties.getWriting().getMaxRequirementChars(),
                "写作要求不能超过 {} 字", properties.getWriting().getMaxRequirementChars());

        AiRecord record = createRecord(request, requirement);
        SseEmitter emitter = new SseEmitter(properties.getWriting().getTimeoutSeconds() * 1000);
        send(emitter, "meta", new AiDtos.StreamMeta(record.getId()));

        StringBuilder content = new StringBuilder();
        AtomicBoolean settled = new AtomicBoolean(false);
        AtomicReference<Disposable> subscriptionRef = new AtomicReference<>();

        Runnable cancel = () -> {
            Disposable subscription = subscriptionRef.get();
            if (subscription != null) {
                subscription.dispose();
            }
            if (settled.compareAndSet(false, true)) {
                fail(record, "已取消生成");
                send(emitter, "error", new AiDtos.AiError("已停止生成"));
                emitter.complete();
            }
            running.remove(record.getId());
        };
        running.put(record.getId(), cancel);

        Msg message = Msg.builder()
                .role(MsgRole.USER)
                .textContent(buildPrompt(request, requirement))
                .build();
        RuntimeContext context = RuntimeContext.builder()
                .sessionId("ai-write-" + record.getId())
                .userId("admin")
                .build();

        Disposable subscription;
        try {
            subscription = aiWritingAgent.streamEvents(List.of(message), context)
                    .subscribe(event -> {
                        if (!(event instanceof TextBlockDeltaEvent delta)) {
                            return;
                        }
                        String text = delta.getDelta();
                        if (StrUtil.isEmpty(text)) {
                            return;
                        }
                        content.append(text);
                        if (!send(emitter, "delta", new AiDtos.AiDelta(text))) {
                            cancel.run();
                        }
                    }, error -> complete(emitter, record, content, settled, error),
                            () -> complete(emitter, record, content, settled, null));
        } catch (RuntimeException ex) {
            cancel.run();
            return emitter;
        }
        subscriptionRef.set(subscription);

        emitter.onTimeout(cancel);
        emitter.onError(error -> cancel.run());
        emitter.onCompletion(() -> {
            if (!settled.get()) {
                cancel.run();
            }
        });
        return emitter;
    }

    public void stop(Long recordId) {
        Runnable cancel = running.get(recordId);
        if (cancel != null) {
            cancel.run();
        }
    }

    public List<AiRecord> latest(int limit) {
        int size = Math.max(1, Math.min(limit, 100));
        return aiRecordMapper.selectList(Wrappers.lambdaQuery(AiRecord.class)
                .select(AiRecord::getId, AiRecord::getTitle, AiRecord::getIndustry, AiRecord::getModel,
                        AiRecord::getStatus, AiRecord::getErrorMessage,
                        AiRecord::getCreateTime, AiRecord::getUpdateTime)
                .orderByDesc(AiRecord::getId)
                .last("limit " + size));
    }

    public AiRecord getById(Long id) {
        AssertUtil.notNull(id, "生成记录不存在");
        AiRecord record = aiRecordMapper.selectById(id);
        AssertUtil.notNull(record, "生成记录不存在");
        return record;
    }

    public void delete(Long id) {
        getById(id);
        running.remove(id);
        aiRecordMapper.deleteById(id);
    }

    public Map<String, Object> config() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("model", properties.getLlm().getModelName());
        data.put("memoryEnabled", properties.getMemory().isEnabled());
        data.put("compactionEnabled", properties.getMemory().isCompactionEnabled());
        data.put("maxRequirementChars", properties.getWriting().getMaxRequirementChars());
        return data;
    }

    private AiRecord createRecord(AiDtos.AiWriteRequest request, String requirement) {
        String title = StrUtil.blankToDefault(StrUtil.trim(request.getTitle()), requirement);
        AiRecord record = new AiRecord();
        record.setTitle(StrUtil.sub(title, 0, 64));
        record.setIndustry(StrUtil.sub(StrUtil.trim(request.getIndustry()), 0, 32));
        record.setModel(properties.getLlm().getModelName());
        record.setRequirement(requirement);
        record.setContentMd("");
        record.setStatus(0);
        record.setCreateTime(LocalDateTime.now());
        record.setUpdateTime(LocalDateTime.now());
        aiRecordMapper.insert(record);
        return record;
    }

    private void complete(SseEmitter emitter, AiRecord record, StringBuilder content,
                          AtomicBoolean settled, Throwable error) {
        if (!settled.compareAndSet(false, true)) {
            return;
        }
        running.remove(record.getId());
        if (error != null) {
            String message = StrUtil.blankToDefault(error.getMessage(), "生成失败");
            log.warn("AI 写作失败 recordId={}", record.getId(), error);
            fail(record, message);
            send(emitter, "error", new AiDtos.AiError(message));
            emitter.complete();
            return;
        }
        String text = StrUtil.trim(content.toString());
        if (StrUtil.isBlank(text)) {
            fail(record, "模型没有返回内容");
            send(emitter, "error", new AiDtos.AiError("模型没有返回内容"));
            emitter.complete();
            return;
        }
        record.setContentMd(text);
        record.setTitle(StrUtil.sub(deriveTitle(text, record.getTitle()), 0, 64));
        record.setStatus(1);
        record.setErrorMessage("");
        record.setUpdateTime(LocalDateTime.now());
        aiRecordMapper.updateById(record);
        send(emitter, "done", new AiDtos.AiDone(record.getId(), record.getTitle(), text));
        emitter.complete();
    }

    private void fail(AiRecord record, String message) {
        record.setStatus(2);
        record.setErrorMessage(StrUtil.sub(StrUtil.blankToDefault(message, "生成失败"), 0, 500));
        record.setUpdateTime(LocalDateTime.now());
        aiRecordMapper.updateById(record);
    }

    private String deriveTitle(String markdown, String fallback) {
        Matcher matcher = H1.matcher(markdown);
        return matcher.find() ? matcher.group(1).trim() : fallback;
    }

    private String buildPrompt(AiDtos.AiWriteRequest request, String requirement) {
        StringBuilder prompt = new StringBuilder("请写一篇微信公众号文章。\n");
        append(prompt, "标题方向", request.getTitle());
        append(prompt, "所属行业", request.getIndustry());
        append(prompt, "参考模板", request.getTemplate());
        append(prompt, "目标读者", request.getAudience());
        append(prompt, "语气风格", request.getTone());
        if (request.getWordCount() != null && request.getWordCount() > 0) {
            prompt.append("篇幅：约 ").append(request.getWordCount()).append(" 字\n");
        }
        prompt.append("写作要求：").append(requirement).append('\n');
        prompt.append("输出要求：只输出 Markdown 正文，第一行为 # 标题，适当使用 :::wx-xxx 样式块。");
        return prompt.toString();
    }

    private void append(StringBuilder prompt, String label, String value) {
        if (StrUtil.isNotBlank(value)) {
            prompt.append(label).append("：").append(value.trim()).append('\n');
        }
    }

    private boolean send(SseEmitter emitter, String event, Object data) {
        try {
            emitter.send(SseEmitter.event()
                    .name(event)
                    .data(data, MediaType.APPLICATION_JSON));
            return true;
        } catch (Exception ex) {
            log.debug("SSE 发送失败：{}", ex.getMessage());
            return false;
        }
    }
}
