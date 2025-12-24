package com.hbwl.pojo.ai;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UserScoreResponse {
    @JsonProperty("conversion_efficiency_score")
    private Float conversionEfficiencyScore;
    @JsonProperty("expression_score")
    private Float expressionScore;
}
