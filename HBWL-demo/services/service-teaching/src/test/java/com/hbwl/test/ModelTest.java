package com.hbwl.test;

import com.hbwl.ai.AITeacherService;
import com.hbwl.ai.service.SmartTeacherService;
import com.hbwl.teaching.pojo.TeachingInput;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@SpringBootTest
public class ModelTest {

    @Autowired
    private AITeacherService aiTeacherService;

    @Autowired
    private SmartTeacherService smartTeacherService;

    @Test
    public void test01(){
        String question = "为什么这个程序会报错";
        String code = "#include<bits/stdc++.h>\n using namespace std; int main(){int a; cin >> a; int b; cin >> b; cout << a/b; return 0;}";
        String codeLanguage = "cpp";
        String input = "1 0\n"; // 输入为null或空字符串都会报错
        Flux<String> flux = aiTeacherService.teach(question, code, codeLanguage, input);
        StepVerifier.create(flux)
                .thenConsumeWhile(element -> {
                    System.out.print(element);
                    return true;
                })
                .verifyComplete();
    }

    @Test
    public void test02(){
        String question = "为什么这个程序会报错";
        String code = "#include<bits/stdc++.h>\n using namespace std; int main(){cout << 1/0; return 0;}";
        String codeLanguage = "cpp";
        Flux<String> flux = aiTeacherService.teach(question, code, codeLanguage);
        StepVerifier.create(flux)
                .thenConsumeWhile(element -> {
                    System.out.print(element);
                    return true;
                })
                .verifyComplete();
    }

    @Test
    public void test03(){
        String question = "什么是SpringBoot?";
        String userId = "8163401c-2e9b-4d62-86b5-019f860765a1";
        String role = "admin";
        TeachingInput teachingInput = new TeachingInput();
        teachingInput.setQuestion(question);
        Flux<String> flux = smartTeacherService.answerQuestion(teachingInput, userId, role);
        StepVerifier.create(flux)
                .thenConsumeWhile(element -> {
                    System.out.print(element);
                    return true;
                })
                .verifyComplete();
    }
}
