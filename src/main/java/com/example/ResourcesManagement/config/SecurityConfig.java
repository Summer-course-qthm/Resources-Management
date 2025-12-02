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
                        // === 1. API CÔNG KHAI ===
                        .requestMatchers("/register", "/login", "/viewRegister", "/viewLogin", "/css/**", "/js/**", "/images/**").permitAll()

                        // === 2. CHỨC NĂNG DÙNG CHUNG (ADMIN + USER) ===

                        // -- Thiết bị --
                        .requestMatchers(HttpMethod.GET, "/viewDevices", "/devices").hasAnyRole("ADMIN", "USER")

                        // -- Phòng ban (Chapter) --
                        // User được phép xem danh sách phòng ban
                        .requestMatchers(HttpMethod.GET, "/viewChapters").hasAnyRole("ADMIN", "USER")

                        // === 3. CHỨC NĂNG USER ===
                        .requestMatchers(HttpMethod.POST, "/request-device").hasRole("USER")

                        // === 4. CHỨC NĂNG QUẢN TRỊ (CHỈ ADMIN) ===
                        .requestMatchers("/dashboardController").hasRole("ADMIN")

                        // -- Quản lý Thiết bị --
                        .requestMatchers("/devices/add", "/devices/edit/**", "/devices/delete/**", "/saveDevice").hasRole("ADMIN")

                        // -- Quản lý Nhân viên --
                        .requestMatchers("/viewEmployees", "/employees/add", "/employees/edit/**", "/employees/delete/**", "/saveEmployee").hasRole("ADMIN")
                        .requestMatchers("/user/**").hasRole("ADMIN")

                        // -- Quản lý Chapter (Thêm/Sửa/Xóa) --
                        // Các URL thêm sửa xóa trong ViewChapterController của bạn:
                        // /chapter/add, /chapter/save (để thêm)
                        // /chapters/edit/{id} (để sửa)
                        .requestMatchers("/chapter/add", "/chapter/save").hasRole("ADMIN")
                        .requestMatchers("/chapters/edit/**", "/chapters/delete/**").hasRole("ADMIN")
                        // Note: Tôi thêm /chapters/delete/** phòng trường hợp bạn thêm chức năng xóa sau này
                        // Giữ lại dòng cũ nếu bạn vẫn dùng controller cũ
                        .requestMatchers("/viewDepartments").hasRole("ADMIN")

                        // -- Quản lý Yêu cầu --
                        .requestMatchers("/viewRequests").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/check-stock").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/request-device").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/request-device/**").hasRole("ADMIN")

                        // -- Checklist --
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