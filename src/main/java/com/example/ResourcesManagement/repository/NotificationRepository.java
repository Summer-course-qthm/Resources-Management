package com.example.ResourcesManagement.repository;

import com.example.ResourcesManagement.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {
    // Lấy thông báo của user cụ thể, sắp xếp mới nhất lên đầu
    List<NotificationEntity> findByUserIdOrderByCreatedAtDesc(Long userId);

    // Đếm số thông báo chưa đọc (để hiện số trên cái chuông)
    long countByUserIdAndIsReadFalse(Long userId);
}