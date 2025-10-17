package com.example.ResourcesManagement.repository;

import com.example.ResourcesManagement.entity.DevicesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceRepository  extends JpaRepository<DevicesEntity,Long> {
    List<DevicesEntity> findByAssignedUserId(Long userId);

    Optional <DevicesEntity> findByDeviceName(String deviceName);


    Optional<DevicesEntity> findFirstByDeviceTypeAndStatus(String deviceType, String status);

    boolean existsByStatusAndDeviceType(String status , String deviceType);

    // kiểm tra 1 người chửi đc mượn 1 devicetype và status available
    boolean existsByAssignedUserIdAndDeviceTypeAndStatus(Long userId, String deviceType, String status);

}
