package com.example.ResourcesManagement.DTO.response;

import lombok.*;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestResponseDTO {
    private Long requestId;
    private String deviceType;
    private String description;
    private String status;
    private UserResponseDTO user;
}
