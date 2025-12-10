package com.hbwl.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hbwl.ai.AICodeAnalyzerService;
import com.hbwl.ai.AICodeEvaluatorService;
import com.hbwl.utils.AnalysisParseUtil;
import dev.langchain4j.agent.tool.P;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ResourceLoader;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SpringBootTest
public class ModelTest {

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private AICodeEvaluatorService aiCodeEvaluatorService;

    @Autowired
    private AICodeAnalyzerService aiCodeAnalyzerService;

    @Autowired
    private AnalysisParseUtil util;

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
        String code = "#include<iostream> \nusing namespace std;\n\nint fibonacci(int n) {\n  if (n <= 0) {\n    return 0;\n  } else if (n == 1 || n == 2) {\n    return 1;\n  } else {\n    return fibonacci(n-1) + fibonacci(n-2);\n  }\n}\n\nint main() {\n  int n;\n  cin >> n;\n  cout << fibonacci(n) << endl;\n  return 0;\n}";
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

    @Test
    public void test03() throws IOException {
        String userCode = resourceLoader.getResource("classpath:code/Segment_Tree.cpp")
                .getContentAsString(StandardCharsets.UTF_8);
        String analyze = aiCodeAnalyzerService.analyze(userCode);
        System.out.println(analyze);
        System.out.println("-----------------------------");
        ObjectMapper objectMapper = new ObjectMapper();
        Pattern pattern = Pattern.compile("\"\"\"(.*?)\"\"\"[\\s\\r\\n]*(\\{.*\\})", Pattern.DOTALL);

        Matcher matcher = pattern.matcher(analyze);

        if (matcher.find()) {
            String textPart = matcher.group(1).trim();
            System.out.println("------------第一部分------------");
            System.out.println(textPart);

            String jsonString = matcher.group(2).trim();
            // 进一步清理JSON字符串：移除可能的多余空白
            jsonString = jsonString.replaceAll("\\s+", " ");
            try {
                Object jsonObject = objectMapper.readValue(jsonString, Object.class);
                System.out.println("------------第二部分------------");
                System.out.println(jsonObject);
            } catch (JsonProcessingException e) {
                System.err.println("提取到的字符串不是有效JSON: " + jsonString);
            }
        }
    }

    @Test
    public void test04(){
        String codeLanguage = "cpp";
        String code = "#include<iostream> \nusing namespace std;\n\nint fibonacci(int n) {\n  if (n <= 0) {\n    return 0;\n  } else if (n == 1 || n == 2) {\n    return 1;\n  } else {\n    return fibonacci(n-1) + fibonacci(n-2);\n  }\n}\n\nint main() {\n  int n;\n  cin >> n;\n  cout << fibonacci(n) << endl;\n  return 0;\n}";
        String question = "计算斐波那契数列的第n项,斐波那契数列的定义为F(1)=1, F(2)=1, F(n)=F(n-1)+F(n-2),(n≥3)";
        String result = "{\"memory\":1302528,\"stdout\":\"55\\n\",\"stderr\":\"\",\"procPeak\":1,\"runTime\":4939860,\"time\":1622000,\"exitStatus\":0,\"status\":\"Accepted\"}";
        String analyze = aiCodeAnalyzerService.analyze(code, codeLanguage, question, result);
        //System.out.println(analyze);
        System.out.println(util.parseAnalysis(analyze).getAnalysis());
        System.out.println("--------------------------------------");
        System.out.println(util.parseAnalysis(analyze).getScore());
    }
}
