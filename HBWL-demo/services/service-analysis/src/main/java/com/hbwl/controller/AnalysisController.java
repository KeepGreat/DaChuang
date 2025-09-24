package com.hbwl.controller;

import com.hbwl.ai.AICodeEvaluatorService;
import com.hbwl.analysis.pojo.AnalysisInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/analysis")
public class AnalysisController {

    @Autowired
    private AICodeEvaluatorService aiCodeEvaluatorService;

    @PostMapping("/evaluate")
    public Flux<String> evaluate(@RequestBody AnalysisInput analysisInput){
        Flux<String> result;
        if (analysisInput.getInput() == null || analysisInput.getInput().isBlank())
            result = aiCodeEvaluatorService.evaluate(analysisInput.getQuestion(), analysisInput.getOutput(),
                    analysisInput.getCodeLanguage(), analysisInput.getCode());
        else result = aiCodeEvaluatorService.evaluate(analysisInput.getQuestion(), analysisInput.getInput(),
                analysisInput.getOutput(), analysisInput.getCodeLanguage(), analysisInput.getCode());
        return result;
    }
}
