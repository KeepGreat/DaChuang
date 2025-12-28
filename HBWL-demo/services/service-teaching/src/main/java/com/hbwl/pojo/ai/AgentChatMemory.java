package com.hbwl.pojo.ai;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("agent_chat_memory")
public class AgentChatMemory {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String memoryId;
    private String content;
}
