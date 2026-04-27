package com.ebike.controller;

import com.ebike.common.Response;
import com.ebike.entity.User;
import com.ebike.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/list")
    public Response<Map<String, Object>> getUserList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<User> users = userService.getAllUsers();
        int total = users.size();
        Map<String, Object> result = new HashMap<>();
        if (total == 0) {
            result.put("data", users);
            result.put("total", 0);
            result.put("page", page);
            result.put("size", size);
            return Response.success(result);
        }
        int start = (page - 1) * size;
        int end = Math.min(start + size, total);
        if (start >= total) {
            start = 0;
        }
        List<User> pageData = users.subList(start, end);
        result.put("data", pageData);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        return Response.success(result);
    }

    @GetMapping("/{id}")
    public Response<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user != null) {
            return Response.success(user);
        }
        return Response.error("用户不存在");
    }

    @PostMapping("/add")
    public Response<String> addUser(@RequestBody User user) {
        user.setStatus(1);
        boolean result = userService.addUser(user);
        if (result) {
            return Response.success("添加成功");
        }
        return Response.error("添加失败");
    }

    @PutMapping("/update")
    public Response<String> updateUser(@RequestBody User user) {
        boolean result = userService.updateUser(user);
        if (result) {
            return Response.success("更新成功");
        }
        return Response.error("更新失败");
    }

    @DeleteMapping("/{id}")
    public Response<String> deleteUser(@PathVariable Long id) {
        boolean result = userService.deleteUser(id);
        if (result) {
            return Response.success("删除成功");
        }
        return Response.error("删除失败");
    }
}
