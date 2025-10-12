package com.hbwl.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class FileContent {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String type;
    private String name;
    private Integer size;
    @TableField("mat_id")
    private Integer matId;
}
