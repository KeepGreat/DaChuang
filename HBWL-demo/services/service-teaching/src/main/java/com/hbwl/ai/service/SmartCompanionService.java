package com.hbwl.ai.service;

import com.hbwl.pojo.ai.*;

public interface SmartCompanionService {

    SessionResponse createSession(CreateSessionRequest createSessionRequest);

    ChatResponse chat(ChatRequest chatRequest);

    HistoryResponse getConversationHistory(String sessionId);

    void resetSession(String sessionId);

    StatusResponse getSessionStatus(String sessionId);
}
