package com.example.ResourcesManagement.viewController;

import com.example.ResourcesManagement.DTO.request.ApproveRequestDTO;
import com.example.ResourcesManagement.DTO.response.RequestResponseDTO;
import com.example.ResourcesManagement.DTO.response.UserResponseDTO;
import com.example.ResourcesManagement.entity.ChecklistEntity;
import com.example.ResourcesManagement.entity.ChecklistItemEntity;
import com.example.ResourcesManagement.entity.DevicesEntity;
import com.example.ResourcesManagement.repository.CheckListRepository;
import com.example.ResourcesManagement.repository.DeviceRepository;
import com.example.ResourcesManagement.service.RequestDeviceService;
import jakarta.servlet.http.HttpSession;
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
    public String approveRequest(
            @ModelAttribute ApproveRequestDTO formDTO, // Hứng dữ liệu từ Form HTML
            HttpSession session // [1] Thêm biến Session để lấy người đang đăng nhập
    ) {
        try {
            // [2] Lấy thông tin Admin từ Session (đã lưu lúc Login)
            UserResponseDTO currentUser = (UserResponseDTO) session.getAttribute("user");

            // Kiểm tra bảo mật: Nếu session hết hạn hoặc chưa đăng nhập -> Đá về trang Login
            if (currentUser == null) {
                System.out.println("Lỗi: Session không tồn tại, yêu cầu đăng nhập lại.");
                return "redirect:/viewLogin";
            }

            // In log để kiểm tra (Debug) - Có thể xóa sau khi code chạy ngon
            System.out.println("--- ĐANG DUYỆT YÊU CẦU ---");
            System.out.println("Request ID: " + formDTO.getRequestId());
            System.out.println("Device ID: " + formDTO.getSelectedDeviceId());
            System.out.println("Admin ID: " + currentUser.getId());

            // [3] Gọi Service với đủ 5 tham số (bao gồm ID của Admin)
            requestDeviceService.approveAndAssignDevice(
                    formDTO.getRequestId(),
                    formDTO.getSelectedDeviceId(),
                    formDTO.getCheckedItems(),
                    formDTO.getNote(),
                    currentUser.getId() // <--- Truyền ID người duyệt vào đây
            );

            // [4] Thành công -> Quay lại danh sách
            return "redirect:/viewRequests";

        } catch (Exception e) {
            e.printStackTrace(); // In lỗi ra Console nếu có
            return "redirect:/viewRequests?error=" + e.getMessage(); // Hoặc trang báo lỗi
        }
    }

    //từ chối yêu cầu
    @PostMapping("/admin/request/reject")
    public String rejectRequest(@RequestParam("requestId") Long requestId){

        // Gọi Service xử lý
        requestDeviceService.rejectRequestManual(requestId);

        return "redirect:/viewRequests?success";
    }

    // Trang quản lý thiết bị ĐANG MƯỢN (APPROVED)
    @GetMapping("/admin/borrowed-devices")
    public String viewBorrowedDevices(Model model) {
        // Chỉ lấy những yêu cầu đã được DUYỆT (tức là đang mượn)
        List<RequestResponseDTO> borrowedList = requestDeviceService.getRequestsByStatus("APPROVED");

        model.addAttribute("borrowedList", borrowedList);
        return "borrowed-devices"; // Trả về file HTML riêng cho gọn
    }

    // --- 2. XỬ LÝ THU HỒI / TRẢ MÁY (POST) ---
    @PostMapping("/admin/request/return")
    public String processReturnDevice(
            @RequestParam("requestId") Long requestId,
            @RequestParam("condition") String condition,
            @RequestParam("note") String note,
            RedirectAttributes redirectAttributes // Dùng để truyền thông báo sang trang sau
    ) {
        try {
            // Gọi Service để xử lý logic trả máy
            requestDeviceService.returnDevice(requestId, condition, note);

            // Nếu thành công -> Gửi thông báo xanh
            redirectAttributes.addFlashAttribute("successMessage", "✅ Đã thu hồi thiết bị thành công!");

        } catch (Exception e) {
            // Nếu có lỗi -> Gửi thông báo đỏ
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "❌ Lỗi: " + e.getMessage());
        }

        // Quay lại trang danh sách đang mượn
        return "redirect:/admin/borrowed-devices";
    }

    


}