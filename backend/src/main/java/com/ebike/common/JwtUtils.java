package com.ebike.common;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class JwtUtils {

    private static final String SECRET_STRING = "ebike_shared_electric_vehicle_system_secret_key_2024";
    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(SECRET_STRING.getBytes(StandardCharsets.UTF_8));
    private static final long EXPIRE_TIME = 24 * 60 * 60 * 1000;

    private static final Map<String, Boolean> BLACKLIST = new ConcurrentHashMap<>();

    public static String generateToken(Long userId, String username, Integer role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("role", role);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE_TIME))
                .signWith(SECRET_KEY)
                .compact();
    }

    public static Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public static Long getUserId(String token) {
        Claims claims = getClaimsFromToken(token);
        return Long.valueOf(claims.get("userId").toString());
    }

    public static String getUsername(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getSubject();
    }

    public static Integer getRole(String token) {
        Claims claims = getClaimsFromToken(token);
        Object role = claims.get("role");
        if (role instanceof Integer) {
            return (Integer) role;
        }
        return Integer.valueOf(role.toString());
    }

    public static boolean isTokenExpired(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            Date expiration = claims.getExpiration();
            return expiration.before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    public static boolean validateToken(String token) {
        if (BLACKLIST.containsKey(token)) {
            return false;
        }
        return !isTokenExpired(token);
    }

    public static void addToBlacklist(String token) {
        BLACKLIST.put(token, true);
    }

    public static boolean isBlacklisted(String token) {
        return BLACKLIST.containsKey(token);
    }
}
