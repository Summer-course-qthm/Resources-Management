package com.example.ResourcesManagement.service;

import com.example.ResourcesManagement.DTO.request.CheckListRequestDTO;
import com.example.ResourcesManagement.entity.ChecklistEntity;

import com.example.ResourcesManagement.entity.DevicesEntity;
import com.example.ResourcesManagement.repository.CheckListItemRepository;
import com.example.ResourcesManagement.repository.DeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CheckListService {

    @Autowired
    DeviceRepository deviceRepository;

    @Autowired
    CheckListItemRepository checkListItemRepository;
//    public void addCheckList(CheckListRequestDTO checkListRequestDTO) {
//        // tìm thiết bị theo id
//        DevicesEntity devicesEntity = deviceRepository.findById(checkListRequestDTO.getDeviceId()).orElseThrow(() -> new RuntimeException("Invalid device ID"));
//        // nếu thiết bị đã có checklist rồi thì không tạo nữa
//        if (devicesEntity.getChecklist() != null) {
//            throw new RuntimeException("Device already has a checklist");
//        }
//
//        // map request sang entity
//        ChecklistEntity checklistEntity = ChecklistEntity.builder()
//                .title(checkListRequestDTO.getTitle())
//                .content(checkListRequestDTO.getDescription())
//                .items(checkListItemRepository.findAll())
//                .build();
//        // save entity
//
//
//        devicesEntity.setChecklist(checklistEntity);
//        devicesEntity.setIsChecked(true);
//
//    }
}
