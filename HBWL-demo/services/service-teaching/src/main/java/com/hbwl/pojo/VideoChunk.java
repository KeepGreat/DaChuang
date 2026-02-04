package com.hbwl.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VideoChunk {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;
    private Integer number;
    private Integer total;
    private Integer size;
    private Integer videoId;
    private Boolean canDeleted = false;
    private LocalDateTime createdAt;
}
