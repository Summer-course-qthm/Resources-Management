package com.example.ResourcesManagement.viewController;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
@Controller
public class ViewUserController
{
    @GetMapping("/user-home") // Đường dẫn khớp với URL trên trình duyệt
    public String showUserHome() {
        return "user-home";
    }
}
