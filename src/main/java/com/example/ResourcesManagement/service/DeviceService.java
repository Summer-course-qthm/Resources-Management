package com.example.ResourcesManagement.service;

import com.example.ResourcesManagement.DTO.request.CreateDeviceRequestDTO;
import com.example.ResourcesManagement.DTO.response.DeviceResponseDTO;
import com.example.ResourcesManagement.entity.DevicesEntity;
import com.example.ResourcesManagement.repository.DeviceRepository;
import com.example.ResourcesManagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DeviceService {

    @Autowired
    private  DeviceRepository deviceRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    public DeviceService(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    // Lấy tất cả thiết bị
    public List<DeviceResponseDTO> getAllDevices() {

        List<DevicesEntity> entities = deviceRepository.findAll();

        //  map qua DTO
        return entities.stream().map( device -> DeviceResponseDTO.builder()
                .name(device.getDeviceName())
                .status(device.getStatus())
                .assignedUser(device.getAssignedUser() != null ? device.getAssignedUser().getUsername() : "Unassigned")
                .build()).collect(Collectors.toList());
    }


    // Thêm thiết bị mới
    public void addDevice( CreateDeviceRequestDTO createDeviceRequestDTO) {

        DevicesEntity devices = DevicesEntity.builder()
                .deviceName(createDeviceRequestDTO.getDeviceName())
                .status(createDeviceRequestDTO.getStatus())
                .note(createDeviceRequestDTO.getNote())
                .isChecked(false) // Mặc định là chưa kiểm tra
                .checklist(null)
                .build();

        deviceRepository.save(devices);
    }

    // Cập nhật trạng thái thiết bị
    public void updateDevice(Long id, CreateDeviceRequestDTO createDeviceRequestDTO) {

        DevicesEntity device = deviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Device not found"));

        device.setStatus(createDeviceRequestDTO.getStatus());
        device.setNote(createDeviceRequestDTO.getNote());
        device.setIsChecked(createDeviceRequestDTO.getIsChecked());
        deviceRepository.save(device);
    }


    public List<DeviceResponseDTO> getDevicesByUserId(Long userId) {
        List<DevicesEntity> devices = deviceRepository.findByAssignedUserId(userId); // lấy danh sách thiết bị theo userId

        return devices.stream()
                .map(device -> DeviceResponseDTO.builder()
                        .name(device.getDeviceName())
                        .status(device.getStatus())
                        .assignedUser(device.getAssignedUser() != null ? device.getAssignedUser().getUsername() : "Unassigned")
                        .build()).toList();

    }
}