package com.example.ResourcesManagement.DTO.response;

import lombok.*;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestResponseDTO {
    private Long id;
    private String deviceType;
    private String description;
    private String status;
    private UserResponseDTO user;
    private String nameDevice; // Tên thiết bị cụ thể (nếu đã được gán)

}
