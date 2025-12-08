package com.hbwl.ai.service.impl;

import com.hbwl.ai.service.SmartCompanionService;
import com.hbwl.ai.tool.SmartCompanionTool;
import com.hbwl.pojo.ai.*;
import org.springframework.stereotype.Service;

@Service
public class SmartCompanionServiceImpl implements SmartCompanionService {

    private final SmartCompanionTool smartCompanionTool = new SmartCompanionTool();

    @Override
    public SessionResponse createSession(CreateSessionRequest createSessionRequest) {
        if (createSessionRequest == null) return null;
        return smartCompanionTool.createSession(createSessionRequest);
    }

    @Override
    public ChatResponse chat(ChatRequest chatRequest) {
        if (chatRequest == null ||
            chatRequest.getSessionId() == null || chatRequest.getSessionId().trim().isEmpty() ||
            chatRequest.getUserInput() == null || chatRequest.getUserInput().trim().isEmpty()) return null;
        return smartCompanionTool.chatWithCompanion(chatRequest);
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
