package com.example.ResourcesManagement.controller;

import com.example.ResourcesManagement.DTO.response.ChapterResponseDTO;
import com.example.ResourcesManagement.service.ChapterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ChapterController {

    @Autowired
    ChapterService chapterService;

    @GetMapping("/chapters")
    public ResponseEntity<List<ChapterResponseDTO>> getChapters() {
        List<ChapterResponseDTO> chapters = chapterService.getChapters();
        return ResponseEntity.ok().body(chapters);

    }

}
