package com.example.ResourcesManagement.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Table(name = "checklist_items") // Bảng này chứa: "Sạc", "Chuột", "Cáp HDMI"...
public class ChecklistItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long itemId;

    @Column(name = "item_description")
    private String itemDescription; // Tên mục: "Sạc pin kèm theo"

    // Liên kết ngược về bảng cha (ChecklistEntity)
    @ManyToOne
    @JoinColumn(name = "checklist_id")
    private ChecklistEntity checklist;
}