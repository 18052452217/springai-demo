package com.example.demo.ai.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelConfig {

    // deepseek
    public static final String DEEPSEEK_API_KEY = "sk-1f81847888c44e70b0f2e59eeafed994";
    public static final String DEEPSEEK_API_URL = "https://api.deepseek.com";

    // 阿里云千问
    public static final String ALIYUN_API_KEY = "sk-9b7ac00915ea4a7a84310c2d6bbcad9b";
    public static final String ALIYUN_API_URL = "https://dashscope.aliyuncs.com/compatible-mode/v1";
    public static final String QWEN_EMBEDDIND_MODEL = "text-embedding-v3";
    public static final String QWEN_PLUS = "qwen-plus";

}
