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



    public String checkStock( RequestResponseDTO requestResponseDTO) { // userId người gửi request,

        // KIỂM TRA type có tồn tại với userid null hoặc status available không
         boolean hasDeviceAvailable = deviceRepository.existsByStatusAndDeviceType(
                requestResponseDTO.getDeviceType(), "available"
        );
        // nếu biến kiểm tra null hoặc rỗng từ chối request
        if (!hasDeviceAvailable ) {
            // không có thiết bị nào phù hợp
            // cập nhật trạng thái request thành REJECTED
            RequestEntity requestEntity = requetsRepository.findById(requestResponseDTO.getRequestId())
                    .orElseThrow(() -> new RuntimeException("Request not found"));
            requestEntity.setStatus("REJECTED"); // từ chối request
            requetsRepository.save(requestEntity);
            return "No available device found. Request rejected.";
        }

        // kiểm tra user có mượn thiết bị nào chưa chưa && type có tồn tại


        boolean userHasDeviceType = deviceRepository.existsByAssignedUserIdAndDeviceTypeAndStatus(
                requestResponseDTO.getUser().getId(),
                requestResponseDTO.getDeviceType(),
                "assigned"
        );

        if (userHasDeviceType) {
            // user đã mượn thiết bị cùng loại
            // cập nhật trạng thái request thành REJECTED
            RequestEntity requestEntity = requetsRepository.findById(requestResponseDTO.getRequestId())
                    .orElseThrow(() -> new RuntimeException("Request not found"));
            requestEntity.setStatus("REJECTED"); // từ chối request
            requetsRepository.save(requestEntity);
            return "User has already borrowed a device of this type. Request rejected.";
        }


        // bàn giao thiết bị là gắn thiết bị cho user mà gửi request
        RequestEntity requestEntity = requetsRepository.findById(requestResponseDTO.getRequestId())
                .orElseThrow(() -> new RuntimeException("Request not found"));
        UserEntity user = userRepository.findById(requestResponseDTO.getUser().getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        // cập nhật trạng thái request thành APPROVED
        requestEntity.setStatus("APPROVED");
        requestEntity.setApprovingUser(user); // người duyệt request
        requetsRepository.save(requestEntity);
        return "Request approved and device assigned successfully.";


    }
}
