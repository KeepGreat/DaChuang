package com.hbwl.teaching.pojo;

import com.hbwl.codesandbox.pojo.CodeSandboxInput;
import lombok.Data;

@Data
public class TeachingInput {
    private String question;
    private CodeSandboxInput codeSandboxInput;
}
