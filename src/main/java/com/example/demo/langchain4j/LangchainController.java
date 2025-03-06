package com.example.demo.langchain4j;

import jakarta.annotation.Resource;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("langchain")
public class LangchainController {

    @Data
    public static class ChatParam {
        public String message;
    }

    @Resource
    private MyLangchainService myLangchainService;

    @PostMapping("chat")
    public String chat(@RequestBody ChatParam chatParam) {
        log.info("/langchain/chat:{}", chatParam);
        return myLangchainService.chat(chatParam.getMessage());
    }

    @PostMapping("sql")
    public String sql(@RequestBody ChatParam chatParam) {
        log.info("/langchain/sql:{}", chatParam);
        return myLangchainService.sql(chatParam.getMessage());
    }
}
