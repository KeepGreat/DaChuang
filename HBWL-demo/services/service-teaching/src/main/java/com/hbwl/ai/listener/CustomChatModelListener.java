package com.hbwl.ai.listener;

import com.hbwl.monitor.AIMetricsCollector;
import com.hbwl.monitor.MonitorContext;
import com.hbwl.monitor.MonitorContextHolder;
import dev.langchain4j.model.chat.listener.ChatModelErrorContext;
import dev.langchain4j.model.chat.listener.ChatModelListener;
import dev.langchain4j.model.chat.listener.ChatModelRequestContext;
import dev.langchain4j.model.chat.listener.ChatModelResponseContext;
import dev.langchain4j.model.output.TokenUsage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

@Component
public class CustomChatModelListener implements ChatModelListener {

    private static final String REQUEST_START_TIME = "requestStartTime";

    private static final String MONITOR_CONTEXT = "monitorContext";

    @Autowired
    private AIMetricsCollector aiMetricsCollector;

    @Override
    public void onRequest(ChatModelRequestContext requestContext) {
//        System.out.println("--------------------------------------");
//        System.out.println("发起模型调用请求");
//        System.out.println("请求模型: " + requestContext.modelProvider());
//        System.out.println("--------------------------------------");
        requestContext.attributes().put(REQUEST_START_TIME, Instant.now());
        //从监控上下文获取信息
        MonitorContext monitorContext = MonitorContextHolder.getContext();
        String userId = monitorContext.getUserId();
        String appId = monitorContext.getAppId();
        requestContext.attributes().put(MONITOR_CONTEXT, monitorContext);
        //获取模型名称
        String modelName = requestContext.chatRequest().modelName();
        aiMetricsCollector.recordRequest(userId, appId, modelName, "started");
    }

    @Override
    public void onResponse(ChatModelResponseContext responseContext) {
//        System.out.println("--------------------------------------");
//        System.out.println("模型调用成功");
//        System.out.println("消耗Token: " + responseContext.chatResponse().tokenUsage().toString());
//        System.out.println("--------------------------------------");
        Map<Object, Object> attributes = responseContext.attributes();
        MonitorContext monitorContext = (MonitorContext) attributes.get(MONITOR_CONTEXT);
        if(monitorContext != null){
            String userId = monitorContext.getUserId();
            String appId = monitorContext.getAppId();
            String modelName = responseContext.chatRequest().modelName();
            recordResponseTime(attributes, userId, appId, modelName);
            recordTokenUsage(responseContext, userId, appId, modelName);
            aiMetricsCollector.recordRequest(userId, appId, modelName, "success");
        }
    }

    @Override
    public void onError(ChatModelErrorContext errorContext) {
//        System.out.println("--------------------------------------");
//        System.out.println("模型调用出错: " + errorContext.toString());
//        System.out.println("--------------------------------------");
        //从监控上下文获取信息
        MonitorContext monitorContext = MonitorContextHolder.getContext();
        String userId = monitorContext.getUserId();
        String appId = monitorContext.getAppId();
        String modelName = errorContext.chatRequest().modelName();
        String errorMessage = errorContext.error().getMessage();
        aiMetricsCollector.recordError(userId, appId, modelName, errorMessage);
        aiMetricsCollector.recordRequest(userId, appId, modelName, "error");

        Map<Object, Object> attributes = errorContext.attributes();
        recordResponseTime(attributes, userId, appId, modelName);
    }

    private void recordResponseTime(Map<Object, Object> attributes, String userId, String appId, String modelName){
        Instant startTime = (Instant) attributes.get(REQUEST_START_TIME);
        if(startTime != null){
            Duration responseTime = Duration.between(startTime, Instant.now());
            aiMetricsCollector.recordResponseTime(userId, appId, modelName, responseTime);
        }
    }

    private void recordTokenUsage(ChatModelResponseContext responseContext, String userId, String appId, String modelName){
        TokenUsage tokenUsage = responseContext.chatResponse().tokenUsage();
        if(tokenUsage != null){
            aiMetricsCollector.recordTokens(userId, appId, modelName, "input", tokenUsage.inputTokenCount());
            aiMetricsCollector.recordTokens(userId, appId, modelName, "output", tokenUsage.outputTokenCount());
            aiMetricsCollector.recordTokens(userId, appId, modelName, "total", tokenUsage.totalTokenCount());
        }
    }
}
