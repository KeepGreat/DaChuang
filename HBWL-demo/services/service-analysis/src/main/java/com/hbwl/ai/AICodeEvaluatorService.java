package com.hbwl.ai;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import reactor.core.publisher.Flux;

public interface AICodeEvaluatorService {

    @SystemMessage(fromResource = "prompt/evaluator-system-prompt.txt")
    @UserMessage("""
            现在有一个编程问题: {{question}}
            测试用例为: {{input}}
            预期输出为: {{output}}
            我使用的编程语言为: {{codeLanguage}}
            我的代码实现为:
            
            {{userCode}}
            
            请帮我评测一下我的代码
            """)
    Flux<String> evaluate(@V("question") String question,
                          @V("input") String input,
                          @V("output") String output,
                          @V("codeLanguage") String codeLanguage,
                          @V("userCode") String userCode);

    @SystemMessage(fromResource = "prompt/evaluator-system-prompt.txt")
    @UserMessage("""
            现在有一个编程问题: {{question}}
            预期输出为: {{output}}
            我使用的编程语言为: {{codeLanguage}}
            我的代码实现为:
            
            {{userCode}}
            
            请帮我评测一下我的代码
            """)
    Flux<String> evaluate(@V("question") String question,
                          @V("output") String output,
                          @V("codeLanguage") String codeLanguage,
                          @V("userCode") String userCode);
}
