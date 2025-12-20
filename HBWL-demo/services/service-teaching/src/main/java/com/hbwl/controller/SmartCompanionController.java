package com.hbwl.controller;

import com.hbwl.ai.service.SmartCompanionService;
import com.hbwl.common.Result;
import com.hbwl.pojo.ai.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/teaching/smartcompanion")
public class SmartCompanionController {

    @Autowired
    private SmartCompanionService smartCompanionService;

    @PostMapping("/session/create")
    public Result createSession(@RequestBody CreateSessionRequest createSessionRequest,
                                @RequestHeader("userId") String userId,
                                @RequestHeader("role") String role){
        SessionResponse response = smartCompanionService.createSession(createSessionRequest);
        return Result.success(response, null);
    }

    @PostMapping("/chat")
    public Result chat(@RequestBody ChatRequest chatRequest,
                       @RequestHeader("userId") String userId){
        ChatResponse response = smartCompanionService.chat(chatRequest, userId);
        return Result.success(response, null);
    }

    @GetMapping("/session/history")
    public Result getSessionHistory(@RequestParam String sessionId){
        HistoryResponse response = smartCompanionService.getConversationHistory(sessionId);
        return Result.success(response, null);
    }

    @DeleteMapping("/session/{id}")
    public Result resetSession(@PathVariable("id") String sessionId){
        smartCompanionService.resetSession(sessionId);
        return Result.success(null);
    }

    @GetMapping("/session/status")
    public Result getSessionStatus(@RequestParam String sessionId){
        StatusResponse response = smartCompanionService.getSessionStatus(sessionId);
        return Result.success(response, null);
    }
}
