package com.example.ResourcesManagement.viewController;

import com.example.ResourcesManagement.DTO.request.CreateDeviceRequestDTO;
import com.example.ResourcesManagement.DTO.response.DeviceResponseDTO;

import com.example.ResourcesManagement.entity.DevicesEntity;
import com.example.ResourcesManagement.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Controller
public class ViewDeviceController {


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
        return "createrDevice"; // Trả về file createrDevice.html
    }
    // 2. Xử lý dữ liệu Form gửi về (POST)
    @PostMapping("/devices/add")
    public String addDevice(@ModelAttribute("deviceRequest") CreateDeviceRequestDTO deviceRequest) {
        // Gọi service để lưu vào database
        deviceService.addDevice(deviceRequest);
        // Lưu xong thì chuyển hướng về trang danh sách
        return "redirect:/viewDevices";
    }

    // sửa thong tin thiết bị
    // 1. Hiển thị Form sửa (GET)
    @GetMapping("/devices/edit/{id}")
    public String showEditDeviceForm(@PathVariable Long id, Model model) { // ✨ ĐÃ SỬA: Dùng @PathVariable
        // Lấy thông tin thiết bị từ service
        DeviceResponseDTO device = deviceService.getDeviceById(id);

        // Tạo DTO cho form để bind data từ DeviceResponseDTO (Dữ liệu cũ)
        CreateDeviceRequestDTO deviceRequest = new CreateDeviceRequestDTO();
        // Giả sử DTO có các setter tương ứng:
        // Cần chỉnh sửa nếu tên field của bạn khác
        // Bạn cần đảm bảo DTO của bạn có các setters này (hoặc constructor đầy đủ tham số)
        // Nếu không, chỉ cần gán các giá trị
        // VÍ DỤ CỦA CREATE DEVICE REQUEST DTO
        /*
          private String deviceName;
          private String deviceType;
          private String note;
          private String status;
        */
        deviceRequest.setDeviceName(device.getName());
        deviceRequest.setDeviceType(device.getType());
        deviceRequest.setNote(device.getNote());
        deviceRequest.setStatus(device.getStatus());

        // Thêm DTO đã điền dữ liệu vào model
        model.addAttribute("deviceRequest", deviceRequest); // Form createrDevice.html dùng deviceRequest
        model.addAttribute("deviceId", id); // Dùng để xác định chế độ Sửa và tạo action URL
        return "createrDevice";
    }
    // 2. Xử lý dữ liệu Form gửi về (POST)
    @PostMapping("/devices/edit/{id}")
    public String editDevice(@PathVariable Long id, @ModelAttribute("deviceRequest") CreateDeviceRequestDTO deviceRequest) { // ✨ ĐÃ SỬA: Dùng @PathVariable
        // Gọi service để cập nhật thông tin thiết bị
        deviceService.updateDevice(id, deviceRequest);
        // Cập nhật xong thì chuyển hướng về trang danh sách
        return "redirect:/viewDevices";
    }


    //3 xóa thết bị
    @GetMapping("devices/delete/{id}")
    public String deleteDevice(@PathVariable Long id) {
        deviceService.deleteDevice(id);
        return "redirect:/viewDevices";
    }
}