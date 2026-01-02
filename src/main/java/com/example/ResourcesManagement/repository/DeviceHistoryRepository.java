package com.example.ResourcesManagement.repository;

import com.example.ResourcesManagement.entity.DeviceHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeviceHistoryRepository extends JpaRepository<DeviceHistoryEntity, Long> {

    // Tìm lịch sử theo Device (Admin xem máy này đã qua tay ai)
    List<DeviceHistoryEntity> findByDeviceIdOrderByActionDateDesc(Long deviceId);

    List<DeviceHistoryEntity> findAllByOrderByActionDateDesc();
}