package com.example.ResourcesManagement.repository;

import com.example.ResourcesManagement.entity.ChecklistEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CheckListRepository extends JpaRepository<ChecklistEntity,Long> {

    ChecklistEntity findByDeviceType(String deviceType);
}
