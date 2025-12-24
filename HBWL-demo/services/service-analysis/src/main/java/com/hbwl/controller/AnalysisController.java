package com.hbwl.controller;

import com.hbwl.ai.AICodeAnalyzerService;
import com.hbwl.ai.AICodeEvaluatorService;
import com.hbwl.analysis.pojo.AnalysisInput;
import com.hbwl.analysis.pojo.AnalysisOutput;
import com.hbwl.utils.AnalysisParseUtil;
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

    @Autowired
    private AICodeAnalyzerService aiCodeAnalyzerService;

    @Autowired
    private AnalysisParseUtil analysisParseUtil;

    //又调沙箱又调ai
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

    //只调ai
    @PostMapping("/analyze")
    public AnalysisOutput analyze(@RequestBody AnalysisInput analysisInput){
        if (analysisInput == null || analysisInput.getCodeLanguage() == null || analysisInput.getCode() == null || analysisInput.getCodeSandboxOutput() == null || analysisInput.getQuestion() == null) return  null;
        String analysis = aiCodeAnalyzerService.analyze(analysisInput.getCode(), analysisInput.getCodeLanguage(), analysisInput.getQuestion(), analysisInput.getCodeSandboxOutput().toString());
        return analysisParseUtil.parseAnalysis(analysis);
    }
}
