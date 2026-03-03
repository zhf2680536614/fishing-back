package com.fishing.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * 豆包大模型配置
 */
@Configuration
public class DoubaoConfig {
    
    /**
     * 豆包 API Key
     */
    @Value("${doubao.api.key:}")
    private String apiKey;
    
    /**
     * 豆包 API 地址
     */
    @Value("${doubao.api.url:https://ark.cn-beijing.volces.com/api/v3/chat/completions}")
    private String apiUrl;
    
    /**
     * 模型名称
     */
    @Value("${doubao.model.name:ep-20260303181647-n4595}")
    private String modelName;

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }
}
