package com.hbwl.pojo.ai;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SessionResponse {
    @JsonProperty("session_id")
    private String sessionId;
    @JsonProperty("welcome_message")
    private String welcomeMessage;
    @JsonProperty("created_at")
    private String createdAt;
    private String message;
    @JsonProperty("max_turns")
    private Integer maxTurns;
}

//class SessionResponse(BaseModel):
//        """创建会话响应模型"""
//session_id: str
//welcome_message: str
//created_at: str
//message: str = "会话创建成功"
