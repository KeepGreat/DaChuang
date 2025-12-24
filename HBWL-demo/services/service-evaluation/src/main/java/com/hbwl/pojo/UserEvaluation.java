package com.hbwl.pojo;

import lombok.Data;

@Data
public class UserEvaluation {
    private String userId;
    private Float accuracyRateScore; //代码准确率 -- 代码评估
    private Float aiDependenceScore; //ai依赖度 -- 智能助教
    private Float conversionEfficiencyScore; //知识转化率 -- 智能学伴
    private Float expressionScore; //表达能力 -- 智能学伴
    private Float masteryDepthScore; //知识总体掌握深度 -- 代码评估
    private Float qualityScore; //代码质量 --代码评估
}
