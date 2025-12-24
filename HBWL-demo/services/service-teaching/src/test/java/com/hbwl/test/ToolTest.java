package com.hbwl.test;

import com.hbwl.ai.tool.SmartCompanionTool;
import com.hbwl.pojo.ai.SessionResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ToolTest {

    private final SmartCompanionTool tool = new SmartCompanionTool();

    @Test
    public void test01(){
        SessionResponse response = tool.createSession(null);
        System.out.println(response);
    }
}
