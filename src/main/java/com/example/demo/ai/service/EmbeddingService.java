package com.example.demo.ai.service;

import ar.training.ai.config.ModelConfig;
import com.alibaba.dashscope.embeddings.TextEmbedding;
import com.alibaba.dashscope.embeddings.TextEmbeddingParam;
import com.alibaba.dashscope.embeddings.TextEmbeddingResult;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class EmbeddingService {
    private static final int DASHSCOPE_MAX_BATCH_SIZE = 25;

    // 模拟向量数据库，存储文本和对应的嵌入向量
    private Map<String, List<Double>> vectorDatabase = new HashMap<>();

    public String embed(List<String> params) {
        TextEmbeddingResult result = null;
        int batchCounter = 0;
        List<String> inputs = new ArrayList<>();
        inputs.addAll(params);
        for (int i = 0; i < inputs.size(); i += DASHSCOPE_MAX_BATCH_SIZE) {
            List<String> batch = inputs.subList(i, Math.min(i + DASHSCOPE_MAX_BATCH_SIZE, inputs.size()));
            TextEmbeddingParam param = TextEmbeddingParam.builder()
                    .apiKey(ModelConfig.ALIYUN_API_KEY)
                    .model(TextEmbedding.Models.TEXT_EMBEDDING_V3)
                    .texts(batch)
                    .build();

            TextEmbedding textEmbedding = new TextEmbedding();
            try {
                TextEmbeddingResult resp = textEmbedding.call(param);
                log.info("TextEmbeddingResult resp: {}", JSON.toJSONString(resp));
                if (resp != null) {
                    if (result == null) {
                        result = resp;
                    } else {
                        for (var emb : resp.getOutput().getEmbeddings()) {
                            emb.setTextIndex(emb.getTextIndex() + batchCounter);
                            result.getOutput().getEmbeddings().add(emb);
                        }
                        result.getUsage().setTotalTokens(result.getUsage().getTotalTokens() + resp.getUsage().getTotalTokens());
                    }
                }
                // 将向量存储到数据库中
                for (int j = 0; j < batch.size(); j++) {
                    vectorDatabase.put(batch.get(j), resp.getOutput().getEmbeddings().get(j).getEmbedding());
                }
            } catch (ApiException | NoApiKeyException e) {
                log.warn("TextEmbeddingResult error={}", e.getMessage());
            }
            batchCounter += batch.size();
        }
        log.info("TextEmbeddingResult result: {}", JSON.toJSONString(result));
        return "OK";
    }

    // 查询方法，返回最相似的文本
    public String embedQuery(String queryText) {
        // 将查询文本转换为向量
        List<String> queryInput = Collections.singletonList(queryText);
        embed(queryInput);
        List<Double> queryVector = vectorDatabase.get(queryText);

        double maxSimilarity = -1;
        String mostSimilarText = null;
        // 在向量数据库中查找最相似的向量
        for (Map.Entry<String, List<Double>> entry : vectorDatabase.entrySet()) {
            String text = entry.getKey();
            List<Double> vector = entry.getValue();
            if (!text.equals(queryText)) {
                double similarity = cosineSimilarity(queryVector, vector);
                if (similarity > maxSimilarity) {
                    maxSimilarity = similarity;
                    mostSimilarText = text;
                }
            }
        }
        return mostSimilarText;
    }

    // 计算两个向量的余弦相似度
    private double cosineSimilarity(List<Double> vectorA, List<Double> vectorB) {
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        // 遍历 List 计算点积和向量的模
        for (int i = 0; i < vectorA.size(); i++) {
            dotProduct += vectorA.get(i) * vectorB.get(i);
            normA += Math.pow(vectorA.get(i), 2);
            normB += Math.pow(vectorB.get(i), 2);
        }
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}
