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

    @Autowired UserRepository userRepository;
    @Autowired RequetsRepository requetsRepository;
    @Autowired DeviceRepository deviceRepository;
    @Autowired DeviceHistoryRepository deviceHistoryRepository;
    @Autowired NotificationRepository notificationRepository;

    // --- 1. USER GỬI YÊU CẦU ---
    public String addRequest(ResquestDeviceDTO request) {
        UserEntity User = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        RequestEntity requestEntity = RequestEntity.builder()
                .requestingUser(User)
                .deviceType(request.getDeviceType())
                .description(request.getDescription())
                .status("PENDING")
                .build();

        requetsRepository.save(requestEntity);
        return "Request added successfully";
    }

    // --- 2. LẤY DANH SÁCH PENDING ---
    public List<RequestResponseDTO> getAllRequest() {
        List<RequestEntity> requestEntities = requetsRepository.findByStatus("PENDING");
        return convertToDTOList(requestEntities);
    }

    // --- 3. LOGIC DUYỆT & BÀN GIAO (Hàm CHÍNH thức) ---
    // (Đã xóa hàm approveRequest cũ thừa thãi đi)
    @Transactional(rollbackFor = Exception.class)
    public void approveAndAssignDevice(Long requestId, Long selectedDeviceId,
                                       List<String> checkedItems, String adminNote) {

        // 1. Tìm Request & Device
        RequestEntity request = requetsRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy yêu cầu"));
        DevicesEntity device = deviceRepository.findById(selectedDeviceId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thiết bị"));

        // 2. Xử lý Checklist (List -> String)
        String checklistResult;
        if (checkedItems == null || checkedItems.isEmpty()) {
            checklistResult = "Không có mục nào được chọn.";
        } else {
            checklistResult = "Đã kiểm tra: " + String.join(", ", checkedItems);
        }

        // 3. Lưu Lịch sử (History)
        DeviceHistoryEntity history = DeviceHistoryEntity.builder()
                .action("BORROW")
                .actionDate(LocalDateTime.now())
                .device(device)
                .user(request.getRequestingUser())
                // .handler(adminUser) // Thêm admin nếu có
                .checklistResult(checklistResult)
                .note(adminNote)
                .build();
        deviceHistoryRepository.save(history);

        // 4. Cập nhật Device
        device.setStatus("assigned");
        device.setAssignedUser(request.getRequestingUser());
        deviceRepository.save(device);

        // 5. Cập nhật Request
        request.setStatus("APPROVED");
        request.setNameDevice(device.getDeviceName());
        requetsRepository.save(request);

        // 6. Gửi Thông báo cho User (Lấy từ hàm cũ sang)
        NotificationEntity notification = NotificationEntity.builder()
                .title("Yêu cầu được duyệt ✅")
                .message("Bạn đã được cấp thiết bị: " + device.getDeviceName())
                .user(request.getRequestingUser())
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();
        notificationRepository.save(notification);
    }

    // --- 4. CÁC HÀM TIỆN ÍCH KHÁC (Giữ nguyên) ---

    // Hàm từ chối (Tự động hoặc Thủ công dùng chung)
    private void rejectRequestLogic(RequestEntity request, String reason) {
        request.setStatus("REJECTED");
        request.setDescription(request.getDescription() + " | Lý do: " + reason);
        requetsRepository.save(request);

        NotificationEntity notification = NotificationEntity.builder()
                .title("Yêu cầu bị từ chối ❌")
                .message("Lý do: " + reason)
                .user(request.getRequestingUser())
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();
        notificationRepository.save(notification);
    }

    // API từ chối thủ công
    public void rejectRequestManual(Long requestId) {
        RequestEntity request = requetsRepository.findById(requestId).orElseThrow();
        rejectRequestLogic(request, "Admin đã từ chối yêu cầu này.");
    }

    // Pre-check (Có thể giữ lại để dùng sau nếu cần check kho tự động)
    public String preCheckRequest(Long requestId) {
        RequestEntity request = requetsRepository.findById(requestId).orElseThrow();
        boolean hasStock = deviceRepository.existsByStatusAndDeviceType("available", request.getDeviceType());
        if (!hasStock) {
            return "REJECTED_NO_STOCK";
        }
        return "OK";
    }

    public void deleteRequest(Long id) {
        requetsRepository.deleteById(id);
    }

    @Transactional
    public String returnDevice(Long deviceId, Long userId) {
        DevicesEntity device = deviceRepository.findById(deviceId).orElseThrow();
        // ... Logic trả máy giữ nguyên ...
        // (Lưu ý: Bạn nên thêm logic lưu History RETURN vào đây giống hàm approve)

        device.setStatus("available");
        device.setAssignedUser(null);
        deviceRepository.save(device);
        return "Device returned successfully.";
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

    // --- Helper: Convert Entity to DTO (Để code đỡ lặp lại) ---
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
                .build();
    }


}