package com.fishing.service;

import com.fishing.config.DoubaoConfig;
import com.fishing.model.FishingResponse;
import com.fishing.model.WeatherRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 垂钓服务
 */
@Service
public class FishingService {
    
    private static final Logger log = LoggerFactory.getLogger(FishingService.class);
    
    private final DoubaoConfig doubaoConfig;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public FishingService(DoubaoConfig doubaoConfig) {
        this.doubaoConfig = doubaoConfig;
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * 获取垂钓指数
     * @param weatherRequest 天气请求
     * @return 垂钓指数响应
     */
    public FishingResponse getFishingIndex(WeatherRequest weatherRequest) {
        log.info("========== 开始获取垂钓指数 ==========");
        log.info("天气请求参数: location={}, temperature={}, weather={}, pressure={}, humidity={}, windSpeed={}", 
                weatherRequest.getLocation(), 
                weatherRequest.getTemperature(), 
                weatherRequest.getWeather(),
                weatherRequest.getPressure(),
                weatherRequest.getHumidity(),
                weatherRequest.getWindSpeed());
        
        // 构建豆包 API 请求
        Map<String, Object> requestBody = buildDoubaoRequest(weatherRequest);
        log.info("豆包 API 请求体: {}", requestBody);
        log.info("豆包 API URL: {}", doubaoConfig.getApiUrl());
        log.info("豆包 API Key: {}", doubaoConfig.getApiKey() != null ? "已配置" : "未配置");
        log.info("豆包 Model: {}", doubaoConfig.getModelName());
        
        // 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + doubaoConfig.getApiKey());
        
        // 使用 HttpEntity 包装请求体和请求头
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
        log.info("请求头 Authorization: Bearer {}", doubaoConfig.getApiKey().substring(0, Math.min(10, doubaoConfig.getApiKey().length())) + "...");
        
        try {
            log.info("开始调用豆包 API...");
            // 调用豆包 API
            String response = restTemplate.postForObject(
                    doubaoConfig.getApiUrl(),
                    requestEntity,
                    String.class
            );
            
            log.info("豆包 API 响应: {}", response);
            
            // 打印完整的响应结构
            log.info("========== 响应完整内容 ==========");
            log.info(response);
            log.info("========== 响应完整内容结束 ==========");
            
            // 解析响应
            FishingResponse result = parseDoubaoResponse(response, weatherRequest);
            log.info("解析后的结果: fishingScore={}, fishingStatus={}, aiAdvice={}", 
                    result.getFishingScore(), result.getFishingStatus(), result.getAiAdvice());
            log.info("========== 获取垂钓指数完成 ==========");
            return result;
        } catch (Exception e) {
            log.error("========== 调用豆包 API 异常 ==========");
            log.error("异常类型: {}", e.getClass().getName());
            log.error("异常消息: {}", e.getMessage());
            log.error("异常堆栈: ", e);
            // 异常处理，返回默认值
            FishingResponse defaultResponse = getDefaultFishingResponse(weatherRequest);
            log.info("返回默认响应: fishingScore={}", defaultResponse.getFishingScore());
            return defaultResponse;
        }
    }

    /**
     * 构建豆包 API 请求（/responses 端点格式）
     * @param weatherRequest 天气请求
     * @return 请求体
     */
    private Map<String, Object> buildDoubaoRequest(WeatherRequest weatherRequest) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", doubaoConfig.getModelName());
        
        // 构建 input 数组（/responses 端点使用 input 而不是 messages）
        List<Map<String, Object>> input = new ArrayList<>();
        Map<String, Object> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        
        // 构建 content 数组
        List<Map<String, String>> content = new ArrayList<>();
        Map<String, String> textContent = new HashMap<>();
        textContent.put("type", "input_text");
        textContent.put("text", buildPrompt(weatherRequest));
        content.add(textContent);
        
        userMessage.put("content", content);
        input.add(userMessage);
        
        requestBody.put("input", input);
        
        return requestBody;
    }

    /**
     * 构建提示词
     * @param weatherRequest 天气请求
     * @return 提示词
     */
    private String buildPrompt(WeatherRequest weatherRequest) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("你是一个专业的钓鱼顾问，根据以下天气信息，计算适钓指数并提供 AI 垂钓建议：\n");
        prompt.append("位置：").append(weatherRequest.getLocation()).append("\n");
        prompt.append("温度：").append(weatherRequest.getTemperature()).append(" 摄氏度\n");
        prompt.append("天气：").append(weatherRequest.getWeather()).append("\n");
        prompt.append("风速：").append(weatherRequest.getWindSpeed()).append(" 级\n");
        prompt.append("风向：").append(weatherRequest.getWindDirection()).append("\n");
        prompt.append("气压：").append(weatherRequest.getPressure()).append(" hPa\n");
        prompt.append("湿度：").append(weatherRequest.getHumidity()).append("%\n\n");
        
        prompt.append("请按照以下格式返回结果：\n");
        prompt.append("适钓指数（0-100）：[数值]\n");
        prompt.append("AI垂钓建议：[30-50字以内的简短建议]\n\n");
        
        prompt.append("适钓指数计算规则：\n");
        prompt.append("- 温度：15-25度为最佳，满分30分\n");
        prompt.append("- 气压：1000-1020 hPa为最佳，满分25分\n");
        prompt.append("- 风力：1-3级为最佳，满分20分\n");
        prompt.append("- 天气：晴天或多云为最佳，满分15分\n");
        prompt.append("- 湿度：40-70%为最佳，满分10分\n\n");
        
        prompt.append("重要：AI垂钓建议必须控制在30-50字之间，简洁明了。");
        
        return prompt.toString();
    }

    /**
     * 解析豆包响应
     * @param response 响应字符串
     * @return 垂钓指数响应
     */
    private FishingResponse parseDoubaoResponse(String response, WeatherRequest weatherRequest) {
        log.info("---------- 开始解析豆包响应 ----------");
        try {
            // 解析豆包 API 的 JSON 响应
            Map<String, Object> responseMap = objectMapper.readValue(response, Map.class);
            log.info("响应 Map keys: {}", responseMap.keySet());
            
            String content = null;
            
            // /responses 端点格式：output[].content[].text
            if (responseMap.containsKey("output")) {
                List<Map<String, Object>> output = (List<Map<String, Object>>) responseMap.get("output");
                log.info("output: {}", output);
                
                if (output != null && !output.isEmpty()) {
                    // 找到 type=message 的元素（最终答案，不是 reasoning 推理过程）
                    for (Map<String, Object> outputItem : output) {
                        String type = (String) outputItem.get("type");
                        log.info("outputItem type: {}", type);
                        
                        if ("message".equals(type)) {
                            log.info("找到 type=message 的元素");
                            log.info("outputItem keys: {}", outputItem.keySet());
                            
                            // 检查是否有 content 字段
                            if (outputItem.containsKey("content")) {
                                List<Map<String, String>> outputContent = (List<Map<String, String>>) outputItem.get("content");
                                log.info("outputContent: {}", outputContent);
                                
                                if (outputContent != null && !outputContent.isEmpty()) {
                                    content = outputContent.get(0).get("text");
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            // 响应本身就是数组格式：[{type=message, role=assistant, content=[...]}]
            else if (responseMap.values().stream().anyMatch(v -> v instanceof List)) {
                // 找到数组类型的值
                for (Map.Entry<String, Object> entry : responseMap.entrySet()) {
                    Object value = entry.getValue();
                    if (value instanceof List) {
                        List<?> list = (List<?>) value;
                        if (!list.isEmpty()) {
                            Object firstItem = list.get(0);
                            if (firstItem instanceof Map) {
                                Map<String, Object> firstMap = (Map<String, Object>) firstItem;
                                log.info("找到数组字段: {}, 第一个元素 keys: {}", entry.getKey(), firstMap.keySet());
                                
                                // 检查是否有 content 字段
                                if (firstMap.containsKey("content")) {
                                    Object contentObj = firstMap.get("content");
                                    if (contentObj instanceof List) {
                                        List<?> contentList = (List<?>) contentObj;
                                        if (!contentList.isEmpty()) {
                                            Object contentItem = contentList.get(0);
                                            if (contentItem instanceof Map) {
                                                Map<String, Object> contentMap = (Map<String, Object>) contentItem;
                                                log.info("contentItem keys: {}", contentMap.keySet());
                                                if (contentMap.containsKey("text")) {
                                                    content = (String) contentMap.get("text");
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            // /chat/completions 端点格式：choices[].message.content
            else if (responseMap.containsKey("choices")) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) responseMap.get("choices");
                log.info("choices: {}", choices);
                
                if (choices != null && !choices.isEmpty()) {
                    Map<String, Object> firstChoice = choices.get(0);
                    Map<String, Object> message = (Map<String, Object>) firstChoice.get("message");
                    content = (String) message.get("content");
                }
            }
            
            if (content == null || content.isEmpty()) {
                log.warn("无法从响应中提取内容，返回默认响应");
                return getDefaultFishingResponse(weatherRequest);
            }
            
            log.info("AI 返回的 content 内容:\n{}", content);
            
            // 解析 content 中的各项数据
            FishingResponse result = new FishingResponse();
            
            // 提取适钓指数
            int score = extractScore(content);
            log.info("提取到的适钓指数: {}", score);
            result.setFishingScore(score);
            
            // 设置适钓状态
            if (score >= 80) {
                result.setFishingStatus("非常适宜");
            } else if (score >= 60) {
                result.setFishingStatus("适宜");
            } else if (score >= 40) {
                result.setFishingStatus("一般");
            } else if (score >= 20) {
                result.setFishingStatus("不太适宜");
            } else {
                result.setFishingStatus("不适宜");
            }
            
            // 提取 AI 垂钓建议
            String aiAdvice = extractAdvice(content);
            log.info("提取到的 AI 垂钓建议: {}", aiAdvice);
            result.setAiAdvice(aiAdvice);
            
            // 设置其他状态为默认值
            result.setPressureStatus("气压适宜");
            result.setTemperatureStatus("温度适宜");
            result.setWindStatus("风力适宜");
            
            log.info("---------- 解析豆包响应完成 ----------");
            return result;
        } catch (Exception e) {
            log.error("---------- 解析豆包响应异常 ----------");
            log.error("异常类型: {}", e.getClass().getName());
            log.error("异常消息: {}", e.getMessage());
            log.error("异常堆栈: ", e);
            // 解析失败时返回默认值
            return getDefaultFishingResponse(weatherRequest);
        }
    }
    
    /**
     * 从响应内容中提取适钓指数
     * @param content 响应内容
     * @return 适钓指数
     */
    private int extractScore(String content) {
        try {
            // 匹配 "适钓指数（0-100）：[数字]" 或类似格式
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("适钓指数.*?[:：]\\s*(\\d+)");
            java.util.regex.Matcher matcher = pattern.matcher(content);
            if (matcher.find()) {
                int score = Integer.parseInt(matcher.group(1));
                return Math.min(100, Math.max(0, score)); // 确保在 0-100 范围内
            }
        } catch (Exception e) {
            // 提取失败返回默认值
        }
        return 75;
    }
    
    /**
     * 从响应内容中提取 AI 垂钓建议
     * @param content 响应内容
     * @return AI 垂钓建议
     */
    private String extractAdvice(String content) {
        try {
            // 匹配 "AI垂钓建议" 后面的内容
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("AI垂钓建议.*?[:：]\\s*([\\s\\S]+)$");
            java.util.regex.Matcher matcher = pattern.matcher(content);
            if (matcher.find()) {
                String advice = matcher.group(1).trim();
                // 截取前50字，确保不超过限制
                if (advice.length() > 50) {
                    advice = advice.substring(0, 50);
                }
                return advice;
            }
        } catch (Exception e) {
            // 提取失败返回默认值
        }
        return "气压适宜，建议用活饵底钓，注意观察鱼情。";
    }

    /**
     * 获取默认垂钓响应
     * @param weatherRequest 天气请求
     * @return 垂钓指数响应
     */
    private FishingResponse getDefaultFishingResponse(WeatherRequest weatherRequest) {
        FishingResponse response = new FishingResponse();
        
        // 计算默认适钓指数
        int score = 75;
        if (weatherRequest != null) {
            // 温度评分
            double temp = weatherRequest.getTemperature();
            if (temp >= 15 && temp <= 25) {
                score += 10;
            } else if (temp >= 10 && temp <= 30) {
                score += 5;
            }
            
            // 气压评分
            int pressure = weatherRequest.getPressure();
            if (pressure >= 1000 && pressure <= 1020) {
                score += 10;
            } else if (pressure >= 980 && pressure <= 1040) {
                score += 5;
            }
            
            // 风力评分
            double windSpeed = weatherRequest.getWindSpeed();
            if (windSpeed >= 1 && windSpeed <= 3) {
                score += 5;
            }
        }
        
        // 确保分数在 0-100 范围内
        score = Math.min(100, Math.max(0, score));
        response.setFishingScore(score);
        
        // 设置适钓状态
        if (score >= 80) {
            response.setFishingStatus("非常适宜");
        } else if (score >= 60) {
            response.setFishingStatus("适宜");
        } else if (score >= 40) {
            response.setFishingStatus("一般");
        } else if (score >= 20) {
            response.setFishingStatus("不太适宜");
        } else {
            response.setFishingStatus("不适宜");
        }
        
        // 设置其他状态
        response.setPressureStatus("气压适宜");
        response.setTemperatureStatus("温度适宜");
        response.setWindStatus("风力适宜");
        
        // 设置 AI 建议（30-50字，根据天气条件生成）
        String advice = generateDefaultAdvice(weatherRequest, score);
        response.setAiAdvice(advice);
        
        return response;
    }
    
    /**
     * 根据天气条件生成默认建议（30-50字）
     * @param weatherRequest 天气请求
     * @param score 适钓指数
     * @return 垂钓建议
     */
    private String generateDefaultAdvice(WeatherRequest weatherRequest, int score) {
        if (weatherRequest == null) {
            return "气压适宜，建议用活饵底钓，注意观察鱼情。";
        }
        
        StringBuilder advice = new StringBuilder();
        
        // 根据适钓指数生成建议
        if (score >= 80) {
            advice.append("天气极佳，");
        } else if (score >= 60) {
            advice.append("天气不错，");
        } else if (score >= 40) {
            advice.append("天气一般，");
        } else {
            advice.append("天气欠佳，");
        }
        
        // 根据温度添加建议
        double temp = weatherRequest.getTemperature();
        if (temp < 10) {
            advice.append("鱼口轻，");
        } else if (temp > 30) {
            advice.append("鱼上浮，");
        }
        
        // 根据气压添加建议
        int pressure = weatherRequest.getPressure();
        if (pressure < 1000) {
            advice.append("钓浮，");
        } else if (pressure > 1020) {
            advice.append("钓底，");
        }
        
        // 添加饵料建议
        advice.append("建议用活饵，注意观察鱼情。");
        
        // 确保在30-50字之间
        String result = advice.toString();
        if (result.length() > 50) {
            result = result.substring(0, 50);
        } else if (result.length() < 30) {
            result += "建议选择深水区，耐心等待。";
        }
        
        return result;
    }
}
