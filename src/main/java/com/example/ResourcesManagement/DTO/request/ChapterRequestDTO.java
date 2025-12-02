package com.example.ResourcesManagement.DTO.request;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChapterRequestDTO {
    private String name;
    private String description;
}
