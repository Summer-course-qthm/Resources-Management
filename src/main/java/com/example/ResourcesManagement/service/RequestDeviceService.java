package com.example.ResourcesManagement.service;

import com.example.ResourcesManagement.DTO.request.ResquestDeviceDTO;
import com.example.ResourcesManagement.DTO.response.RequestResponseDTO;
import com.example.ResourcesManagement.DTO.response.UserResponseDTO;
import com.example.ResourcesManagement.entity.DevicesEntity;
import com.example.ResourcesManagement.entity.RequestEntity;
import com.example.ResourcesManagement.entity.UserEntity;
import com.example.ResourcesManagement.repository.DeviceRepository;
import com.example.ResourcesManagement.repository.RequetsRepository;
import com.example.ResourcesManagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RequestDeviceService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RequetsRepository requetsRepository;

    @Autowired
    DeviceRepository deviceRepository;

    public String addRequest(ResquestDeviceDTO request) {
        UserEntity User = userRepository.findById(request.getUserId()).orElseThrow(()-> new RuntimeException("User not found"));
        RequestEntity requestEntity = RequestEntity.builder()
                .requestingUser(User)
                .deviceType(request.getDeviceType())
                .description(request.getDescription())
                .status("PENDING") // Mặc định trạng thái là PENDING
                .build();

        requetsRepository.save(requestEntity);
        System.out.println("Request saved: " + requestEntity);
        return "Request added successfully";



    }



    public List<RequestResponseDTO> getAllRequest() {
        List<RequestEntity> requestEntities = requetsRepository.findAll();
        // Chuyển đổi List<RequestEntity> thành List<RequestResponseDTO>
        List<RequestResponseDTO> requestResponseDTOs = requestEntities.stream().map(requestEntity -> {
           // người gửi request
            UserEntity userEntity = requestEntity.getRequestingUser();
            // map user qua userResponseDTO
            UserResponseDTO userResponseDTO = UserResponseDTO.builder()
                    .id(userEntity.getId())
                    .username(userEntity.getUsername())
                    .chapterName(userEntity.getChapter() != null ? userEntity.getChapter().getName() : null)
                    .build();
            return RequestResponseDTO.builder()
                    .requestId(requestEntity.getRequestId())
                    .user(userResponseDTO)
                    .deviceType(requestEntity.getDeviceType())
                    .description(requestEntity.getDescription())
                    .status(requestEntity.getStatus())
                    .build();
        }).toList();

        return requestResponseDTOs;
    }



    public String checkStock( Long  requestId , Long userId) { // userId người duyệt request,

        // KIỂM TRA type có tồn tại với userid null hoặc status available không
        RequestEntity requestEntity = requetsRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        String deviceType = requestEntity.getDeviceType();
         boolean hasDeviceAvailable = deviceRepository.existsByStatusAndDeviceType(
                 "available" ,deviceType
        );
         System.out.println(hasDeviceAvailable);
        // nếu biến kiểm tra null hoặc rỗng từ chối request
        if (!hasDeviceAvailable ) {
            // không có thiết bị nào phù hợp
            // cập nhật trạng thái request thành REJECTED
            requestEntity.setStatus("REJECTED"); // từ chối request
            requetsRepository.save(requestEntity);
            return "No available device found. Request rejected.";
        }

        // kiểm tra user có mượn thiết bị nào chưa chưa && type có tồn tại
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        boolean userHasDeviceType = requetsRepository.existsByDeviceTypeAndRequestingUser(deviceType, requestEntity.getRequestingUser() );


        System.out.println(userHasDeviceType);
        if (userHasDeviceType) {
            // user đã mượn thiết bị cùng loại
            // cập nhật trạng thái request thành REJECTED
            requestEntity.setStatus("REJECTED"); // từ chối request
            requestEntity.setApprovingUser(userEntity);
            requetsRepository.save(requestEntity);
            return "User has already borrowed a device of this type. Request rejected.";
        }


        // bàn giao thiết bị là gắn thiết bị cho user mà gửi request

        // cập nhật trạng thái request thành APPROVED
        requestEntity.setStatus("APPROVED");
        requestEntity.setApprovingUser(userEntity); // người duyệt request
        requetsRepository.save(requestEntity);
        return "Request approved and device assigned successfully.";


    }

    public void deleteRequest(Long id) {
        RequestEntity requestEntity = requetsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        requetsRepository.delete(requestEntity);
    }

    //  trả thiết bị
    public String returnDevice(Long deviceId, Long userId) {
        DevicesEntity device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("Device not found"));

        // kiểm tra thiết bị có được đúng người đó mượn không
        if (device.getAssignedUser() == null || !device.getAssignedUser().getId().equals(userId)) {
            return "Device is not assigned to this user.";
        }

        // cập nhật trạng thái thiết bị thành available và bỏ gán user
        device.setStatus("available");
        device.setAssignedUser(null);
        deviceRepository.save(device);

        return "Device returned successfully.";
    }

    public long countRequestDevices() {
        // Đếm tất cả các request có trạng thái là 'PENDING'
        return requetsRepository.countByStatus("PENDING");
    }
}
