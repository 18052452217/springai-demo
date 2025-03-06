package com.example.demo.config;

import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DeepSeekConfig {

    @Bean
    public OpenAiChatModel openAiChatModel() {
        OpenAiApi openAiApi = new OpenAiApi("https://api.deepseek.com", "sk-1f81847888c44e70b0f2e59eeafed994");
        return new OpenAiChatModel(openAiApi);
    }
}
