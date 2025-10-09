package com.example.ResourcesManagement.config;


import com.example.ResourcesManagement.utils.JwtAuthenticationFilter;
import jakarta.servlet.Filter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration // đanh dấu đây là một class cấu hình
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    // Cho phép các API nhất định mà không cần login
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        
        http.csrf(csrf -> csrf.disable()) // tắt CSRF để test bằng Postman
                .authorizeHttpRequests(auth -> auth
                        //api nào không cần login
                        .requestMatchers("/register", "/login").permitAll() // mở quyền

                        // api chỉ có admin mới được truy cập
                        .requestMatchers(HttpMethod.GET, "/user/listUser").hasRole("ADMIN") // chỉ admin mới được truy cập
                        .anyRequest().authenticated() // còn lại yêu cầu JWT/token
                        
                )

                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        
        ;
        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
