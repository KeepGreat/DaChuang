package com.hbwl.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hbwl.common.Result;
import com.hbwl.pojo.Answer;
import com.hbwl.service.AnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/practice/answer")
public class AnswerController {

    @Autowired
    private AnswerService answerService;

    @PostMapping
    public Result addAnswer(@RequestBody Answer answer,
                            @RequestHeader("role") String role){
        if (!(role.equals("teacher") || role.equals("admin"))) return Result.error("权限不足");
        int row = answerService.addAnswer(answer);
        if (row == -1) return Result.error("参数不能为空");
        if (row == 0) return Result.error("增加答案失败");
        return Result.success("增加答案成功");
    }

    @DeleteMapping("{id}")
    public Result deleteAnswerById(@PathVariable Integer id,
                                   @RequestHeader("role") String role){
        if (!(role.equals("teacher") || role.equals("admin"))) return Result.error("权限不足");
        int row = answerService.deleteAnswerById(id);
        if (row == -1) return Result.error("参数不能为空");
        if (row == 0) return Result.error("删除答案失败");
        return Result.success("删除答案成功");
    }

    @PutMapping
    public Result updateAnswerById(@RequestBody Answer answer,
                                   @RequestHeader("role") String role){
        if (!(role.equals("teacher") || role.equals("admin"))) return Result.error("权限不足");
        int row = answerService.updateAnswerById(answer);
        if (row == -1) return Result.error("参数不能为空");
        if (row == 0) return Result.error("更新答案失败");
        return Result.success("更新答案成功");
    }

    @GetMapping("/id")
    public Result getAnswerByQuestionIds(@RequestParam List<Integer> questionIds){
        return Result.success(answerService.getAnswersByQuestionIds(questionIds), null);
    }

    @GetMapping
    public Result getAnswers(@RequestParam(required = false) Integer id,
                             @RequestParam(required = false) Integer questionId){
        Answer answer = new Answer();
        answer.setId(id);
        answer.setQuestionId(questionId);
        return Result.success(answerService.getAnswers(answer), null);
    }

    @GetMapping("/{page}/{size}")
    public Result getAnswersPage(@PathVariable("page") int pageNo, @PathVariable("size") int pageSize,
                                 @RequestParam(required = false) Integer id,
                                 @RequestParam(required = false) Integer questionId){
        Answer answer = new Answer();
        answer.setId(id);
        answer.setQuestionId(questionId);
        return Result.success(answerService.getAnswersPage(pageNo, pageSize, answer), null);
    }
}
