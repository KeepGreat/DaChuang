package com.hbwl.pojo.ai;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ChatResponse {
    @JsonProperty("session_id")
    private String sessionId;
    @JsonProperty("ai_response")
    private String aiResponse;
    @JsonProperty("turn_count")
    private Integer turnCount;
    @JsonProperty("current_topic")
    private String currentTopic;
    @JsonProperty("is_guide_mode")
    private Boolean isGuideMode;
    @JsonProperty("guide_count")
    private Integer guideCount;
    @JsonProperty("response_time")
    private String responseTime;
    @JsonProperty("user_scores")
    private UserScoreResponse userScores;
    @JsonProperty("is_session_ended")
    private Boolean isSessionEnded;
    @JsonProperty("max_turns")
    private Integer maxTurns;
    @JsonProperty("remaining_turns")
    private Integer remainingTurns;
}

//class ChatResponse(BaseModel):
//        """对话响应模型"""
//session_id: str
//ai_response: str
//turn_count: int
//current_topic: str
//is_guide_mode: bool = False
//guide_count: int = 0
//response_time: str
