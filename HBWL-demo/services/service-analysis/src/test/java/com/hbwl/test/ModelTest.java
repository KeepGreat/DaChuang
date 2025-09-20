package com.hbwl.test;

import com.hbwl.ai.AICodeAnalyzerService;
import com.hbwl.ai.AICodeEvaluatorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ResourceLoader;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@SpringBootTest
public class ModelTest {

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private AICodeEvaluatorService aiCodeEvaluatorService;

    @Autowired
    private AICodeAnalyzerService aiCodeAnalyzerService;

    @Test
    public void complexQuestionTest() throws IOException {
        String question = resourceLoader.getResource("classpath:doc/question.txt")
                .getContentAsString(StandardCharsets.UTF_8);
        String input = resourceLoader.getResource("classpath:doc/input.txt")
                .getContentAsString(StandardCharsets.UTF_8);
        String output = resourceLoader.getResource("classpath:doc/output.txt")
                .getContentAsString(StandardCharsets.UTF_8);
        String codeLanguage = "cpp";
        String userCode = resourceLoader.getResource("classpath:code/Segment_Tree.cpp")
                .getContentAsString(StandardCharsets.UTF_8);
        Flux<String> flux = aiCodeEvaluatorService.evaluate(question, input, output, codeLanguage, userCode);
        StepVerifier.create(flux)
                .thenConsumeWhile(element -> {
                    System.out.print(element);
                    return true;
                })
                .verifyComplete();
    }

    @Test
    public void test02(){
        String question = "计算斐波那契数列的第n项,斐波那契数列的定义为F(1)=1, F(2)=1, F(n)=F(n-1)+F(n-2),(n≥3)";
        String input = "10\n";
        String output = "55";
        String codeLanguage = "cpp";
        //注意代码字符串的JSON转义！！！！
        String code = "#include \nusing namespace std;\n\nint fibonacci(int n) {\n  if (n <= 0) {\n    return 0;\n  } else if (n == 1 || n == 2) {\n    return 1;\n  } else {\n    return fibonacci(n-1) + fibonacci(n-2);\n  }\n}\n\nint main() {\n  int n;\n  cin >> n;\n  cout << fibonacci(n) << endl;\n  return 0;\n}";
        Flux<String> flux = aiCodeEvaluatorService.evaluate(question, input, output, codeLanguage, code);
        StepVerifier.create(flux)
                .thenConsumeWhile(element -> {
                    System.out.print(element);
                    return true;
                })
                .verifyComplete();
//        String analyze = aiCodeAnalyzerService.analyze(code);
//        System.out.println(analyze);
    }
}
