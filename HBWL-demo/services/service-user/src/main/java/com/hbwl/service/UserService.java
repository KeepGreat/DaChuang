package com.hbwl.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hbwl.pojo.User;

import java.util.List;
import java.util.UUID;

public interface UserService {

    int addUser(User user);

    int deleteUserById(UUID id);

    int updateUserById(User user);

    User getUserById(UUID id);

    List<User> getUsers(User user);

    Page<User> getUsersPage(int pageNo, int pageSize, User user);
}
