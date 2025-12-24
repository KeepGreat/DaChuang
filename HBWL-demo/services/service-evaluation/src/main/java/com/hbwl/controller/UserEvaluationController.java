package com.hbwl.controller;

import com.hbwl.common.Result;
import com.hbwl.pojo.UserEvaluation;
import com.hbwl.service.UserEvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

//TODO ： 目前前端仅能get，而不能delete，后续应完善delete功能来同步保证删除用户的操作

@RestController
@RequestMapping("/api/evaluation/evaluation")
public class UserEvaluationController {

    @Autowired
    private UserEvaluationService userEvaluationService;

    @GetMapping
    public Result getUserEvaluation(@RequestHeader("userId") String userId){
        return Result.success(userEvaluationService.getEvaluationByUserId(userId), null);
    }

    //以下三个接口不供前端调用，仅供feign调用
    @PostMapping("/practice")
    public Result evaluateBaseOnPractice(@RequestBody Map<String, String> map){
        String userId = map.get("userId");
        String json = map.get("json");
        UserEvaluation userEvaluation = userEvaluationService.evaluateBaseOnPractice(userId, json);
        if(userEvaluation == null)  return Result.error("用户分数数据不存在");
        return Result.success(userEvaluation , null);
    }

    @PostMapping("/teaching")
    public Result evaluateBaseOnAITeaching(@RequestBody Map<String, Object> map){
        UserEvaluation userEvaluation = userEvaluationService.evaluateBaseOnAITeaching(map);
        if(userEvaluation == null)   return Result.error("用户分数数据不存在");
        return Result.success(userEvaluation, null);
    }

    @PostMapping("/smartcompanion")
    public Result evaluateBaseOnSmartCompanion(@RequestBody Map<String, Object> map){
        UserEvaluation userEvaluation = userEvaluationService.evaluateBaseOnSmartCompanion(map);
        if(userEvaluation == null)  return Result.error("用户分数数据不存在");
        return Result.success(userEvaluation , null);
    }
}
