package com.example.demo.deepceek;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class DeepSeekClient {
    private final RestTemplate restTemplate;
    private final String apiKey;
    private final String baseUrl;
    
    public String chat(String message) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);
        
        Map<String, Object> messageMap = new HashMap<>();
        messageMap.put("role", "user");
        messageMap.put("content", message);
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "deepseek-chat");
        requestBody.put("messages", List.of(messageMap));
        
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        
        var response = restTemplate.postForObject(
            baseUrl + "/chat/completions",
            request,
            DeepSeekResponse.class
        );
        
        return response != null ? response.getChoices().get(0).getMessage().getContent() : "";
    }
} 