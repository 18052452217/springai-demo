package com.example.demo.deepceek;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 报表管理
 *
 * @author yang.li
 * @date 2022/7/26 10:08
 */
@RestController
@RequestMapping("/ai")
public class AiController {

    public static final String DEEPSEEK_MODEL = "deepseek-chat";
    public static final String QIAN_WEN_MODEL = "qwen-plus";
    @Resource
    private OpenAiChatModel chatModel;
    @Resource
    private EmbeddingModel embeddingModel;
    @Resource
    private VectorStore vectorStore;
    private final List<Message> chatHistoryList = new ArrayList<>();
    @PostConstruct
    public void init () {
        chatHistoryList.add(new SystemMessage("你是一个基于deepseek的知识助手."));
    }
    @PostMapping("/chat" )
    public String test (@RequestBody AiParam aiParam) {
        chatHistoryList.add (new UserMessage(aiParam.getMessage())) ;
        Prompt prompt = new Prompt(chatHistoryList,
                OpenAiChatOptions.builder()
                        .model(QIAN_WEN_MODEL)
                        .temperature(0.4)
                        .build());
        ChatResponse chatResponse = chatModel.call(prompt);
        if (chatResponse.getResult() != null && chatResponse.getResult().getOutput () != null) {
            chatHistoryList.add (chatResponse.getResult().getOutput());
            return chatResponse.getResult().getOutput().getContent();
        }
        return "服务繁忙，请稍后重试！";
    }

    @PostMapping("/embed" )
    public EmbeddingResponse embed(@RequestBody AiParam aiParam) {
        return this.embeddingModel.embedForResponse(
                List.of("你好", aiParam.getMessage())
        );
    }

    @PostMapping("/milvus/add" )
    public Boolean milvusAdd(@RequestBody AiParam aiParam) {
        List <Document> documents = aiParam.getMessages().stream()
                .map(s -> new Document(s, Map.of("meta1", "meta1"))).collect(Collectors.toList());
        documents.addAll(List.of(
                new Document("Spring AI rocks!! Spring AI rocks!! Spring AI rocks!! Spring AI rocks!! Spring AI rocks!!", Map.of("meta1", "meta1")),
                new Document("The World is Big and Salvation Lurks Around the Corner"),
                new Document("You walk forward facing the past and you turn back toward the future.", Map.of("meta2", "meta2"))));
        vectorStore.add(documents);
        return true;
    }

    @PostMapping("/milvus/search" )
    public List<Document> search(@RequestBody AiParam aiParam) {
        FilterExpressionBuilder b = new FilterExpressionBuilder();
        List<Document> documentList =vectorStore.similaritySearch(SearchRequest.builder()
                .query(aiParam.getMessage())
                .topK(5)
                .similarityThreshold(0.5)
                .filterExpression(b.and(
                        b.in("author","john", "jill"),
                        b.eq("article_type", "blog")).build()).build());
        return documentList;
    }
}