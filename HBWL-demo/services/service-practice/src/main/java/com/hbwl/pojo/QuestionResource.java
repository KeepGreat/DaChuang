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
    private Integer type;
    private Integer size;
    private Integer questionId;
}
