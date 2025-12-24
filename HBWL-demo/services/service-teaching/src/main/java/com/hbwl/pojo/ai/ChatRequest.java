package com.hbwl.pojo.ai;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ChatRequest {
    @JsonProperty("session_id")
    private String sessionId;
    @JsonProperty("user_input")
    private String userInput;
}

/**
 * class ChatRequest(BaseModel):
 *     """对话请求模型"""
 *     session_id: str = Field(..., description="会话ID")
 *     user_input: str = Field(..., min_length=1, max_length=1000, description="用户输入")
 * */
