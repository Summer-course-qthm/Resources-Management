package com.example.ResourcesManagement.viewController;

import com.example.ResourcesManagement.DTO.response.DeviceResponseDTO;
import com.example.ResourcesManagement.controller.DeviceController;
import com.example.ResourcesManagement.entity.DevicesEntity;
import com.example.ResourcesManagement.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Controller
public class ViewDeviceController {
    @Autowired
     private DeviceController DeviceController;

    @Autowired
    private DeviceService deviceService;
    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/viewDevices")
    public String viewDevices(Model model) {
        List<DeviceResponseDTO> devices = deviceService.getAllDevices();
        model.addAttribute("devices", devices);
        return "index";
    }

}
