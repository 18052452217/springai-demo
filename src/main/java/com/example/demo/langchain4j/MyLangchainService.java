package com.example.demo.langchain4j;

import dev.langchain4j.experimental.rag.content.retriever.sql.SqlDatabaseContentRetriever;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

@Service
@Slf4j
public class MyLangchainService {

    @Autowired
    private ChatLanguageModel chatLanguageModel;

    @Resource
    private DataSource dataSource;

    public String chat(String message) {
        String result = chatLanguageModel.chat(message);
        log.info("result = {}" ,result);
        return result;
    }

    public String sql(String message) {
        ContentRetriever contentRetriever = SqlDatabaseContentRetriever.builder()
                .dataSource(dataSource)
                .chatLanguageModel(chatLanguageModel)
                .build();

        Assistant assistant = AiServices.builder(Assistant.class)
                .chatLanguageModel(chatLanguageModel)
                .contentRetriever(contentRetriever)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .build();
//        return assistant.answer(message);
        // 异步处理
        new Thread(() -> {
            Utils.startConversationWith(assistant);
        });
        return "";
    }
}
