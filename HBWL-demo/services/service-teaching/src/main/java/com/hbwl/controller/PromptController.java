package com.hbwl.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hbwl.ai.service.PromptService;
import com.hbwl.common.Result;
import com.hbwl.pojo.ai.PromptInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/teaching/ai/prompt")
public class PromptController {

    @Autowired
    private PromptService promptService;

    @PostMapping
    public Result addPrompt(@RequestPart("promptInfo") String promptInfoJSON,
                            @RequestPart("promptString") String promptString,
                            @RequestHeader("role") String role) {
        if (!(role.equals("teacher") || role.equals("admin"))) return Result.error("权限不足");
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            PromptInfo promptInfo = objectMapper.readValue(promptInfoJSON, PromptInfo.class);

            int row = promptService.addPrompt(promptInfo, promptString);
            if (row == -1) return Result.error("参数不能为空");
            if (row == 0) return Result.error("提示词接收成功，但在保存时失败了");
            return Result.success("提示词上传成功");
        } catch (Exception e){
            return Result.error("提示词上传失败" + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result deletePrompt(@PathVariable("id") Integer id,
                               @RequestHeader("role") String role) {
        if (!(role.equals("teacher") || role.equals("admin"))) return Result.error("权限不足");
        int row = promptService.deletePrompt(id);
        if (row == -1) return Result.error("参数不能为空");
        if (row == 0) return Result.error("提示词不存在或已删除");
        return Result.success("提示词删除成功");

    }

    @PutMapping
    public Result updatePrompt(@RequestPart("promptInfo") String promptInfoJSON,
                               @RequestPart("promptString") String promptString,
                               @RequestHeader("role") String role) {
        if (!(role.equals("teacher") || role.equals("admin"))) return Result.error("权限不足");
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            PromptInfo promptInfo = objectMapper.readValue(promptInfoJSON, PromptInfo.class);

            int row = promptService.updatePrompt(promptInfo, promptString);
            if (row == -1) return Result.error("参数不能为空");
            if (row == 0) return Result.error("提示词接收成功，但在更新时失败了");
            return Result.success("提示词更新成功");
        } catch (Exception e) {
            return Result.error("提示词更新失败" + e.getMessage());
        }
    }

    @GetMapping
    public Result getPromptInfos(@RequestParam(required = false) Integer id,
                                 @RequestParam(required = false) String creatorId,
                                 @RequestHeader("role") String role) {
        if (!(role.equals("teacher") || role.equals("admin"))) return Result.error("权限不足");
        PromptInfo promptInfo = new PromptInfo();
        promptInfo.setId(id);
        promptInfo.setCreatorId(creatorId);
        return Result.success(promptService.getPromptInfos(id, creatorId), "查询提示词成功");
    }

    @GetMapping("/load")
    public Result loadPrompt(@RequestParam Integer id,
                             @RequestHeader("role") String role) {
        if (!(role.equals("teacher") || role.equals("admin"))) return Result.error("权限不足");
        String prompt = promptService.getPrompt(id);
        if (prompt == null) return Result.error("提示词不存在");
        return Result.success(prompt, "加载提示词成功");
    }

    @PutMapping("/setRuntime")
    public Result setRuntimePrompt(@RequestBody Integer id,
                                   @RequestHeader("role") String role) {
        if (!(role.equals("teacher") || role.equals("admin"))) return Result.error("权限不足");
        boolean success = promptService.setRuntimePrompt(id);
        if (!success) return Result.error("设置运行时提示词失败，可能是提示词不存在");
        return Result.success("设置运行时提示词成功");
    }

    @GetMapping("/getRuntime")
    public Result getRuntimePrompt(@RequestHeader("role") String role) {
        if (!(role.equals("teacher") || role.equals("admin"))) return Result.error("权限不足");
        String prompt = promptService.getRuntimePrompt();
        if (prompt == null) return Result.error("运行时提示词未设置");
        return Result.success(prompt, "获取运行时提示词成功");
    }
}
