package com.hbwl.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hbwl.pojo.QuestionIndex;

import java.util.List;

public interface QuestionIndexService {

    int addQuestionIndexes(List<QuestionIndex> indexList);

    int deleteQuestionIndexes(List<Integer> indexList);

    int updateQuestionIndex(QuestionIndex questionIndex);

    List<QuestionIndex> getQuestionIndexes(QuestionIndex questionIndex);

    Page<QuestionIndex> getQuestionIndexesPage(int pageNo, int pageSize, QuestionIndex questionIndex);
}
