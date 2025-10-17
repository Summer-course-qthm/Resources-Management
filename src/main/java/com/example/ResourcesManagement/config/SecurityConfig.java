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
                        .requestMatchers("/register", "/login").permitAll()

                        // === 2. API CHỈ DÀNH CHO ADMIN ===
                        // Quản lý User
                        .requestMatchers(HttpMethod.GET, "/user/listUser").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/user/delete/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/user/update/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/user/removeFromChapter/**").hasRole("ADMIN")
                        // Quản lý Device
                        .requestMatchers(HttpMethod.POST, "/devices").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/devices/**").hasRole("ADMIN")
                        // Quản lý Chapter
                        .requestMatchers(HttpMethod.PUT, "/chapter/update/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/chapter/delete/**").hasRole("ADMIN")
                        // Quản lý Checklist
                        .requestMatchers(HttpMethod.POST, "/checklist", "/checkListItem").hasRole("ADMIN")
                        // Quản lý Request (xử lý yêu cầu)
                        .requestMatchers(HttpMethod.PUT, "/check-stock").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/request-device").hasRole("ADMIN")

                        // === 3. API DÀNH CHO USER (Admin không dùng) ===
                        // User gửi yêu cầu mượn thiết bị
                        .requestMatchers(HttpMethod.POST, "/request-device").hasRole("USER")

                        // === 4. API DÙNG CHUNG (Cần đăng nhập, bất kể vai trò) ===
                        .requestMatchers(HttpMethod.GET, "/devices", "/chapters", "/users/byChapter/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/devices/user/**").authenticated()

                        // === 5. QUY TẮC CUỐI CÙNG ===
                        // Bất kỳ request nào khác chưa được định nghĩa ở trên đều yêu cầu phải xác thực
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