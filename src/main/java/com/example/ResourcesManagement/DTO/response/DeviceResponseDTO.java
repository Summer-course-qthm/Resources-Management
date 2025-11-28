package com.example.ResourcesManagement.DTO.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceResponseDTO {
    private Long deviceId;
    private String name;
    private String type;
    private String status;
    private String note;
    private String assignedUser; // Username of the assigned user



}
