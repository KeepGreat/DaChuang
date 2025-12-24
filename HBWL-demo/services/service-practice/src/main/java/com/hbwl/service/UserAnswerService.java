package com.hbwl.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hbwl.codesandbox.pojo.CodeSandboxInput;
import com.hbwl.pojo.UserAnswer;
import com.hbwl.pojo.dto.UserAnswerOutputDTO;

import java.io.IOException;
import java.util.List;

public interface UserAnswerService {

    int addUserAnswer(UserAnswer userAnswer);

    int deleteUserAnswerById(Integer id);

    int deleteUserAnswerByUserId(String userId);

    int updateUserAnswerById(UserAnswer userAnswer);

    List<UserAnswer> getUserAnswers(UserAnswer userAnswer);

    Page<UserAnswer> getUserAnswersPage(int pageNo, int pageSize, UserAnswer userAnswer);

    //判断常规题的非简答题
    List<UserAnswer> judgeAnswersAuto(List<UserAnswer> userAnswers);

    //判断常规题的简答题
    int judgeAnswerManual(UserAnswer userAnswer);

    //判断编程题
    UserAnswerOutputDTO judgeCodeAnswer(CodeSandboxInput codeSandboxInput, UserAnswer userAnswer) throws IOException;
}
