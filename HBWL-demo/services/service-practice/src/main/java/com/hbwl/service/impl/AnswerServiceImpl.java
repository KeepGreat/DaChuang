package com.hbwl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.hbwl.mapper.AnswerMapper;
import com.hbwl.pojo.Answer;
import com.hbwl.service.AnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AnswerServiceImpl implements AnswerService {

    @Autowired
    private AnswerMapper answerMapper;

    @Override
    public int addAnswer(Answer answer) {
        if (answer == null) return -1;
        return answerMapper.insert(answer);
    }

    @Override
    public int deleteAnswerById(Integer id) {
        if (id == null) return -1;
        return answerMapper.deleteById(id);
    }

    @Override
    public int updateAnswerById(Answer answer) {
        if (answer == null || answer.getId() == null) return -1;
        UpdateWrapper<Answer> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", answer.getId());
        if (answer.getContent() != null) updateWrapper.set("content", answer.getContent());
        if (answer.getAnalysis() != null) updateWrapper.set("analysis", answer.getAnalysis());
        if (answer.getQuestionId() != null) updateWrapper.set("question_id", answer.getQuestionId());
        return answerMapper.update(null, updateWrapper);
    }

    @Override
    public List<Answer> getAnswers(Answer answer) {
        if (answer == null) return List.of();
        QueryWrapper<Answer> queryWrapper = new QueryWrapper<>();
        if (answer.getId() != null) queryWrapper.eq("id", answer.getId());
        if (answer.getQuestionId() != null) queryWrapper.eq("question_id", answer.getQuestionId());
        return answerMapper.selectList(queryWrapper);
    }

    @Override
    public List<Answer> getAnswersByQuestionIds(List<Integer> ids) {
        return answerMapper.selectList(new QueryWrapper<Answer>().in("question_id", ids));
    }
}
