package com.example.ResourcesManagement.repository;

import com.example.ResourcesManagement.entity.DevicesEntity;
import com.example.ResourcesManagement.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceRepository extends JpaRepository<DevicesEntity, Long> {

    // 1. Tìm danh sách thiết bị đang được gán cho user ID cụ thể
    List<DevicesEntity> findByAssignedUserId(Long userId);

    // 2. Tìm thiết bị theo tên chính xác
    Optional<DevicesEntity> findByDeviceName(String deviceName);

    // 3. Tìm thiết bị ĐẦU TIÊN khớp loại và trạng thái (trả về 1 cái hoặc null)
    Optional<DevicesEntity> findFirstByDeviceTypeAndStatus(String deviceType, String status);

    // 4. Kiểm tra sự tồn tại (trả về true/false)
    boolean existsByStatusAndDeviceType(String status, String deviceType);

    // 5. Kiểm tra logic nghiệp vụ: Người này có đang mượn loại máy này với trạng thái này không
    boolean existsByAssignedUserIdAndDeviceTypeAndStatus(Long userId, String deviceType, String status);

    // 6. Tìm kiếm cho thanh Search (Tìm theo tên HOẶC loại)
    List<DevicesEntity> findByDeviceNameContainingIgnoreCaseOrDeviceTypeContainingIgnoreCase(String name, String type);

    // 7. Tìm thiết bị đầu tiên (Dùng để auto-assign nếu cần)
    Optional<DevicesEntity> findFirstByStatusAndDeviceType(String status, String deviceType);

    // 8. Kiểm tra user có giữ loại máy này không
    boolean existsByDeviceTypeAndAssignedUser(String deviceType, UserEntity user);

    // 9. Đếm số lượng
    int countByAssignedUserIdAndDeviceTypeAndStatus(Long id, String deviceType, String assigned);

    // =========================================================================
    // PHẦN BỔ SUNG ĐỂ CHẠY ĐƯỢC CONTROLLER "PROCESS REQUEST"
    // =========================================================================

//    /**
//     * CÁCH 1: Tìm theo TÊN thiết bị (chứa từ khóa) và Trạng thái.
//     * Dùng cái này nếu trong Controller bạn gọi: findByDeviceNameContainingAndStatus
//     */
// Tìm chính xác loại máy (VD: "Laptop") và đang rảnh (AVAILABLE)

        List<DevicesEntity> findByDeviceTypeAndStatus(String deviceType, String status);

//
}