package com.hbwl.controller;

import com.hbwl.codesandbox.pojo.CodeSandboxInput;
import com.hbwl.tool.CodeExecutionTool;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sandbox")
public class CodeSandboxController {

    private CodeExecutionTool codeExecutionTool = new CodeExecutionTool();

    @PostMapping("/execute")
    public String execute(@RequestBody CodeSandboxInput codeSandboxInput){
        return codeExecutionTool.compileAndExecute(codeSandboxInput.getCodeLanguage(),
                codeSandboxInput.getCode(), codeSandboxInput.getInput());
    }
}
