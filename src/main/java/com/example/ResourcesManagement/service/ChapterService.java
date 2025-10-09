package com.example.ResourcesManagement.service;

import com.example.ResourcesManagement.DTO.response.ChapterResponseDTO;
import com.example.ResourcesManagement.entity.ChapterEntity;
import com.example.ResourcesManagement.repository.ChapterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChapterService {
    @Autowired
    ChapterRepository chapterRepository;

    public List<ChapterResponseDTO> getChapters() {
        List<ChapterEntity> chapters = chapterRepository.findAll();

        return chapters.stream().map(chapter -> ChapterResponseDTO.builder()
                .name(chapter.getName())
                .description(chapter.getDescription())
                .build()).toList();

    }
}
