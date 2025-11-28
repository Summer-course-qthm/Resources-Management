/*
package com.example.ResourcesManagement.controller;

import com.example.ResourcesManagement.DTO.request.CheckListRequestDTO;
import com.example.ResourcesManagement.service.CheckListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CheckListController {

    @Autowired
    CheckListService checkListService;

    @PostMapping ("/checklist")
    public ResponseEntity<String> createCheckList(@RequestBody CheckListRequestDTO checkListRequestDTO) {
        checkListService.addCheckList(checkListRequestDTO);
        return ResponseEntity.ok().body("CheckList created successfully");

    }


}
*/
