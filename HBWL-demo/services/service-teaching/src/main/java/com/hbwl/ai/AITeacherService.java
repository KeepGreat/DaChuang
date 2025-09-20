package com.hbwl.ai;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import reactor.core.publisher.Flux;

/**
 * 主要职责: AI问答、代码错误修复、代码解释、思路提示
 * */

public interface AITeacherService {

    @SystemMessage(fromResource = "prompt/teacher-system-prompt-test.txt")
    @UserMessage("""
            老师你好，我在学习时遇到如下问题: {{question}}
            我使用的代码语言为: {{codeLanguage}}
            我的代码如下: {{code}}
            这个程序的输入为: {{input}}
            """)
    Flux<String> teach(@V("question") String question,
                       @V("code") String code,
                       @V("codeLanguage") String codeLanguage,
                       @V("input") String input);

    @SystemMessage(fromResource = "prompt/teacher-system-prompt-test.txt")
    @UserMessage("""
            老师你好，我在学习时遇到如下问题: {{question}}
            我使用的代码语言为: {{codeLanguage}}
            我的代码如下: {{code}}
            """)
    Flux<String> teach(@V("question") String question,
                       @V("code") String code,
                       @V("codeLanguage") String codeLanguage);
}
