package com.example.ResourcesManagement.config;

import com.example.ResourcesManagement.utils.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Tắt CSRF để đơn giản hóa cho API và Form
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Sử dụng Stateless vì dùng JWT
                .authorizeHttpRequests(auth -> auth
                        // === 1. PUBLIC (Không cần đăng nhập) ===
                        .requestMatchers(
                                "/login", "/viewLogin",
                                "/register", "/viewRegister",
                                "/logout",
                                "/css/**", "/js/**", "/images/**" // Cho phép tài nguyên tĩnh
                        ).permitAll()

                        // === 2. TRANG DÀNH RIÊNG CHO USER (Và Admin cũng vào được để test) ===
                        .requestMatchers("/user-home").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/request-device/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/my-requests").hasAnyRole("USER", "ADMIN")

                        // === 3. CHỨC NĂNG DÙNG CHUNG (User xem được danh sách) ===
                        .requestMatchers(HttpMethod.GET, "/viewDevices", "/devices").hasAnyRole("ADMIN", "USER")
                        .requestMatchers(HttpMethod.GET, "/viewChapters", "/viewDepartments").hasAnyRole("ADMIN", "USER")

                        // === 4. TRANG QUẢN TRỊ (CHỈ ADMIN) ===
                        // Dashboard
                        .requestMatchers("/dashboardController").hasRole("ADMIN")

                        // Quản lý nhân viên
                        .requestMatchers("/viewEmployees", "/employees/**", "/user/**").hasRole("ADMIN")

                        // Quản lý thiết bị (Thêm/Sửa/Xóa)
                        .requestMatchers("/devices/add", "/devices/edit/**", "/devices/delete/**", "/saveDevice").hasRole("ADMIN")

                        // Quản lý phòng ban (Thêm/Sửa/Xóa)
                        .requestMatchers("/chapter/**", "/chapters/**").hasRole("ADMIN")

                        // Quản lý yêu cầu (Duyệt/Từ chối)
                        .requestMatchers(HttpMethod.GET,"/viewRequests").hasRole("ADMIN")
                        .requestMatchers("/api/request/**").hasRole("ADMIN") // Các API duyệt, từ chối, check-stock

                        // Checklist
                        .requestMatchers("/checklist/**", "/checkListItem/**").hasRole("ADMIN")

                        // === 5. MẶC ĐỊNH: BẮT BUỘC ĐĂNG NHẬP ===
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}