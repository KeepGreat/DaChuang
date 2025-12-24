package com.hbwl.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class Question {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String name;
    private Integer type; //0：判断，1：选择，2：简答，3：编程
    private String content; //<65535字
    private Integer score;
    private Boolean hasResource;
}
