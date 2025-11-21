package com.example.ResourcesManagement.viewController;

import com.example.ResourcesManagement.DTO.request.LoginResquestDTO;
import com.example.ResourcesManagement.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller // Đảm bảo dùng @Controller
public class ViewLoginController {

    @Autowired
    UserService userService;

    @GetMapping("/viewLogin")
    public String loginUser(Model model) {
        model.addAttribute("user", new LoginResquestDTO());
        return "login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute("user") LoginResquestDTO user, HttpServletResponse response) {
        try {
            // 1. Lấy token từ service
            String token = userService.login(user);
            // 2. Tạo một Cookie mới chứa token
            // "JWT_TOKEN" là tên do mình tự đặt
            Cookie jwtCookie = new Cookie("JWT_TOKEN", token);
            // 3. Cấu hình Cookie (Quan trọng)
            jwtCookie.setHttpOnly(true);  // Tăng bảo mật: JavaScript không thể đọc được cookie này (chống XSS)
            jwtCookie.setPath("/");       // Cookie có hiệu lực trên toàn bộ trang web (tất cả đường dẫn)
            jwtCookie.setMaxAge(60 * 60); // Thời gian sống: 1 giờ (3600 giây) - nên khớp với thời gian hết hạn của token trong JwtService
            // 4. Gửi Cookie về cho trình duyệt
            response.addCookie(jwtCookie);
            // 5. Chuyển hướng sang trang chủ hoặc trang dashboard
            return "redirect:/dashboardController"  ; // chuyển qua controller bên thết bị

        } catch (Exception e) {
            // Nếu đăng nhập thất bại, quay lại trang login và báo lỗi (có thể thêm param ?error)
            return "redirect:/viewLogin?error";
        }
    }
}