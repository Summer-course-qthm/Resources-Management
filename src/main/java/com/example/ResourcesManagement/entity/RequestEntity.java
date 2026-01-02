package com.example.ResourcesManagement.entity;

import jakarta.persistence.*;
import lombok.*;

// User Entity
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "requests")
public class RequestEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Long requestId;

    private String deviceType;
    private String description;
    private String status;
    private  String nameDevice;

    // Nhiều request được tạo bởi một User
    @ManyToOne
    @JoinColumn(name = "request_user_id")
    private UserEntity requestingUser; // người tạo request

    // Nhiều request được duyệt bởi một User
    @ManyToOne
    @JoinColumn(name = "approve_user_id" )
    private UserEntity approvingUser; // người duyệt request

    @ManyToOne
    @JoinColumn(name = "device_id") // Tạo cột device_id trong bảng requests để liên kết
    private DevicesEntity device;
}
