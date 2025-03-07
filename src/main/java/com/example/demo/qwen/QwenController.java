package com.example.demo.qwen;


import com.example.demo.deepceek.AiParam;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 报表管理
 *
 * @author yang.li
 * @date 2022/7/26 10:08
 */
@RestController
@RequestMapping("/ai/qwen")
public class QwenController {
    @Resource
    private QwenService qwenService;
    @Resource
    private EmbeddingService embeddingService;
    @PostMapping("/chat" )
    public String chat(@RequestBody AiParam aiParam) {
        return qwenService.chat(aiParam.getMessages().get(0), aiParam.getMessages().get(1));
    }

    @PostMapping("/embed" )
    public String embed(@RequestBody AiParam aiParam) {
        return embeddingService.embed(aiParam.getMessages());
    }

    @PostMapping("/embed/query" )
    public String embedQuery(@RequestBody AiParam aiParam) {
        return embeddingService.embedQuery(aiParam.getMessage());
    }
}