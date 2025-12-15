package com.example.ResourcesManagement.DTO.request;

import lombok.Data;
import java.util.List;

@Data // Lombok tự sinh Getter/Setter
public class ApproveRequestDTO {

    // name="requestId"
    private Long requestId;

    // name="selectedDeviceId"
    private Long selectedDeviceId;

    // name="checkedItems" (Spring tự gom checkbox thành List)
    private List<String> checkedItems;

    // name="note"
    private String note;
}