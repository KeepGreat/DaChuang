package com.hbwl.controller;

import com.hbwl.pojo.Answer;
import com.hbwl.service.AnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/practice/answer")
public class AnswerController {

    @Autowired
    private AnswerService answerService;

    @PutMapping
    public String addAnswer(@RequestBody Answer answer){
        int row = answerService.addAnswer(answer);
        if (row == -1) return "参数不能为空";
        if (row == 0) return "增加答案失败";
        return "增加答案成功";
    }
}
