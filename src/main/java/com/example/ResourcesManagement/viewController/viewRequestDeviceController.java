package com.example.ResourcesManagement.viewController;

import com.example.ResourcesManagement.DTO.response.RequestResponseDTO;
import com.example.ResourcesManagement.service.RequestDeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

public class viewRequestDeviceController {


    @Autowired
    private RequestDeviceService requestDeviceService;

    // Endpoint hiển thị trang danh sách yêu cầu
    @GetMapping("/viewRequests")
    public String viewRequests(Model model) {
        // 1. Gọi Service lấy danh sách Request (đã convert sang DTO)
        List<RequestResponseDTO> requests = requestDeviceService.getAllRequest();

        // 2. Lấy số lượng yêu cầu chờ duyệt (để hiện số đỏ trên menu)
        long count = requestDeviceService.countRequestDevices();

        // 3. Đẩy dữ liệu sang file HTML
        model.addAttribute("listRequest", requests); // Biến này dùng trong th:each="req : ${listRequest}"
        model.addAttribute("requestDeviceCount", count);

        // 4. Trả về tên file HTML (admin-request-pending.html)
        return "requests";
    }
}
