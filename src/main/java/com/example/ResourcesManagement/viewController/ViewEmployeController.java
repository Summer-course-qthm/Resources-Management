package com.example.ResourcesManagement.viewController;

import com.example.ResourcesManagement.DTO.request.CreateUserRequestDTO;
import com.example.ResourcesManagement.entity.UserEntity;
import com.example.ResourcesManagement.service.RequestDeviceService;
import com.example.ResourcesManagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ViewEmployeController {

    // lấy danh sách nhân viên
    @Autowired
    UserService userService;

    @Autowired
    RequestDeviceService requestDeviceService;

    @GetMapping("/viewEmployees") // cái api ni ch config trong security để ai cũng truy cập đc
    public String viewEmployees(Model model) {

        long requestDeviceCount = requestDeviceService.countRequestDevices();
        model.addAttribute("requestDeviceCount", requestDeviceCount);
        model.addAttribute("employees", userService.getListUser());
        return "employee";
    }


    // --- 2. THÊM NHÂN VIÊN (ADD) ---

    // Hiển thị Form thêm mới (GET)
    @GetMapping("/employees/add")
    public String showAddEmployeeForm(Model model) {
        // Tạo đối tượng DTO rỗng
        model.addAttribute("userRequest", new CreateUserRequestDTO());
        return "createEmployee"; // Trả về file HTML createEmployee
    }

    // Xử lý dữ liệu thêm mới (POST)
    @PostMapping("/employees/add")
    public String addEmployee(@ModelAttribute("userRequest") CreateUserRequestDTO userRequest) {
        userService.createUser(userRequest);
        return "redirect:/viewEmployees";
    }

    // --- 3. SỬA NHÂN VIÊN (EDIT) ---

    // Hiển thị Form sửa (GET)
    @GetMapping("/employees/edit/{id}")
    public String showEditEmployeeForm(@PathVariable Long id, Model model) {
        // Lấy thông tin user cũ từ service (Bạn cần hàm lấy user theo ID trả về Entity hoặc DTO)
        // Giả sử dùng hàm findUserEntityById mà mình đã gợi ý ở bước trước, hoặc getUserById
        UserEntity user = userService.findUserEntityById(id);

        // Tạo DTO và đổ dữ liệu cũ vào để hiển thị lên form
        CreateUserRequestDTO userRequest = new CreateUserRequestDTO();
        userRequest.setUsername(user.getUsername());
        userRequest.setEmail(user.getEmail());
        userRequest.setPhone(user.getPhone());
        userRequest.setPassword(""); // Mật khẩu để trống hoặc xử lý tùy ý

        if (user.getChapter() != null) {
            userRequest.setChapterId(user.getChapter().getId());
        }

        model.addAttribute("userRequest", userRequest);
        model.addAttribute("userId", id); // Gửi ID sang để Form biết là đang Sửa

        return "createEmployee"; // Dùng chung file view với Thêm mới
    }

    // Xử lý dữ liệu sửa (POST)
    @PostMapping("/employees/edit/{id}")
    public String editEmployee(@PathVariable Long id, @ModelAttribute("userRequest") CreateUserRequestDTO userRequest) {
        userService.updateUser(id, userRequest);
        return "redirect:/viewEmployees";
    }

    // --- 4. XÓA NHÂN VIÊN ---
    @GetMapping("/employees/delete/{id}")
    public String deleteEmployee(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/viewEmployees";
    }




}
