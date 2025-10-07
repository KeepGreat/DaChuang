package com.hbwl.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.NonNull;

@Data
public class Course {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String name;
    private String description;
    private Integer sectionId;
}
