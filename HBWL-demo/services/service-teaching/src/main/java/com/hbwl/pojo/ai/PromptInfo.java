package com.hbwl.pojo.ai;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class PromptInfo {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String name; //txt文件名称
    private String description;
    private String creatorId;
}
