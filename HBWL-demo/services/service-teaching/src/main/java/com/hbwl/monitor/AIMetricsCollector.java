package com.hbwl.monitor;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class AIMetricsCollector {

    @Autowired
    private MeterRegistry meterRegistry;

    //缓存已创建的指标，避免重复创建
    private final ConcurrentHashMap<String, Counter> requestCounterCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Counter> errorCounterCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Counter> tokenCounterCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Timer> responseTimerCache = new ConcurrentHashMap<>();

    public void recordRequest(String userId, String appId, String modelName, String status){
        String key = String.format("requests.%s.%s.%s.%s", userId, appId, modelName, status);
        Counter counter = requestCounterCache.computeIfAbsent(key, k ->
                Counter.builder("ai_requests_total")
                        .description("AI请求总数")
                        .tag("userId", userId)
                        .tag("appId", appId)
                        .tag("modelName", modelName)
                        .tag("status", status)
                        .register(meterRegistry));
        counter.increment();
    }

    public void recordError(String userId, String appId, String modelName, String errorMessage){
        String key = String.format("errors.%s.%s.%s.%s", userId, appId, modelName, errorMessage);
        Counter counter = errorCounterCache.computeIfAbsent(key, k ->
                Counter.builder("ai_errors_total")
                        .description("AI错误总数")
                        .tag("userId", userId)
                        .tag("appId", appId)
                        .tag("modelName", modelName)
                        .tag("errorMessage", errorMessage)
                        .register(meterRegistry));
        counter.increment();
    }

    public void recordTokens(String userId, String appId, String modelName, String tokenType, long tokenCount){
        String key = String.format("tokens.%s.%s.%s.%s", userId, appId, modelName, tokenType);
        Counter counter = tokenCounterCache.computeIfAbsent(key, k ->
                Counter.builder("ai_tokens_total")
                        .description("AI使用的Token总数")
                        .tag("userId", userId)
                        .tag("appId", appId)
                        .tag("modelName", modelName)
                        .tag("tokenType", tokenType)
                        .register(meterRegistry));
        counter.increment(tokenCount);
    }

    public void recordResponseTime(String userId, String appId, String modelName, Duration duration){
        String key = String.format("response_time.%s.%s.%s", userId, appId, modelName);
        Timer timer = responseTimerCache.computeIfAbsent(key, k ->
                Timer.builder("ai_response_time")
                        .description("AI响应时间")
                        .tag("userId", userId)
                        .tag("appId", appId)
                        .tag("modelName", modelName)
                        .register(meterRegistry));
        timer.record(duration);
    }
}
