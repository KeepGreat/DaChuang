package com.hbwl.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hbwl.analysis.pojo.AnalysisInput;
import com.hbwl.analysis.pojo.AnalysisOutput;
import com.hbwl.codesandbox.pojo.CodeSandboxInput;
import com.hbwl.codesandbox.pojo.CodeSandboxOutput;
import com.hbwl.feign.AnalysisFeignClient;
import com.hbwl.feign.CodeSandboxFeignClient;
import com.hbwl.mapper.AnswerMapper;
import com.hbwl.mapper.QuestionMapper;
import com.hbwl.mapper.QuestionResourceMapper;
import com.hbwl.mapper.UserAnswerMapper;
import com.hbwl.pojo.Answer;
import com.hbwl.pojo.Question;
import com.hbwl.pojo.QuestionResource;
import com.hbwl.pojo.UserAnswer;
import com.hbwl.pojo.dto.UserAnswerOutputDTO;
import com.hbwl.service.UserAnswerService;
import com.hbwl.utils.QuestionResourceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class UserAnswerServiceImpl implements UserAnswerService {

    @Autowired
    private UserAnswerMapper userAnswerMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private AnswerMapper answerMapper;

    @Autowired
    private QuestionResourceMapper questionResourceMapper;

    @Autowired
    private QuestionResourceUtil questionResourceUtil;

    @Autowired
    private CodeSandboxFeignClient codeSandboxFeignClient;

    @Autowired
    private AnalysisFeignClient analysisFeignClient;

    @Override
    public int addUserAnswer(UserAnswer userAnswer) {
        if (userAnswer == null ||
                userAnswer.getContent() == null || userAnswer.getContent().trim().isEmpty() ||
                userAnswer.getUserId() == null || userAnswer.getUserId().trim().isEmpty() ||
                userAnswer.getQuestionId() == null) return -1;
        return userAnswerMapper.insert(userAnswer);
    }

    @Override
    public int deleteUserAnswerById(Integer id) {
        if (id == null) return -1;
        return userAnswerMapper.deleteById(id);
    }

    @Override
    public int deleteUserAnswerByUserId(String userId) {
        if (userId == null || userId.trim().isEmpty()) return -1;
        QueryWrapper<UserAnswer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        return userAnswerMapper.delete(queryWrapper);
    }

    @Override
    public int updateUserAnswerById(UserAnswer userAnswer) {
        if (userAnswer == null || userAnswer.getId() == null) return -1;
        UpdateWrapper<UserAnswer> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", userAnswer.getId());
        if (userAnswer.getContent() != null) updateWrapper.set("content", userAnswer.getContent());
        if (userAnswer.getUserId() != null) updateWrapper.set("user_id", userAnswer.getUserId());
        if (userAnswer.getQuestionId() != null) updateWrapper.set("question_id", userAnswer.getQuestionId());
        if (userAnswer.getScore() != null) updateWrapper.set("score", userAnswer.getScore());
        if (userAnswer.getComment() != null) updateWrapper.set("comment", userAnswer.getComment());
        return userAnswerMapper.update(null, updateWrapper);
    }

    @Override
    public List<UserAnswer> getUserAnswers(UserAnswer userAnswer) {
        if (userAnswer == null) return userAnswerMapper.selectList(null);
        QueryWrapper<UserAnswer> queryWrapper = new QueryWrapper<>();
        if (userAnswer.getId() != null) queryWrapper.eq("id", userAnswer.getId());
        if (userAnswer.getUserId() != null) queryWrapper.eq("user_id", userAnswer.getUserId());
        if (userAnswer.getQuestionId() != null) queryWrapper.eq("question_id", userAnswer.getQuestionId());
        if (userAnswer.getQuestionType() != null) queryWrapper.eq("question_type", userAnswer.getQuestionType());
        if (userAnswer.getScore() != null) queryWrapper.eq("score", userAnswer.getScore());
        return userAnswerMapper.selectList(queryWrapper);
    }

    @Override
    public Page<UserAnswer> getUserAnswersPage(int pageNo, int pageSize, UserAnswer userAnswer) {
        Page<UserAnswer> page = new Page<>(pageNo, pageSize);
        QueryWrapper<UserAnswer> queryWrapper = new QueryWrapper<>();
        if (userAnswer.getId() != null) queryWrapper.eq("id", userAnswer.getId());
        if (userAnswer.getUserId() != null) queryWrapper.eq("user_id", userAnswer.getUserId());
        if (userAnswer.getQuestionId() != null) queryWrapper.eq("question_id", userAnswer.getQuestionId());
        if (userAnswer.getQuestionType() != null) queryWrapper.eq("question_type", userAnswer.getQuestionType());
        if (userAnswer.getScore() != null) queryWrapper.eq("score", userAnswer.getScore());
        return userAnswerMapper.selectPage(page, queryWrapper);
    }

    @Override
    public List<UserAnswer> judgeAnswersAuto(List<UserAnswer> userAnswers) {
        if (userAnswers == null || userAnswers.isEmpty()) return null;
        List<UserAnswer> result = new ArrayList<>();
        for (UserAnswer userAnswer : userAnswers) {
            if (userAnswer.getQuestionId() == null) continue;
            Integer questionId = userAnswer.getQuestionId();
            List<Answer> answerList = answerMapper.selectList(new QueryWrapper<Answer>().eq("question_id", questionId));
            if (answerList.size() != 1) continue;
            Question question = questionMapper.selectById(questionId);
            if (question == null) continue;
            Answer answer = answerList.get(0);
            if (answer.getContent().equals(userAnswer.getContent())) userAnswer.setScore(question.getScore());
            else userAnswer.setScore(0);
            result.add(userAnswer);
        }
        return result;
    }

    @Override
    public int judgeAnswerManual(UserAnswer userAnswer) {
        if (userAnswer == null || userAnswer.getId() == null || userAnswer.getScore() == null) return -1;
        UpdateWrapper<UserAnswer> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", userAnswer.getId());
        updateWrapper.set("score", userAnswer.getScore());
        if (userAnswer.getComment() != null) updateWrapper.set("comment", userAnswer.getComment());
        return userAnswerMapper.update(null, updateWrapper);
    }

    @Override
    public UserAnswerOutputDTO judgeCodeAnswer(CodeSandboxInput codeSandboxInput, UserAnswer userAnswer) throws IOException {
        if (codeSandboxInput.getCodeLanguage() == null || codeSandboxInput.getCode() == null ||
            userAnswer == null || userAnswer.getQuestionId() == null || userAnswer.getUserId() == null) return null;
        try {
            //读取问题测试用例,目前只限定只有1个
            Integer questionId = userAnswer.getQuestionId();
            Question question = questionMapper.selectById(questionId);
            UserAnswerOutputDTO userAnswerOutputDTO = new UserAnswerOutputDTO();

            QueryWrapper<QuestionResource> wrapper = new QueryWrapper<>();
            wrapper.eq("question_id", questionId);
            wrapper.eq("type", 0);
            List<QuestionResource> questionResourceList = questionResourceMapper.selectList(wrapper);
            if (questionResourceList.size() != 1) return null;
            QuestionResource questionResource = questionResourceList.get(0);
            String resourceName = questionResource.getName();
            Resource resource = questionResourceUtil.loadFile(resourceName);
            InputStreamReader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
            String input = FileCopyUtils.copyToString(reader);
            //获取用户代码运行结果
            codeSandboxInput.setInput(input);
            String execute = codeSandboxFeignClient.execute(codeSandboxInput);
            CodeSandboxOutput codeSandboxOutput = JSONUtil.toBean(execute, CodeSandboxOutput.class);
            //调用analysis模块对用户代码进行分析
            AnalysisInput analysisInput = new AnalysisInput();
            analysisInput.setCode(codeSandboxInput.getCode());
            analysisInput.setCodeLanguage(codeSandboxInput.getCodeLanguage());
            analysisInput.setQuestion(question.getContent());
            analysisInput.setCodeSandboxOutput(codeSandboxOutput);
            AnalysisOutput analysisOutput = analysisFeignClient.analyze(analysisInput);
            userAnswer.setComment(analysisOutput.getAnalysis());
            //代码运行错误
            if (!codeSandboxOutput.getStatus().equals("Accepted")){
                userAnswerOutputDTO.setCodeSandboxOutput(codeSandboxOutput);
                userAnswerOutputDTO.setUserAnswer(userAnswer);
                return userAnswerOutputDTO;
            }
            //获取问题用例答案，目前只限定1个
            wrapper.clear();
            wrapper.eq("question_id", questionId);
            wrapper.eq("type", 1);
            List<QuestionResource> questionResourceList1 = questionResourceMapper.selectList(wrapper);
            if (questionResourceList1.size() != 1) return null;
            QuestionResource questionResource1 = questionResourceList1.get(0);
            String resourceName1 = questionResource1.getName();
            Resource resource1 = questionResourceUtil.loadFile(resourceName1);
            InputStreamReader reader1 = new InputStreamReader(resource1.getInputStream(), StandardCharsets.UTF_8);
            String answer = FileCopyUtils.copyToString(reader1);
            //将用户代码执行结果与问题用例答案进行对比
            if (answer.equals(codeSandboxOutput.getStdout())) userAnswer.setScore(question.getScore());
            else userAnswer.setScore(0);
            userAnswerOutputDTO.setUserAnswer(userAnswer);
            userAnswerOutputDTO.setCodeSandboxOutput(codeSandboxOutput);
            userAnswerOutputDTO.setAnalysisOutput(analysisOutput);
            return userAnswerOutputDTO;
        } catch (IOException e){
            throw new IOException(e.getMessage());
        }
    }
}
