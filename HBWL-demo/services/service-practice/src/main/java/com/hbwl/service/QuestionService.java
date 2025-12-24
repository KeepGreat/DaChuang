package com.hbwl.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hbwl.pojo.Question;
import com.hbwl.pojo.QuestionIndex;

import java.util.List;
import java.util.Map;

public interface QuestionService {

    int addQuestion(Question question, QuestionIndex questionIndex);

    int deleteQuestionById(Integer id);

    int deleteQuestionByIndex(QuestionIndex index);

    int updateQuestionById(Question question);

    List<Question> getQuestions(Question question);

    Page<Question> getQuestionsPage(int pageNo, int pageSize, Question question);

    List<Question> getQuestionsByIndex(QuestionIndex questionIndex);

    Page<Question> getQuestionsPageByIndex(int pageNo, int pageSize, QuestionIndex questionIndex);
}
