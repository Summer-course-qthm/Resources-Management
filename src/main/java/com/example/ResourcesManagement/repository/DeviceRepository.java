package com.example.ResourcesManagement.repository;

import com.example.ResourcesManagement.entity.DevicesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeviceRepository  extends JpaRepository<DevicesEntity,Long> {
    List<DevicesEntity> findByAssignedUserId(Long userId);
}
