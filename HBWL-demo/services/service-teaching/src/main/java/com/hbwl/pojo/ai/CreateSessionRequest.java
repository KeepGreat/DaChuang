package com.hbwl.pojo.ai;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CreateSessionRequest {
    @JsonProperty("session_id")
    private String sessionId;
    @JsonProperty("clear_history")
    private Boolean isClearHistory;
}

//class CreateSessionRequest(BaseModel):
//        """创建会话请求模型"""
//session_id: Optional[str] = Field(
//        default=None,
//        description="可选的会话ID，如果不提供则自动生成"
//)
//clear_history: bool = Field(
//        default=True,
//        description="是否清空历史记录"
//)
