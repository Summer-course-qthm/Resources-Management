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

    // Hành động: "BORROW" (Mượn), "RETURN" (Trả), "MAINTENANCE" (Bảo trì)
    @Column(nullable = false)
    private String action;

    private LocalDateTime actionDate; // Ngày thực hiện

    @Column(columnDefinition = "TEXT")
    private String checklistResult; // Lưu kết quả checklist dưới dạng chuỗi (VD: "Màn hình: OK, Phím: OK")

    private String note; // Ghi chú thêm (nếu có)

    @ManyToOne
    @JoinColumn(name = "device_id")
    private DevicesEntity device;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user; // Người thực hiện hành động (người mượn hoặc người trả)

    @ManyToOne
    @JoinColumn(name = "handler_id")
    private UserEntity handler; // Admin xử lý (người duyệt)
}