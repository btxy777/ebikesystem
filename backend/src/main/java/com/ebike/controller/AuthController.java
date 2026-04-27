package com.ebike.controller;

import com.ebike.common.JwtUtils;
import com.ebike.common.Response;
import com.ebike.entity.User;
import com.ebike.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Response<Map<String, Object>> login(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");

        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return Response.error("用户名和密码不能为空");
        }

        User user = userService.login(username, password);
        if (user != null) {
            if (user.getStatus() != null && user.getStatus() == 0) {
                return Response.error("账号已被禁用");
            }

            String token = JwtUtils.generateToken(user.getId(), user.getUsername(), user.getRole());

            Map<String, Object> result = new HashMap<>();
            result.put("id", user.getId());
            result.put("username", user.getUsername());
            result.put("phone", user.getPhone());
            result.put("role", user.getRole());
            result.put("roleName", getRoleName(user.getRole()));
            result.put("status", user.getStatus());
            result.put("token", token);
            return Response.success(result);
        } else {
            return Response.error("用户名或密码错误");
        }
    }

    @PostMapping("/logout")
    public Response<Map<String, Object>> logout(@RequestHeader("Authorization") String token) {
        try {
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                JwtUtils.addToBlacklist(token);
                Map<String, Object> result = new HashMap<>();
                result.put("message", "登出成功");
                return Response.success(result);
            }
            return Response.error("无效的Token");
        } catch (Exception e) {
            return Response.error("登出失败");
        }
    }

    @PostMapping("/register")
    public Response<Map<String, Object>> register(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");
        String phone = params.get("phone");
        String roleStr = params.get("role");

        if (username == null || username.trim().isEmpty()) {
            return Response.error("用户名不能为空");
        }
        if (password == null || password.trim().isEmpty()) {
            return Response.error("密码不能为空");
        }
        if (phone == null || phone.trim().isEmpty()) {
            return Response.error("手机号不能为空");
        }

        if (!phone.matches("^1[3-9]\\d{9}$")) {
            return Response.error("手机号格式不正确");
        }

        if (username.trim().length() < 3 || username.trim().length() > 20) {
            return Response.error("用户名长度必须在3-20个字符之间");
        }

        if (password.trim().length() < 6 || password.trim().length() > 20) {
            return Response.error("密码长度必须在6-20个字符之间");
        }

        Integer role = 1;
        if (roleStr != null && !roleStr.trim().isEmpty()) {
            try {
                role = Integer.valueOf(roleStr);
            } catch (NumberFormatException e) {
                return Response.error("角色格式错误");
            }
            if (role != 1) {
                return Response.error("只能注册普通用户");
            }
        }

        String result = userService.register(username, password, phone, role);
        if ("success".equals(result)) {
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("message", "注册成功");
            return Response.success(resultMap);
        } else {
            return Response.error(result);
        }
    }

    @GetMapping("/info")
    public Response<Map<String, Object>> getUserInfo(@RequestHeader("Authorization") String token) {
        try {
            token = token.substring(7);
            Long userId = JwtUtils.getUserId(token);
            User user = userService.getUserById(userId);
            if (user != null) {
                Map<String, Object> result = new HashMap<>();
                result.put("id", user.getId());
                result.put("username", user.getUsername());
                result.put("phone", user.getPhone());
                result.put("role", user.getRole());
                result.put("roleName", getRoleName(user.getRole()));
                result.put("status", user.getStatus());
                result.put("createTime", user.getCreateTime());
                return Response.success(result);
            } else {
                return Response.error("用户不存在");
            }
        } catch (Exception e) {
            return Response.error("Token解析失败");
        }
    }

    @GetMapping("/operators")
    public Response<Map<String, Object>> getOperators(
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            token = token.substring(7);
            Integer role = JwtUtils.getRole(token);
            if (role != 3) {
                return Response.error("只有管理员可以查看运维人员列表");
            }
            List<User> operators = userService.getOperators();
            operators.forEach(u -> u.setPassword(null));
            int total = operators.size();
            Map<String, Object> result = new HashMap<>();
            if (total == 0) {
                result.put("data", operators);
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
            List<User> pageData = operators.subList(start, end);
            result.put("data", pageData);
            result.put("total", total);
            result.put("page", page);
            result.put("size", size);
            return Response.success(result);
        } catch (Exception e) {
            return Response.error("Token解析失败");
        }
    }

    private String getRoleName(Integer role) {
        if (role == null) return "未知";
        switch (role) {
            case 1: return "用户";
            case 2: return "运维";
            case 3: return "管理员";
            default: return "未知";
        }
    }
}
