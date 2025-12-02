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
@Table(name = "checklist_items")
public class ChecklistItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long itemId;

    @Column(name = "item_description")
    private String itemDescription;

    // thêm mục đã check hay chưa
    @Column(name = "is_checked")
    private Boolean isChecked;

    // Nhiều mục con thuộc về một Checklist

    @ManyToOne
    @JoinColumn(name = "checklist_id")
    private ChecklistEntity checklist;
}
