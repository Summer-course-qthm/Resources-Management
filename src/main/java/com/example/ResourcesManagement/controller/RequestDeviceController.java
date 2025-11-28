/*
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
    public ResponseEntity<String> checkRequest(@RequestParam Long requestId, @RequestParam Long userId) {
      String statusRequest = requestDeviceService.checkStock(requestId, userId);
       return ResponseEntity.ok().body(statusRequest);
    }

    // lấy hết request
    @GetMapping("/request-device")
    public ResponseEntity<List<RequestResponseDTO>> getAllRequest() {
        List<RequestResponseDTO> ListRequest = requestDeviceService.getAllRequest();
        return ResponseEntity.ok().body(ListRequest);
    }

    // xóa request
    @DeleteMapping ("/request-device/{id}")
    public ResponseEntity<String> deleteRequest(@PathVariable Long id) {
        requestDeviceService.deleteRequest(id);
        return ResponseEntity.ok().body("Request deleted successfully");
    }

    //trả thiết bi
    @PostMapping("/return-device")
    public ResponseEntity<String> returnDevice(@RequestParam Long deviceId , @RequestParam Long userId) {
        String returnStatus = requestDeviceService.returnDevice(deviceId , userId);
        return ResponseEntity.ok().body(returnStatus);
    }



}
*/
