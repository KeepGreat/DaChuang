package com.hbwl.test;

import com.hbwl.service.UserCallService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UserCallFrequencyTest {

    @Autowired
    private UserCallService userCallService;

    @Test
    public void test01(){
        String userId = "test-123";
        // 第一次调用
        long count1 = userCallService.recordUserCall(userId);
        System.out.println("第一次调用，次数: " + count1);

        // 第二次调用
        long count2 = userCallService.recordUserCall(userId);
        System.out.println("第二次调用，次数: " + count2);

        // 查询当前次数
        long currentCount = userCallService.getUserCallCount(userId, null);
        System.out.println("查询到的调用次数: " + currentCount);
    }
}
