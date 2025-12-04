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
@Table(name = "notifications")
public class NotificationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;   // Ví dụ: "Yêu cầu được duyệt"
    private String message; // Ví dụ: "Yêu cầu mượn Laptop của bạn đã được Admin chấp nhận."
    private boolean isRead = false; // Mặc định là false (Chưa đọc)

    private LocalDateTime createdAt; // Thời gian tạo thông báo

    // Thông báo này gửi cho ai?
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;
}