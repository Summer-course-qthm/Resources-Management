package com.example.ResourcesManagement.DTO.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateDeviceRequestDTO {
    private String deviceType; // laptop / monitor / phone / tablet / accessory (loại thiết
    private String deviceName;
    private String status; // available / assigned / under_maintenance(có sẵn / đã gán / đang bảo trì)
    private String note;
    private  Boolean isChecked;
    private  Long checklistId;
}
