package com.hbwl.controller;

import com.hbwl.codesandbox.pojo.CodeSandboxInput;
import com.hbwl.tool.CodeExecutionTool;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/codesandbox")
public class CodeSandboxController {

    private CodeExecutionTool codeExecutionTool = new CodeExecutionTool();

    @PostMapping("/execute")
    public String execute(@RequestBody CodeSandboxInput codeSandboxInput){
        System.out.println("-------------");
        System.out.println("调用CodeSandbox,使用语言:" + codeSandboxInput.getCodeLanguage());
        System.out.println("-------------");
        return codeExecutionTool.compileAndExecute(codeSandboxInput.getCodeLanguage(),
                codeSandboxInput.getCode(), codeSandboxInput.getInput());
    }
}
