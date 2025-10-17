package com.example.ResourcesManagement.controller;

import com.example.ResourcesManagement.DTO.request.CreateUserRequestDTO;
import com.example.ResourcesManagement.DTO.request.LoginResquestDTO;
import com.example.ResourcesManagement.DTO.response.UserResponseDTO;
import com.example.ResourcesManagement.entity.UserEntity;
import com.example.ResourcesManagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class UserController {
    @Autowired
    UserService userService;


    @PostMapping("/register")
    public ResponseEntity<String> createStudent(@RequestBody CreateUserRequestDTO createUserRequestDTO) { //mapping
        userService.createUser( createUserRequestDTO); //service
        return ResponseEntity.ok("Create successfully");  //status code -> restful
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginResquestDTO request) {
        String token = userService.login(request);
        return ResponseEntity.ok(token);
    }

    // admin
    @GetMapping("/user/listUser")
    public ResponseEntity<List<UserResponseDTO>> getListUser() {
        List<UserResponseDTO> listUser = userService.getListUser();
        return ResponseEntity.ok(listUser);
    }

    // xóa user
    @DeleteMapping("/user/delete/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("Delete successfully");
    }

    // Cập nhật user
    @PutMapping("/user/update/{id}")
    public ResponseEntity<String> updateUser(@PathVariable Long id, @RequestBody CreateUserRequestDTO updateUserRequestDTO) {
        userService.updateUser(id, updateUserRequestDTO);
        return ResponseEntity.ok("Update successfully");
        // viết tường minh
        //return ResponseEntity.status(200).body("Update successfully");
    }

    //xóa user khỏi chapter( dùng PUT bên user cập nhập chapterId = null)
    @PutMapping("/user/removeFromChapter/{id}")
    public ResponseEntity<String> removeUserFromChapter(@PathVariable Long id) {
        userService.removeUserFromChapter(id);
        return ResponseEntity.ok("User removed from chapter successfully");
    }

    // laays user theo chapter

    @GetMapping("/users/byChapter/{chapterId}")
    public ResponseEntity<List<UserResponseDTO>> getUsersByChapter(@PathVariable Long chapterId) {
        List<UserResponseDTO> users = userService.getUsersByChapter(chapterId);
        return ResponseEntity.ok(users);
    }
}
