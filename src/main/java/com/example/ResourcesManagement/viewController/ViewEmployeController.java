package com.example.ResourcesManagement.viewController;

import com.example.ResourcesManagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewEmployeController {

    // lấy danh sách nhân viên
    @Autowired
    UserService userService;

    @GetMapping("/viewEmployees") // cái api ni ch config trong security để ai cũng truy cập đc
    public String viewEmployees(Model model) {
        model.addAttribute("employees", userService.getListUser());
        return "employee";
    }

}
