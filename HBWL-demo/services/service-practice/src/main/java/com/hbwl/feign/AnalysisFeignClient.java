package com.hbwl.feign;

import com.hbwl.analysis.pojo.AnalysisInput;
import com.hbwl.analysis.pojo.AnalysisOutput;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "service-analysis")
public interface AnalysisFeignClient {

    @PostMapping("/api/analysis/analyze")
    AnalysisOutput analyze(AnalysisInput analysisInput);
}
