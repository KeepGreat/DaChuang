package com.hbwl.controller;

import com.hbwl.codesandbox.pojo.CodeSandboxInput;
import com.hbwl.feign.CodeSandboxFeignClient;
import com.hbwl.properties.AnalysisProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/analysis/test")
public class TestController {

    @Autowired
    private AnalysisProperties analysisProperties;

    @Autowired
    private CodeSandboxFeignClient codeSandboxFeignClient;

    @GetMapping("/config")
    public String getConfig(){
        return analysisProperties.getBaseURL() + "\n" +
                analysisProperties.getApiKey() + "\n" +
                analysisProperties.getModelName();
    }

    @PostMapping("/feign")
    public String testFeignClient(@RequestBody CodeSandboxInput codeSandboxInput){
        return codeSandboxFeignClient.execute(codeSandboxInput);
    }
}
