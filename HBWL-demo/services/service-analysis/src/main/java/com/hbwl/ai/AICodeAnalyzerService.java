package com.hbwl.ai;

import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface AICodeAnalyzerService {

    //这个方法作为tool供AICodeEvaluatorService调用
    @SystemMessage(fromResource = "prompt/analyzer-system-prompt.txt")
    @UserMessage("评测以下代码: {{it}}")
    @Tool(name = "CodeStructureEvaluator", value = """
            调用专门负责评测代码结构的大模型，可根据用户代码给出评测结果
            """)
    String analyze(String code);

    //这个方法直接供Controller调用，与上述方法的区别在于此方法提供问题信息和代码执行结果，能让ai更全面的进行分析
    @SystemMessage(fromResource = "prompt/analyzer-system-prompt-2.txt")
    @UserMessage("""
            请帮我评测一下我的代码
            我遇到的编程问题为:{{question}}
            我使用的编程语言为:{{lan}}
            我的代码为:{{code}}
            我的代码执行情况为:{{result}}
            """)
    String analyze(@V("code")String code,
                   @V("lan")String codeLanguage,
                   @V("question")String question,
                   @V("result")String codeSandboxOutput);
}
