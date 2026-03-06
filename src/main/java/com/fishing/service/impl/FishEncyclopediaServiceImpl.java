package com.fishing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fishing.config.DoubaoConfig;
import com.fishing.mapper.FishEncyclopediaMapper;
import com.fishing.pojo.entity.FishEncyclopediaEntity;
import com.fishing.pojo.vo.FishVO;
import com.fishing.service.FishEncyclopediaService;
import com.fishing.utils.MinioUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

@Service
@Slf4j
public class FishEncyclopediaServiceImpl extends ServiceImpl<FishEncyclopediaMapper, FishEncyclopediaEntity> implements FishEncyclopediaService {

    private final MinioUtils minioUtils;
    private final DoubaoConfig doubaoConfig;

    private static final String FISH_IMAGE_DIR = "fish_image";

    public FishEncyclopediaServiceImpl(MinioUtils minioUtils, DoubaoConfig doubaoConfig) {
        this.minioUtils = minioUtils;
        this.doubaoConfig = doubaoConfig;
    }

    @Override
    public FishVO identifyFish(MultipartFile image) {
        try {
            String base64Image = imageToBase64(image);
            String fishName = callAIIdentify(base64Image);
            return findFishByName(fishName);
        } catch (Exception e) {
            log.error("AI 识鱼失败", e);
            throw new RuntimeException("AI 识鱼失败: " + e.getMessage());
        }
    }

    private String imageToBase64(MultipartFile image) throws Exception {
        byte[] bytes = image.getBytes();
        return "data:" + image.getContentType() + ";base64," + java.util.Base64.getEncoder().encodeToString(bytes);
    }

    private String callAIIdentify(String base64Image) {
        try {
            String prompt = "请根据这张图片识别鱼类，只返回鱼类的中文学名，不要其他内容。如果无法识别，请返回'未知鱼类'。";

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", doubaoConfig.getModelName()); // 使用配置文件中的模型
            requestBody.put("max_tokens", 1024);
            requestBody.put("stream", false);

            Map<String, Object> userMessage = new HashMap<>();
            userMessage.put("role", "user");

            List<Map<String, Object>> content = new ArrayList<>();

            Map<String, Object> textPart = new HashMap<>();
            textPart.put("type", "text");
            textPart.put("text", prompt);
            content.add(textPart);

            Map<String, Object> imagePart = new HashMap<>();
            imagePart.put("type", "image_url");
            Map<String, Object> imageUrlObj = new HashMap<>();
            imageUrlObj.put("url", base64Image);
            imagePart.put("image_url", imageUrlObj);
            content.add(imagePart);

            userMessage.put("content", content);

            List<Map<String, Object>> messages = new ArrayList<>();
            messages.add(userMessage);
            requestBody.put("messages", messages);

            String response = callDoubaoApi(requestBody);
            return parseAIResponse(response);
        } catch (Exception e) {
            log.error("调用 AI 识别失败", e);
            return "未知鱼类";
        }
    }

    private String callDoubaoApi(Map<String, Object> requestBody) throws Exception {
        URL url = new URL(doubaoConfig.getStreamApiUrl());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "Bearer " + doubaoConfig.getApiKey());
        conn.setDoOutput(true);

        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        String json = mapper.writeValueAsString(requestBody);
        log.info("AI 识鱼请求体: {}", json);

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = json.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        int responseCode = conn.getResponseCode();
        log.info("AI 识鱼 API 响应码: {}", responseCode);

        if (responseCode != 200) {
            // 读取错误信息
            StringBuilder errorResponse = new StringBuilder();
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getErrorStream(), "utf-8"))) {
                String line;
                while ((line = br.readLine()) != null) {
                    errorResponse.append(line);
                }
            }
            log.error("AI 识鱼 API 错误信息: {}", errorResponse.toString());
            throw new RuntimeException("AI API 返回错误: " + responseCode + " - " + errorResponse.toString());
        }

        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), "utf-8"))) {
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
        }

        log.info("AI 识鱼 API 响应: {}", response.toString());
        return response.toString();
    }

    private String parseAIResponse(String response) {
        try {
            com.fasterxml.jackson.databind.JsonNode root = new com.fasterxml.jackson.databind.ObjectMapper().readTree(response);
            String content = root.path("choices").get(0).path("message").path("content").asText();
            String result = content.trim();
            if (result.contains("未知") || result.contains("无法识别")) {
                return "未知鱼类";
            }
            return result;
        } catch (Exception e) {
            log.error("解析 AI 响应失败: {}", response, e);
            return "未知鱼类";
        }
    }

    private FishVO findFishByName(String fishName) {
        LambdaQueryWrapper<FishEncyclopediaEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(FishEncyclopediaEntity::getName, fishName)
               .or()
               .like(FishEncyclopediaEntity::getAlias, fishName)
               .last("LIMIT 1");

        FishEncyclopediaEntity entity = this.getOne(wrapper);
        if (entity == null) {
            return createUnknownFish(fishName);
        }

        FishVO vo = new FishVO();
        BeanUtils.copyProperties(entity, vo);
        vo.setIsProtected(entity.getProtectionLevel() != null && entity.getProtectionLevel() == 1);

        // 解析图片列表并拼接完整URL
        if (entity.getImages() != null && !entity.getImages().isEmpty()) {
            vo.setImages(parseImages(entity.getImages()));
        } else {
            vo.setImages("[]");
        }

        return vo;
    }

    private FishVO createUnknownFish(String fishName) {
        FishVO vo = new FishVO();
        vo.setName(fishName);
        vo.setAlias("");
        vo.setCategory("");
        vo.setIsProtected(false);
        vo.setHabits("暂无该鱼类的详细信息");
        vo.setEdibleValue("暂无该鱼类的食用价值信息");
        vo.setImages("[]");
        return vo;
    }

    /**
     * 解析图片JSON字符串并拼接完整URL
     */
    private String parseImages(String imagesJson) {
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            java.util.List<String> images = mapper.readValue(imagesJson, new com.fasterxml.jackson.core.type.TypeReference<java.util.List<String>>() {});
            java.util.List<String> fullUrls = images.stream()
                    .map(img -> minioUtils.getFullUrl(img, FISH_IMAGE_DIR))
                    .collect(java.util.stream.Collectors.toList());
            return mapper.writeValueAsString(fullUrls);
        } catch (Exception e) {
            log.error("解析鱼类图片失败: {}", imagesJson, e);
            return "[]";
        }
    }
}
