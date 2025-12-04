package com.example.ResourcesManagement.repository;

import com.example.ResourcesManagement.entity.DevicesEntity;
import com.example.ResourcesManagement.entity.RequestEntity;
import com.example.ResourcesManagement.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequetsRepository  extends JpaRepository<RequestEntity , Long> {
    List<RequestEntity> findByStatus(String status);

    // Hàm tìm thiết bị đầu tiên theo loại và trạng thái
    Optional<DevicesEntity> findFirstByDeviceTypeAndStatus(String deviceType, String status);

    // Hàm kiểm tra xem user đã gửi yêu cầu cùng loại thiết bị chưa
    boolean existsByDeviceTypeAndRequestingUser(String deviceType, UserEntity userEntity);

    // Hàm đếm số lượng request theo trạng thái
    long countByStatus(String status);

    List<RequestEntity> findByRequestingUserId(Long id);
}
