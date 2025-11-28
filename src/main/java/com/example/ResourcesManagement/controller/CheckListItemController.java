/*
package com.example.ResourcesManagement.controller;

import com.example.ResourcesManagement.DTO.request.CheckListItemRequestDTO;
import com.example.ResourcesManagement.service.CheckListItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CheckListItemController {
    @Autowired
    CheckListItemService checkListItemService;
    // thêm checkListItem
    @PostMapping("/checkListItem")
    public ResponseEntity<String> addCheckListItem(@RequestBody CheckListItemRequestDTO checkListItemRequestDTO) {
        checkListItemService.addItem(checkListItemRequestDTO);

        return ResponseEntity.ok().body("Thêm checkListItem thành công");
    }
}
*/
