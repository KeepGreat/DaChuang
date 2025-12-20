package com.hbwl.jwt.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hbwl.common.Result;
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
    public Mono<ResponseEntity<Result>> authenticate(@RequestBody Map<String, String> map){
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
        String localRole = user.getRole();

        if (!username.equals(localUsername)) {
            return buildFailureResponse("用户不存在");
        }
        if (!passwordEncoder.matches(password, localPassword)) {
            return buildFailureResponse("密码错误");
        }
        Map<String, String> payload = new HashMap<>();
        payload.put("username", localUsername);
        payload.put("role", localRole);
        return buildSuccessResponse(jwtTokenUtil.generateToken(localId, payload));
    }

    //刷新token
    @PostMapping("/refreshtoken")
    public Mono<ResponseEntity<Result>> refreshToken(@RequestBody String oldToken){
        String usernameFromToken = jwtTokenUtil.getUsernameFromToken(oldToken);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", usernameFromToken);
        User user = userMapper.selectOne(queryWrapper);
        String localId = user.getId();
        if (oldToken == null || oldToken.isEmpty()){
            return buildFailureResponse("参数不能为空");
        }
        if (!localId.equals(jwtTokenUtil.getUserIdFromToken(oldToken))){
            return buildFailureResponse("token信息出错！");
        }
        return buildSuccessResponse(jwtTokenUtil.refreshToken(oldToken));
    }

    //获取用户权限role
    @PostMapping("/identify")
    public Mono<ResponseEntity<Result>> identify(@RequestBody String token){
        if (token == null || token.isEmpty()){
            return buildFailureResponse("参数不能为空");
        }
        String usernameFromToken = jwtTokenUtil.getUsernameFromToken(token);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", usernameFromToken);
        User user = userMapper.selectOne(queryWrapper);
        String localId = user.getId();
        if (!localId.equals(jwtTokenUtil.getUserIdFromToken(token))){
            return buildFailureResponse("token信息出错！");
        }
        return buildSuccessResponse(jwtTokenUtil.getUserRoleFromToken(token));
    }

    //获取用户id
    @PostMapping("/identity")
    public Mono<ResponseEntity<Result>> identity(@RequestBody String token){
        if (token == null || token.isEmpty()){
            return buildFailureResponse("参数不能为空");
        }
        String usernameFromToken = jwtTokenUtil.getUsernameFromToken(token);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", usernameFromToken);
        User user = userMapper.selectOne(queryWrapper);
        String localId = user.getId();
        if (!localId.equals(jwtTokenUtil.getUserIdFromToken(token))){
            return buildFailureResponse("token信息出错！");
        }
        return buildSuccessResponse(jwtTokenUtil.getUserIdFromToken(token));
    }

    private Mono<ResponseEntity<Result>> buildSuccessResponse(Object data){
        return Mono.just(ResponseEntity.ok(Result.success(data, "success")));
    }

    private Mono<ResponseEntity<Result>> buildFailureResponse(String message){
        return Mono.just(ResponseEntity.badRequest().body(Result.error(message)));
    }
}
