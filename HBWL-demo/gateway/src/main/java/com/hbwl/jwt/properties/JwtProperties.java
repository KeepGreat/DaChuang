package com.hbwl.jwt.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@ConfigurationProperties(prefix = "gateway.jwt")
@Component
public class JwtProperties {

    private String secretKey;

    private Long expiration;

    private String headerName;
}
