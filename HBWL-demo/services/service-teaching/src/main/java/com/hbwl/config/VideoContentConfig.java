package com.hbwl.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class VideoContentConfig {

    @Value("${video.upload.chunk-dir}")
    private String uploadChunkDir;

    @Value("${video.upload.final-dir}")
    private String uploadFinalDir;

    @Bean(name = "chunk")
    public Path videoChunkStorageLocation() {
        Path path = Paths.get(uploadChunkDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(path);
            return path;
        } catch (IOException e){
            throw new RuntimeException("无法创建文件目录: " + path, e);
        }
    }

    @Bean(name = "video")
    public Path videoFinalStorageLocation() {
        Path path = Paths.get(uploadFinalDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(path);
            return path;
        } catch (IOException e){
            throw new RuntimeException("无法创建文件目录: " + path, e);
        }
    }
}
