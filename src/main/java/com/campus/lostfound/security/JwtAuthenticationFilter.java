package com.campus.lostfound.security;

import com.campus.lostfound.config.AppProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final AntPathMatcher matcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String path = stripContext(request);
        String method = request.getMethod();
        try {
            if (isPublic(path, method)) {
                filterChain.doFilter(request, response);
                return;
            }

            if (path.startsWith("/admin/")) {
                String token = bearer(request);
                if (token == null) {
                    response.setStatus(401);
                    return;
                }
                final Claims claims;
                try {
                    claims = jwtService.parseAdminToken(token);
                } catch (JwtException e) {
                    response.setStatus(401);
                    return;
                }
                if (!"admin".equals(claims.get("typ"))) {
                    response.setStatus(401);
                    return;
                }
                UserContext.setUserId(Long.parseLong(claims.getSubject()));
                filterChain.doFilter(request, response);
                return;
            }

            if (path.startsWith("/api/")) {
                String token = bearer(request);
                if (token == null) {
                    response.setStatus(401);
                    return;
                }
                final Claims claims;
                try {
                    claims = jwtService.parseUserToken(token);
                } catch (JwtException e) {
                    response.setStatus(401);
                    return;
                }
                if (!"user".equals(claims.get("typ"))) {
                    response.setStatus(401);
                    return;
                }
                UserContext.setUserId(Long.parseLong(claims.getSubject()));
            }

            filterChain.doFilter(request, response);
        } finally {
            UserContext.clear();
        }
    }

    private String stripContext(HttpServletRequest request) {
        String path = request.getRequestURI();
        String ctx = request.getContextPath();
        if (ctx != null && !ctx.isEmpty() && path.startsWith(ctx)) {
            path = path.substring(ctx.length());
        }
        return path;
    }

    private String bearer(HttpServletRequest request) {
        String h = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (h != null && h.startsWith("Bearer ")) {
            return h.substring(7);
        }
        return null;
    }

    private boolean isPublic(String path, String method) {
        if (path.startsWith("/api/auth/")) return true;
        if (path.startsWith("/admin/auth/")) return true;
        if (path.startsWith("/uploads/")) return true;
        // 微信登录前需上传头像，此时用户尚无 token
        if (HttpMethod.POST.matches(method) && path.startsWith("/api/upload")) return true;
        if (HttpMethod.OPTIONS.matches(method)) return true;
        if (HttpMethod.GET.matches(method)) {
            if (matcher.match("/api/categories", path)) return true;
            if (matcher.match("/api/categories/**", path)) return true;
            if (matcher.match("/api/items", path)) return true;
            if (matcher.match("/api/items/*", path)) {
                // 列表、详情可匿名；「我的」及子路径需登录
                return !path.startsWith("/api/items/mine");
            }
            if (matcher.match("/api/announcements", path)) return true;
            if (matcher.match("/api/announcements/*", path)) return true;
        }
        return false;
    }
}
