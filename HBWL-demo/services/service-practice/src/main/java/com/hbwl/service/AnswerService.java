package com.hbwl.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hbwl.pojo.Answer;

import java.util.List;

public interface AnswerService {

    int addAnswer(Answer answer);

    int deleteAnswerById(Integer id);

    int updateAnswerById(Answer answer);

    List<Answer> getAnswers(Answer answer);

    List<Answer> getAnswersByQuestionIds(List<Integer> ids);
}
