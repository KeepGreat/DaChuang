package com.hbwl.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hbwl.pojo.Question;
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
    public String addQuestion(@RequestBody Question question){
        int row = questionService.addQuestion(question);
        if (row == -1) return "参数不能为空";
        if (row == 0) return "增加问题失败";
        return "增加问题成功";
    }

    @DeleteMapping("{id}")
    public String deleteQuestion(@PathVariable("id") Integer id){
        int row = questionService.deleteQuestionById(id);
        if (row == -1) return "参数不能为空";
        if (row == 0) return "删除问题失败";
        return "删除问题成功";
    }

    @PutMapping
    public String updateQuestion(@RequestBody Question question){
        int row = questionService.updateQuestionById(question);
        if (row == -1) return "参数不能为空";
        if (row == 0) return "更新问题失败";
        return "更新问题成功";
    }

    @GetMapping
    public List<Question> getQuestions(@RequestParam(required = false) Integer id,
                                       @RequestParam(required = false) String name,
                                       @RequestParam(required = false) Integer type,
                                       @RequestParam(required = false) Integer location,
                                       @RequestHeader("userId") String userId){
        Question question = new Question();
        question.setId(id);
        question.setName(name);
        question.setType(type);
        question.setLocation(location);
        return questionService.getQuestions(question);
    }

    @GetMapping("/{page}/{size}")
    public Page<Question> getQuestionsPage(@PathVariable("page") int pageNo, @PathVariable("size") int pageSize,
                                           @RequestParam(required = false) Integer id,
                                           @RequestParam(required = false) String name,
                                           @RequestParam(required = false) Integer type,
                                           @RequestParam(required = false) Integer location){
        Question question = new Question();
        question.setId(id);
        question.setName(name);
        question.setType(type);
        question.setLocation(location);
        return questionService.getQuestionsPage(pageNo, pageSize, question);
    }
}
