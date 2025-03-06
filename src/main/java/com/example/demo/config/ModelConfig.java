package com.example.demo.config;

import io.milvus.client.MilvusServiceClient;
import io.milvus.param.ConnectParam;
import io.milvus.param.IndexType;
import io.milvus.param.MetricType;
import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.TokenCountBatchingStrategy;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.milvus.MilvusVectorStore;
import org.springframework.ai.vectorstore.redis.RedisVectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPooled;

@Configuration
public class ModelConfig {

    // deepseek
    public static final String DEEPSEEK_API_KEY = "sk-1f81847888c44e70b0f2e59eeafed994";
    public static final String DEEPSEEK_API_URL = "https://api.deepseek.com";

    // 阿里云千问
    public static final String ALIYUN_API_KEY = "sk-9b7ac00915ea4a7a84310c2d6bbcad9b";
    public static final String ALIYUN_API_URL = "https://dashscope.aliyuncs.com/compatible-mode/v1";

    public static final String TEXT_MODEL = "text-embedding-v3";

    public static final String MILVUS_URL = "127.0.0.1:19530";
    @Bean
    public OpenAiChatModel openAiChatModel() {
        OpenAiApi openAiApi = new OpenAiApi(DEEPSEEK_API_URL, DEEPSEEK_API_KEY);
        return new OpenAiChatModel(openAiApi);
    }

    @Bean
    public EmbeddingModel embeddingModel() {
        OpenAiApi openAiApi = new OpenAiApi(DEEPSEEK_API_URL, DEEPSEEK_API_KEY);
        return new OpenAiEmbeddingModel(
                openAiApi,
                MetadataMode.EMBED,
                OpenAiEmbeddingOptions.builder()
                        .model(TEXT_MODEL)
                        .build(),
                RetryUtils.DEFAULT_RETRY_TEMPLATE);
    }

//    @Bean
//    public VectorStore vectorStore(MilvusServiceClient milvusClient, EmbeddingModel embeddingModel) {
//        return MilvusVectorStore.builder(milvusClient, embeddingModel)
//                .collectionName("test_vector_store")
//                .databaseName("default")
//                .indexType(IndexType.IVF_FLAT)
//                .metricType(MetricType.COSINE)
//                .batchingStrategy(new TokenCountBatchingStrategy())
//                .initializeSchema(true)
//                .build();
//    }

    @Bean
    public MilvusServiceClient milvusClient() {
        return new MilvusServiceClient(ConnectParam.newBuilder()
                .withAuthorization("minioadmin", "minioadmin")
                .withUri(MILVUS_URL)
                .build());
    }

    @Bean
    public JedisPooled jedisPooled() {
        return new JedisPooled("r-bp1nq29kupvzls7jys.redis.rds.aliyuncs.com",
                6379);
    }

    @Bean
    public VectorStore vectorStore(JedisPooled jedisPooled, EmbeddingModel embeddingModel) {
        return RedisVectorStore.builder(jedisPooled, embeddingModel)
                .indexName("custom-index")                // Optional: defaults to "spring-ai-index"
                .prefix("custom-prefix")                  // Optional: defaults to "embedding:"
                .metadataFields(                         // Optional: define metadata fields for filtering
                        RedisVectorStore.MetadataField.tag("country"),
                        RedisVectorStore.MetadataField.numeric("year"))
                .initializeSchema(true)                   // Optional: defaults to false
                .batchingStrategy(new TokenCountBatchingStrategy()) // Optional: defaults to TokenCountBatchingStrategy
                .build();
    }
}
