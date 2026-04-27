package com.ebike.service.impl;

import com.ebike.entity.User;
import com.ebike.mapper.UserMapper;
import com.ebike.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User login(String username, String password) {
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            return null;
        }
        if (!password.equals(user.getPassword())) {
            return null;
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            return null;
        }
        return user;
    }

    @Override
    public User getUserById(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    public String register(String username, String password, String phone, Integer role) {
        User existUser = userMapper.selectByUsername(username);
        if (existUser != null) {
            return "用户名已存在";
        }

        User existPhone = userMapper.selectByPhone(phone);
        if (existPhone != null) {
            return "手机号存在";
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setPhone(phone);
        user.setRole(role);
        user.setStatus(1);
        user.setCreateTime(new Date());

        int result = userMapper.insert(user);
        if (result > 0) {
            return "success";
        } else {
            return "注册失败";
        }
    }

    @Override
    public List<User> getOperators() {
        return userMapper.selectByRole(2);
    }

    @Override
    public List<User> getAllUsers() {
        return userMapper.selectAll();
    }

    @Override
    public boolean updateUser(User user) {
        return userMapper.update(user) > 0;
    }

    @Override
    public boolean deleteUser(Long id) {
        return userMapper.deleteById(id) > 0;
    }

    @Override
    public long getUserCount() {
        return userMapper.selectCount();
    }

    @Override
    public boolean addUser(User user) {
        if (user.getCreateTime() == null) {
            user.setCreateTime(new Date());
        }
        return userMapper.insert(user) > 0;
    }
}
