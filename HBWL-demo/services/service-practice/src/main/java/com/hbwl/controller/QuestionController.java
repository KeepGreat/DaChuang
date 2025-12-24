package com.hbwl.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hbwl.common.Result;
import com.hbwl.pojo.Question;
import com.hbwl.pojo.QuestionIndex;
import com.hbwl.pojo.dto.QuestionDTO;
import com.hbwl.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/practice/question")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @PostMapping
    public Result addQuestion(@RequestBody QuestionDTO questionDTO,
                              @RequestHeader("role") String role){
        if (!(role.equals("teacher") || role.equals("admin"))) return Result.error("权限不足");
        int row = questionService.addQuestion(questionDTO.getQuestion(), questionDTO.getQuestionIndex());
        if (row == -1) return Result.error("参数不能为空");
        if (row == 0) return Result.error("插入问题失败");
        return Result.success("插入问题成功");
    }

    @DeleteMapping("{id}")
    public Result deleteQuestionById(@PathVariable("id") Integer id,
                                 @RequestHeader("role") String role){
        if (!(role.equals("teacher") || role.equals("admin"))) return Result.error("权限不足");
        int row = questionService.deleteQuestionById(id);
        if (row == -1) return Result.error("参数不能为空");
        if (row == 0) return Result.error("删除问题失败");
        return Result.success("删除问题成功");
    }

    @DeleteMapping
    public Result deleteQuestionsByIndex(@RequestParam Integer practiceId,
                                  @RequestHeader("role") String role) {
        if (!(role.equals("teacher") || role.equals("admin"))) return Result.error("权限不足");
        QuestionIndex questionIndex = new QuestionIndex();
        questionIndex.setPracticeId(practiceId);
        int row = questionService.deleteQuestionByIndex(questionIndex);
        if (row == -1) return Result.error("参数不能为空");
        if (row == 0) return Result.error("删除问题失败");
        return Result.success("删除问题成功");
    }

    @PutMapping
    public Result updateQuestionById(@RequestBody Question question,
                                 @RequestHeader("role") String role){
        if (!(role.equals("teacher") || role.equals("admin"))) return Result.error("权限不足");
        int row = questionService.updateQuestionById(question);
        if (row == -1) return Result.error("参数不能为空");
        if (row == 0) return Result.error("更新问题失败");
        return Result.success("更新问题成功");
    }

    @GetMapping
    public Result getQuestions(@RequestParam(required = false) Integer id,
                               @RequestParam(required = false) String name,
                               @RequestParam(required = false) Integer type,
                               @RequestParam(required = false) Integer score,
                               @RequestParam(required = false) Boolean hasResource){
        Question question = new Question();
        question.setId(id);
        question.setName(name);
        question.setType(type);
        question.setHasResource(hasResource);
        List<Question> list = questionService.getQuestions(question);
        if (list.isEmpty()) return Result.error("查询问题失败");
        return Result.success(list, "查询问题成功");
    }

    @GetMapping("/{page}/{size}")
    public Result getQuestionsPage(@PathVariable("page") int pageNo, @PathVariable("size") int pageSize,
                                   @RequestParam(required = false) Integer id,
                                   @RequestParam(required = false) String name,
                                   @RequestParam(required = false) Integer type,
                                   @RequestParam(required = false) Integer score,
                                   @RequestParam(required = false) Boolean hasResource){
        Question question = new Question();
        question.setId(id);
        question.setName(name);
        question.setType(type);
        question.setHasResource(hasResource);
        Page<Question> page = questionService.getQuestionsPage(pageNo, pageSize, question);
        if (page.getSize() == 0) return Result.error("查询问题失败");
        return Result.success(page, "查询问题成功");
    }

    @GetMapping("/index")
    public Result getQuestionsByIndex(@RequestParam Integer practiceId){
        QuestionIndex questionIndex = new QuestionIndex();
        questionIndex.setPracticeId(practiceId);
        List<Question> list = questionService.getQuestionsByIndex(questionIndex);
        if (list.isEmpty()) return Result.error("查询问题失败");
        return Result.success(list, "查询问题成功");
    }

    @GetMapping("/index/{page}/{size}")
    public Result getQuestionsPageByIndex(@PathVariable("page") int pageNo, @PathVariable("size") int pageSize,
                                          @RequestParam Integer practiceId){
        QuestionIndex questionIndex = new QuestionIndex();
        questionIndex.setPracticeId(practiceId);
        Page<Question> page = questionService.getQuestionsPageByIndex(pageNo, pageSize, questionIndex);
        if (page.getSize() == 0) return Result.error("查询问题失败");
        return Result.success(page, "查询问题成功");
    }
}
