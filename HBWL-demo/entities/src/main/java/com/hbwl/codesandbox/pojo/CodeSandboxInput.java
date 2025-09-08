package com.hbwl.codesandbox.pojo;

import lombok.Data;

@Data
public class CodeSandboxInput {
    private String codeLanguage;
    private String code;
    private String input;
}
