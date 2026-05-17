package com.hbwl.ai.config;

import com.hbwl.ai.AITeacherService;
import com.hbwl.ai.listener.CustomChatModelListener;
import com.hbwl.ai.tool.CodeSandboxTool;
import com.hbwl.ai.properties.TeachingProperties;
import com.hbwl.ai.utils.AITeacherChatMemoryStore;
import com.hbwl.ai.utils.PromptBaseTemplateLoader;
import com.hbwl.ai.utils.RuntimePromptManager;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
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
import dev.langchain4j.web.search.WebSearchTool;
import dev.langchain4j.web.search.searchapi.SearchApiWebSearchEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

import static dev.langchain4j.store.embedding.filter.MetadataFilterBuilder.metadataKey;

@Configuration
public class ModelConfig {

    @Autowired
    private TeachingProperties teachingProperties;

    private static final String SEARCH_ENGINE_API_KEY = "W5XtS94VJzoMPbq5WUm3bK59";
    private static final String SEARCH_ENGINE_NAME = "baidu";

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

    //用于给AI向数据库中查询相关向量
    @Bean
    public EmbeddingStoreContentRetriever embeddingStoreContentRetriever(EmbeddingStore<TextSegment> embeddingStore,
                                                                         EmbeddingModel embeddingModel){
        return EmbeddingStoreContentRetriever.builder()
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .maxResults(5)
                .minScore(0.75)
                .dynamicFilter(query -> {
                    List<Integer> materialId = query.metadata().invocationParameters().get("materialId");
                    return metadataKey("material_id").isIn(materialId);
                })
                .build();
    }

    //用于给AI提供联网搜索工具
    @Bean
    public WebSearchTool webSearchTool(){
        return WebSearchTool.from(
                SearchApiWebSearchEngine.builder()
                        .apiKey(SEARCH_ENGINE_API_KEY)
                        .engine(SEARCH_ENGINE_NAME)
                        .build()
        );
    }

    //智能助教
    @Bean
    public AITeacherService aiTeacherService(StreamingChatModel streamingChatModel,
                                             CodeSandboxTool codeSandboxTool,
                                             EmbeddingStoreContentRetriever embeddingStoreContentRetriever,
                                             AITeacherChatMemoryStore aiTeacherChatMemoryStore,
                                             PromptBaseTemplateLoader promptBaseTemplateLoader,
                                             RuntimePromptManager runtimePromptManager,
                                             WebSearchTool webSearchTool){
        ChatMemoryProvider chatMemoryProvider = memory -> MessageWindowChatMemory.builder()
                .id(memory)
                .maxMessages(10)
                .chatMemoryStore(aiTeacherChatMemoryStore)
                .build();

        return AiServices.builder(AITeacherService.class)
                .streamingChatModel(streamingChatModel)
                .tools(codeSandboxTool)
                .tools(webSearchTool) //联网搜索
                .contentRetriever(embeddingStoreContentRetriever)
                .chatMemoryProvider(chatMemoryProvider)
                .systemMessageProvider(memoryId -> promptBaseTemplateLoader.getBasePrompt())
                .systemMessageTransformer(systemMessage -> {
                    String runtimePrompt = runtimePromptManager.getRuntimePrompt();
                    return runtimePromptManager.composePrompt(systemMessage, runtimePrompt);
                })
                .build();
    }
}
