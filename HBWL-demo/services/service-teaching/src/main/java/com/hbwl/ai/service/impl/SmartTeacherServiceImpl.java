package com.hbwl.ai.service.impl;

import com.hbwl.ai.AITeacherService;
import com.hbwl.ai.service.SmartTeacherService;
import com.hbwl.codesandbox.pojo.CodeSandboxInput;
import com.hbwl.service.UserCallService;
import com.hbwl.teaching.pojo.TeachingInput;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@Slf4j
public class SmartTeacherServiceImpl implements SmartTeacherService {

    @Autowired
    private AITeacherService aiTeacherService;

    @Autowired
    private UserCallService userCallService;

    @Value("${agent.max-call-per-hour}")
    private Integer MAX_CALL_PER_HOUR;

    @Override
    public Flux<String> teach(TeachingInput teachingInput, String userId, String role) {
        if (teachingInput == null ||
            teachingInput.getQuestion() == null || teachingInput.getQuestion().trim().isEmpty() ||
            teachingInput.getCodeSandboxInput() == null || teachingInput.getCodeSandboxInput().getCodeLanguage() == null || teachingInput.getCodeSandboxInput().getCode() == null) return null;
        CodeSandboxInput codeSandboxInput = teachingInput.getCodeSandboxInput();

        //频率判断
        long callCount = userCallService.getUserCallCount(userId, null);
        if (role.equals("student") && callCount > MAX_CALL_PER_HOUR){
            return Flux.just("调用频率过高，请稍后再试");
        }
        //记录用户调用
        callCount = userCallService.recordUserCall(userId);
        log.info("用户ID: {} ; 小时内调用次数: {}", userId, callCount);
        Flux<String> result;
        if (codeSandboxInput.getInput() == null || codeSandboxInput.getInput().isBlank())
            result = aiTeacherService.teach(teachingInput.getQuestion(), codeSandboxInput.getCode(),
                    codeSandboxInput.getCodeLanguage(), codeSandboxInput.getInput());
        else result = aiTeacherService.teach(teachingInput.getQuestion(), codeSandboxInput.getCode(),
                codeSandboxInput.getCodeLanguage());
        return result;
    }

    @Override
    public Flux<String> answerQuestion(TeachingInput teachingInput, String userId, String role) {
        //频率判断
        long callCount = userCallService.getUserCallCount(userId, null);
        if (role.equals("student") && callCount > MAX_CALL_PER_HOUR){
            return Flux.just("调用频率过高，请稍后再试");
        }
        //记录用户调用
        callCount = userCallService.recordUserCall(userId);
        log.info("用户ID: {} ; 小时内调用次数: {}", userId, callCount);
        return aiTeacherService.answerQuestion(teachingInput.getQuestion());
    }
}
