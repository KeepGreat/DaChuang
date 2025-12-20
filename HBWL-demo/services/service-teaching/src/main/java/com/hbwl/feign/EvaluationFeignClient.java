package com.hbwl.feign;

import com.hbwl.common.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(value = "service-evaluation")
public interface EvaluationFeignClient {

    @PostMapping("/api/evaluation/evaluation/smartcompanion")
    Result evaluateBaseOnSmartCompanion(@RequestBody Map<String, Object> map);

    @PostMapping("/teaching")
    Result evaluateBaseOnAITeaching(@RequestBody Map<String, Object> map);
}
