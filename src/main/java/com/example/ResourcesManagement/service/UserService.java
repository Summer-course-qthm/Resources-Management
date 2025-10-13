package com.example.ResourcesManagement.service;

import com.example.ResourcesManagement.DTO.request.CreateUserRequestDTO;
import com.example.ResourcesManagement.DTO.request.LoginResquestDTO;
import com.example.ResourcesManagement.DTO.response.UserResponseDTO;
import com.example.ResourcesManagement.entity.ChapterEntity;
import com.example.ResourcesManagement.entity.UserEntity;
import com.example.ResourcesManagement.repository.ChapterRepository;
import com.example.ResourcesManagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class UserService implements UserDetailsService { // <-- THAY ĐỔI 1: implements UserDetailsService

    @Autowired
    UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ChapterRepository chapterRepository;

    public String  createUser(CreateUserRequestDTO createUserRequestDTO) {
        if (userRepository.findByUsername(createUserRequestDTO.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        UserEntity user = new UserEntity();
        user.setUsername(createUserRequestDTO.getUsername());
        user.setPassword(passwordEncoder.encode(createUserRequestDTO.getPassword()));
        user.setRole("USER"); // Mặc định role là USER
        user.setPhone(createUserRequestDTO.getPhone());
        user.setEmail(createUserRequestDTO.getEmail());

        ChapterEntity cha = chapterRepository.findById(createUserRequestDTO.getChapterId())
                .orElseThrow(() -> new RuntimeException("Chapter not found"));
        user.setChapter(cha);

        userRepository.save(user);
        return "Register successful";
    }


    public String login(LoginResquestDTO request) {
        UserEntity user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        // Tạo token và return
        return jwtService.generateToken(user.getUsername());
    }

    public List<UserResponseDTO> getListUser() {
        return userRepository.findAll().stream()
                .map(user -> {
                    UserResponseDTO dto = new UserResponseDTO();
                    dto.setId(user.getId());
                    dto.setUsername(user.getUsername());
                    if (user.getChapter() != null) {
                        dto.setChapterName(user.getChapter().getName());
                    } else {
                        dto.setChapterName("N/A");
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public void deleteUser(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("User ID must not be null");
        }
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }



    public void updateUser(Long id, CreateUserRequestDTO updateUserRequestDTO) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setUsername(updateUserRequestDTO.getUsername());
        user.setPassword(passwordEncoder.encode(updateUserRequestDTO.getPassword()));
        user.setPhone(updateUserRequestDTO.getPhone());
        user.setEmail(updateUserRequestDTO.getEmail());

        ChapterEntity chapter = chapterRepository.findById(updateUserRequestDTO.getChapterId())
                .orElseThrow(() -> new RuntimeException("Chapter not found"));
        user.setChapter(chapter);

        userRepository.save(user);
    }

    // THAY ĐỔI 2: Thêm phương thức bắt buộc của UserDetailsService
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Tìm kiếm user trong CSDL bằng username
        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng với username: " + username));

        // Tạo một đối tượng UserDetails từ UserEntity
        // Spring Security sẽ sử dụng thông tin này để xác thực và phân quyền
        return new User(
                userEntity.getUsername(),
                userEntity.getPassword(),
                // Chuyển đổi role (String) của bạn thành GrantedAuthority
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + userEntity.getRole()))
        );
    }
}