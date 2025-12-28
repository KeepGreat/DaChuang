package com.hbwl.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hbwl.common.Result;
import com.hbwl.pojo.UserAnswer;
import com.hbwl.pojo.dto.UserAnswerInputDTO;
import com.hbwl.pojo.dto.UserAnswerOutputDTO;
import com.hbwl.service.UserAnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//TODO: 前端在处理提交并判题编程题时，直接使用add方法增加userAnswer，导致调用judegecode时ai的comment写不进去

@RestController
@RequestMapping("/api/practice/useranswer")
public class UserAnswerController {

    @Autowired
    private UserAnswerService userAnswerService;

    @PostMapping
    public Result addUserAnswer(@RequestBody UserAnswer userAnswer){
        userAnswer.setScore(-1);
        int row = userAnswerService.addUserAnswer(userAnswer);
        if (row == -1) return Result.error("参数不能为空");
        if (row == 0) return Result.error("插入用户答案失败");
        return Result.success("插入用户答案成功");
    }

    @DeleteMapping
    public Result deleteUserAnswerById(@RequestParam Integer id){
        int row = userAnswerService.deleteUserAnswerById(id);
        if (row == -1) return Result.error("参数不能为空");
        if (row == 0) return Result.error("删除用户答案失败");
        return Result.success("删除用户答案成功");
    }

    @DeleteMapping("/userid")
    public Result deleteUserAnswerByUserId(@RequestParam String userId,
                                           @RequestHeader("role") String role){
        if (!(role.equals("teacher") || role.equals("admin"))) return Result.error("权限不足");
        int row = userAnswerService.deleteUserAnswerByUserId(userId);
        if (row == -1) return Result.error("参数不能为空");
        if (row == 0) return Result.error("删除用户答案失败");
        return Result.success("删除用户答案成功");
    }

    @PutMapping
    public Result updateUserAnswerById(@RequestBody UserAnswer userAnswer){
        userAnswer.setScore(null);
        userAnswer.setComment(null);
        int row = userAnswerService.updateUserAnswerById(userAnswer);
        if (row == -1) return Result.error("参数不能为空");
        if (row == 0) return Result.error("更新用户答案失败");
        return Result.success("更新用户答案成功");
    }

    @GetMapping
    public Result getUserAnswers(@RequestParam(required = false) Integer id,
                                 @RequestParam(required = false) String userId,
                                 @RequestParam(required = false) Integer questionId,
                                 @RequestParam(required = false) Integer questionType,
                                 @RequestParam(required = false) Integer score){
        UserAnswer userAnswer = new UserAnswer();
        userAnswer.setId(id);
        userAnswer.setUserId(userId);
        userAnswer.setQuestionId(questionId);
        userAnswer.setQuestionType(questionType);
        userAnswer.setScore(score);
        List<UserAnswer> list = userAnswerService.getUserAnswers(userAnswer);
//        if (list == null || list.isEmpty()) return Result.error("查询用户答案失败");
        return Result.success(list, "查询用户答案成功");
    }

    @GetMapping("/{page}/{size}")
    public Result getUserAnswersPage(@PathVariable("page") int pageNo, @PathVariable("size") int pageSize,
                                     @RequestParam(required = false) Integer id,
                                     @RequestParam(required = false) String userId,
                                     @RequestParam(required = false) Integer questionId,
                                     @RequestParam(required = false) Integer questionType,
                                     @RequestParam(required = false) Integer score){
        UserAnswer userAnswer = new UserAnswer();
        userAnswer.setId(id);
        userAnswer.setUserId(userId);
        userAnswer.setQuestionId(questionId);
        userAnswer.setQuestionType(questionType);
        userAnswer.setScore(score);
        Page<UserAnswer> page = userAnswerService.getUserAnswersPage(pageNo, pageSize, userAnswer);
//        if (page == null || page.getSize() == 0) return Result.error("查询用户答案失败");
        return Result.success(page, "查询用户答案成功");
    }

    //自动判题，判断常规题中的非简答题，直接进行字符串比较来判题
    //列表中每个元素的questionId不能为空
    @PostMapping("/judge/auto")
    public Result judgeAnswersAuto(@RequestBody List<UserAnswer> userAnswerList){
        List<UserAnswer> list = userAnswerService.judgeAnswersAuto(userAnswerList);
        if (list == null || list.isEmpty()) return Result.error("自动判题失败");
        return Result.success(list, "自动判题成功");
    }

    //手动判题，判断常规题中的简答题，教师在前端输入好分数后传给后端存入数据库
    //id和score不能为空
    @PostMapping("/judge/manual")
    public Result judgeAnswerManual(@RequestBody UserAnswer userAnswer,
                                    @RequestHeader("role") String role){
        if (!(role.equals("teacher") || role.equals("admin"))) return Result.error("权限不足");
        int row = userAnswerService.judgeAnswerManual(userAnswer);
        if (row == -1) return Result.error("参数不能为空");
        if (row == 0) return Result.error("手动判题结果存储失败");
        return Result.success("手动判题结果存储成功");
    }

    //代码判题，判断编程题，需传入指定dto
    @PostMapping("/judge/code")
    public Result judgeCodeAnswer(@RequestBody UserAnswerInputDTO userAnswerInputDTO){
        try {
            UserAnswerOutputDTO userAnswerOutputDTO = userAnswerService.judgeCodeAnswer(userAnswerInputDTO.getCodeSandboxInput(), userAnswerInputDTO.getUserAnswer());
            return Result.success(userAnswerOutputDTO, "代码判题成功");
        } catch (Exception e){
            return Result.error("代码判题错误：" + e.getMessage());
        }
    }
}
