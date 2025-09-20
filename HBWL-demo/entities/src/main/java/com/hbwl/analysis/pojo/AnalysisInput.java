package com.hbwl.analysis.pojo;

import lombok.Data;

@Data
public class AnalysisInput {
    private String question;
    private String input;
    private String output;
    private String codeLanguage;
    private String code;
}
