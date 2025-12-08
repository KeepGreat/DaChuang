package com.hbwl.test;

import com.hbwl.ai.service.AITeacherService;
import com.hbwl.ai.service.SmartCompanionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@SpringBootTest
public class ModelTest {

    @Autowired
    private AITeacherService aiTeacherService;

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
}
