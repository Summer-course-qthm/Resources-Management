package com.example.ResourcesManagement.DTO.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateUserRequestDTO {
    private String username;
    private String password;
    private String email;
    private String phone;
    private Long chapterId;

}
