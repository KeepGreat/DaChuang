package com.hbwl.controller;

import com.hbwl.ai.AITeacherService;
import com.hbwl.codesandbox.pojo.CodeSandboxInput;
import com.hbwl.teaching.pojo.TeachingInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/teaching")
public class TeachingController {

    @Autowired
    private AITeacherService aiTeacherService;

    @PostMapping("/teach")
    public Flux<String> teach(@RequestBody TeachingInput teachingInput){
        CodeSandboxInput codeSandboxInput = teachingInput.getCodeSandboxInput();
        Flux<String> flux = aiTeacherService.teach(teachingInput.getQuestion(),
                codeSandboxInput.getCode(), codeSandboxInput.getCodeLanguage(), codeSandboxInput.getInput());
        return flux;
    }
}
