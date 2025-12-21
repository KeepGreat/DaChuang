package com.hbwl.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class Question {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String name;
    private Integer type;
    private Integer location;
    private String content;
    private Boolean hasResource;
}
