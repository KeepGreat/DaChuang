package com.hbwl.ai.config;

import com.hbwl.ai.AICodeAnalyzerService;
import com.hbwl.ai.AICodeEvaluatorService;
import com.hbwl.ai.listener.CustomChatModelListener;
import com.hbwl.ai.tool.CodeSandboxTool;
import com.hbwl.properties.AnalysisProperties;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.service.AiServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import dev.langchain4j.model.openai.OpenAiChatModel;

import java.util.List;

@Configuration
public class ModelConfig {

    @Autowired
    private AnalysisProperties analysisProperties;

    @Bean
    public ChatModel chatModel(){
        return OpenAiChatModel.builder()
                .apiKey(analysisProperties.getApiKey())
                .baseUrl(analysisProperties.getBaseURL())
                .modelName(analysisProperties.getModelName())
                .listeners(List.of(new CustomChatModelListener()))
                .build();
    }

    @Bean
    public StreamingChatModel streamingChatModel(){
        return OpenAiStreamingChatModel.builder()
                .apiKey(analysisProperties.getApiKey())
                .baseUrl(analysisProperties.getStreamBaseURL())
                .modelName(analysisProperties.getStreamModelName())
                .listeners(List.of(new CustomChatModelListener()))
                .build();
    }

    //作为tool供AICodeEvaluatorService调用
    @Bean
    public AICodeAnalyzerService aiCodeAnalyzerService(ChatModel chatModel){
        return AiServices.builder(AICodeAnalyzerService.class)
                .chatModel(chatModel)
                .build();
    }

    @Bean
    public AICodeEvaluatorService aiCodeEvaluatorService(StreamingChatModel streamingChatModel,
                                                         AICodeAnalyzerService aiCodeAnalyzerService,
                                                         CodeSandboxTool codeSandboxTool){
        return AiServices.builder(AICodeEvaluatorService.class)
                .streamingChatModel(streamingChatModel)
                .tools(aiCodeAnalyzerService, codeSandboxTool)
                .build();
    }
}
