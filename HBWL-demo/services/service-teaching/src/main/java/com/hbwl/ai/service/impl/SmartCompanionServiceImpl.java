package com.hbwl.ai.service.impl;

import com.hbwl.ai.service.SmartCompanionService;
import com.hbwl.ai.tool.SmartCompanionTool;
import com.hbwl.feign.EvaluationFeignClient;
import com.hbwl.pojo.ai.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class SmartCompanionServiceImpl implements SmartCompanionService {

    private final SmartCompanionTool smartCompanionTool = new SmartCompanionTool();

    @Autowired
    private EvaluationFeignClient evaluationFeignClient;

    @Override
    public SessionResponse createSession(CreateSessionRequest createSessionRequest) {
        if (createSessionRequest == null) return null;
        createSessionRequest.setMaxTurns(6);
        return smartCompanionTool.createSession(createSessionRequest);
    }

    @Override
    public ChatResponse chat(ChatRequest chatRequest, String userId) {
        if (chatRequest == null ||
            chatRequest.getSessionId() == null || chatRequest.getSessionId().trim().isEmpty() ||
            chatRequest.getUserInput() == null || chatRequest.getUserInput().trim().isEmpty()) return null;
        ChatResponse chatResponse = smartCompanionTool.chatWithCompanion(chatRequest);
        //分析对话结果
        if (chatResponse.getIsSessionEnded()){
            Map<String, Object> scores = new HashMap<>();
            UserScoreResponse userScores = chatResponse.getUserScores();
            scores.put("expressionScore", userScores.getExpressionScore());
            scores.put("conversionEfficiencyScore", userScores.getConversionEfficiencyScore());
            evaluationFeignClient.evaluateBaseOnSmartCompanion(scores);
        }
        return chatResponse;
    }

    @Override
    public HistoryResponse getConversationHistory(String sessionId) {
        if (sessionId == null || sessionId.trim().isEmpty()) return null;
        return smartCompanionTool.getConversationHistory(sessionId);
    }

    @Override
    public void resetSession(String sessionId) {
        if (sessionId == null || sessionId.trim().isEmpty()) return;
        smartCompanionTool.resetSession(sessionId);
    }

    @Override
    public StatusResponse getSessionStatus(String sessionId) {
        if (sessionId == null || sessionId.trim().isEmpty()) return null;
        return smartCompanionTool.getSessionStatus(sessionId);
    }
}
