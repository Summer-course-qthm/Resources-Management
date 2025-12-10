package com.example.ResourcesManagement.viewController;

import com.example.ResourcesManagement.DTO.response.RequestResponseDTO;
import com.example.ResourcesManagement.entity.DevicesEntity;
import com.example.ResourcesManagement.repository.DeviceRepository;
import com.example.ResourcesManagement.service.RequestDeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class ViewRequestDeviceController {


    @Autowired
    private RequestDeviceService requestDeviceService;

    @Autowired
    private DeviceRepository deviceRepository;

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
        return "admin-request-pending";
    }


    @GetMapping("/admin/request/process/{id}")
    public String showProcessPage(@PathVariable("id") Long requestId, Model model) {

        // 1. Lấy thông tin Request hiện tại
        // (Lưu ý: Bạn có thể dùng hàm findById trả về DTO hoặc Entity tùy code cũ của bạn)
        RequestResponseDTO request = requestDeviceService.getRequestById(requestId);

        // 2. Tìm thiết bị trong kho khớp với loại (DeviceType) và đang AVAILABLE
        // Ví dụ: tìm các máy "Laptop" đang "Sẵn sàng"
        List<DevicesEntity> availableDevices = deviceRepository.findByDeviceNameContainingAndStatus(
                request.getDeviceType(),
                "AVAILABLE"
        );

        // 3. Đẩy dữ liệu sang View
        model.addAttribute("req", request);
        model.addAttribute("devices", availableDevices);

        return "admin-request-process"; // Trả về file HTML mới
    }
}
