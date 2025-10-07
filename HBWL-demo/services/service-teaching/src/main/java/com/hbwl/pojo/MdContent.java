package com.hbwl.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class MdContent {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String content;
    private Integer matId;
}
