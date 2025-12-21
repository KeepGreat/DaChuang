package com.hbwl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hbwl.mapper.QuestionMapper;
import com.hbwl.pojo.Question;
import com.hbwl.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class QuestionServiceImpl implements QuestionService {

    @Autowired
    private QuestionMapper questionMapper;

    @Override
    public int addQuestion(Question question) {
        if (question == null) return -1;
        return questionMapper.insert(question);
    }

    @Override
    public int deleteQuestionById(Integer id) {
        if (id == null) return -1;
        return questionMapper.deleteById(id);
    }

    @Override
    public int updateQuestionById(Question question) {
        if (question == null) return -1;
        UpdateWrapper<Question> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", question.getId());
        if (question.getName() != null) updateWrapper.set("name", question.getName());
        if (question.getType() != null) updateWrapper.set("type", question.getType());
        if (question.getLocation() != null) updateWrapper.set("location", question.getLocation());
        if (question.getContent() != null) updateWrapper.set("content", question.getContent());
        if (question.getHasResource() != null) updateWrapper.set("has_resource", question.getHasResource());
        return questionMapper.update(null, updateWrapper);
    }

    @Override
    public List<Question> getQuestions(Question question) {
        if (question == null) return questionMapper.selectList(null);
        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
        if (question.getId() != null) queryWrapper.eq("id", question.getId());
        if (question.getName() != null) queryWrapper.like("name", question.getName());
        if (question.getType() != null) queryWrapper.eq("type", question.getType());
        if (question.getLocation() != null) queryWrapper.eq("location", question.getLocation());
        return questionMapper.selectList(queryWrapper);
    }

    @Override
    public Page<Question> getQuestionsPage(int pageNo, int pageSize, Question question) {
        Page<Question> page = new Page<>(pageNo, pageSize);
        if (question == null) return questionMapper.selectPage(page, null);
        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
        if (question.getId() != null) queryWrapper.eq("id", question.getId());
        if (question.getName() != null) queryWrapper.like("name", question.getName());
        if (question.getType() != null) queryWrapper.eq("type", question.getType());
        if (question.getLocation() != null) queryWrapper.eq("location", question.getLocation());
        return questionMapper.selectPage(page, queryWrapper);
    }
}
