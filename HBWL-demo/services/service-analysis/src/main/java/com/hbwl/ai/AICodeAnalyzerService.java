package com.hbwl.ai;

import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

public interface AICodeAnalyzerService {

    @SystemMessage(fromResource = "prompt/analyzer-system-prompt.txt")
    @UserMessage("评测以下代码: {{it}}")
    @Tool(name = "CodeStructureEvaluator", value = """
            调用专门负责评测代码结构的大模型，可根据用户代码给出评测结果
            """)
    String analyze(String code);
}
