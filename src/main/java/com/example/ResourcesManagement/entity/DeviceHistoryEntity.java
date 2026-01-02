package com.example.ResourcesManagement.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "device_history")
public class DeviceHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- THÔNG TIN HÀNH ĐỘNG ---

    // Loại hành động: "BORROW" (Mượn), "RETURN" (Trả), "MAINTENANCE" (Bảo trì)
    // Nên đặt nullable = false để không bao giờ bị null
    @Column(name = "action", nullable = false, length = 50)
    private String action; // lưu lịch sử mượn / trả

    @Column(nullable = false)
    private LocalDateTime actionDate; // Ngày giờ thực hiện

    // --- KẾT QUẢ CHECKLIST (SNAPSHOT) ---
    // Đây là nơi lưu chuỗi kết quả: "Sạc: Có, Chuột: Có..."
    // Dùng TEXT để chứa được nội dung dài
    @Column(columnDefinition = "TEXT")
    private String checklistResult;

    // Ghi chú thêm (Ví dụ: "Máy bị trầy xước nhẹ")
    @Column(columnDefinition = "TEXT")
    private String note;

    // --- CÁC MỐI QUAN HỆ (QUAN TRỌNG) ---

    // 1. Máy nào? (Bắt buộc phải có)
    // FetchType.LAZY: Giúp tải trang nhanh hơn, chỉ query thông tin máy khi cần thiết
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false)
    private DevicesEntity device;

    // 2. Người dùng liên quan? (Người mượn / Người trả)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    // 3. Admin xử lý? (Người duyệt yêu cầu này)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "handler_id")
    private UserEntity handler;
}