package com.hbwl.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class QuestionResource {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String description;
    private String name;
    private Integer type; //0：测试用例，1：用例答案，2：问题描述资料
    private Integer size;
    private Integer questionId;
}
