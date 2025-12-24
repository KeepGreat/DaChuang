package com.hbwl.analysis.pojo;

import com.hbwl.codesandbox.pojo.CodeSandboxOutput;
import lombok.Data;

@Data
public class AnalysisInput {
    private String question;
    private String input;
    private String output;
    private String codeLanguage;
    private String code;

    private CodeSandboxOutput codeSandboxOutput;
}
