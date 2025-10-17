package com.example.ResourcesManagement.repository;

import com.example.ResourcesManagement.entity.ChecklistItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CheckListItemRepository extends JpaRepository<ChecklistItemEntity, Long> {

}
