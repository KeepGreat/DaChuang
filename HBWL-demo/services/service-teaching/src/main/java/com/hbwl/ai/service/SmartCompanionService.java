package com.hbwl.ai.service;

import com.hbwl.pojo.ai.*;

public interface SmartCompanionService {

    SessionResponse createSession(CreateSessionRequest createSessionRequest);

    //设置userId是为了向评估模块反馈
    ChatResponse chat(ChatRequest chatRequest, String userId);

    HistoryResponse getConversationHistory(String sessionId);

    void resetSession(String sessionId);

    StatusResponse getSessionStatus(String sessionId);
}
