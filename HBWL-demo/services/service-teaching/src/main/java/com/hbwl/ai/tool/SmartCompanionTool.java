package com.hbwl.ai.tool;

import com.hbwl.pojo.ai.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

public class SmartCompanionTool {

    private static final RestTemplate restTemplate;

    private static final String baseURL = "http://localhost:8000";

    static {
        SimpleClientHttpRequestFactory simpleClientHttpRequestFactory = new SimpleClientHttpRequestFactory();
        simpleClientHttpRequestFactory.setConnectTimeout(20000);
        simpleClientHttpRequestFactory.setReadTimeout(180000);
        restTemplate = new RestTemplate(simpleClientHttpRequestFactory);
    }

//    @app.post("/api/v1/sessions", response_model=SessionResponse)
//    async def create_session(request: CreateSessionRequest = None):
//            """
//    创建新的对话会话
//
//    - 如果提供 session_id，则使用指定的 ID
//    - 如果不提供，则生成新的唯一 ID
//    - 可以指定是否清空历史记录
//    """
    public SessionResponse createSession(CreateSessionRequest request){
        String url = baseURL + "/api/v1/sessions";
        ResponseEntity<SessionResponse> response = restTemplate.postForEntity(url, request, SessionResponse.class);
        return response.getBody();
    }

//    @app.post("/api/v1/chat", response_model=ChatResponse)
//    async def chat_with_companion(chat_request: ChatRequest):
//            """
//    与智能学伴进行对话交互
//
//    参数:
//    - session_id: 会话ID（从创建会话接口获取）
//    - user_input: 用户输入的消息
//
//    返回:
//    - ai_response: AI的回复
//    - turn_count: 当前对话轮数
//    - current_topic: 当前讨论话题
//    - is_guide_mode: 是否处于引导者模式
//    """
    public ChatResponse chatWithCompanion(ChatRequest request){
        String url = baseURL + "/api/v1/chat";
        ResponseEntity<ChatResponse> response = restTemplate.postForEntity(url, request, ChatResponse.class);
        return response.getBody();
    }

//    @app.get("/api/v1/sessions/{session_id}/history", response_model=HistoryResponse)
//    async def get_conversation_history(session_id: str):
//            """
//    获取指定会话的完整对话历史记录
//    """
    public HistoryResponse getConversationHistory(String sessionId){
        String url = baseURL + "/api/v1/sessions/" + sessionId + "/history";
        ResponseEntity<HistoryResponse> response = restTemplate.getForEntity(url, HistoryResponse.class);
        return response.getBody();
    }

//    @app.delete("/api/v1/sessions/{session_id}")
//    async def reset_session(session_id: str):
//            """
//    重置指定会话，清空对话历史
//    """
    public void resetSession(String sessionId){
        String url = baseURL + "/api/v1/sessions/" + sessionId;
        restTemplate.delete(url);
    }

//    @app.get("/api/v1/sessions/{session_id}/status")
//    async def get_session_status(session_id: str):
//            """
//    获取会话状态信息
//    """
    public StatusResponse getSessionStatus(String sessionId){
        String url = baseURL + "/api/v1/sessions/" + sessionId + "/status";
        ResponseEntity<StatusResponse> response = restTemplate.getForEntity(url, StatusResponse.class);
        return response.getBody();
    }
}
