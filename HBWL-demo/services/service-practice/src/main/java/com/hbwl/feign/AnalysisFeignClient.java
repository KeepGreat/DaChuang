package com.hbwl.feign;

import com.hbwl.analysis.pojo.AnalysisInput;
import com.hbwl.analysis.pojo.AnalysisOutput;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

// TODO : 既然analysis模块只被practice调用，那可否将analysis直接整合到practice中

@FeignClient(value = "service-analysis")
public interface AnalysisFeignClient {

    @PostMapping("/api/analysis/analyze")
    AnalysisOutput analyze(AnalysisInput analysisInput);
}
