package com.example.ResourcesManagement.viewController;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewLogOutController {
    @GetMapping("/logout")
    public String logout(HttpServletResponse response) {
        // 1. Tạo một cookie mới trùng tên với cookie đăng nhập
        Cookie cookie = new Cookie("JWT_TOKEN", null);

        // 2. Cấu hình giống hệt cookie cũ (quan trọng là Path)
        cookie.setPath("/");
        cookie.setHttpOnly(true);

        // 3. Quan trọng nhất: Đặt tuổi thọ = 0 để trình duyệt xóa nó ngay lập tức
        cookie.setMaxAge(0);

        // 4. Gửi yêu cầu xóa cookie về trình duyệt
        response.addCookie(cookie);

        // 5. Chuyển hướng về trang đăng nhập
        return "redirect:/viewLogin";
    }

}
