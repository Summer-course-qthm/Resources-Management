package com.example.ResourcesManagement.utils;

import com.example.ResourcesManagement.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie; // Nhớ import Cookie
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

    // Hàm hỗ trợ để lấy token từ Header HOẶC Cookie
    private String getTokenFromRequest(HttpServletRequest request) {
        // 1. Ưu tiên lấy từ Header (cho Postman/Mobile App)
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        // 2. Nếu không có trong Header, tìm trong Cookie (cho Web/Thymeleaf)
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("JWT_TOKEN".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // Sử dụng hàm hỗ trợ để lấy token
        final String jwt = getTokenFromRequest(request);
        final String username;

        // Nếu không tìm thấy token ở đâu cả, cho qua để các filter khác xử lý (hoặc chặn lại sau)
        if (jwt == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // Nếu có token, tiến hành giải mã và xác thực
        try {
            username = jwtService.extractUsername(jwt);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                if (jwtService.isTokenValid(jwt, userDetails.getUsername())) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Nếu token lỗi hoặc hết hạn, không làm gì cả, cứ để request đi tiếp (sẽ bị chặn bởi SecurityConfig)
            // Bạn có thể log lỗi ở đây nếu cần
        }

        filterChain.doFilter(request, response);
    }
}