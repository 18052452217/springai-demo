package com.example.demo.ai.service;

import ar.training.ai.config.ModelConfig;
import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@Slf4j
public class LLMService {
    public String llaChat(String sysMessage, String userMessage) {
        try {
            Generation gen = new Generation();
            Message systemMsg = Message.builder()
                    .role(Role.SYSTEM.getValue())
                    .content(sysMessage)
                    .build();
            Message userMsg = Message.builder()
                    .role(Role.USER.getValue())
                    .content(userMessage)
                    .build();
            GenerationParam param = GenerationParam.builder()
                    // 若没有配置环境变量，请用百炼API Key将下行替换为：.apiKey("sk-xxx")
                    .apiKey(ModelConfig.ALIYUN_API_KEY)
                    .model(ModelConfig.QWEN_PLUS)
                    .messages(Arrays.asList(systemMsg, userMsg))
                    .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                    .build();
            GenerationResult result = gen.call(param);
            log.info("LLAService GenerationResult result:{}", JSON.toJSONString(result));
            String resultStr = result.getOutput().getChoices().get(0).getMessage().getContent();
            String resultJson = resultStr.replace("```json", "")
                    .replace("```sql", "")
                    .replace("```", "")
                    .replace("\n", "")
                    .trim();
            log.info("LLAService GenerationResult resultJson:{}", resultJson);
            return resultJson;
        } catch (Exception e) {
            log.warn("LLAService GenerationResult failed", e);
            log.warn("错误信息：" + e.getMessage());
            log.warn("请参考文档：https://help.aliyun.com/zh/model-studio/developer-reference/error-code");
        }
        return "";
    }
}
