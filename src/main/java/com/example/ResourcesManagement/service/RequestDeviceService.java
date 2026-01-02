package com.example.ResourcesManagement.service;

import com.example.ResourcesManagement.DTO.request.ResquestDeviceDTO;
import com.example.ResourcesManagement.DTO.response.RequestResponseDTO;
import com.example.ResourcesManagement.DTO.response.UserResponseDTO;
import com.example.ResourcesManagement.entity.*;
import com.example.ResourcesManagement.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RequestDeviceService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RequetsRepository requetsRepository;
    @Autowired
    private DeviceRepository deviceRepository;
    @Autowired
    private DeviceHistoryRepository deviceHistoryRepository;
    @Autowired
    private NotificationRepository notificationRepository;

    // --- 1. USER GỬI YÊU CẦU ---
    public String addRequest(ResquestDeviceDTO request) {
        UserEntity user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        RequestEntity requestEntity = RequestEntity.builder()
                .requestingUser(user)
                .deviceType(request.getDeviceType())
                .description(request.getDescription())
                .status("PENDING")
                .build();

        requetsRepository.save(requestEntity);
        return "Request added successfully";
    }

    // --- 2. CÁC HÀM LẤY DANH SÁCH ---

    // Lấy danh sách chờ duyệt (PENDING)
    public List<RequestResponseDTO> getAllRequest() {
        return convertToDTOList(requetsRepository.findByStatus("PENDING"));
    }

    // [QUAN TRỌNG] Lấy cả PENDING và APPROVED (Để Admin vừa duyệt vừa trả máy)
    public List<RequestResponseDTO> getAllManageableRequests() {
        // Cần thêm hàm findByStatusIn trong Repository nếu chưa có
        // Hoặc dùng tạm logic này nếu repo chưa hỗ trợ IN
        List<RequestEntity> pending = requetsRepository.findByStatus("PENDING");
        List<RequestEntity> approved = requetsRepository.findByStatus("APPROVED");
        pending.addAll(approved);
        return convertToDTOList(pending);
    }

    // Lấy danh sách theo trạng thái cụ thể (Dùng cho trang "Đang mượn")
    public List<RequestResponseDTO> getRequestsByStatus(String status) {
        return convertToDTOList(requetsRepository.findByStatus(status));
    }

    // --- 3. LOGIC DUYỆT & BÀN GIAO (Hoàn chỉnh) ---
    @Transactional(rollbackFor = Exception.class)
    public void approveAndAssignDevice(Long requestId, Long selectedDeviceId,
                                       List<String> checkedItems, String adminNote,
                                       Long adminId) { // [1] Nhận thêm ID của Admin

        // 1. Tìm Request, Device và Admin
        RequestEntity request = requetsRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu (ID: " + requestId + ")"));

        DevicesEntity device = deviceRepository.findById(selectedDeviceId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thiết bị (ID: " + selectedDeviceId + ")"));

        UserEntity adminUser = userRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Admin (ID: " + adminId + ")"));

        // 2. Xử lý Checklist (List -> String)
        String checklistResult;
        if (checkedItems == null || checkedItems.isEmpty()) {
            checklistResult = "Không có mục nào được chọn.";
        } else {
            checklistResult = "Đã kiểm tra: " + String.join(", ", checkedItems);
        }

        // 3. Lưu Lịch sử (History) - ACTION: BORROW
        DeviceHistoryEntity history = DeviceHistoryEntity.builder()
                .action("BORROW")
                .actionDate(LocalDateTime.now())
                .device(device)
                .user(request.getRequestingUser()) // Người mượn
                .handler(adminUser)                // [2] Người xử lý là Admin
                .checklistResult(checklistResult)
                .note(adminNote)
                .build();
        deviceHistoryRepository.save(history);

        // 4. Cập nhật trạng thái Device
        device.setStatus("assigned");
        device.setAssignedUser(request.getRequestingUser());
        deviceRepository.save(device);

        // 5. Cập nhật Request
        request.setStatus("APPROVED");
        request.setNameDevice(device.getDeviceName());

        // [QUAN TRỌNG] Lưu liên kết thiết bị để sau này trả máy
        request.setDevice(device);

        // [QUAN TRỌNG] Lưu người duyệt đơn này
        request.setApprovingUser(adminUser);

        requetsRepository.save(request);

        // 6. Gửi Thông báo cho User
        NotificationEntity notification = NotificationEntity.builder()
                .title("Yêu cầu được duyệt ✅")
                .message("Bạn đã được cấp thiết bị: " + device.getDeviceName())
                .user(request.getRequestingUser())
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();
        notificationRepository.save(notification);
    }
    // --- 4. LOGIC TRẢ THIẾT BỊ (Thu hồi) ---
    @Transactional(rollbackFor = Exception.class)
    public void returnDevice(Long requestId, String condition, String note) {
        // 1. Tìm lại yêu cầu mượn gốc
        RequestEntity request = requetsRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu id: " + requestId));

        // Kiểm tra trạng thái hợp lệ
        if (!"APPROVED".equals(request.getStatus()) && !"BORROWED".equals(request.getStatus())) {
            throw new RuntimeException("Yêu cầu này không hợp lệ để trả máy (Status: " + request.getStatus() + ")");
        }

        // 2. Lấy thông tin thiết bị từ yêu cầu
        DevicesEntity device = request.getDevice();
        if (device == null) {
            throw new RuntimeException("Lỗi dữ liệu: Yêu cầu này chưa được liên kết với thiết bị nào!");
        }

        // 3. Cập nhật trạng thái Yêu cầu -> Đã trả (RETURNED)
        request.setStatus("RETURNED");
        requetsRepository.save(request);

        // 4. Cập nhật trạng thái Thiết bị -> Về kho hoặc Bảo trì
        if ("DAMAGED".equalsIgnoreCase(condition) || "LOST".equalsIgnoreCase(condition)) {
            device.setStatus("MAINTENANCE");
        } else {
            device.setStatus("available");
        }
        device.setAssignedUser(null); // Gỡ người dùng ra
        deviceRepository.save(device);

        // 5. Ghi Lịch sử (History) - ACTION: RETURN
        DeviceHistoryEntity history = DeviceHistoryEntity.builder()
                .device(device)
                .user(request.getRequestingUser())
                .action("RETURN")
                .actionDate(LocalDateTime.now())
                .checklistResult(condition) // Tình trạng trả
                .note(note) // Ghi chú phạt/hỏng
                .build();
        deviceHistoryRepository.save(history);

        // 6. Gửi thông báo
        createNotification(request.getRequestingUser(), "Trả thiết bị thành công ✅",
                "Bạn đã hoàn tất trả thiết bị: " + device.getDeviceName());
    }

    // --- CÁC HÀM TIỆN ÍCH KHÁC ---

    // Hàm tạo thông báo chung
    private void createNotification(UserEntity user, String title, String message) {
        NotificationEntity notification = NotificationEntity.builder()
                .title(title)
                .message(message)
                .user(user)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();
        notificationRepository.save(notification);
    }

    // Từ chối yêu cầu (Thủ công)
    public void rejectRequestManual(Long requestId) {
        RequestEntity request = requetsRepository.findById(requestId).orElseThrow();

        request.setStatus("REJECTED");
        request.setDescription(request.getDescription() + " | Admin đã từ chối.");
        requetsRepository.save(request);

        createNotification(request.getRequestingUser(), "Yêu cầu bị từ chối ❌",
                "Admin đã từ chối yêu cầu của bạn.");
    }

    // Từ chối kèm lý do (Nếu bạn dùng Modal nhập lý do)
    public void rejectRequest(Long requestId, String reason) {
        RequestEntity request = requetsRepository.findById(requestId).orElseThrow();

        request.setStatus("REJECTED");
        requetsRepository.save(request);

        createNotification(request.getRequestingUser(), "Yêu cầu bị từ chối ❌",
                "Lý do: " + reason);
    }

    public void deleteRequest(Long id) {
        requetsRepository.deleteById(id);
    }

    public long countRequestDevices() {
        return requetsRepository.countByStatus("PENDING");
    }

    public List<RequestResponseDTO> getMyRequests(Long id) {
        return convertToDTOList(requetsRepository.findByRequestingUserId(id));
    }

    public RequestResponseDTO getRequestById(Long requestId) {
        RequestEntity requestEntity = requetsRepository.findById(requestId).orElseThrow();
        return convertSingleDTO(requestEntity);
    }

    // --- Helper: Convert Entity to DTO ---
    private List<RequestResponseDTO> convertToDTOList(List<RequestEntity> entities) {
        return entities.stream().map(this::convertSingleDTO).toList();
    }

    private RequestResponseDTO convertSingleDTO(RequestEntity entity) {
        UserEntity user = entity.getRequestingUser();
        UserResponseDTO userDTO = UserResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .chapterName(user.getChapter() != null ? user.getChapter().getName() : null)
                .build();

        return RequestResponseDTO.builder()
                .id(entity.getRequestId())
                .user(userDTO)
                .deviceType(entity.getDeviceType())
                .description(entity.getDescription())
                .status(entity.getStatus())
                // [ĐÃ SỬA] Thêm dòng này để fix lỗi Thymeleaf "nameDevice cannot be found"
                .nameDevice(entity.getNameDevice())
                .build();
    }
}