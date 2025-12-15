package com.example.ResourcesManagement.viewController;

import com.example.ResourcesManagement.DTO.request.ApproveRequestDTO;
import com.example.ResourcesManagement.DTO.response.RequestResponseDTO;
import com.example.ResourcesManagement.entity.ChecklistEntity;
import com.example.ResourcesManagement.entity.ChecklistItemEntity;
import com.example.ResourcesManagement.entity.DevicesEntity;
import com.example.ResourcesManagement.repository.CheckListRepository;
import com.example.ResourcesManagement.repository.DeviceRepository;
import com.example.ResourcesManagement.service.RequestDeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
public class ViewRequestDeviceController {

    @Autowired
    private RequestDeviceService requestDeviceService;

    @Autowired
    private DeviceRepository deviceRepository;

    // 1. THÊM REPOSITORY ĐỂ LẤY MẪU CHECKLIST
    @Autowired
    private CheckListRepository checklistRepository;

    // Endpoint hiển thị trang danh sách yêu cầu
    @GetMapping("/viewRequests")
    public String viewRequests(Model model) {
        List<RequestResponseDTO> requests = requestDeviceService.getAllRequest();
        long count = requestDeviceService.countRequestDevices();
        model.addAttribute("listRequest", requests);
        model.addAttribute("requestDeviceCount", count);
        return "admin-request-pending";
    }

    // --- TRANG XỬ LÝ YÊU CẦU (GET) ---
    @GetMapping("/admin/request/process/{id}")
    public String showProcessPage(@PathVariable("id") Long requestId, Model model) {

        // 1. Lấy thông tin Request
        RequestResponseDTO request = requestDeviceService.getRequestById(requestId);

        // 2. Tìm thiết bị trong kho khớp loại
        List<DevicesEntity> availableDevices = deviceRepository.findByDeviceTypeAndStatus(
                request.getDeviceType(), "AVAILABLE"
        );

        // 3. --- LOGIC MỚI: TÌM MẪU CHECKLIST THEO LOẠI MÁY ---
        // Ví dụ: Request là "Laptop" -> Tìm Checklist có deviceType="Laptop"
        ChecklistEntity checklistTemplate = checklistRepository.findByDeviceType(request.getDeviceType());

        List<ChecklistItemEntity> items = new ArrayList<>();
        if (checklistTemplate != null) {
            // Nếu tìm thấy mẫu, lấy danh sách câu hỏi ra (Sạc, Chuột...)
            items = checklistTemplate.getItems();
        }

        // 4. Đẩy dữ liệu sang View
        model.addAttribute("req", request);
        model.addAttribute("devices", availableDevices);
        model.addAttribute("checklistItems", items); // Gửi list câu hỏi sang HTML

        return "admin-request-process";
    }

    // --- XỬ LÝ FORM DUYỆT (POST) ---
//    // Endpoint này sẽ khớp với th:action="@{/admin/request/approve}" bên HTML
    @PostMapping("/admin/request/approve")
    public String approveRequest(@ModelAttribute ApproveRequestDTO formDTO) {

        // 1. Gọi Service xử lý logic
        requestDeviceService.approveAndAssignDevice(
                formDTO.getRequestId(),
                formDTO.getSelectedDeviceId(),
                formDTO.getCheckedItems(),
                formDTO.getNote()
        );

        // 2. Xử lý xong thì chuyển hướng ngay về trang danh sách
        // Không mang theo dữ liệu gì cả
        return "redirect:/viewRequests";
    }

    //từ chối yêu cầu

}