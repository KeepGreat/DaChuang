package com.hbwl.utils;

import cn.hutool.json.JSONUtil;
import com.hbwl.pojo.UserEvaluation;
import org.springframework.stereotype.Component;

@Component
public class UserEvaluationUtil {
    /*解析UserEvaluation类*/
    public UserEvaluation parseUserEvaluation(String eachScore) {
        UserEvaluation userEvaluation = JSONUtil.toBean(eachScore, UserEvaluation.class);
        return userEvaluation;
    }

    /*通过Hutool生成JSON格式的类*/
    public String generationUserEvaluation(UserEvaluation evaluation) {
        String jsonStr = JSONUtil.toJsonStr(evaluation);
        return jsonStr;
    }

    public Float getTotalScore(UserEvaluation evaluation) {
        Float totalScore = 0.0f;
        Float accuracyRateScore = evaluation.getAccuracyRateScore();
        Float aiDependenceScore = evaluation.getAiDependenceScore();
        Float conversionEfficiencyScore = evaluation.getConversionEfficiencyScore();
        Float expressionScore = evaluation.getExpressionScore();
        Float masteryDepthScore = evaluation.getMasteryDepthScore();
        Float qualityScore = evaluation.getQualityScore();

        Float totalScoreSum = (float) Math.pow(accuracyRateScore,2) +
                (float) Math.pow(aiDependenceScore,2) +
                (float) Math.pow(conversionEfficiencyScore,2) +
                (float) Math.pow(expressionScore,2) +
                (float) Math.pow(masteryDepthScore,2) +
                (float) Math.pow(qualityScore,2);

        totalScore = (float)Math.sqrt((double)totalScoreSum/6.0);

        return totalScore;
    }
}
