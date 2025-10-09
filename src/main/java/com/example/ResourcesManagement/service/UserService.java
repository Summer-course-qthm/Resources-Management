package com.example.ResourcesManagement.service;

import com.example.ResourcesManagement.DTO.request.CreateUserRequestDTO;
import com.example.ResourcesManagement.DTO.request.LoginResquestDTO;
import com.example.ResourcesManagement.DTO.response.UserResponseDTO;
import com.example.ResourcesManagement.entity.ChapterEntity;
import com.example.ResourcesManagement.entity.UserEntity;
import com.example.ResourcesManagement.repository.ChapterRepository;
import com.example.ResourcesManagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;


@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ChapterRepository chapterRepository;

    public String  createUser(CreateUserRequestDTO createUserRequestDTO) {
        // kiểm tra user tồn tại chưa
        if (userRepository.findByUsername(createUserRequestDTO.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        //tạo user mới
        UserEntity user = new UserEntity();
        user.setUsername(createUserRequestDTO.getUsername());
        user.setPassword(passwordEncoder.encode(createUserRequestDTO.getPassword())); //mã hóa password
        user.setRole("USER");
        user.setPhone(createUserRequestDTO.getPhone());
        user.setEmail(createUserRequestDTO.getEmail());
        // lấy chapter ừ repository
        ChapterEntity cha = chapterRepository.findById(createUserRequestDTO.getChapterId())
                .orElseThrow(() -> new RuntimeException("Chapter not found"));
        user.setChapter(cha);
        //lưu
        userRepository.save(user);
        return "Register successful";
    }


    public String login(LoginResquestDTO request) {
        // kiem tra username va password
        // kiểm tra user có tồn tại hay kh
        UserEntity user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        // pass của 'user' -> ở trong db đã được encode(password: àashfdjgjksaf)
        // cầm password 'thiệt' mà user input, dùng hàm so với cái đã encode trong db
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        //tạo token và return
        return jwtService.generateToken(user.getUsername());



    }

    public ArrayList<UserResponseDTO> getListUser() {
        ArrayList<UserEntity> listUser = (ArrayList<UserEntity>) userRepository.findAll();
        ArrayList<UserResponseDTO> listUserResponse = new ArrayList<>();
        for (UserEntity user : listUser) {
            UserResponseDTO userResponseDTO = new UserResponseDTO();
            userResponseDTO.setId(user.getId());
            userResponseDTO.setUsername(user.getUsername());
            userResponseDTO.setChapterName(user.getChapter().getName());

            listUserResponse.add(userResponseDTO);
        }
        return listUserResponse;
    }

    public void deleteUser(Long id) {
        // Bước 1: Kiểm tra xem ID có bị null không
        if (id == null) {
            throw new IllegalArgumentException("User ID must not be null");
        }

        // Bước 2: Kiểm tra xem user có thực sự tồn tại trong database không
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }

        // Bước 3: Nếu mọi thứ đều ổn, tiến hành xóa
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
}
