package com.example.ResourcesManagement.viewController;

import com.example.ResourcesManagement.DTO.request.CreateUserRequestDTO;
import com.example.ResourcesManagement.service.UserService;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

@Controller
public class ViewRegisterController {
    @Autowired
    UserService userService;

    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/viewRegister")
    public  String createUser(Model model) {
        model.addAttribute("user", new CreateUserRequestDTO());
            return "register";
    }

    @PostMapping("/register")
    public String  registerUser(@ModelAttribute("user") CreateUserRequestDTO  user) {
        userService.createUser(user);
        return "login";
    }
}
