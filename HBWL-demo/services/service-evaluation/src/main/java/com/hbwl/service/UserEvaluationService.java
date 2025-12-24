package com.hbwl.service;

import com.hbwl.pojo.UserEvaluation;

import java.util.Map;

public interface UserEvaluationService {

    //根据代码评估整合相关评分
    UserEvaluation evaluateBaseOnPractice(String userId, String json);

    //根据智能助教整合相关评分
    UserEvaluation evaluateBaseOnAITeaching(Map<String, Object> map);

    //根据智能学伴整合相关评分
    UserEvaluation evaluateBaseOnSmartCompanion(Map<String, Object> map);

    UserEvaluation getEvaluationByUserId(String userId);

    //以下三个方法根据代码评估返回三方面评分
//    Float evaluateAccuracyRateScore(String json, Float previousScore);
//
//    Float evaluateMasteryDepthScore(String json, Float previousScore);
//
//    Float evaluateQualityScore(String json, Float previousScore);

    //以下一个方法根据智能助教分析结果返回一方面评分,传参暂定
    //方案：从学生开始学习（获取到课堂资源）并第一次向助教提问时开始计时，到学生完成课堂学习（学生点击“完成学习”按钮）时结束计时
    //在这个时间段内统计学生调用助教的次数，根据频率进行评分
    //使用token进行标记起始和终止
//    Float evaluateAiDependenceScore(String s, Float previousScore);
//
//    //以下两个方法根据智能学伴分析评分返回两方面评分,传参暂定
//    Float evaluateConversionEfficiencyScore(Float previousScore);
//
//    Float evaluateExpressionScore(Float previousScore);
}
