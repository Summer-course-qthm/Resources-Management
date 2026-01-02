package com.example.ResourcesManagement.DTO.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class DeviceHistoryResponseDTO {
    private Long id;
    private String actionType;      // "Mượn", "Trả", "Bảo trì" (đã việt hóa từ backend)
    private LocalDateTime actionDate; // Ngày giờ thực hiện

    // Thông tin thiết bị
    private String deviceName;
    private String deviceCode;      // Mã tài sản (nếu có)

    // Thông tin con người
    private String performedBy;     // Tên người thực hiện (User hoặc Staff bảo trì)
    private String handledBy;       // Tên Admin/IT xác nhận (handler)

    // Chi tiết tình trạng
    private String checklistResult; // "Sạc: Có, Chuột: Mất..."
    private String note;            // Ghi chú thêm

    // Liên kết (để bấm vào xem chi tiết)
    private Long requestId;         // ID của đơn mượn (để tạo link href="/request/detail/{id}")
}