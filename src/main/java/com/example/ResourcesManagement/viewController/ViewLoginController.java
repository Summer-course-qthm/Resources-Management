package com.example.ResourcesManagement.viewController;

import com.example.ResourcesManagement.DTO.request.LoginResquestDTO;
import com.example.ResourcesManagement.DTO.response.UserResponseDTO; // Nhớ import cái này
import com.example.ResourcesManagement.entity.UserEntity;
import com.example.ResourcesManagement.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession; // Import Session
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ViewLoginController {

    @Autowired
    UserService userService;

    @GetMapping("/viewLogin")
    public String loginUser(Model model) {
        model.addAttribute("user", new LoginResquestDTO());
        return "login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute("user") LoginResquestDTO userDTO,
                        HttpServletResponse response,
                        HttpSession session) { // [1] Thêm biến Session vào đây
        try {
            // 1. Xác thực và lấy Token (Giữ nguyên logic cũ của bạn)
            String token = userService.login(userDTO);

            // 2. Tạo Cookie (Giữ nguyên)
            Cookie jwtCookie = new Cookie("JWT_TOKEN", token);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setPath("/");
            jwtCookie.setMaxAge(60 * 60);
            response.addCookie(jwtCookie);

            // 3. Lấy thông tin User từ Database
            UserEntity currentUserEntity = userService.getUserByUsername(userDTO.getUsername());

            // [2] --- QUAN TRỌNG: CHUYỂN ĐỔI VÀ LƯU VÀO SESSION ---
            // Vì bên Controller duyệt đơn bạn ép kiểu về UserResponseDTO,
            // nên ở đây phải lưu đúng kiểu đó.
            UserResponseDTO sessionUser = UserResponseDTO.builder()
                    .id(currentUserEntity.getId())           // Quan trọng nhất: ID không được null
                    .username(currentUserEntity.getUsername())
                    .chapterName(currentUserEntity.getChapter() != null ? currentUserEntity.getChapter().getName() : "")
                    .build();

            // Lưu vào session với key là "user"
            session.setAttribute("user", sessionUser);
            // -----------------------------------------------------

            // 4. Phân luồng
            if ("ADMIN".equalsIgnoreCase(currentUserEntity.getRole())) {
                System.out.println("Login success: ADMIN (ID: " + sessionUser.getId() + ")");
                return "redirect:/dashboardController"; // Hoặc trang admin của bạn
            } else {
                System.out.println("Login success: USER (ID: " + sessionUser.getId() + ")");
                return "redirect:/user-home";
            }

        } catch (Exception e) {
            e.printStackTrace(); // In lỗi ra xem cho dễ
            return "redirect:/viewLogin?error";
        }
    }
}