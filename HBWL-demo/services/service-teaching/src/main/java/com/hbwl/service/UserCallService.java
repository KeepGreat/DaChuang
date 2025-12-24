package com.hbwl.service;

import com.hbwl.pojo.UserCallRecord;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 该服务负责统计用户调用AI频率、持久化存储用户调用AI记录以及向评分模块反馈用户调用频率的功能
 *
 * TODO: 当前只实现了将用户调用记录保存到数据库的方法，并没有实现定期删除的功能
 *
 * */
public interface UserCallService {

    //记录用户调用，供AIService调用来记录
    //返回记录情况
    int recordUserCall(String userId);

    //获取指定小时用户调用次数
    //若不传hourTimestamp则获取当前小时用户调用次数
    long getUserCallCount(String userId, Long hourTimestamp);

    //获取用户最近N个小时内的调用统计
    Map<String, Long> getUserCallCountHistory(String userId, int hours);

    //根据userId和小时时间戳删除redis中的键,userId若为空则删除全部，timestamp为空则删除当前小时
    int resetUserCallCount(String userId, Long hourTimestamp);

    //根据小时时间戳来处理该时间段内所有的UserCallRecord,包括存储和向评估模块传值
    //可以在redis中每个用户只维护一对键值对，由此方法来删除键值对
    int processUserCallRecordByHourTimestamp(Long hourTimestamp);

    //自动处理UserCallRecord
    void processUserCallRecordBatchAuto();

    //存储用户调用信息,仅存储不处理
    int saveUserCallRecord(String userId, Long hourTimestamp, Integer callCount);

    int deleteUserCallRecord(String userId, Long hourTimestamp, LocalDateTime createdAtStart, LocalDateTime createdAtEnd);

    List<UserCallRecord> getUserCallRecords(String userId, Long hourTimestamp, LocalDateTime createdAtStart, LocalDateTime createdAtEnd);
}
