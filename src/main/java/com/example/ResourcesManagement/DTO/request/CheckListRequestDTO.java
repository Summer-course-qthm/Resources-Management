package com.example.ResourcesManagement.DTO.request;

import lombok.*;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckListRequestDTO {
    private String useName; // người tạo checklist
    private String title;
    private String description;
    private Long deviceId;



}
