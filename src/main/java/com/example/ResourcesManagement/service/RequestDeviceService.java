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
    UserRepository userRepository;

    @Autowired
    RequetsRepository requetsRepository; // Tên biến giữ nguyên theo file gốc của bạn

    @Autowired
    DeviceRepository deviceRepository;

    @Autowired
    DeviceHistoryRepository deviceHistoryRepository;

    @Autowired
    NotificationRepository notificationRepository; // Repository mới cho thông báo

    // --- 1. USER GỬI YÊU CẦU --- thêm vào database
    public String addRequest(ResquestDeviceDTO request) {
        UserEntity User = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        RequestEntity requestEntity = RequestEntity.builder()
                .requestingUser(User)
                .deviceType(request.getDeviceType())
                .description(request.getDescription())
                .status("PENDING") // Mặc định chờ duyệt
                .build();

        requetsRepository.save(requestEntity);
        return "Request added successfully";
    }

    // --- 2. LẤY DANH SÁCH YÊU CẦU PENDING (CHO ADMIN) ---
    public List<RequestResponseDTO> getAllRequest() {
        List<RequestEntity> requestEntities = requetsRepository.findByStatus("PENDING");

        return requestEntities.stream().map(requestEntity -> {
            UserEntity userEntity = requestEntity.getRequestingUser();
            UserResponseDTO userResponseDTO = UserResponseDTO.builder()
                    .id(userEntity.getId())
                    .username(userEntity.getUsername())
                    .chapterName(userEntity.getChapter() != null ? userEntity.getChapter().getName() : null)
                    .build();

            return RequestResponseDTO.builder()
                    .requestId(requestEntity.getRequestId())
                    .user(userResponseDTO)
                    .deviceType(requestEntity.getDeviceType())
                    .description(requestEntity.getDescription())
                    .status(requestEntity.getStatus())
                    .build();
        }).toList();
    }

    // --- 3. LOGIC PRE-CHECK (KIỂM TRA TRƯỚC KHI HIỆN CHECKLIST) ---
    public String preCheckRequest(Long requestId) {
        RequestEntity request = requetsRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        UserEntity user = request.getRequestingUser();
        String deviceType = request.getDeviceType();

        // Check 1: Kho còn hàng không?
        boolean hasStock = deviceRepository.existsByStatusAndDeviceType("available", deviceType);
        if (!hasStock) {
            rejectRequestLogic(request, "Hệ thống tự động từ chối: Kho hết thiết bị loại " + deviceType);
            return "REJECTED_NO_STOCK";
        }

        // Check 2: User có đang giữ quá 2 thiết bị cùng loại không?
        int currentHolding = deviceRepository.countByAssignedUserIdAndDeviceTypeAndStatus(user.getId(), deviceType, "assigned");
        if (currentHolding >= 2) {
            rejectRequestLogic(request, "Hệ thống tự động từ chối: User đang mượn quá 2 thiết bị loại " + deviceType);
            return "REJECTED_QUOTA_EXCEEDED";
        }

        return "OK";
    }

    // --- 4. LOGIC APPROVE (DUYỆT + CẤP PHÁT + LỊCH SỬ + THÔNG BÁO) ---
    @Transactional(rollbackFor = Exception.class)
    public String approveRequest(Long requestId, Long adminId, String checklistNotes) {
        // 1. Lấy Request
        RequestEntity request = requetsRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (!"PENDING".equals(request.getStatus())) {
            throw new RuntimeException("Yêu cầu này đã được xử lý trước đó.");
        }

        // 2. Lấy Admin
        UserEntity admin = userRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        // 3. Tìm thiết bị Available
        DevicesEntity deviceToAssign = deviceRepository.findFirstByStatusAndDeviceType("available", request.getDeviceType())
                .orElseThrow(() -> new RuntimeException("Lỗi: Thiết bị vừa hết hàng trong lúc chờ duyệt."));

        // 4. Cập nhật trạng thái Thiết bị
        deviceToAssign.setStatus("assigned");
        deviceToAssign.setAssignedUser(request.getRequestingUser());
        deviceToAssign.setNote("Được cấp phát ngày " + LocalDateTime.now());
        deviceRepository.save(deviceToAssign);

        // 5. Cập nhật trạng thái Request
        request.setStatus("APPROVED");
        request.setApprovingUser(admin); // Ai duyệt
        request.setNameDevice(deviceToAssign.getDeviceName());
        requetsRepository.save(request);

        // 6. Lưu Lịch sử (History)
        DeviceHistoryEntity history = DeviceHistoryEntity.builder()
                .action("BORROW")  // Hành động mượn
                .actionDate(LocalDateTime.now())
                .user(request.getRequestingUser())
                .handler(admin)
                .device(deviceToAssign)
                .checklistResult(checklistNotes)
                .note("Cấp phát qua Request #" + requestId)
                .build();
        deviceHistoryRepository.save(history);

        // 7. TẠO THÔNG BÁO (NOTIFICATION) GỬI USER
        NotificationEntity notification = NotificationEntity.builder()
                .title("Yêu cầu được duyệt ✅")
                .message("Yêu cầu mượn " + request.getDeviceType() + " của bạn đã được chấp nhận. Thiết bị: " + deviceToAssign.getDeviceName())
                .user(request.getRequestingUser())
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();
        notificationRepository.save(notification);

        return "Duyệt thành công! Đã cấp thiết bị: " + deviceToAssign.getDeviceName();
    }

    // --- 5. LOGIC TỪ CHỐI (TỰ ĐỘNG & THỦ CÔNG) ---
    private void rejectRequestLogic(RequestEntity request, String reason) {
        request.setStatus("REJECTED");
        request.setDescription(request.getDescription() + " | [Lý do từ chối]: " + reason);
        requetsRepository.save(request);

        // TẠO THÔNG BÁO TỪ CHỐI
        NotificationEntity notification = NotificationEntity.builder()
                .title("Yêu cầu bị từ chối ❌")
                .message("Yêu cầu mượn " + request.getDeviceType() + " bị từ chối. Lý do: " + reason)
                .user(request.getRequestingUser())
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();
        notificationRepository.save(notification);
    }

    // API từ chối thủ công (khi Admin bấm nút Hủy)
    public void rejectRequestManual(Long requestId, Long adminId) {
        RequestEntity request = requetsRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        UserEntity admin = userRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        request.setApprovingUser(admin);
        rejectRequestLogic(request, "Admin đã từ chối yêu cầu này.");
    }

    // --- 6. XÓA REQUEST (CHỈ XÓA KHI CẦN THIẾT) ---
    public void deleteRequest(Long id) {
        RequestEntity requestEntity = requetsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        requetsRepository.delete(requestEntity);
    }

    // --- 7. TRẢ THIẾT BỊ (RETURN) ---
    @Transactional
    public String returnDevice(Long deviceId, Long userId) {
        DevicesEntity device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("Device not found"));

        if (device.getAssignedUser() == null || !device.getAssignedUser().getId().equals(userId)) {
            return "Device is not assigned to this user.";
        }

        UserEntity user = device.getAssignedUser();

        // Reset thiết bị
        device.setStatus("available");
        device.setAssignedUser(null);
        deviceRepository.save(device);

        // Lưu lịch sử trả
        DeviceHistoryEntity history = DeviceHistoryEntity.builder()
                .action("RETURN")
                .actionDate(LocalDateTime.now())
                .user(user)
                .device(device)
                .note("Người dùng trả thiết bị")
                .build();
        deviceHistoryRepository.save(history);

        return "Device returned successfully.";
    }

    public long countRequestDevices() {
        return requetsRepository.countByStatus("PENDING");
    }

    public List<RequestResponseDTO> getMyRequests(Long id) {
        List<RequestEntity> requestEntities = requetsRepository.findByRequestingUserId(id); // id là userId gửi

        return requestEntities.stream().map(requestEntity -> {
            UserEntity userEntity = requestEntity.getRequestingUser();
            UserResponseDTO userResponseDTO = UserResponseDTO.builder()
                    .id(userEntity.getId())
                    .username(userEntity.getUsername())
                    .chapterName(userEntity.getChapter() != null ? userEntity.getChapter().getName() : null)
                    .build();

            return RequestResponseDTO.builder()
                    .requestId(requestEntity.getRequestId())
                    .user(userResponseDTO)
                    .deviceType(requestEntity.getDeviceType())
                    .description(requestEntity.getDescription())
                    .status(requestEntity.getStatus())
                    .build();
        }).toList();
    }

}