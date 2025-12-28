package com.hbwl.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Material {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String type;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer courseId;
}
