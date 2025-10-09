package com.example.ResourcesManagement.entity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

// User Entity
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "checklists")
public class ChecklistEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "checklist_id")
    private Long checklistId;

    private String title;
    private String content;

    // Một Checklist có nhiều mục con (Checklist_Items)
    @OneToMany(mappedBy = "checklist", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChecklistItemEntity> items;

    // Một Checklist có thể được áp dụng cho nhiều thiết bị
    @OneToMany(mappedBy = "checklist", cascade = CascadeType.ALL)
    private List<DevicesEntity> devices;
}
