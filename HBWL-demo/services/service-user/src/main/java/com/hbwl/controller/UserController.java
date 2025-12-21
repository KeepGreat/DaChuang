package com.hbwl.controller;

import com.hbwl.pojo.User;
import com.hbwl.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public String register(@RequestBody User user) {
        int row = userService.addUser(user);
        if (row == -1) return "参数不能为空";
        if (row == 0) return "注册用户失败";
        return "注册用户成功";
    }

    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable("id")UUID id){
        int row = userService.deleteUserById(id);
        if (row == -1) return "参数不能为空";
        if (row == 0) return "删除用户失败";
        return "删除用户成功";
    }

    @PutMapping
    public String updateUser(@RequestBody User user){
        int row = userService.updateUserById(user);
        if (row == -1) return "参数不能为空";
        if (row == 0) return "更新用户失败";
        return "更新用户成功";
    }

    @GetMapping
    public List<User> getUsers(@RequestParam(required = false) String id,
                               @RequestParam(required = false) String username){
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        return userService.getUsers(user);
    }
}
