package com.hbwl.analysis.pojo;

import lombok.Data;

import java.util.Map;

@Data
public class AnalysisOutput {
    private String analysis;
    private Map<String, Integer> score;
}
