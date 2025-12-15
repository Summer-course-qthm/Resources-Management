package com.example.ResourcesManagement.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Table(name = "checklists") // Bảng này chứa: "Mẫu kiểm tra Laptop", "Mẫu màn hình"...
public class ChecklistEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "checklist_id")
    private Long checklistId;

    private String title;   // Ví dụ: "Quy trình bàn giao Laptop 2025"
    private String description; // (Đã đổi từ content -> description cho rõ nghĩa)

    @Column(name = "created_by")
    private Long createdBy; // ID người tạo

    // --- QUAN TRỌNG: MẪU NÀY DÙNG CHO LOẠI MÁY NÀO? ---
    // Thay vì list devices, ta chỉ lưu tên loại.
    // Khi mượn máy "Laptop", hệ thống sẽ tìm Checklist nào có deviceType = "Laptop"
    @Column(name = "device_type", unique = true)
    private String deviceType;

    // --- DANH SÁCH CÁC MỤC CON ---
    // Giữ nguyên dòng này: 1 Mẫu có nhiều mục kiểm tra
    @OneToMany(mappedBy = "checklist", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChecklistItemEntity> items;

    // --- XÓA ĐOẠN NÀY ---
    // @OneToMany(mappedBy = "checklist", cascade = CascadeType.ALL)
    // private List<DevicesEntity> devices;  <-- XÓA: Mẫu không nên dính vào máy cụ thể
}