package com.hbwl.controller;

import com.hbwl.common.Result;
import com.hbwl.pojo.UserCallRecord;
import com.hbwl.service.UserCallService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teaching/usercall")
public class UserCallController {

    @Autowired
    private UserCallService userCallService;

    //以下方法为测试方法
    //记录用户调用到redis
    @PostMapping("/record")
    public Result recordUserCall(@RequestHeader("userId") String userId){
        long count = userCallService.recordUserCall(userId);
        return Result.success(count, null);
    }

    //以下方法为测试方法
    //从redis获取用户调用次数
    @GetMapping("/count")
    public Result getUserCallCount(@RequestHeader("userId") String userId){
        long userCallCount = userCallService.getUserCallCount(userId, null);
        return Result.success(userCallCount, null);
    }

    //从数据库中获取用户调用记录
    //不从header中获取是因为feign调用时不经过网关
    @GetMapping("/data")
    public Result getUserCallRecord(@RequestParam String userId){
        List<UserCallRecord> userCallRecords = userCallService.getUserCallRecords(userId, null, null, null);
        return Result.success(userCallRecords, null);
    }

    //以下为测试方法
    //向数据库存储所有用户调用记录
    @PostMapping("/data")
    public Result processUserCallRecord(){
        int row = userCallService.processUserCallRecordByHourTimestamp(null);
        return Result.success(row, null);
    }
}
