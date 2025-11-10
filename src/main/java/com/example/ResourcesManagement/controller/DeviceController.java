package com.example.ResourcesManagement.controller;

import com.example.ResourcesManagement.DTO.request.CreateDeviceRequestDTO;
import com.example.ResourcesManagement.DTO.response.DeviceResponseDTO;
import com.example.ResourcesManagement.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class DeviceController {
    @Autowired
    private DeviceService deviceService;

    // lấy về tất cả thiêt bị
    @GetMapping("/devices")
    public ResponseEntity <List<DeviceResponseDTO>>  getAllDevices() {
        List<DeviceResponseDTO> devices =  deviceService.getAllDevices();
        return ResponseEntity.ok().body(devices);
    }

    // lấy về thiết bị theo user id
    @GetMapping("/devices/user/{userId}")
    public ResponseEntity<List<DeviceResponseDTO>> getDevicesByUserId(@PathVariable Long userId) {
        List<DeviceResponseDTO> devices = deviceService.getDevicesByUserId(userId);
        return ResponseEntity.ok().body(devices);
    }

    // thêm thiết bị mới
    @PostMapping("/devices")
    public ResponseEntity<String> addDevice(@RequestBody CreateDeviceRequestDTO deviceRequest) {
        deviceService.addDevice( deviceRequest);
        return ResponseEntity.ok().body("Device added successfully");
    }


    // cập nhật thiết bị
    @PutMapping("/devices/{id}")
    public ResponseEntity<String> updateDevice(@PathVariable Long id, @RequestBody CreateDeviceRequestDTO createDeviceRequestDTO) {
        deviceService.updateDevice(id, createDeviceRequestDTO);
        return ResponseEntity.ok().body("Device updated successfully");
    }

    // xóa thiết bị
    @DeleteMapping("/devices/{id}")
    public ResponseEntity<String> deleteDevice(@PathVariable Long id) {
        deviceService.deleteDevice(id);
        return ResponseEntity.ok().body("Device deleted successfully");
    }
}
