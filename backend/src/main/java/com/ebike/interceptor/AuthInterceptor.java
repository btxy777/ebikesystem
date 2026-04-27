package com.ebike.interceptor;

import com.ebike.common.JwtUtils;
import com.ebike.common.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private static final String TOKEN_HEADER = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }

        String uri = request.getRequestURI();
        System.out.println("=== Auth Interceptor ===");
        System.out.println("URI: " + uri);

        if ("/api/auth/login".equals(uri) || "/api/auth/logout".equals(uri) || "/api/auth/register".equals(uri)) {
            System.out.println("Skipping auth for login/logout/register");
            return true;
        }

        String token = request.getHeader(TOKEN_HEADER);
        System.out.println("Token header: " + (token != null ? "present" : "null"));

        if (token == null || !token.startsWith(TOKEN_PREFIX)) {
            System.out.println("No token or invalid format");
            sendUnauthorizedResponse(response, "未登录或Token无效");
            return false;
        }

        token = token.substring(TOKEN_PREFIX.length());
        System.out.println("Token (after prefix removal): " + token.substring(0, Math.min(20, token.length())) + "...");

        if (JwtUtils.isBlacklisted(token)) {
            System.out.println("Token is blacklisted");
            sendUnauthorizedResponse(response, "Token已失效，请重新登录");
            return false;
        }

        if (!JwtUtils.validateToken(token)) {
            System.out.println("Token validation failed");
            sendUnauthorizedResponse(response, "Token已过期，请重新登录");
            return false;
        }

        try {
            Integer role = JwtUtils.getRole(token);
            Long userId = JwtUtils.getUserId(token);
            System.out.println("Token valid! role=" + role + ", userId=" + userId);

            if (uri.startsWith("/api/auth/register") || uri.startsWith("/api/auth/operators")) {
                if (role != 3) {
                    sendForbiddenResponse(response, "只有管理员可以操作");
                    return false;
                }
            }

            request.setAttribute("userId", userId);
            request.setAttribute("role", role);
        } catch (Exception e) {
            System.out.println("Error parsing token: " + e.getMessage());
            sendUnauthorizedResponse(response, "Token解析失败");
            return false;
        }

        return true;
    }

    private void sendUnauthorizedResponse(HttpServletResponse response, String message) throws IOException {
        System.out.println("Sending 401 response: " + message);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        Response<?> errorResponse = Response.unauthorized(message);
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(errorResponse);
        System.out.println("Response body: " + json);
        response.getWriter().write(json);
    }

    private void sendForbiddenResponse(HttpServletResponse response, String message) throws IOException {
        System.out.println("Sending 403 response: " + message);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");
        Response<?> errorResponse = Response.error(403, message);
        response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
    }
}
