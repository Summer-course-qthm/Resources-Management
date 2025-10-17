package com.example.ResourcesManagement.repository;

import com.example.ResourcesManagement.entity.DevicesEntity;
import com.example.ResourcesManagement.entity.RequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RequetsRepository  extends JpaRepository<RequestEntity , Long> {
    Object findByStatus(String pending);
    Optional<DevicesEntity> findFirstByDeviceTypeAndStatus(String deviceType, String status);
}
