package com.hbwl.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Practice {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String name;
    private Integer questionNum;
    private LocalDateTime createdAt; //格式为yyyy-mm-ddThh:mm:ss
    private LocalDateTime expiredAt; //格式为yyyy-mm-ddThh:mm:ss
}
