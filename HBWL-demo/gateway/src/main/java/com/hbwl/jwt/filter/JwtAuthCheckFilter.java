package com.hbwl.jwt.filter;

import com.hbwl.jwt.properties.JwtProperties;
import com.hbwl.jwt.utils.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtAuthCheckFilter implements GlobalFilter, Ordered {

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        String requestUrl = request.getURI().getPath();

        if (!requestUrl.equals("/authenticate") &&
                !requestUrl.equals("/refreshtoken") &&
                !requestUrl.equals("/identify") &&
                !requestUrl.equals("/user/register")){
            String token = request.getHeaders().getFirst(jwtProperties.getHeaderName());
            boolean isExpired = jwtTokenUtil.isTokenExpired(token);
            if (isExpired){
                return buildFailureResponse(response, "请重新登录");
            }
            //从JWT中解析出用户Id并继续执行过滤器链，转发请求
            ServerHttpRequest mutableReq = request
                    .mutate()
                    .header("userId", jwtTokenUtil.getUserIdFromToken(token))
                    .header("role", jwtTokenUtil.getUserRoleFromToken(token))
                    .build();
            ServerWebExchange mutableExchange = exchange.mutate().request(mutableReq).build();
            return chain.filter(mutableExchange);
        } else{
            //登录和刷新token请求放行
            return chain.filter(exchange);
        }
    }

    private Mono<Void> buildFailureResponse(ServerHttpResponse response, String message) {
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("message", message);
        DataBuffer buffer = response.bufferFactory().wrap(responseBody.toString().getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
