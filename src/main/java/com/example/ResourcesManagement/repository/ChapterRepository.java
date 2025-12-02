package com.example.ResourcesManagement.repository;

import com.example.ResourcesManagement.entity.ChapterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChapterRepository extends JpaRepository<ChapterEntity, Long> {
    boolean existsByName(String name);
}
