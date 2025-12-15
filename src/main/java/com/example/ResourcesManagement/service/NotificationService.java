package com.example.ResourcesManagement.service;

import com.example.ResourcesManagement.DTO.response.NotificationResponseDTO;
import com.example.ResourcesManagement.entity.NotificationEntity;
import com.example.ResourcesManagement.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    public List<NotificationResponseDTO> getMyNotifications(Long userId) {
        // 1. Lấy danh sách từ DB (đã sắp xếp mới nhất trước)
        List<NotificationEntity> entities = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);

        // 2. Chuyển đổi sang DTO và trả về
        return entities.stream()
                .map(NotificationResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // Hàm đếm số thông báo chưa đọc (nếu bạn muốn hiển thị chấm đỏ trên menu)
    public long countUnread(Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }
}