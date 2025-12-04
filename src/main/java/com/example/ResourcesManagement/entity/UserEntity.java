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
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    // phone
    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String role; // e.g., "USER", "ADMIN"

    // Nhiều User thuộc về một Chapter
    @ManyToOne
    @JoinColumn(name = "chapter_id")
    private ChapterEntity chapter;

}
