package com.hbwl.pojo.ai;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class StatusResponse {
    @JsonProperty("session_id")
    private String sessionId;
    @JsonProperty("turn_count")
    private Integer turnCount;
    @JsonProperty("current_topic")
    private String currentTopic;
    @JsonProperty("is_guide_mode")
    private Boolean isGuideMode;
    @JsonProperty("guide_trigger_count")
    private Integer guideTriggerCount;
    @JsonProperty("conversation_history_length")
    private Integer conversationHistoryLength;
    private String timestamp;
}

//class StatusResponse(BaseModel):
//        """状态响应模型"""
//session_id: str
//turn_count: int
//current_topic: str
//is_guide_mode: bool
//guide_trigger_count: int
//conversation_history_length: int
//timestamp: str
