package com.hbwl.pojo.dto;

import com.hbwl.codesandbox.pojo.CodeSandboxInput;
import com.hbwl.pojo.UserAnswer;
import lombok.Data;

@Data
public class UserAnswerInputDTO {
    private CodeSandboxInput codeSandboxInput;
    private UserAnswer userAnswer;
}
