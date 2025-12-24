package com.hbwl.pojo.ai;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class HistoryResponse {
    @JsonProperty("session_id")
    private String sessionId;
    @JsonProperty("history")
    private List<HistoryItem> historyItemList;
    @JsonProperty("total_turns")
    private Integer totalTurns;
    @JsonProperty("current_topic")
    private String currentTopic;
    @JsonProperty("turn_count")
    private Integer turnCount;
}

//class HistoryResponse(BaseModel):
//        """历史记录响应模型"""
//session_id: str
//history: List[HistoryItem]
//total_turns: int
//current_topic: str
//turn_count: int
