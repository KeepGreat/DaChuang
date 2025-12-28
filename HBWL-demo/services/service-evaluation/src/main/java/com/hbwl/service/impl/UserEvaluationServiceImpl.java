package com.hbwl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hbwl.mapper.UserScoreMapper;
import com.hbwl.pojo.UserEvaluation;
import com.hbwl.pojo.UserScore;
import com.hbwl.service.UserEvaluationService;
import com.hbwl.utils.TestScoreUtil;
import com.hbwl.utils.UserEvaluationUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@Transactional
@Slf4j
public class UserEvaluationServiceImpl implements UserEvaluationService {

    @Autowired
    private UserScoreMapper userScoreMapper;

    @Autowired
    private TestScoreUtil testScoreUtil;

    @Autowired
    private UserEvaluationUtil userEvaluationUtil;

    @Override
    public UserEvaluation evaluateBaseOnPractice(String userId, String json) {
        log.info("收到了评估练习部分的请求，开始进行多维度评估，收到的参数：\n{}\n{}", userId, json);
        QueryWrapper<UserScore> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        UserScore userScore = userScoreMapper.selectOne(wrapper);
        UserEvaluation evaluation = userEvaluationUtil.parseUserEvaluation(userScore.getEachScore());

        evaluation.setAccuracyRateScore(evaluateAccuracyRateScore(json, evaluation.getAccuracyRateScore()));
        evaluation.setMasteryDepthScore(evaluateMasteryDepthScore(json, evaluation.getMasteryDepthScore()));
        evaluation.setQualityScore(evaluateQualityScore(json, evaluation.getQualityScore()));

        String newUserEvaluation = userEvaluationUtil.generationUserEvaluation(evaluation);
        Float totalScore = userEvaluationUtil.getTotalScore(evaluation);
        userScore.setEachScore(newUserEvaluation);
        userScore.setTotalScore(totalScore);
        userScoreMapper.updateById(userScore);
        return evaluation;
    }

    @Override
    public UserEvaluation evaluateBaseOnAITeaching(Map<String, Object> map) {
        String userId = (String) map.get("userId");
        Integer callCount = (Integer) map.get("callCount");
//        UserCallRecord userCallRecord = (UserCallRecord) userCallFeignClient.getUserCallRecord(userId).getData();
//        Integer callCount = userCallRecord.getCallCount();

        //从数据库中拿到对应的评分数据
        QueryWrapper<UserScore> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        UserScore userScore = userScoreMapper.selectOne(wrapper);
        //对json评分数据进行解析
        UserEvaluation evaluation = userEvaluationUtil.parseUserEvaluation(userScore.getEachScore());
        //修改ai依赖评分
        evaluation.setAiDependenceScore(evaluateAiDependenceScore(callCount, evaluation.getAiDependenceScore()));
        //将UserEvaluation转化为josn格式
        String newUserEvaluation = userEvaluationUtil.generationUserEvaluation(evaluation);
        Float totalScore = userEvaluationUtil.getTotalScore(evaluation);
        userScore.setEachScore(newUserEvaluation);
        userScore.setTotalScore(totalScore);
        userScoreMapper.updateById(userScore);
        return evaluation;
    }

    @Override
    public UserEvaluation evaluateBaseOnSmartCompanion(Map<String, Object> map) {
        String userId = (String) map.get("userId");
        Number expressionScoreNumber = (Number) map.get("expressionScore");
        Float expressionScore = expressionScoreNumber.floatValue();
        Number conversionEfficiencyScoreNumber = (Number) map.get("conversionEfficiencyScore");
        Float conversionEfficiencyScore = conversionEfficiencyScoreNumber.floatValue();

        //从数据库中拿到对应的评分数据
        QueryWrapper<UserScore> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        UserScore userScore = userScoreMapper.selectOne(wrapper);
        //对json评分数据进行解析
        UserEvaluation evaluation = userEvaluationUtil.parseUserEvaluation(userScore.getEachScore());

        //修改知识转化率和表达评分
        evaluation.setExpressionScore(evaluateExpressionScore(expressionScore, evaluation.getExpressionScore()));
        evaluation.setConversionEfficiencyScore(evaluateConversionEfficiencyScore(conversionEfficiencyScore, evaluation.getConversionEfficiencyScore()));

        //将UserEvaluation转化为json格式
        String newUserEvaluation = userEvaluationUtil.generationUserEvaluation(evaluation);
        Float totalScore = userEvaluationUtil.getTotalScore(evaluation);
        userScore.setEachScore(newUserEvaluation);
        userScore.setTotalScore(totalScore);
        userScoreMapper.updateById(userScore);
        return evaluation;
    }

    @Override
    public UserEvaluation getEvaluationByUserId(String userId) {
        QueryWrapper<UserScore> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        UserScore userScore = userScoreMapper.selectOne(wrapper);
        UserEvaluation evaluation = userEvaluationUtil.parseUserEvaluation(userScore.getEachScore());
        evaluation.setUserId(userId);
        return evaluation;
    }

    private Float evaluateAccuracyRateScore(String json, Float previousScore) {
        //得到加权后的评分(转化为100分制)
        Float evaluationScore = testScoreUtil.getWeightedScore(json,
                0.25f , 0.25f , 0.25f , 0.25f);
        return (evaluationScore + previousScore) / 2f;
    }

    private Float evaluateMasteryDepthScore(String json, Float previousScore) {
        Float evaluationScore = testScoreUtil.getWeightedScore(json,
                0.3f , 0.3f , 0.2f , 0.2f);
        return (evaluationScore + previousScore) / 2f;
    }

    private Float evaluateQualityScore(String json, Float previousScore) {
        Float evaluationScore = testScoreUtil.getWeightedScore(json,
                0.4f , 0.1f , 0.1f , 0.4f);
        return (evaluationScore + previousScore) / 2f;
    }

    private Float evaluateAiDependenceScore(Integer callCount, Float previousScore) {
        Float weightedScore = 10f * (10 - Math.min(8 , callCount));
        return (weightedScore + previousScore) / 2f;
    }

    private Float evaluateConversionEfficiencyScore(Float conversionEfficiencyScore, Float previousScore) {
        return (conversionEfficiencyScore + previousScore) / 2;
    }

    private Float evaluateExpressionScore(Float expressionScore , Float previousScore) {
        return (expressionScore + previousScore) / 2;
    }

}
