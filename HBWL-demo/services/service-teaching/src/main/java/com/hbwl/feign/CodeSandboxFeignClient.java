package com.hbwl.feign;

import com.hbwl.codesandbox.pojo.CodeSandboxInput;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "service-code-sandbox")
public interface CodeSandboxFeignClient {

    @PostMapping("/api/codesandbox/execute")
    String execute(@RequestBody CodeSandboxInput input);
}
