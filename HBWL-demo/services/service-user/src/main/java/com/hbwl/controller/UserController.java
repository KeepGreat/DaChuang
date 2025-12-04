package com.hbwl.controller;

import com.hbwl.common.Result;
import com.hbwl.pojo.User;
import com.hbwl.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public Result register(@RequestBody User user) {
        int row = userService.addUser(user);
        if (row == -1) return Result.error("参数不能为空");
        if (row == 0) return Result.error("注册用户失败");
        return Result.success("注册用户成功");
    }

    @DeleteMapping("/{id}")
    public Result deleteUser(@PathVariable("id")String id,
                             @RequestHeader("role") String role){
        if (!role.equals("admin")) return Result.error("权限不足");
        int row = userService.deleteUserById(id);
        if (row == -1) return Result.error("参数不能为空");
        if (row == 0) return Result.error("删除用户失败");
        return Result.success("删除用户成功");
    }

    @PutMapping
    public Result updateUser(@RequestBody User user,
                             @RequestHeader("role") String role){
        if (!role.equals("admin")) return Result.error("权限不足");
        int row = userService.updateUserById(user);
        if (row == -1) return Result.error("参数不能为空");
        if (row == 0) return Result.error("更新用户失败");
        return Result.success("更新用户成功");
    }

    @GetMapping
    public Result getUsers(@RequestParam(required = false) String id,
                               @RequestParam(required = false) String username,
                               @RequestParam(required = false) String userRole,
                               @RequestHeader("role") String role){
        if (!role.equals("admin")) return Result.error("权限不足");
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setRole(userRole);
        return Result.success(userService.getUsers(user), "查询用户成功");
    }
}
