package com.example.ResourcesManagement.viewController;

import com.example.ResourcesManagement.DTO.request.LoginResquestDTO;
import com.example.ResourcesManagement.entity.UserEntity;
import com.example.ResourcesManagement.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.net.SocketOption;

@Controller
public class ViewLoginController {

    @Autowired
    UserService userService;

    // ❌ ĐÃ XÓA: UserRepository (Không inject Repository vào Controller nữa)

    @GetMapping("/viewLogin")
    public String loginUser(Model model) {
        model.addAttribute("user", new LoginResquestDTO());
        return "login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute("user") LoginResquestDTO userDTO, HttpServletResponse response) {
        try {
            // 1. Xác thực và lấy Token
            String token = userService.login(userDTO);

            // 2. Tạo Cookie
            Cookie jwtCookie = new Cookie("JWT_TOKEN", token);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setPath("/");
            jwtCookie.setMaxAge(60 * 60);
            response.addCookie(jwtCookie);

            // 3. --- LOGIC PHÂN LUỒNG ---

            // ✅ SỬA LẠI: Gọi qua Service chứ không gọi Repo trực tiếp
            UserEntity currentUser = userService.getUserByUsername(userDTO.getUsername());

            if ("ADMIN".equalsIgnoreCase(currentUser.getRole())) {
                // in ra console để kiểm tra
                System.out.println("Đăng nhập với vai trò ADMIN");
                return "redirect:/dashboardController";
            } else {
                System.out.println("Đăng nhập với vai trò USER");
                return "user-home";
            }

        } catch (Exception e) {
            return "redirect:/viewLogin?error";
        }
    }
}