package com.fishing.controller;

import com.fishing.service.AIService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

/**
 * AI 控制器
 */
@RestController
@RequestMapping("/ai")
@Slf4j
public class AIController {

    private final AIService aiService;

    public AIController(AIService aiService) {
        this.aiService = aiService;
    }

    /**
     * AI 对话 - 流式输出
     * @param request 请求体，包含 message
     * @return SseEmitter 流式响应
     */
    @PostMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chat(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        log.info("收到 AI 对话请求: {}", message);
        return aiService.chat(message);
    }

    /**
     * AI 分析钓点 - 流式输出
     * @param spotName 钓点名称
     * @param spotType 钓点类型
     * @param address 地址
     * @param fishInfo 鱼种信息
     * @return SseEmitter 流式响应
     */
    @GetMapping(value = "/analyze-spot", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter analyzeSpot(
            @RequestParam(required = false) String spotName,
            @RequestParam(required = false) String spotType,
            @RequestParam(required = false) String address,
            @RequestParam(required = false) String fishInfo) {
        log.info("收到 AI 分析钓点请求: {} - {}", spotName, spotType);
        return aiService.analyzeSpot(spotName, spotType, address, fishInfo);
    }
}
