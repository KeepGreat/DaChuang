package com.hbwl.utils;

import cn.hutool.json.JSONUtil;
import com.hbwl.pojo.TestScore;
import org.springframework.stereotype.Component;

@Component
public class TestScoreUtil {
    private final int INFACTOR = 20;

    private TestScore parseTestScore(String json){
        TestScore testScore = JSONUtil.toBean(json, TestScore.class);
        return testScore;
    }

    public Float getWeightedScore(String json , float weight_readability ,
                                  float weight_structure ,float weight_modularity , float weight_guidelines){
        TestScore testScore = parseTestScore(json);
        float readability = (float) testScore.getReadability() * INFACTOR;
        float structure = (float) testScore.getStructure() * INFACTOR;
        float modularity = (float) testScore.getModularity() * INFACTOR;
        float guidelines = (float) testScore.getGuidelines() * INFACTOR;

        float weightedScore = readability * weight_readability +
                structure * weight_structure +
                modularity * weight_modularity +
                guidelines * weight_guidelines;

        return weightedScore;
    }

}
