package com.hbwl.pojo.ai;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ErrorResponse {
    private Boolean error;
    private String message;
    private String timestamp;
}

//class ErrorResponse(BaseModel):
//        """错误响应模型"""
//error: bool = True
//message: str
//timestamp: str