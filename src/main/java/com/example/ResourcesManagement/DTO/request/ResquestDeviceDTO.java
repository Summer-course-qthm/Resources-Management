package com.example.ResourcesManagement.DTO.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResquestResquestDTO {
    private Long userId;
    private String type;
    private String description;
    private Long deviceId;


}
