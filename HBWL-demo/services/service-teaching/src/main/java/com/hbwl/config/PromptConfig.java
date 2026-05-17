package com.hbwl.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;

@Configuration
public class PromptConfig {

    @Value("${agent.prompt.upload-dir}")
    private String uploadDir;

    @Bean(name = "prompt")
    public Path promptStorageLocation(){
        Path path = Path.of(uploadDir).toAbsolutePath().normalize();
        try {
            java.nio.file.Files.createDirectories(path);
            return path;
        } catch (Exception e){
            throw new RuntimeException("无法创建文件目录: " + path, e);
        }
    }
}
