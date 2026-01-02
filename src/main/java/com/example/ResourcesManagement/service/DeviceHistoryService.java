package com.example.ResourcesManagement.service;

import com.example.ResourcesManagement.DTO.response.DeviceHistoryResponseDTO;
import com.example.ResourcesManagement.entity.DeviceHistoryEntity;
import com.example.ResourcesManagement.repository.DeviceHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DeviceHistoryService {

    @Autowired
    private DeviceHistoryRepository historyRepository;

    // --- CHỨC NĂNG DUY NHẤT: LẤY TẤT CẢ LỊCH SỬ ---
    public List<DeviceHistoryResponseDTO> getAllDeviceHistory() {
        // Gọi hàm tìm tất cả và sắp xếp
        List<DeviceHistoryEntity> histories = historyRepository.findAllByOrderByActionDateDesc();

        // Chuyển đổi sang DTO để trả về Controller
        return histories.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // --- HÀM PHỤ: CHUYỂN ĐỔI ENTITY -> DTO ---
    private DeviceHistoryResponseDTO mapToDTO(DeviceHistoryEntity deviceHistoryEntity) {
        if (deviceHistoryEntity == null) return null;

        // 1. Việt hóa hành động
        String actionDisplay = deviceHistoryEntity.getAction();
        if ("BORROW".equalsIgnoreCase(actionDisplay)) actionDisplay = "Mượn thiết bị";
        else if ("RETURN".equalsIgnoreCase(actionDisplay)) actionDisplay = "Trả thiết bị";
        else if ("MAINTENANCE".equalsIgnoreCase(actionDisplay)) actionDisplay = "Bảo trì";

        // 2. Xử lý null an toàn (tránh lỗi nếu User hoặc Device đã bị xóa)
        String deviceName = (deviceHistoryEntity.getDevice() != null) ? deviceHistoryEntity.getDevice().getDeviceName() : "Không xác định";
        String deviceId = (deviceHistoryEntity.getDevice() != null)
                ? String.valueOf(deviceHistoryEntity.getDevice().getDeviceId())
                : "";

        String performedBy = (deviceHistoryEntity.getUser() != null) ? deviceHistoryEntity.getUser().getUsername() : "N/A";
        String handledBy = (deviceHistoryEntity.getHandler() != null) ? deviceHistoryEntity.getHandler().getUsername() : "Hệ thống";

        return DeviceHistoryResponseDTO.builder()
                .id(deviceHistoryEntity.getId())
                .actionType(actionDisplay)
                .actionDate(deviceHistoryEntity.getActionDate())
                .deviceName(deviceName)
                .deviceCode(deviceId)
                .performedBy(performedBy)
                .handledBy(handledBy)
                .checklistResult(deviceHistoryEntity.getChecklistResult())
                .note(deviceHistoryEntity.getNote())
                .build();
    }
}