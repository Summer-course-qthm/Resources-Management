package com.example.ResourcesManagement.DTO.request;

import lombok.*;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckListRequestDTO {
    private Long userId;
    private String name;
    private String description;
    private Long deviceId;


}
