package com.example.ResourcesManagement.repository;

import com.example.ResourcesManagement.entity.DeviceHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeviceHistoryRepository extends JpaRepository<DeviceHistoryEntity, Long> {

    // --- DÒNG ĐÚNG (Đã sửa) ---
    // Spring sẽ tìm thuộc tính 'device' -> rồi tìm tiếp 'deviceId' bên trong nó
    List<DeviceHistoryEntity> findByDeviceDeviceIdOrderByActionDateDesc(Long deviceId);

    // --- CÁC HÀM KHÁC ---
    List<DeviceHistoryEntity> findByUserIdOrderByActionDateDesc(Long userId);

    List<DeviceHistoryEntity> findByAction(String action);
}