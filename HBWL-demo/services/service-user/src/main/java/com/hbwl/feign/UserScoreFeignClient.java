package com.hbwl.feign;

import com.hbwl.common.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(value = "service-evaluation")
public interface UserScoreFeignClient {

    @PostMapping("/api/evaluation/score")
    Result addUserScore(@RequestBody Map<String, String> map);
}
