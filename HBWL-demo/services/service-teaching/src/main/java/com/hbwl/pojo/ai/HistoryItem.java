package com.hbwl.pojo.ai;

import lombok.Data;

@Data
public class HistoryItem {
    private String role;
    private String content;
    private Integer index;
}

//class HistoryItem(BaseModel):
//        """历史记录项模型"""
//role: str  # user, assistant, guide
//content: str
//index: int