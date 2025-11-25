package com.hbwl.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hbwl.pojo.Question;

import java.util.List;

public interface QuestionService {

    int addQuestion(Question question);

    int deleteQuestionById(Integer id);

    int updateQuestionById(Question question);

    List<Question> getQuestions(Question question);

    Page<Question> getQuestionsPage(int pageNo, int pageSize, Question question);
}
