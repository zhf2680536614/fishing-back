package com.fishing.service;

import com.fishing.config.DoubaoConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * AI 服务
 */
@Service
@Slf4j
public class AIService {

    private final DoubaoConfig doubaoConfig;
    private final ObjectMapper objectMapper;
    private final ExecutorService executorService;

    // 系统提示词 - 定义 AI 的角色和行为
    private static final String SYSTEM_PROMPT = """
        你是一位专业的钓鱼配饵顾问，拥有丰富的钓鱼经验和饵料知识。

        你的职责：
        1. 根据用户的问题，提供专业的饵料配方建议
        2. 考虑季节、天气、水温、钓点环境等因素
        3. 针对目标鱼种推荐合适的饵料
        4. 提供实用的钓鱼技巧和注意事项

        回答要求：
        1. 回答要专业、实用、易懂
        2. 可以推荐具体的商品饵品牌（如蓝鲫、九一八、速攻等）
        3. 说明饵料搭配的比例和使用方法
        4. 提供钓深、钓法等配套建议
        5. 回答控制在 200-500 字之间

        注意事项：
        1. 如果是保护鱼类，提醒用户放流
        2. 提醒用户遵守当地钓鱼法规
        3. 建议环保钓鱼，保护水域环境
        """;

    public AIService(DoubaoConfig doubaoConfig) {
        this.doubaoConfig = doubaoConfig;
        this.objectMapper = new ObjectMapper();
        this.executorService = Executors.newCachedThreadPool();
    }

    /**
     * AI 对话 - 流式输出（使用 chat/completions 端点）
     * @param message 用户消息
     * @return SseEmitter
     */
    public SseEmitter chat(String message) {
        SseEmitter emitter = new SseEmitter(120000L); // 2分钟超时

        executorService.execute(() -> {
            HttpURLConnection connection = null;
            try {
                // 使用流式输出端点
                String apiUrl = doubaoConfig.getStreamApiUrl();
                log.info("使用流式输出端点: {}", apiUrl);

                // 构建 chat/completions 请求体
                Map<String, Object> requestBody = buildChatCompletionsRequest(message);
                String jsonBody = objectMapper.writeValueAsString(requestBody);
                log.info("请求体: {}", jsonBody);

                // 创建连接
                URL url = new URL(apiUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Authorization", "Bearer " + doubaoConfig.getApiKey());
                connection.setRequestProperty("Accept", "text/event-stream");
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setConnectTimeout(30000);
                connection.setReadTimeout(120000);

                // 发送请求
                try (OutputStream os = connection.getOutputStream()) {
                    os.write(jsonBody.getBytes(StandardCharsets.UTF_8));
                }

                // 检查响应码
                int responseCode = connection.getResponseCode();
                log.info("响应状态码: {}", responseCode);

                if (responseCode != HttpURLConnection.HTTP_OK) {
                    String errorMsg = "API 调用失败，状态码: " + responseCode;
                    log.error(errorMsg);
                    try (BufferedReader errorReader = new BufferedReader(
                            new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8))) {
                        StringBuilder errorBody = new StringBuilder();
                        String line;
                        while ((line = errorReader.readLine()) != null) {
                            errorBody.append(line);
                        }
                        log.error("错误响应: {}", errorBody);
                        // 发送错误给客户端
                        Map<String, String> errorData = new HashMap<>();
                        errorData.put("error", "API 调用失败: " + errorBody);
                        emitter.send(SseEmitter.event().data(objectMapper.writeValueAsString(errorData)));
                    }
                    emitter.complete();
                    return;
                }

                // 获取输入流
                InputStream inputStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

                // 读取 SSE 响应
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty()) {
                        continue;
                    }

                    log.debug("处理 SSE 行: {}", line);

                    // 解析 SSE 格式
                    if (line.startsWith("data:")) {
                        String data = line.substring(5).trim();

                        // 处理 [DONE]
                        if ("[DONE]".equals(data)) {
                            log.info("收到 [DONE] 信号");
                            emitter.send(SseEmitter.event().data("[DONE]"));
                            break;
                        }

                        // 解析 chat/completions 格式
                        processChatCompletionsData(data, emitter);
                    }
                }

                reader.close();
                emitter.complete();
                log.info("AI 对话完成");

            } catch (Exception e) {
                log.error("AI 对话异常", e);
                try {
                    Map<String, String> errorData = new HashMap<>();
                    errorData.put("error", e.getMessage());
                    emitter.send(SseEmitter.event().data(objectMapper.writeValueAsString(errorData)));
                    emitter.completeWithError(e);
                } catch (Exception ex) {
                    log.error("发送错误信息失败", ex);
                }
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        });

        // 处理连接关闭
        emitter.onCompletion(() -> log.info("SSE 连接完成"));
        emitter.onTimeout(() -> log.warn("SSE 连接超时"));
        emitter.onError((e) -> log.error("SSE 连接错误", e));

        return emitter;
    }

    /**
     * 处理 chat/completions 格式的数据
     */
    private void processChatCompletionsData(String data, SseEmitter emitter) {
        try {
            JsonNode root = objectMapper.readTree(data);

            // 处理 choices 格式
            JsonNode choices = root.get("choices");
            if (choices != null && choices.isArray() && choices.size() > 0) {
                JsonNode choice = choices.get(0);

                // 检查 finish_reason
                JsonNode finishReason = choice.get("finish_reason");
                if (finishReason != null && !finishReason.isNull()) {
                    return;
                }

                // 获取 delta
                JsonNode delta = choice.get("delta");
                if (delta != null && delta.has("content")) {
                    JsonNode content = delta.get("content");
                    if (content != null && !content.isNull()) {
                        String text = content.asText();
                        if (!text.isEmpty()) {
                            log.info("流式增量: {}", text);
                            sendContent(emitter, text);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.warn("解析 chat/completions 响应失败: {}", data, e);
        }
    }

    /**
     * 发送内容给客户端
     */
    private void sendContent(SseEmitter emitter, String content) {
        try {
            Map<String, String> responseData = new HashMap<>();
            responseData.put("content", content);
            emitter.send(SseEmitter.event().data(objectMapper.writeValueAsString(responseData)));
        } catch (Exception e) {
            log.error("发送内容失败", e);
        }
    }

    /**
     * 构建 chat/completions 请求体
     * @param message 用户消息
     * @return 请求体 Map
     */
    private Map<String, Object> buildChatCompletionsRequest(String message) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", doubaoConfig.getModelName());

        // 构建消息列表
        List<Map<String, String>> messages = new ArrayList<>();

        // 系统消息
        Map<String, String> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", SYSTEM_PROMPT);
        messages.add(systemMessage);

        // 用户消息
        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", message);
        messages.add(userMessage);

        requestBody.put("messages", messages);
        requestBody.put("stream", true); // 启用流式输出
        requestBody.put("temperature", 0.7);
        requestBody.put("max_tokens", 2048);

        return requestBody;
    }
}
