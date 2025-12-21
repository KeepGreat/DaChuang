package com.hbwl.jwt.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hbwl.jwt.mapper.UserMapper;
import com.hbwl.jwt.pojo.User;
import com.hbwl.jwt.utils.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
public class JwtAuthController {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserMapper userMapper;

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    //登录界面内颁发token
    @PostMapping("/authenticate")
    public Mono<ResponseEntity<Map<String, Object>>> authenticate(@RequestBody Map<String, String> map){
        String username = map.get("username");
        String password = map.get("password");
        if (username == null || password == null || username.isEmpty() || password.isEmpty()){
            return buildFailureResponse("参数不能为空");
        }

        //数据库查找
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        User user = userMapper.selectList(queryWrapper).get(0);
        String localId = user.getId();
        String localUsername = user.getUsername();
        String localPassword = user.getPassword();


        if (!username.equals(localUsername)) {
            return buildFailureResponse("用户不存在");
        }
        if (!passwordEncoder.matches(password, localPassword)) {
            return buildFailureResponse("密码错误");
        }
        Map<String, String> payload = new HashMap<>();
        payload.put("username", username);
        payload.put("role", "admin");
        return buildSuccessResponse(jwtTokenUtil.generateToken(localId, payload));
    }

    //刷新token
    @PostMapping("/refreshtoken")
    public Mono<ResponseEntity<Map<String, Object>>> refreshToken(@RequestBody Map<String, Object> map){
        String oldToken = (String) map.get("token");
        String userId = (String) map.get("userId");
        if (oldToken == null || userId == null || oldToken.isEmpty()){
            return buildFailureResponse("参数不能为空");
        }
        if (!userId.equals(jwtTokenUtil.getUserIdFromToken(oldToken))){
            return buildFailureResponse("token信息出错！");
        }
        return buildSuccessResponse(jwtTokenUtil.refreshToken(oldToken));
    }

    private Mono<ResponseEntity<Map<String, Object>>> buildSuccessResponse(Object data){
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("data", data);
        responseBody.put("status", "success");
        return Mono.just(ResponseEntity.ok(responseBody));
    }

    private Mono<ResponseEntity<Map<String, Object>>> buildFailureResponse(String message){
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("message", message);
        responseBody.put("status", "failure");
        return Mono.just(ResponseEntity.badRequest().body(responseBody));
    }
}
