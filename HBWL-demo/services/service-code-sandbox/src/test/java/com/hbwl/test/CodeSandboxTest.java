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

    @Test
    public void test02(){
        String codeLanguage = "cpp";
        String input = "10\n";
        String code = "#include<iostream> \nusing namespace std;\n\nint fibonacci(int n) {\n  if (n <= 0) {\n    return 0;\n  } else if (n == 1 || n == 2) {\n    return 1;\n  } else {\n    return fibonacci(n-1) + fibonacci(n-2);\n  }\n}\n\nint main() {\n  int n;\n  cin >> n;\n  cout << fibonacci(n) << endl;\n  return 0;\n}";
        String result = tool.compileAndExecute(codeLanguage, code, input);
        System.out.println(result);
    }
}
