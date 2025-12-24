package com.hbwl.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;

//注意：(userId + hourTimestamp)为唯一的，即同一时间内同一用户只能有一条记录
@Data
public class UserCallRecord {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String userId;
    private Long hourTimestamp; //小时时间戳，计算频率的最小时间单位
    private Integer callCount; //指定时间段内的调用次数
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
