package com.example.ResourcesManagement.service;

import com.example.ResourcesManagement.DTO.request.CheckListItemRequestDTO;
import com.example.ResourcesManagement.entity.ChecklistItemEntity;
import com.example.ResourcesManagement.repository.CheckListItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CheckListItemService {
    @Autowired
    CheckListItemRepository checkListItemRepository;

    public void addItem(CheckListItemRequestDTO checkListItemRequestDTO) {
        ChecklistItemEntity checklistItemEntity = ChecklistItemEntity.builder()
                .itemDescription(checkListItemRequestDTO.getDescription())
                .build();
        checkListItemRepository.save(checklistItemEntity);

    }
}
