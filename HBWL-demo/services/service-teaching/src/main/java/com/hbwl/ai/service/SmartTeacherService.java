package com.hbwl.ai.service;

import com.hbwl.teaching.pojo.TeachingInput;
import reactor.core.publisher.Flux;

public interface SmartTeacherService {

    Flux<String> teach(TeachingInput teachingInput, String userId, String role);

    Flux<String> answerQuestion(TeachingInput teachingInput, String userId, String role);
}
