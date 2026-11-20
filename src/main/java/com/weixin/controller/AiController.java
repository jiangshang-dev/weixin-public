package com.weixin.controller;

import com.weixin.common.R;
import com.weixin.domain.entity.AiRecord;
import com.weixin.dto.AiDtos;
import com.weixin.service.AiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;

/**
 * AI 写作接口：流式生成、生成记录、停止生成。
 */
@RestController
@RequestMapping("/api/admin/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    /** 流式生成，SSE 事件：meta / delta / done / error */
    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream(@Valid @RequestBody AiDtos.AiWriteRequest request) {
        return aiService.stream(request);
    }

    /** 停止指定记录的生成 */
    @PostMapping("/records/{id}/stop")
    public R<Void> stop(@PathVariable Long id) {
        aiService.stop(id);
        return R.ok();
    }

    @GetMapping("/records")
    public R<List<AiRecord>> records(@RequestParam(defaultValue = "50") int limit) {
        return R.ok(aiService.latest(limit));
    }

    @GetMapping("/records/{id}")
    public R<AiRecord> record(@PathVariable Long id) {
        return R.ok(aiService.getById(id));
    }

    @DeleteMapping("/records/{id}")
    public R<Void> delete(@PathVariable Long id) {
        aiService.delete(id);
        return R.ok();
    }

    /** 当前模型与记忆/压缩开关，供页面展示 */
    @GetMapping("/config")
    public R<Map<String, Object>> config() {
        return R.ok(aiService.config());
    }
}
