package com.hbwl.test;

import cn.hutool.json.JSONObject;
import com.hbwl.codesandbox.pojo.CodeSandboxInput;
import com.hbwl.tool.CodeExecutionTool;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CodeSandboxTest {

    private CodeExecutionTool tool = new CodeExecutionTool();

    @Test
    public void test01(){
        String result = tool.compileAndExecute("python", "print(\"Hello World\")", null);
        System.out.println(result);
    }
}
