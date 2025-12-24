package com.hbwl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hbwl.mapper.QuestionIndexMapper;
import com.hbwl.mapper.QuestionMapper;
import com.hbwl.pojo.Question;
import com.hbwl.pojo.QuestionIndex;
import com.hbwl.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class QuestionServiceImpl implements QuestionService {

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private QuestionIndexMapper questionIndexMapper;

    @Override
    public int addQuestion(Question question, QuestionIndex questionIndex) {
        if (question == null || questionIndex == null) return -1;
        //addQuestion
        if (question.getType() != null || question.getContent() != null || question.getHasResource() != null) return -1;
        int row = 0;
        row = questionMapper.insert(question);
        if (row == 0) return 0;
        //addQuestionIndex
        if (questionIndex.getPracticeId() == null) return -1;
        questionIndex.setQuestionId(question.getId());
        row = questionIndexMapper.insert(questionIndex);
        if (row == 0) throw new RuntimeException("插入QuestionIndex失败，进行回滚");
        return row;
    }

    @Override
    public int deleteQuestionById(Integer id) {
        if (id == null) return -1;
        //deleteQuestion
        int row = 0;
        row = questionMapper.deleteById(id);
        if (row == 0) return 0;
        //deleteQuestionIndex
        QueryWrapper<QuestionIndex> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("question_id", id);
        row = questionIndexMapper.delete(queryWrapper);
        if (row == 0) throw new RuntimeException("删除QuestionIndex失败，进行回滚");
        return row;
    }

    @Override
    public int deleteQuestionByIndex(QuestionIndex index) {
        if (index == null || index.getPracticeId() == null) return -1;
        QueryWrapper<QuestionIndex> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("practure_id", index.getPracticeId());
        List<QuestionIndex> questionIndexList = questionIndexMapper.selectList(queryWrapper);
        int row = 0;
        for (QuestionIndex questionIndex : questionIndexList) {
            //deleteQuestion
            row = questionMapper.deleteById(questionIndex.getId());
            if (row == 0) throw new RuntimeException("批量删除Question失败，进行回滚");
        }
        //deleteQuestionIndex
        row = questionIndexMapper.delete(queryWrapper);
        if (row == 0) throw new RuntimeException("删除QuestionIndex失败，进行回滚");
        return row;
    }

    @Override
    public int updateQuestionById(Question question) {
        if (question == null) return -1;
        UpdateWrapper<Question> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", question.getId());
        if (question.getName() != null) updateWrapper.set("name", question.getName());
        if (question.getType() != null) updateWrapper.set("type", question.getType());
        if (question.getContent() != null) updateWrapper.set("content", question.getContent());
        if (question.getScore() != null) updateWrapper.set("score", question.getScore());
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
        if (question.getScore() != null) queryWrapper.eq("score", question.getScore());
        if (question.getHasResource() != null) queryWrapper.eq("has_resource", question.getHasResource());
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
        if (question.getScore() != null) queryWrapper.eq("score", question.getScore());
        if (question.getHasResource() != null) queryWrapper.eq("has_resource", question.getHasResource());
        return questionMapper.selectPage(page, queryWrapper);
    }

    @Override
    public List<Question> getQuestionsByIndex(QuestionIndex questionIndex) {
        if (questionIndex == null) return questionMapper.selectList(null);
        QueryWrapper<QuestionIndex> queryWrapper = new QueryWrapper<>();
        if (questionIndex.getPracticeId() != null) queryWrapper.eq("practice_id", questionIndex.getPracticeId());
        if (questionIndex.getQuestionId() != null) queryWrapper.eq("question_id", questionIndex.getQuestionId());
        List<QuestionIndex> questionIndexList = questionIndexMapper.selectList(queryWrapper);
        List<Question> questions = new ArrayList<>();
        for (QuestionIndex index : questionIndexList) {
            questions.add(questionMapper.selectById(index.getId()));
        }
        return questions;
    }

    @Override
    public Page<Question> getQuestionsPageByIndex(int pageNo, int pageSize, QuestionIndex questionIndex) {
        Page<Question> page = new Page<>(pageNo, pageSize);
        QueryWrapper<QuestionIndex> queryWrapper = new QueryWrapper<>();
        if (questionIndex.getPracticeId() != null) queryWrapper.eq("practice_id", questionIndex.getPracticeId());
        if (questionIndex.getQuestionId() != null) queryWrapper.eq("question_id", questionIndex.getQuestionId());
        List<QuestionIndex> questionIndexList = questionIndexMapper.selectList(queryWrapper);
        List<Question> questions = new ArrayList<>();
        for (QuestionIndex index : questionIndexList) {
            questions.add(questionMapper.selectById(index.getId()));
        }
        return page.setRecords(questions);
    }
}
