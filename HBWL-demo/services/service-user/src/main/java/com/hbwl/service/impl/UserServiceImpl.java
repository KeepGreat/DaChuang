package com.hbwl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hbwl.common.Result;
import com.hbwl.feign.UserScoreFeignClient;
import com.hbwl.mapper.UserMapper;
import com.hbwl.pojo.User;
import com.hbwl.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserScoreFeignClient userScoreFeignClient;

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public int addUser(User user) {
        if (user == null || user.getUsername() == null || user.getPassword() == null) return -1;
        String userId = UUID.randomUUID().toString();
        user.setId(userId);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Result result = userScoreFeignClient.addUserScore(Map.of("userId", userId));
        if (result.getCode() != 200) return 0;
        System.out.println("插入数据: " + user );
        return userMapper.insert(user);
    }

    @Override
    public int deleteUserById(String id) {
        if (id == null) return -1;
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        return userMapper.delete(queryWrapper);
    }

    @Override
    public int updateUserById(User user) {
        if (user == null || user.getId() == null) return -1;
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", user.getId());
        if (user.getUsername() != null) updateWrapper.set("username", user.getUsername());
        if (user.getPassword() != null) updateWrapper.set("password", user.getPassword());
        if (user.getRole() != null) updateWrapper.set("role", user.getRole());
        return userMapper.update(null, updateWrapper);
    }

    @Override
    public User getUserById(String id) {
        //这个方法用于获取用户信息，需要去掉密码内容
        User user = userMapper.selectById(id);
        user.setPassword(null);
        return user;
    }

    @Override
    public List<User> getUsers(User user) {
        if (user == null) return userMapper.selectList(null);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (user.getUsername() != null) queryWrapper.like("username", user.getUsername());
        if (user.getRole() != null) queryWrapper.eq("role", user.getRole());
        return userMapper.selectList(queryWrapper);
    }

    @Override
    public Page<User> getUsersPage(int pageNo, int pageSize, User user) {
        Page<User> page = new Page<>(pageNo, pageSize);
        if (user == null) return userMapper.selectPage(page, null);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (user.getUsername() != null) queryWrapper.like("username", user.getUsername());
        return userMapper.selectPage(page, queryWrapper);
    }
}
