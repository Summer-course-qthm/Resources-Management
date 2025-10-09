package com.example.ResourcesManagement.repository;

import com.example.ResourcesManagement.entity.RequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequetsRepository  extends JpaRepository<RequestEntity , Long> {
}
