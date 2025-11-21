package com.example.ResourcesManagement.viewController;

import com.example.ResourcesManagement.DTO.request.CreateDeviceRequestDTO;
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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    public String viewDevices(Model model, @RequestParam(required = false) String keyword) {
        List<DeviceResponseDTO> devices;

        // Nếu có từ khóa tìm kiếm thì gọi hàm search, ngược lại gọi hàm getAll
        if (keyword != null && !keyword.isEmpty()) {
            devices = deviceService.searchDevices(keyword);
        } else {
            devices = deviceService.getAllDevices();
        }

        model.addAttribute("devices", devices);
        model.addAttribute("keyword", keyword);

        return "devices";

    }

    // thêm thiết bị
    // 1. Hiển thị Form thêm mới (GET)
    @GetMapping("/devices/add")
    public String showAddDeviceForm(Model model) {
        // Tạo đối tượng rỗng để form điền dữ liệu vào
        model.addAttribute("deviceRequest", new CreateDeviceRequestDTO());
        return "createrDevice"; // Trả về file add-device.html
    }

    // 2. Xử lý dữ liệu Form gửi về (POST)
    @PostMapping("/devices/add")
    public String addDevice(@ModelAttribute("deviceRequest") CreateDeviceRequestDTO deviceRequest) {
        // Gọi service để lưu vào database
        deviceService.addDevice(deviceRequest);

        // Lưu xong thì chuyển hướng về trang danh sách
        return "redirect:/viewDevices";
    }

}
