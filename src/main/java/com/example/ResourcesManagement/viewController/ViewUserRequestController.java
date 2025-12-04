package com.example.ResourcesManagement.viewController;

import com.example.ResourcesManagement.DTO.request.ResquestDeviceDTO;
import com.example.ResourcesManagement.DTO.response.RequestResponseDTO;
import com.example.ResourcesManagement.entity.UserEntity;
import com.example.ResourcesManagement.repository.UserRepository;
import com.example.ResourcesManagement.service.RequestDeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class ViewUserRequestController {

    @Autowired
    RequestDeviceService requestDeviceService;

    @Autowired
    UserRepository userRepository;

    // --- 1. HIỂN THỊ FORM (Tạo Model trước theo ý bạn) ---
    @GetMapping("/request-device")
    public String showRequestForm(Model model) {
        // Tạo đối tượng DTO rỗng để bind dữ liệu form
        model.addAttribute("requestDTO", new ResquestDeviceDTO());
        return "useRequestDevice"; // Trả về file requests.html
    }

    // --- 2. XỬ LÝ (Hứng Model từ HTML về) ---
    @PostMapping("/request-device/create")
    public String createRequest(@ModelAttribute("requestDTO") ResquestDeviceDTO requestDTO,
                                Model model) {
        try {
            // 1. Lấy User đang đăng nhập (Vì Form không gửi ID người dùng để bảo mật)
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            UserEntity currentUser = userRepository.findByUsername(auth.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // 2. Gán ID người dùng vào DTO vừa nhận được từ form
            requestDTO.setUserId(currentUser.getId());

            // 3. Gọi Service
            requestDeviceService.addRequest(requestDTO);

            return "redirect:/my-requests?success";

        } catch (Exception e) {
            // Nếu lỗi, gửi lại chính object đó để không mất dữ liệu người dùng đã nhập
            model.addAttribute("requestDTO", requestDTO);
            model.addAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            return "useRequestDevice";
        }
    }


    // 3. Hiển thị danh sách yêu cầu của User
    @GetMapping("/my-requests")
    public String viewMyRequests(Model model) {
        // Lấy User hiện tại
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserEntity currentUser = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Gọi Service lấy danh sách của RIÊNG user này
        List<RequestResponseDTO> myRequests = requestDeviceService.getMyRequests(currentUser.getId());

        // Đẩy danh sách ra View
        model.addAttribute("myRequests", myRequests);

        return "my-requests"; // Trả về my-requests.html
    }
}