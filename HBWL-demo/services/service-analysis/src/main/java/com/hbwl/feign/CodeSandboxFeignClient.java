package com.hbwl.feign;

import com.hbwl.codesandbox.pojo.CodeSandboxInput;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "CodeSandbox")
public interface CodeSandboxFeignClient {

    @PostMapping("/sandbox/execute")
    String execute(@RequestBody CodeSandboxInput input);
}
