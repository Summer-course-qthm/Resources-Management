package com.example.ResourcesManagement.viewController;

import com.example.ResourcesManagement.service.DeviceHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewHistoryDeviveController {
    @Autowired
    DeviceHistoryService deviceHistoryService;

    // lấy về trang lịch sử thiết bị
    @GetMapping("/viewDeviceHistory")
    public String viewDeviceHistory(Model model) {
        model.addAttribute("deviceHistoryList",deviceHistoryService.getAllDeviceHistory());

        return "device-history";
    }
}
