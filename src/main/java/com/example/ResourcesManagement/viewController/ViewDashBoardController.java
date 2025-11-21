package com.example.ResourcesManagement.viewController;

import com.example.ResourcesManagement.DTO.response.DeviceResponseDTO;
import com.example.ResourcesManagement.service.ChapterService;
import com.example.ResourcesManagement.service.DeviceService;
import com.example.ResourcesManagement.service.RequestDeviceService;
import com.example.ResourcesManagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ViewDashBoardController {
    @Autowired
    UserService userService;

    @Autowired
    DeviceService deviceService;

    @Autowired
    ChapterService chapterService;

    @Autowired
    RequestDeviceService requestDeviceService;

    // thống kê số lượng user, device, chapter, requestDevice
    @GetMapping("/dashboardController")
    public String viewDashboard(Model model) {
        long userCount = userService.countUsers();
        long deviceCount = deviceService.countDevices();
        long chapterCount = chapterService.countChapters();
        long requestDeviceCount = requestDeviceService.countRequestDevices();

        model.addAttribute("userCount", userCount);
        model.addAttribute("deviceCount", deviceCount);
        model.addAttribute("chapterCount", chapterCount);
        model.addAttribute("requestDeviceCount", requestDeviceCount);

        return "dashboard";
    }



}
