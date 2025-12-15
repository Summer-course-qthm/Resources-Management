package com.example.ResourcesManagement.DTO.response;

import com.example.ResourcesManagement.entity.NotificationEntity;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponseDTO {
    private Long id;
    private String title;
    private String message;
    private boolean isRead;
    private LocalDateTime createdAt;
    private String type; // SUCCESS, ERROR, INFO

    // Hàm chuyển từ Entity sang DTO
    public static NotificationResponseDTO fromEntity(NotificationEntity entity) {
        String determinedType = "INFO"; // Mặc định là màu xanh dương

        // Logic tự động chọn màu sắc dựa trên tiêu đề
        if (entity.getTitle().toLowerCase().contains("duyệt") ||
                entity.getTitle().toLowerCase().contains("chấp nhận")) {
            determinedType = "SUCCESS"; // Màu xanh lá
        } else if (entity.getTitle().toLowerCase().contains("từ chối") ||
                entity.getTitle().toLowerCase().contains("hủy")) {
            determinedType = "ERROR"; // Màu đỏ
        }

        return NotificationResponseDTO.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .message(entity.getMessage())
                .isRead(entity.isRead())
                .createdAt(entity.getCreatedAt())
                .type(determinedType)
                .build();
    }
}