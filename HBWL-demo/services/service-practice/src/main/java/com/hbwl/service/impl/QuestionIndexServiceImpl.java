package com.hbwl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hbwl.mapper.QuestionIndexMapper;
import com.hbwl.pojo.QuestionIndex;
import com.hbwl.service.QuestionIndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class QuestionIndexServiceImpl implements QuestionIndexService {

    @Autowired
    private QuestionIndexMapper questionIndexMapper;

    @Override
    public int addQuestionIndexes(List<QuestionIndex> indexList) {
        if (indexList == null || indexList.isEmpty()) return -1;
        int row = 0;
        for (QuestionIndex index : indexList) {
            row = questionIndexMapper.insert(index);
            if (row == 0) throw new RuntimeException("插入问题索引失败，进行回滚");
        }
        return row;
    }

    @Override
    public int deleteQuestionIndexes(List<Integer> indexList) {
        if (indexList == null || indexList.isEmpty()) return -1;
        int row = 0;
        for (Integer index : indexList) {
            row = questionIndexMapper.deleteById(index);
            if (row == 0) throw new RuntimeException("删除问题索引失败，进行回滚");
        }
        return row;
    }

    @Override
    public int updateQuestionIndex(QuestionIndex questionIndex) {
        if (questionIndex == null) return -1;
        return questionIndexMapper.updateById(questionIndex);
    }

    @Override
    public List<QuestionIndex> getQuestionIndexes(QuestionIndex questionIndex) {
        if (questionIndex == null) return questionIndexMapper.selectList(null);
        QueryWrapper<QuestionIndex> queryWrapper = new QueryWrapper<>();
        if (questionIndex.getQuestionId() != null) queryWrapper.eq("question_id", questionIndex.getQuestionId());
        if (questionIndex.getPracticeId() != null) queryWrapper.eq("practice_id", questionIndex.getPracticeId());
        return questionIndexMapper.selectList(queryWrapper);
    }

    @Override
    public Page<QuestionIndex> getQuestionIndexesPage(int pageNo, int pageSize, QuestionIndex questionIndex) {
        Page<QuestionIndex> page = new Page<>(pageNo, pageSize);
        if (questionIndex == null) return questionIndexMapper.selectPage(page, null);
        QueryWrapper<QuestionIndex> queryWrapper = new QueryWrapper<>();
        if (questionIndex.getQuestionId() != null) queryWrapper.eq("question_id", questionIndex.getQuestionId());
        if (questionIndex.getPracticeId() != null) queryWrapper.eq("practice_id", questionIndex.getPracticeId());
        return questionIndexMapper.selectPage(page, queryWrapper);
    }
}
