package com.ebike.service;

import com.ebike.entity.User;
import java.util.List;

public interface UserService {
    User login(String username, String password);
    User getUserById(Long id);
    String register(String username, String password, String phone, Integer role);
    List<User> getOperators();
    List<User> getAllUsers();
    boolean updateUser(User user);
    boolean deleteUser(Long id);
    long getUserCount();
    boolean addUser(User user);
}
