package com.hbwl.controller;

import com.hbwl.ai.AITeacherService;
import com.hbwl.ai.service.SmartTeacherService;
import com.hbwl.codesandbox.pojo.CodeSandboxInput;
import com.hbwl.teaching.pojo.TeachingInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/teaching/ai")
public class TeachingController {

    @Autowired
    private SmartTeacherService smartTeacherService;

    //不能用包装类传输Flux<String>
    @PostMapping("/teach")
    public Flux<String> teach(@RequestBody TeachingInput teachingInput,
                              @RequestHeader("userId") String userId,
                              @RequestHeader("role") String role){
        return smartTeacherService.teach(teachingInput, userId, role);
    }

    @PostMapping("/answer")
    public Flux<String> answer(@RequestBody TeachingInput teachingInput,
                               @RequestHeader("userId") String userId,
                               @RequestHeader("role") String role){
        return smartTeacherService.answerQuestion(teachingInput, userId, role);
    }
}
