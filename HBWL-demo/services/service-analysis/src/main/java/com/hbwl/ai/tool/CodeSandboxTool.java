package com.hbwl.ai.tool;

import com.hbwl.codesandbox.pojo.CodeSandboxInput;
import com.hbwl.feign.CodeSandboxFeignClient;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 这里调用code-sandbox服务
 * */

@Component
public class CodeSandboxTool {

    @Autowired
    private CodeSandboxFeignClient codeSandboxClient;

    @Tool(name = "codeSandbox", value = """
            执行用户代码并返回执行结果
            """)
    public String execute(@P("编程语言") String codeLanguage,
                          @P("用户代码") String code,
                          @P("测试用例") String input){
        CodeSandboxInput codeSandboxInput = new CodeSandboxInput();
        codeSandboxInput.setCodeLanguage(codeLanguage);
        codeSandboxInput.setCode(code);
        codeSandboxInput.setInput(input);
        return codeSandboxClient.execute(codeSandboxInput);
    }
}
