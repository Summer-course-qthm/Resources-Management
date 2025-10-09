package com.example.ResourcesManagement.utils;

import com.example.ResourcesManagement.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
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

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        // 1. Kiểm tra xem header Authorization có hợp lệ không
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response); // Nếu không hợp lệ, cho qua để các filter khác xử lý
            return;
        }

        // 2. Trích xuất token và username
        jwt = authHeader.substring(7);
        username = jwtService.extractUsername(jwt);

        // 3. Kiểm tra user hợp lệ và chưa được xác thực trong SecurityContext
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Tải thông tin user từ database
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // 4. Nếu token hợp lệ, tiến hành xác thực
            if (jwtService.isTokenValid(jwt, userDetails.getUsername())) {
                // Tạo đối tượng xác thực
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities() // Gán quyền (role) cho user
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 5. Lưu thông tin xác thực vào SecurityContextHolder
                // Đây là bước quan trọng nhất, báo cho Spring Security biết user này đã hợp lệ
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        // 6. Chuyển request đi tiếp
        filterChain.doFilter(request, response);
    }
}