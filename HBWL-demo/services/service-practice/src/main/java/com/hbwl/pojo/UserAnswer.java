package com.hbwl.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class UserAnswer {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String content;
    private String userId;
    private Integer questionId;
    private Integer questionType;
    private Integer score;
    private String comment;
}
