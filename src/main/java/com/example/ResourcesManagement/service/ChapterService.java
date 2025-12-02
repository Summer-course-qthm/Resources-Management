package com.example.ResourcesManagement.service;

import com.example.ResourcesManagement.DTO.request.ChapterRequestDTO;
import com.example.ResourcesManagement.DTO.response.ChapterResponseDTO;
import com.example.ResourcesManagement.entity.ChapterEntity;
import com.example.ResourcesManagement.entity.UserEntity;
import com.example.ResourcesManagement.repository.ChapterRepository;
import com.example.ResourcesManagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChapterService {
    @Autowired
    ChapterRepository chapterRepository;

    @Autowired
    UserRepository userRepository;

    public List<ChapterResponseDTO> getChapters() {
        List<ChapterEntity> chapters = chapterRepository.findAll();

        return chapters.stream().map(chapter -> ChapterResponseDTO.builder()
                .id(chapter.getId())
                .name(chapter.getName())
                .description(chapter.getDescription())
                .build()).toList();

    }

    // thêm chapter mới
    public void addChapter(ChapterRequestDTO chapterResponseDTO) {
        // kiem tra ten chapter da ton tai chua
        if (chapterRepository.existsByName(chapterResponseDTO.getName())) {
            throw new RuntimeException("Chapter name already exists");
        }

        ChapterEntity chapter = ChapterEntity.builder()
                .name(chapterResponseDTO.getName())
                .description(chapterResponseDTO.getDescription())
                .build();

        chapterRepository.save(chapter);
    }

    public void updateChapter(Long id, ChapterRequestDTO chapterRequestDTO) {
        ChapterEntity chapter = chapterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Chapter not found"));

        chapter.setName(chapterRequestDTO.getName());
        chapter.setDescription(chapterRequestDTO.getDescription());

        chapterRepository.save(chapter);
    }

    public void deleteChapter(Long id) {
        ChapterEntity chapter = chapterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Chapter not found"));



        // Kiểm tra nếu có user nào thuộc chapter này thì không cho xóa
        List<UserEntity> users = userRepository.findByChapter(chapter);
        if (!users.isEmpty()) {
            throw new RuntimeException("Cannot delete chapter with associated users");
        }


        chapterRepository.delete(chapter);
    }

    // chuyển tất cả user từ chapter này sang chapter khác

    public void transferUsersToChapter(Long oldChapterId, Long newChapterId) {
        // lấy tất cả nhân viên thuộc chapter cũ
        ChapterEntity oldChapter = chapterRepository.findById(oldChapterId)
                .orElseThrow(() -> new RuntimeException("Old Chapter not found"));
        List <UserEntity> users = userRepository.findByChapter(oldChapter);

        // lấy chapter mới
        ChapterEntity newChapter = chapterRepository.findById(newChapterId)
                .orElseThrow(() -> new RuntimeException("New Chapter not found"));
        // chuyển từng user sang chapter mới
        for (UserEntity user : users) {
            user.setChapter(newChapter);
            userRepository.save(user);
        }

    }

    public long countChapters() {
        return chapterRepository.count();
    }

    public ChapterRequestDTO getChapterById(Long id) {
        ChapterEntity chapter = chapterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Chapter not found"));

        return ChapterRequestDTO.builder()
                .name(chapter.getName())
                .description(chapter.getDescription())
                .build();
    }
}
