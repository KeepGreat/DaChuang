package com.hbwl.controller;

import com.hbwl.common.Result;
import com.hbwl.pojo.UserScore;
import com.hbwl.service.UserScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

//TODO: 这个controller只供内部调用，后续改一下传参

@RestController
@RequestMapping("/api/evaluation/score")
public class UserScoreController {

    @Autowired
    private UserScoreService userScoreService;

    @GetMapping
    public Result getUserScore(@RequestHeader("userId") String userId) {
        UserScore userScore = userScoreService.getUserScore(userId);
        if(userScore == null) return Result.error("查找数据为空");
        return Result.success(userScore, null);
    }

    @PostMapping
    public Result addUserScore(@RequestBody Map<String, String> map) {
        String userId = map.get("userId");
        int add = userScoreService.addUserScore(userId);
        if(add == -1)  return Result.error("添加数据不能为空");
        if(add == 0)  return Result.error("添加数据失败");
        return Result.success("添加数据成功");
    }

    @PutMapping
    public Result updateUserScore(@RequestBody UserScore userScore) {
        int update = userScoreService.updateUserScoreByUserId(userScore);
        if(update == -1)   return Result.error("更新数据不能为空");
        if(update == 0)  return Result.error("更新失败");
        return Result.success("数据更新成功");
    }

    @DeleteMapping
    public Result deleteUserScore(@RequestHeader("userId") String userId) {
        int delete = userScoreService.deleteUserScoreByUserId(userId);
        if(delete == -1)  return Result.error("删除数据不能为空");
        if(delete == 0)  return Result.error("删除失败");
        return Result.success("删除成功");
    }
}

