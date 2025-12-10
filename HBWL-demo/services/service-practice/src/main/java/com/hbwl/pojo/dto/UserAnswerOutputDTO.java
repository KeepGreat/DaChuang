package com.hbwl.pojo.dto;

import com.hbwl.analysis.pojo.AnalysisOutput;
import com.hbwl.codesandbox.pojo.CodeSandboxOutput;
import com.hbwl.pojo.UserAnswer;
import lombok.Data;

@Data
public class UserAnswerOutputDTO {
    private UserAnswer userAnswer;
    private CodeSandboxOutput codeSandboxOutput;
    private AnalysisOutput analysisOutput;
}
