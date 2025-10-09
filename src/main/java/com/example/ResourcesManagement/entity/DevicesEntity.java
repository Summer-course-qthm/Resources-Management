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
@Table(name = "devices")
public class DevicesEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "device_id")
    private Long deviceId;

    @Column(name = "device_name")
    private String deviceName;

    @Column(name = "device_type")
    private String deviceType; // laptop, monitor, keyboard, mouse, etc.


    private String status; // available / assigned / under_maintenance
    private String note;
    private Boolean isChecked;

    // Nhiều thiết bị có thể được gán cho một User
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity assignedUser;

    // Nhiều thiết bị có thể dùng chung một checklist
    @ManyToOne
    @JoinColumn(name = "checklist_id")
    private ChecklistEntity checklist;

}
