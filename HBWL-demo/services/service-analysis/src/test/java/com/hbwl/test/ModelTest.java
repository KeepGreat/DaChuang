package com.hbwl.test;

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
}
