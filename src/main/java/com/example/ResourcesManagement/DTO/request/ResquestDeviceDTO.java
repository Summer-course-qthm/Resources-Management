package com.example.ResourcesManagement.DTO.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResquestDeviceDTO {
    private Long userId; // người tạo request
    private String DeviceType; // loại thiết bị cần mượn
    private String description;


}
