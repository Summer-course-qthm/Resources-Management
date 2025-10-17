package com.example.ResourcesManagement.controller;

import com.example.ResourcesManagement.DTO.request.ResquestDeviceDTO;
import com.example.ResourcesManagement.DTO.response.RequestResponseDTO;
import com.example.ResourcesManagement.service.RequestDeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class RequestDeviceController {
    @Autowired
    private RequestDeviceService requestDeviceService;

    // user gửi yêu cầu mượn thiết bị
    @PostMapping("/request-device")
    public ResponseEntity<String> requestDevice(@RequestBody ResquestDeviceDTO request) {
        requestDeviceService.addRequest(request);
        return ResponseEntity.ok().body("Request sent successfully");
    }

    // admin bắt đầu xử lý yêu cầu (chấp nhận hoặc từ chối)
    @PutMapping ("/check-stock")
    public ResponseEntity<String> checkRequest(@RequestBody RequestResponseDTO requestResponseDTO) {
      String statusRequest = requestDeviceService.checkStock(requestResponseDTO);
       return ResponseEntity.ok().body("Request processed successfully");
    }

    // lấy hết request
    @GetMapping("/request-device")
    public ResponseEntity<List<RequestResponseDTO>> getAllRequest() {
        List<RequestResponseDTO> ListRequest = requestDeviceService.getAllRequest();
        return ResponseEntity.ok().body(ListRequest);
    }


    //user bàn trả thiết bị



}
