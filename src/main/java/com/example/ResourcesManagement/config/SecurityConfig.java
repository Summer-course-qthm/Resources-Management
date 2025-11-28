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
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // === 1. API CÔNG KHAI (Không cần đăng nhập) ===
                        .requestMatchers("/register", "/login", "/viewRegister", "/viewLogin", "/css/**", "/js/**", "/images/**").permitAll()

                        // === 2. CHỨC NĂNG DÙNG CHUNG (ADMIN + USER) ===
                        // Xem danh sách thiết bị (Theo yêu cầu: User được xem)
                        .requestMatchers(HttpMethod.GET, "/viewDevices").hasAnyRole("ADMIN", "USER")
                        // API lấy dữ liệu thiết bị (nếu trang web gọi AJAX)
                        .requestMatchers(HttpMethod.GET, "/devices").hasAnyRole("ADMIN", "USER")

                        // === 3. CHỨC NĂNG CỦA USER (ADMIN KHÔNG DÙNG) ===
                        // Gửi yêu cầu mượn thiết bị (User cần quyền này để thao tác, nếu bạn muốn chặn luôn thì xóa dòng này)
                        .requestMatchers(HttpMethod.POST, "/request-device").hasRole("USER")

                        // === 4. CHỨC NĂNG QUẢN TRỊ (CHỈ ADMIN) ===

                        // --- Dashboard ---
                        .requestMatchers("/dashboardController").hasRole("ADMIN")

                        // --- Quản lý Thiết bị (Thêm, Sửa, Xóa) ---
                        .requestMatchers("/devices/add", "/devices/edit/**", "/devices/delete/**").hasRole("ADMIN") // Form giao diện
                        .requestMatchers("/saveDevice").hasRole("ADMIN") // Action lưu

                        // --- Quản lý Nhân viên (Thêm, Sửa, Xóa, Xem danh sách) ---
                        .requestMatchers("/viewEmployees").hasRole("ADMIN")
                        .requestMatchers("/employees/add", "/employees/edit/**", "/employees/delete/**").hasRole("ADMIN") // Form giao diện
                        .requestMatchers("/saveEmployee").hasRole("ADMIN") // Action lưu

                        // --- Các API Quản trị User (Backend) ---
                        .requestMatchers("/user/listUser", "/user/delete/**", "/user/update/**", "/user/removeFromChapter/**").hasRole("ADMIN")

                        // --- Quản lý Chapter ---
                        .requestMatchers("/viewDepartments").hasRole("ADMIN")
                        .requestMatchers("/chapter/**").hasRole("ADMIN")

                        // --- Quản lý Yêu cầu (Duyệt/Xóa) ---
                        .requestMatchers("/viewRequests").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/check-stock").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/request-device").hasRole("ADMIN") // Xem danh sách yêu cầu
                        .requestMatchers(HttpMethod.DELETE, "/request-device/**").hasRole("ADMIN")

                        // --- Quản lý Checklist ---
                        .requestMatchers("/checklist/**", "/checkListItem/**").hasRole("ADMIN")

                        // === 5. QUY TẮC CUỐI CÙNG ===
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