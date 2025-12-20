package com.hbwl.ai.config;

import com.hbwl.ai.AITeacherService;
import com.hbwl.ai.listener.CustomChatModelListener;
import com.hbwl.ai.tool.CodeSandboxTool;
import com.hbwl.ai.properties.TeachingProperties;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ModelConfig {

    @Autowired
    private TeachingProperties teachingProperties;

    @Bean
    public ChatModel chatModel(){
        return OpenAiChatModel.builder()
                .apiKey(teachingProperties.getApiKey())
                .baseUrl(teachingProperties.getBaseURL())
                .modelName(teachingProperties.getModelName())
                .listeners(List.of(new CustomChatModelListener()))
                .build();
    }

    @Bean
    public StreamingChatModel streamingChatModel(){
        return OpenAiStreamingChatModel.builder()
                .apiKey(teachingProperties.getApiKey())
                .baseUrl(teachingProperties.getStreamBaseURL())
                .modelName(teachingProperties.getStreamModelName())
                .listeners(List.of(new CustomChatModelListener()))
                .build();
    }

    @Bean
    public EmbeddingModel embeddingModel(){
        return OpenAiEmbeddingModel.builder()
                .apiKey(teachingProperties.getApiKey())
                .modelName(teachingProperties.getEmbedModelName())
                .baseUrl(teachingProperties.getEmbedBaseURL())
                .maxSegmentsPerBatch(8)
                .build();
    }

    //用于存储向量
    @Bean
    public EmbeddingStore<TextSegment> embeddingStore(EmbeddingModel embeddingModel){
        return PgVectorEmbeddingStore.builder()
                .host("localhost")
                .port(5433)
                .database("postgres")
                .user("postgres")
                .password("123456")
                .table("teaching_material")
                .dimension(embeddingModel.dimension()) //1024维
                .createTable(true)
                .build();
    }

    //向量注入
//    @Bean
//    public EmbeddingStoreIngestor embeddingStoreIngestor(EmbeddingModel embeddingModel,
//                                                         EmbeddingStore<TextSegment> embeddingStore){
//        return EmbeddingStoreIngestor.builder()
//                .embeddingModel(embeddingModel)
//                .embeddingStore(embeddingStore)
//                .documentSplitter(new DocumentByParagraphSplitter(1000, 200))
//                .build();
//    }

    //用于给AI向数据库中查询相关向量
    @Bean
    public EmbeddingStoreContentRetriever embeddingStoreContentRetriever(EmbeddingStore<TextSegment> embeddingStore,
                                                                         EmbeddingModel embeddingModel){
        return EmbeddingStoreContentRetriever.builder()
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .maxResults(5)
                .minScore(0.75)
                .build();
    }

    //智能助教
    @Bean
    public AITeacherService aiTeacherService(StreamingChatModel streamingChatModel,
                                             CodeSandboxTool codeSandboxTool,
                                             EmbeddingStoreContentRetriever embeddingStoreContentRetriever){
        return AiServices.builder(AITeacherService.class)
                .streamingChatModel(streamingChatModel)
                .tools(codeSandboxTool)
                .contentRetriever(embeddingStoreContentRetriever)
                .build();
    }
}
